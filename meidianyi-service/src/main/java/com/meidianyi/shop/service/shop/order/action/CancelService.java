package com.meidianyi.shop.service.shop.order.action;

import com.beust.jcommander.internal.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BigDecimalPlus;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.Operator;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.data.AccountData;
import com.meidianyi.shop.service.pojo.shop.member.data.ScoreData;
import com.meidianyi.shop.service.pojo.shop.member.data.UserCardData;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.api.StoreCancelOrderParam;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.shop.activity.factory.OrderCreateMpProcessorFactory;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.operation.RecordTradeService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperationJudgment;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.record.OrderActionService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mp取消订单
 * @author 王帅
 *
 */
@Component
public class CancelService extends ShopBaseService implements IorderOperate<OrderOperateQueryParam,OrderOperateQueryParam> {

    @Autowired
    private OrderInfoService orderInfo;

    @Autowired
    private RecordTradeService recordMemberTrade;

    @Autowired
    private OrderActionService orderAction;

    @Autowired
    private OrderGoodsService orderGoods;

    @Autowired
    private GoodsService goods;

    @Autowired
    private GoodsSpecProductService goodsSpecProduct;

    @Autowired
    private CouponService coupon;

    @Autowired
    private  AtomicOperation atomicOperation;

    @Autowired
    private OrderCreateMpProcessorFactory orderCreateMpProcessorFactory;

    @Override
    public OrderServiceCode getServiceCode() {
        return OrderServiceCode.CANCEL;
    }

    @Override
    public Object query(OrderOperateQueryParam param) throws MpException {
        return null;
    }

    @Override
    public ExecuteResult execute(OrderOperateQueryParam param) {
        OrderInfoVo order = orderInfo.getByOrderId(param.getOrderId(), OrderInfoVo.class);
        if(order == null) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_NOT_EXIST, null);
        }
        if(!OrderOperationJudgment.mpIsCancel(order)) {
            logger().error("该订单不能取消");
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_CANCEL_NOT_CANCEL, null);
        }
        try {

            transaction(()->{
                //退支付金额
                returnOrder(order);
                orderInfo.setOrderstatus(order.getOrderSn(), OrderConstant.ORDER_CANCELLED);
                //退优惠卷
                if(BigDecimalUtil.compareTo(order.getDiscount() , null) > 0) {
                    coupon.releaserCoupon(order.getOrderSn());
                }
                //库存更新
                updateStockAndStatus(order);
            });
        } catch (Exception e) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_CANCEL_FAIL, null);
        }
        //订单状态记录
        orderAction.addRecord(order, param, order.getOrderStatus() , "买家取消订单");
        return null;
    }

    public void returnOrder(OrderInfoVo order) throws MpException {
        if(BigDecimalUtil.compareTo(order.getScoreDiscount(), null) > 0) {
            //积分
            refundScoreDiscount(order, order.getScoreDiscount());
        }
        if(BigDecimalUtil.compareTo(order.getUseAccount(), null) > 0) {
            refundUseAccount(order, order.getUseAccount());
            //余额
        }
        if(BigDecimalUtil.compareTo(order.getMemberCardBalance(), null) > 0) {
            //卡余额
            refundMemberCardBalance(order, order.getMemberCardBalance());
        }
    }
    /**
     * 	退会员卡余额
     * @param order
     * @param money
     * @throws MpException
     */
    public void refundMemberCardBalance(OrderInfoVo order , BigDecimal money) throws MpException {
        if(BigDecimalUtil.compareTo(money, null) == 0) {
            return;
        }

        TradeOptParam tradeOpt = TradeOptParam
            .builder()
            .adminUserId(0)
            .tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val())
            .tradeType(RecordTradeEnum.TYPE_CRASH_MCARD_ACCOUNT_REFUND.val())
            .build();

        UserCardData userCardData = UserCardData.newBuilder().
            userId(order.getUserId()).
            cardId(order.getCardId()).
            cardNo(order.getCardNo()).
            money(money).
//		reason("订单取消，订单会员卡余额支付退款").
    reasonId(RemarkTemplate.ORDER_CANCEL_RETURN_CARD_ACCOUNT.code).
            //普通会员卡
                type(CardConstant.MCARD_TP_NORMAL).
                orderSn(order.getOrderSn()).
                tradeOpt(tradeOpt).
                chargeType(CardConstant.CHARGE_TYPE_REFUND)
            .build();
        //调用退会员卡接口
        recordMemberTrade.updateUserEconomicData(userCardData);
    }

    /**
     * 	退余额
     * @param order
     * @param money
     * @throws MpException
     */
    public void refundUseAccount(OrderInfoVo order , BigDecimal money) throws MpException {
        if(BigDecimalUtil.compareTo(money, null) == 0) {
            return;
        }
        AccountData accountData = AccountData.newBuilder().
            userId(order.getUserId()).
            orderSn(order.getOrderSn()).
            //退款金额
                amount(money).
                remarkCode(RemarkTemplate.ORDER_CANCEL.code).
                remarkData(order.getOrderSn()).
            //remark("订单取消："+order.getOrderSn()+"余额退款").
                payment(order.getPayCode()).
            //支付类型
                isPaid(RecordTradeEnum.UACCOUNT_RECHARGE.val()).
            //后台处理时为操作人id为0
                adminUser(0).
            //用户余额退款
                tradeType(RecordTradeEnum.TYPE_CRASH_MACCOUNT_REFUND.val()).
            //资金流量-支出
                tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val()).build();
        //调用退余额接口
        recordMemberTrade.updateUserEconomicData(accountData);
    }

    /**
     * 	积分退款
     * @param order
     * @param money
     * @return
     * @throws MpException
     */
    public void refundScoreDiscount(OrderInfoVo order , BigDecimal money) throws MpException {
        if(BigDecimalUtil.compareTo(money, null) == 0) {
            return;
        }
        //金额换算成积分
        Integer score = BigDecimalUtil.multiplyOrDivide(
            BigDecimalPlus.create(new BigDecimal(order.getScoreProportion()), Operator.multiply),
            BigDecimalPlus.create(money,null)
        ).intValue();

        ScoreData scoreData = ScoreData.newBuilder().
            userId(order.getUserId()).
            orderSn(order.getOrderSn()).
            //退款积分
                score(score).
                remarkCode(RemarkTemplate.ORDER_CANCEL_SCORE_ACCOUNT.code).
                remarkData(order.getOrderSn()).
            //remark("订单取消："+order.getOrderSn()+"退款，退积分：score").
            //后台处理时为操作人id为0
                adminUser(0).
            //用户余额充值
                tradeType(RecordTradeEnum.TYPE_CRASH_POWER_MACCOUNT.val()).
            //资金流量-支出
                tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val()).
            //积分变动是否来自退款
                isFromRefund(NumberUtils.BYTE_ONE).build();
        //调用退积分接口
        recordMemberTrade.updateUserEconomicData(scoreData);
    }

    /**
     * 更新库存与活动状态
     * @param order
     *
     */
    public void updateStockAndStatus(OrderInfoVo order) throws MpException {
        List<OrderReturnGoodsVo> goods = orderGoods.getByOrderId(order.getOrderId()).into(OrderReturnGoodsVo.class);
        goods.forEach(orderGoods-> orderGoods.setOrderId(order.getOrderId()));
        if(order.getIsLock().equals(OrderConstant.YES)) {
            //下单流程或者下单前已经扣减库存，需恢复设配库存销量
            atomicOperation.updateStockAndSales(goods, order, true);
        }
        //处方
        orderCreateMpProcessorFactory.processReturnOrder(null,BaseConstant.ACTIVITY_TYPE_PRESCRIPTION,null,goods);
        //订单类型
        List<Byte> orderType = Lists.newArrayList(OrderInfoService.orderTypeToByte(order.getGoodsType()));
        //活动更新状态或库存(goodsType.retainAll后最多会出现一个单一营销+赠品活动)
        orderType.retainAll(OrderCreateMpProcessorFactory.CANCEL_ACTIVITY);
        for (Byte type : orderType) {
            if(BaseConstant.ACTIVITY_TYPE_GIFT.equals(type)){
                //赠品修改活动库存
                orderCreateMpProcessorFactory.processReturnOrder(null,BaseConstant.ACTIVITY_TYPE_GIFT,null,goods.stream().filter(x->OrderConstant.IS_GIFT_Y.equals(x.getIsGift())).collect(Collectors.toList()));
            }else {
                //修改活动库存
                orderCreateMpProcessorFactory.processReturnOrder(null, type, order.getActivityId(), goods.stream().filter(x->OrderConstant.IS_GIFT_N.equals(x.getIsGift())).collect(Collectors.toList()));
            }
        }
    }

    /**
     * 调取药房取消订单接口
     * @param shopCode 门店编码
     * @param orderSn 订单编码
     * @return true 取消订单成功，false 失败
     */
    private boolean syncStoreCancelOrder(String shopCode,String orderSn){
        StoreCancelOrderParam param = new StoreCancelOrderParam(shopCode,orderSn);
        String appId = ApiExternalRequestConstant.APP_ID_STORE;
        Integer shopId = getShopId();
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, ApiExternalRequestConstant.SERVICE_NAME_CANCEL_ORDER, Util.toJson(param));
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())){
            logger().debug("调取药房："+shopCode+"，取消订单接口异常："  + apiExternalRequestResult.getError() + ",msg " + apiExternalRequestResult.getMsg());
            return false;
        }
        return true;
    }
}

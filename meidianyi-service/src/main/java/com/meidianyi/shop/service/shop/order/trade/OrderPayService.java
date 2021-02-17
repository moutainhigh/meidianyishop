package com.meidianyi.shop.service.shop.order.trade;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BigDecimalPlus;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.Operator;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.data.AccountData;
import com.meidianyi.shop.service.pojo.shop.member.data.ScoreData;
import com.meidianyi.shop.service.pojo.shop.member.data.UserCardData;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.virtual.VirtualOrderPayInfo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.saas.shop.ThirdPartyMsgServices;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.operation.RecordTradeService;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.refund.record.OrderRefundRecordService;
import com.meidianyi.shop.service.shop.order.refund.record.RefundAmountRecordService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
/**
 * 订单支付
 * @author 王帅
 *
 */
@Component
public class OrderPayService extends ShopBaseService{

    @Autowired
    private RecordTradeService recordMemberTrade;

    @Autowired
    private RefundAmountRecordService refundAmountRecord;

    @Autowired
    private OrderRefundRecordService orderRefundRecord;

    @Autowired
    private TradesRecordService tradesRecord;

    @Autowired
    private UserCardService card;

    @Autowired
    private MpPaymentService pay;

    @Autowired
    private OrderInfoService order;

    @Autowired
    private OrderGoodsService orderGoods;

    @Autowired
    private ThirdPartyMsgServices thirdPartyMsgServices;
    @Autowired
    private OrderOperateSendMessage sendMessage;

    /**
     * 订单系统内金额支付方法
     * @param order 订单
     * @param useAccount 用户余额
     * @param score 积分
     * @param cardBalance 会员卡金额
     * @throws MpException 见具体方法
     */
    public void payMethodInSystem(OrderInfoRecord order, BigDecimal useAccount, BigDecimal score, BigDecimal cardBalance) throws MpException {
        payUseAccount(order, useAccount);
//        payMemberCardBalance(order, cardBalance);
//        payScoreDiscount(order, score);
    }

    /**
     * 是否需要继续微信支付
     * @param orderInfo 订单
     * @param orderSn 订单号（定金bk特殊）
     * @param money 金额（定金bk特殊）
     * @param goodsNameForPay 商品描述
     * @param clientIp mp端ip
     * @param openId openid
     * @param activityType 活动类型
     * @throws MpException
     */
    public WebPayVo isContinuePay(OrderInfoRecord orderInfo, String orderSn, BigDecimal money, String goodsNameForPay,String clientIp, String openId, Byte activityType ) throws MpException {
        logger().info("继续支付接口start");
        if(orderInfo.getOrderStatus() == OrderConstant.ORDER_WAIT_DELIVERY || orderInfo.getOrderStatus() == OrderConstant.ORDER_PIN_PAYED_GROUPING){
            thirdPartyMsgServices.thirdPartService(orderInfo);
            //新订单提醒
            sendMessage.sendNewOrderMessage(orderInfo);
            return null;
        }else if(OrderConstant.PAY_WAY_FRIEND_PAYMENT == orderInfo.getOrderPayWay()) {
            //TODO好友代付
            return null;
        }else if(BigDecimalUtil.compareTo(money, null) > 0) {
            //非系统金额支付
            try {
                logger().info("微信预支付调用接口调用start");
                WebPayVo webPayVo = pay.wxUnitOrder(clientIp, goodsNameForPay, orderSn, money, openId);
                webPayVo.setOrderSn(orderInfo.getOrderSn());
                webPayVo.setOrderType(activityType == null ? null : activityType.toString());
                order.updatePrepayId(webPayVo.getResult().getPrepayId(), orderInfo.getOrderId(), orderSn);
                logger().info("微信预支付调用接口调用end");
                return webPayVo;
            } catch (WxPayException e) {
                logger().error("微信预支付调用接口失败WxPayException，订单号：{},异常：{}", orderInfo.getOrderSn(), e);
                throw new MpException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }catch (Exception e) {
                logger().error("微信预支付调用接口失败Exception，订单号：{},异常：{}", orderInfo.getOrderSn(), e.getMessage());
                throw new MpException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }
        }
        return null;
    }

    public String getGoodsNameForPay(OrderInfoRecord orderInfo, List<OrderGoodsBo> orderGoodsBo) {
        StringBuilder result = new StringBuilder(orderGoodsBo.get(0).getGoodsName());
        int maxLength = 32;
        if(result.length() > maxLength){
            result.substring(0, maxLength);
        }
        result.append(orderGoodsBo.size() == 1 ? StringUtils.EMPTY : "等").append(orderInfo.getGoodsAmount()).append("件");
        return result.toString();
    }

    public String getGoodsNameForPay(String orderSn) {
        OrderInfoRecord orderInfo = order.getOrderByOrderSn(orderSn);
        List<OrderGoodsBo> orderGoodsBo = orderGoods.getByOrderId(orderInfo.getOrderId()).into(OrderGoodsBo.class);
        StringBuilder result = new StringBuilder(orderGoodsBo.get(0).getGoodsName());
        int maxLength = 32;
        if(result.length() > maxLength){
            result.substring(0, maxLength);
        }
        result.append(orderGoodsBo.size() == 1 ? StringUtils.EMPTY : "等").append(orderInfo.getGoodsAmount()).append("件");
        return result.toString();
    }

    /**
     * 会员卡余额
     * @param order 订单
     * @param money 金额
     * @throws MpException 见具体方法
     */
    public void payMemberCardBalance(OrderInfoRecord order, BigDecimal money) throws MpException {
        logger().info("下单支付会员卡余额start:{}", money);
        if(BigDecimalUtil.compareTo(money, null) == 0) {
            return;
        }
        /**
		 * 交易记录信息
		 */
		TradeOptParam tradeOpt = TradeOptParam
				.builder()
				.adminUserId(0)
				.tradeType(RecordTradeEnum.TYPE_CRASH_MEMBER_CARD_PAY.val())
				.tradeFlow(RecordTradeEnum.TRADE_FLOW_IN.val())
				.build();
        UserCardParam card = this.card.getCard(order.getCardNo());
        UserCardData userCardData = UserCardData.newBuilder().
            userId(order.getUserId()).
            cardId(card == null ? null : card.getCardId()).
            cardNo(order.getCardNo()).
            money(money.negate()).
            reasonId(RemarkTemplate.ORDER_MAKE_CARD_ACCOUNT_PAY.code).
            reason(order.getOrderSn()).
            //目前只有普通会员卡有余额
            type(CardConstant.MCARD_TP_NORMAL).
            orderSn(order.getOrderSn()).
            tradeOpt(tradeOpt).build();
        //调用退会员卡接口
        recordMemberTrade.updateUserEconomicData(userCardData);
        logger().info("下单支付会员卡余额start:{}", money);
    }

    /**
     * 用户余额
     * @param order 订单
     * @param money 金额
     * @throws MpException 见具体方法
     */
    public void payUseAccount(OrderInfoRecord order, BigDecimal money) throws MpException {
        logger().info("下单支付用户余额start:{}", money);
        if(BigDecimalUtil.compareTo(money, null) == 0) {
            return;
        }
        AccountData accountData = AccountData.newBuilder().
            userId(order.getUserId()).
            orderSn(order.getOrderSn()).
            //下单金额
                amount(money.negate()).
                remarkCode(RemarkTemplate.ORDER_MAKE.code).
                remarkData(order.getOrderSn()).
                payment(order.getPayCode()).
            //支付类型
                isPaid(RecordTradeEnum.UACCOUNT_CONSUMPTION.val()).
            //后台处理时为操作人id为0
                adminUser(0).
            //用户余额支付
                tradeType(RecordTradeEnum.TYPE_CRASH_ACCOUNT_PAY.val()).
            //资金流量-支出
                tradeFlow(RecordTradeEnum.TRADE_FLOW_IN.val()).build();
        //调用退余额接口
        recordMemberTrade.updateUserEconomicData(accountData);
        logger().info("下单支付用户余额end:{}", money);
    }

    /**
     * 积分
     * @param order 订单
     * @param money 金额
     * @throws MpException 见具体方法
     */
    public void payScoreDiscount(OrderInfoRecord order, BigDecimal money) throws MpException {
        logger().info("下单支付用户积分start:{}", money);
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
            //积分
                score(-score).
                remarkCode(RemarkTemplate.ORDER_MAKE.code).
                remarkData(order.getOrderSn()).
               // remark("下单："+order.getOrderSn()).
            //后台处理时为操作人id为0
                adminUser(0).
            //积分消费
                tradeType(RecordTradeEnum.TYPE_SCORE_PAY.val()).
            //资金流量-收入
                tradeFlow(RecordTradeEnum.TRADE_FLOW_IN.val()).
            //积分变动是否来自退款
                isFromRefund(RecordTradeEnum.IS_FROM_REFUND_N.val()).build();
        //调用退积分接口

        recordMemberTrade.updateUserEconomicData(scoreData);
        logger().info("下单支付用户积分end:{}", money);
    }

    /**
     * 虚拟订单微信退款
     * @param order
     * @param money
     * @throws MpException
     */
    public void refundVirtualWx(VirtualOrderPayInfo order , BigDecimal money) throws MpException {
        if(OrderConstant.PAY_CODE_WX_PAY.equals(order.getPayCode())) {
            //orderRefundRecord.wxPayRefund(order , money);
        }
        //交易记录
        tradesRecord.addRecord(money,order.getOrderSn(),order.getUserId(),TradesRecordService.TRADE_CONTENT_MONEY,RecordTradeEnum.TYPE_CASH_REFUND.val(),RecordTradeEnum.TRADE_FLOW_OUT.val(),TradesRecordService.TRADE_STATUS_ARRIVAL);
    }

}

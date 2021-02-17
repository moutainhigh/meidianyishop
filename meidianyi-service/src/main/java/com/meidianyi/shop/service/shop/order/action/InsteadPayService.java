package com.meidianyi.shop.service.shop.order.action;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PaymentRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.SubOrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.insteadpay.InsteadPay;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead.InsteadPayParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead.InsteadPayVo;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentRecordParam;
import com.meidianyi.shop.service.pojo.wxapp.order.CreateOrderVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.shop.order.OrderReadService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.refund.ReturnMethodService;
import com.meidianyi.shop.service.shop.order.sub.SubOrderService;
import com.meidianyi.shop.service.shop.order.trade.OrderPayService;
import com.meidianyi.shop.service.shop.order.trade.TradesRecordService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import com.meidianyi.shop.service.shop.payment.PaymentRecordService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 好友代付
 * @author 王帅
 */
@Service
public class InsteadPayService extends ShopBaseService implements IorderOperate<OrderOperateQueryParam, InsteadPayParam> {

    @Autowired
    private OrderInfoService orderInfo;

    @Autowired
    private SubOrderService subOrderService;

    @Autowired
    private OrderReadService orderReadService;

    @Autowired
    private MpPaymentService pay;

    @Autowired
    private OrderPayService orderPay;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private OrderOperateSendMessage sendMessage;

    @Autowired
    private TradesRecordService tradesRecord;

    @Autowired
    public PaymentRecordService record;

    @Autowired
    private AtomicOperation atomicOperation;

    @Autowired
    private PayService payService;

    @Autowired
    private ReturnMethodService returnMethodService;

    @Override
    public OrderServiceCode getServiceCode() {
        return OrderServiceCode.INSTEAD_PAY;
    }

    @Override
    public Object query(OrderOperateQueryParam param) throws MpException {
        logger().info("代付query start:");
        OrderInfoRecord order = orderInfo.getOrderByOrderSn(param.getOrderSn());
        if(order == null) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_NOT_EXIST, null);
        }
        if(!order.getOrderStatus().equals(OrderConstant.ORDER_WAIT_PAY)) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_OPERATE_NO_INSTANCEOF, null);
        }
        if(order.getOrderStatus() >= OrderConstant.ORDER_WAIT_DELIVERY) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_PAY_WAY_INSTEAD_PAY_FINISH, null);
        }
        //vo
        InsteadPayVo vo = new InsteadPayVo();
        //获取已付金额
        BigDecimal amountPaid = orderInfo.getOrderFinalAmount(order.into(OrderListInfoVo.class), true);
        //待支付金额
        BigDecimal waitPayMoney = BigDecimalUtil.subtrac(order.getInsteadPayMoney(), order.getMoneyPaid());
        //校验
        if(BigDecimalUtil.compareTo(waitPayMoney, null) < 1) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_PAY_WAY_INSTEAD_PAY_FINISH, null);
        }
        //代付配置
        InsteadPay cfg = Util.parseJson(order.getInsteadPay(), InsteadPay.class);
        if(cfg == null) {
            logger().error("代付订单支付页面代付配置为null");
            return ExecuteResult.create(JsonResultCode.CODE_PARAM_ERROR, null);
        }
        if(BigDecimalUtil.compareTo(waitPayMoney, OrderConstant.CENT) == 0) {
            //1分随便付
            vo.setIsShowEdit(OrderConstant.NO);
            vo.setMoneyPaid(waitPayMoney);
        }else if(param.getWxUserInfo().getUserId().equals(order.getUserId())) {
            //自己付
            vo.setIsShowEdit(OrderConstant.YES);
            vo.setMoneyPaid(BigDecimalUtil.BIGDECIMAL_ZERO);
        }else if(order.getInsteadPayNum() == 1) {
            //单人付
            vo.setIsShowEdit(OrderConstant.NO);
            vo.setMoneyPaid(waitPayMoney);
        }else {
            //多人付
            //订单实际金额
            BigDecimal orderAmount = BigDecimalUtil.addOrSubtrac(
                BigDecimalUtil.BigDecimalPlus.create(amountPaid, BigDecimalUtil.Operator.subtrac),
                BigDecimalUtil.BigDecimalPlus.create(order.getMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(order.getInsteadPayMoney())
            );
            //代付金额三阶梯
            Object[][] threeStages = new Object[3][2];
            //一阶段
            threeStages[0] = new Object[]{BigDecimalUtil.multiplyOrDivide(
                BigDecimalUtil.BigDecimalPlus.create(orderAmount, BigDecimalUtil.Operator.multiply),
                BigDecimalUtil.BigDecimalPlus.create(new BigDecimal(cfg.getPayRatioNumber().get(0)), BigDecimalUtil.Operator.divide),
                BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.BIGDECIMAL_100)
            ),
                cfg.getPayRatioText().get(0)
            };
            //二阶段
            threeStages[1] = new Object[]{BigDecimalUtil.multiplyOrDivide(
                BigDecimalUtil.BigDecimalPlus.create(orderAmount, BigDecimalUtil.Operator.multiply),
                BigDecimalUtil.BigDecimalPlus.create(new BigDecimal(cfg.getPayRatioNumber().get(1)), BigDecimalUtil.Operator.divide),
                BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.BIGDECIMAL_100)
                ),
                cfg.getPayRatioText().get(1)
            };
            //三阶段
            threeStages[2] = new Object[]{waitPayMoney, cfg.getPayRatioText().get(2)};

            vo.setThreeStages(threeStages);
            vo.setIsShowEdit(OrderConstant.YES);
            vo.setMoneyPaid(BigDecimalUtil.BIGDECIMAL_ZERO);
        }
        vo.setMessage(order.getUserId().equals(param.getWxUserInfo().getUserId()) ?
            (order.getInsteadPayNum() == 0 ? cfg.getOrderUserMessageMultiple() : cfg.getOrderUserMessageSingle()) :
            (order.getInsteadPayNum() == 0 ? cfg.getInsteadPayMessageMultiple() : cfg.getInsteadPayMessageSingle()));
        vo.setAmountPaid(amountPaid);
        vo.setWaitPayMoney(waitPayMoney);
        vo.setOrder(orderReadService.mpGet(new OrderParam(param.getOrderSn())));
        vo.setIsSelf(param.getWxUserInfo().getUserId().equals(order.getUserId()) ? OrderConstant.YES : OrderConstant.NO);
        logger().info("代付query end:");
        return vo;
    }

    @Override
    public ExecuteResult execute(InsteadPayParam param) {
        logger().info("代付execute start:{}", param.toString());
        OrderInfoRecord order = orderInfo.getOrderByOrderSn(param.getOrderSn());
        ExecuteResult check = check(param, order);
        if(check != null && !check.isSuccess()) {
            return check;
        }
        AtomicReference<SubOrderInfoRecord> subOrderRef = new AtomicReference<>();
        transaction(()->{
            subOrderRef.set(subOrderService.create(order.getOrderSn(), param.getMoneyPaid(), param.getMessage(), param.getWxUserInfo().getUserId(), param.getUsername() == null ? "" : param.getUsername()));
        });
        SubOrderInfoRecord subOrder = subOrderRef.get();
        WebPayVo webPayVo = null;
        try {
            webPayVo = pay(param, order, subOrder);
        } catch (MpException e) {
            return ExecuteResult.create(e.getErrorCode(), null);
        }
        webPayVo.setOrderSn(subOrder.getSubOrderSn());
        subOrderService.updatePrepayId(subOrder, webPayVo);
        return ExecuteResult.create(new CreateOrderVo(subOrder.getSubOrderSn(), webPayVo));
    }

    private WebPayVo pay(InsteadPayParam param, OrderInfoRecord order, SubOrderInfoRecord subOrder) throws MpException {
        logger().info("微信预支付调用接口start");
        WebPayVo webPayVo = null;
        try {
            webPayVo = pay.wxUnitOrder(param.getClientIp(), orderPay.getGoodsNameForPay(order, orderGoodsService.getOrderGoods(order.getOrderSn()).into(OrderGoodsBo.class)), subOrder.getSubOrderSn(), subOrder.getMoneyPaid(), param.getWxUserInfo().getWxUser().getOpenId());
        } catch (WxPayException e) {
            logger().error("微信预支付调用接口失败WxPayException，订单号：{},异常：{}", order.getOrderSn(), e);
            throw new MpException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
        }catch (Exception e) {
            logger().error("微信预支付调用接口失败Exception，订单号：{},异常：{}", order.getOrderSn(), e.getMessage());
            throw new MpException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
        }
        logger().info("微信预支付调用接口end");
        return webPayVo;
    }

    private final static Integer INSTEAD_PAY_MESSAGE_MAX_LENGTH = 20;
    /**
     * 校验
     * @param param
     * @param order
     * @return
     */
    private ExecuteResult check(InsteadPayParam param, OrderInfoRecord order) {
        if(order == null) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_NOT_EXIST, null);
        }
        if(!order.getOrderStatus().equals(OrderConstant.ORDER_WAIT_PAY)) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_OPERATE_NO_INSTANCEOF, null);
        }
        if(order.getOrderStatus() >= OrderConstant.ORDER_WAIT_DELIVERY) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_PAY_WAY_INSTEAD_PAY_FINISH, null);
        }
        //留言校验
        if(StringUtils.isNotBlank(param.getMessage()) && param.getMessage().length() > INSTEAD_PAY_MESSAGE_MAX_LENGTH) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_PAY_WAY_INSTEAD_PAY_FINISH, "留言长度限制20字以内");
        }
        //待支付金额
        BigDecimal waitPayMoney = BigDecimalUtil.subtrac(order.getInsteadPayMoney(), order.getMoneyPaid());
        //校验
        if(BigDecimalUtil.compareTo(waitPayMoney, null) < 1) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_PAY_WAY_INSTEAD_PAY_FINISH, null);
        }
        //校验
        if(BigDecimalUtil.compareTo(waitPayMoney, OrderConstant.CENT) == -1) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_PAY_WAY_INSTEAD_PAY_FINISH, "支付金额错误");
        }
        //超待支付校验
        if (BigDecimalUtil.compareTo(param.getMoneyPaid(), waitPayMoney) == 1) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_PAY_WAY_INSTEAD_PAY_FINISH, "支付金额不能大于{$waitPayMoney}元");
        }
        //单人付校验(非自己)
        if(NumberUtils.SHORT_ONE.equals(order.getInsteadPayNum())
            && !param.getWxUserInfo().getUserId().equals(order.getUserId())
            && BigDecimalUtil.compareTo(param.getMoneyPaid(), waitPayMoney) != 0) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_PAY_WAY_INSTEAD_PAY_FINISH, "支付金额应为{$waitPayMoney}元");
        }
        return null;
    }

    public void businessLogic(PaymentRecordParam param, SubOrderInfoRecord order) throws MpException {
        //添加支付记录
        PaymentRecordRecord paymentRecord = record.addPaymentRecord(param);
        //交易记录
        tradesRecord.addRecord(order.getMoneyPaid(),order.getSubOrderSn(),order.getUserId(), TradesRecordService.TRADE_CONTENT_MONEY, RecordTradeEnum.TYPE_CRASH_WX_PAY.val(),RecordTradeEnum.TRADE_FLOW_IN.val(),TradesRecordService.TRADE_CONTENT_MONEY);
        //完成支付
        subOrderService.payFinish(order.getSubOrderSn(), paymentRecord);
        //订单
        checkMainOrderToWaitDeliver(order.getSubOrderSn());
    }

    public void checkMainOrderToWaitDeliver(String subOrderSn) throws MpException {
        //代付订单
        SubOrderInfoRecord subOrder = subOrderService.get(subOrderSn);
        //订单
        OrderInfoRecord order = orderInfo.getOrderByOrderSn(subOrder.getMainOrderSn());
        //获取已支付金额（包含当此）
        BigDecimal moneyPaid = BigDecimalUtil.add(order.getMoneyPaid(), subOrder.getMoneyPaid());
        if(BigDecimalUtil.compareTo(moneyPaid, order.getInsteadPayMoney()) > -1){
            logger().info("代付子单支付回调,订单支付完成");
            //支付金额大于代付金额
            toWaitDeliver(order);
            //超付退款
            overpay(moneyPaid, order.getInsteadPayMoney(), subOrder.getMainOrderSn());
            //设置支付金额
            moneyPaid = order.getInsteadPayMoney();
        }
        //设置订单moneyPaid
        orderInfo.insteadPayOrderSetMoney(order, moneyPaid);
        //消息推送
        sendMessage.sendinsteadPay(subOrder, order);
    }

    private void toWaitDeliver(OrderInfoRecord order) throws MpException {
        if(order.getOrderStatus().equals(OrderConstant.ORDER_WAIT_PAY)) {
            logger().info("代付子单支付回调,设置订单为待发货,更新库存");
            orderInfo.setPayTime(order.getOrderSn());
            if (order.getOrderAuditType().equals(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT)){
                //待审核
                orderInfo.setOrderstatus(order.getOrderSn(), OrderConstant.ORDER_TO_AUDIT);
            }else if (order.getOrderAuditType().equals(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE)){
                //待开方
                orderInfo.setOrderstatus(order.getOrderSn(), OrderConstant.ORDER_TO_AUDIT_OPEN);
            }else {
                //代发货
                orderInfo.setOrderstatus(order.getOrderSn(), OrderConstant.ORDER_WAIT_DELIVERY);
            }
            //订单商品
            Result<OrderGoodsRecord> goods = orderGoodsService.getByOrderId(order.getOrderId());
            //库存销量
            atomicOperation.updateStockAndSalesByLock(order, goods.into(OrderGoodsBo.class), false);
            // 订单生效时营销活动后续处理
            try {
                payService.processOrderEffective(order);
            } catch (MpException e) {
                if(!JsonResultCode.CODE_ORDER_GIFT_GOODS_ZERO.equals(e.getErrorCode())){
                    //赠品减活动库存失败忽略
                    throw e;
                }
            }
        }
    }

    private void overpay(BigDecimal moneyPaid, BigDecimal insteadPayMoney, String mainOrderSn) {
        if(BigDecimalUtil.compareTo(moneyPaid, insteadPayMoney) == 1){
            logger().info("代付子单支付回调_支付金额大于代付金额");
            BigDecimal overMoney = BigDecimalUtil.subtrac(moneyPaid, insteadPayMoney);
            returnMethodService.returnSubOrder(mainOrderSn, overMoney, 0);
        }


    }
}

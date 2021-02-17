package com.meidianyi.shop.service.shop.order.refund;

import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BigDecimalPlus;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.Operator;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderRefundRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.PaymentRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.SubOrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.IncrSequenceUtil;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.data.AccountData;
import com.meidianyi.shop.service.pojo.shop.member.data.ScoreData;
import com.meidianyi.shop.service.pojo.shop.member.data.UserCardData;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.virtual.VirtualOrderPayInfo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead.ReturnMqParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam;
import com.meidianyi.shop.service.saas.schedule.TaskJobMainService;
import com.meidianyi.shop.service.shop.operation.RecordTradeService;
import com.meidianyi.shop.service.shop.order.action.ReturnService;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.refund.record.OrderRefundRecordService;
import com.meidianyi.shop.service.shop.order.refund.record.RefundAmountRecordService;
import com.meidianyi.shop.service.shop.order.sub.SubOrderService;
import com.meidianyi.shop.service.shop.order.trade.TradesRecordService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import com.meidianyi.shop.service.shop.payment.PaymentRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 * @author 王帅
 *
 */
@Component
public class ReturnMethodService extends ShopBaseService{
	
	private static String RETURN_METHOD_PREFIX = "refund_";
	@Autowired
	private RecordTradeService recordMemberTrade;
	@Autowired
	private RefundAmountRecordService refundAmountRecord;
	@Autowired
	private OrderRefundRecordService orderRefundRecord;
	@Autowired
	private TradesRecordService tradesRecord;
    @Autowired
    private OrderInfoService orderInfo;
    @Autowired
    private PaymentRecordService paymentRecord;
    @Autowired
    private MpPaymentService mpPayment;
    @Autowired
    private SubOrderService subOrderService;
    @Autowired
    private TaskJobMainService taskJobMainService;
    @Autowired
    private OrderOperateSendMessage sendMessage;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private  ReturnOrderService returnOrder;
	/**
	 * 	退款统一入口（微信、余额、积分、卡余额等）
	 * @param methodName 
	 * @param order
	 * @param money
	 * @return
	 * @throws MpException 
	 */
	public void refundMethods(String methodName , OrderInfoVo order , Integer retId ,BigDecimal money) throws MpException {
		try {
			//RETURN_METHOD_PREFIX + methodName获取该优先级退款的具体方法
			Method method = getClass().getMethod(FieldsUtil.underLineToCamel(RETURN_METHOD_PREFIX + methodName), OrderInfoVo.class,Integer.class,BigDecimal.class);
			method.invoke(this, order,retId,money);
		} catch (InvocationTargetException e) {
			//反射捕获自定义异常
			Throwable cause = e.getCause();
			if (cause instanceof MpException) {
				throw (MpException)cause;
			}else {
				logger().error("退款统一入口调用方法异常(非MpException)retId:"+retId, e);
				throw new MpException(JsonResultCode.CODE_ORDER_RETURN_METHOD_REFLECT_ERROR, e.getMessage());
			}
		} catch (Exception e) {
			logger().error("退款统一入口调用异常retId:"+retId, e);
			throw new MpException(JsonResultCode.CODE_ORDER_RETURN_METHOD_REFLECT_ERROR,e.getMessage());
		}
	}
	/**
	 * 	补款退款
	 * @param order
	 * @param money
	 * @return
	 * @throws MpException
	 */
	public void refundBkOrderMoney(OrderInfoVo order , Integer retId ,BigDecimal money) throws MpException {
        order.setIsReturnBk(true);
		refundMoneyPaid(order, retId, money);
	}
	/**
	 * 	退会员卡余额
	 * @param order
	 * @param money
	 * @throws MpException 
	 */
	public void refundMemberCardBalance(OrderInfoVo order , Integer retId ,BigDecimal money) throws MpException {
		if(BigDecimalUtil.compareTo(money, null) == 0) {
			return;
		}
		
		/**
		 * 交易记录信息
		 */
		TradeOptParam tradeOpt = TradeOptParam
				.builder()
				.adminUserId(0)
				.tradeType(RecordTradeEnum.TYPE_CRASH_MCARD_ACCOUNT_REFUND.val())
				.tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val())
				.build();
		
		UserCardData userCardData = UserCardData.newBuilder().
            userId(order.getUserId()).
            cardId(order.getCardId()).
            cardNo(order.getCardNo()).
            money(money).
//		reason("订单会员卡余额支付退款").
    reasonId(RemarkTemplate.ORDER_RETURN_CARD_ACCOUNT.code).
            //普通会员卡
                type(CardConstant.MCARD_TP_NORMAL).
                orderSn(order.getOrderSn()).
                chargeType(CardConstant.CHARGE_TYPE_REFUND).
                tradeOpt(tradeOpt).build();
		
		//调用退会员卡接口
		recordMemberTrade.updateUserEconomicData(userCardData);
		//记录
		refundAmountRecord.addRecord(order.getOrderSn(), order.getUserId(), RefundAmountRecordService.MEMBER_CARD_BALANCE, money, retId);
	}
	
	/**
	 * 	退余额
	 * @param order
	 * @param money
	 * @throws MpException 
	 */
	public void refundUseAccount(OrderInfoVo order , Integer retId ,BigDecimal money) throws MpException {
		if(BigDecimalUtil.compareTo(money, null) == 0) {
			return;
		}
		AccountData accountData = AccountData.newBuilder().
		userId(order.getUserId()).
		orderSn(order.getOrderSn()).
		//退款金额
		amount(money).
		remarkCode(RemarkTemplate.ORDER_RETURN.code).
		remarkData(order.getOrderSn()).
		//remark("订单："+order.getOrderSn()+"余额退款").
		payment(order.getPayCode()).
		//支付类型
		isPaid(RecordTradeEnum.UACCOUNT_RECHARGE.val()).
		//后台处理时为操作人id为0
		adminUser(0).
		//交易类型
		tradeType(RecordTradeEnum.TYPE_CRASH_MACCOUNT_REFUND.val()).
		//资金流量-支出
		tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val()).build();
		//调用退余额接口
		recordMemberTrade.updateUserEconomicData(accountData);
		//记录
		refundAmountRecord.addRecord(order.getOrderSn(), order.getUserId(), RefundAmountRecordService.USE_ACCOUNT, money, retId);
	}
	
	/**
	 * 	积分退款
	 * @param order
	 * @param money
	 * @return
	 * @throws MpException
	 */
	public void refundScoreDiscount(OrderInfoVo order , Integer retId ,BigDecimal money) throws MpException {
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
		remarkCode(RemarkTemplate.ORDER_RETURN_SCORE_ACCOUNT.code).
		remarkData(order.getOrderSn()).
		//remark("订单："+order.getOrderSn()+"退款，退积分：score").
		//后台处理时为操作人id为0
		adminUser(0).
		//交易类型
		tradeType(RecordTradeEnum.TYPE_SCORE_REFUND.val()).
		//资金流量-支出
		tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val()).
		//积分变动是否来自退款
		isFromRefund(NumberUtils.BYTE_ONE).build();
		//调用退积分接口
		recordMemberTrade.updateUserEconomicData(scoreData);
		//记录
		refundAmountRecord.addRecord(order.getOrderSn(), order.getUserId(), RefundAmountRecordService.SCORE_DISCOUNT, money, retId);
	}
	
	/**
	 * 	微信退款
	 * @param order
	 * @param money
	 * @return
	 * @throws MpException
	 */
	public void refundMoneyPaid(OrderInfoVo order , Integer retId ,BigDecimal money) throws MpException {
		if(OrderConstant.PAY_WAY_FRIEND_PAYMENT == order.getOrderPayWay()) {
            returnSubOrder(order.getOrderSn(), money, retId);
            return;
		}
		if(OrderConstant.PAY_CODE_WX_PAY.equals(order.getPayCode())) {
            logger().info("微信退款（refundMoneyPaid）start,退款金额：{}", money);
            //子订单取主订单订单号
            String orderSn = orderInfo.isSubOrder(order) ? order.getMainOrderSn() : order.getOrderSn();
            //补款加后缀
            if(Boolean.TRUE.equals(order.getIsReturnBk())) {
                //重置
                order.setIsReturnBk(false);
                orderSn = orderSn + OrderConstant.BK_SN_SUFFIX;
            }
            String refundSn = null;
            //支付记录
            PaymentRecordRecord payRecord = paymentRecord.getPaymentRecordByOrderSn(orderSn);
            if(payRecord == null) {
                logger().error("wxPayRefund 微信支付记录未找到 order_sn="+orderSn);
                throw new MpException(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_NO_RECORD);
            }
            //该次退款曾经执行记录
            OrderRefundRecordRecord returnRecord = orderRefundRecord.getReturnRecord(retId, payRecord.getPaySn(), money);
            if(returnRecord != null && OrderRefundRecordService.SUCCESS.equals(returnRecord.getDealStatus())){
                return;
            }
            //微信退款结果
            WxPayRefundResult refundResult = null;
            try {
                //退款流水号
                refundSn = IncrSequenceUtil.generateOrderSn(OrderConstant.RETURN_SN_PREFIX);
                //微信金额单为为分需单位换算
                refundResult = refundByApi(payRecord.getPayCode(), payRecord.getTradeNo(), refundSn,
                    BigDecimalUtil.multiply(payRecord.getTotalFee(), new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue(),
                    BigDecimalUtil.multiply(money, new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue());
                if(returnRecord != null){
                    //二次退款恢复记录状态
                    orderRefundRecord.updateStatus(returnRecord, OrderRefundRecordService.SUCCESS, StringUtils.EMPTY);
                }else {
                    //退款记录
                    orderRefundRecord.addRecord(refundSn, payRecord, refundResult, retId, money, StringUtils.EMPTY);
                }
                logger().info("微信退款（refundMoneyPaid）end");
			} catch (MpException e) {
                logger().warn("微信退款异常（refundMoneyPaid）,错误信息表ORDER_REFUND_RECORD");
                if(returnRecord != null){
                    //二次退款依然失败
                    orderRefundRecord.updateStatus(returnRecord, OrderRefundRecordService.FAIL, e.getCodeParam() == null ? StringUtils.EMPTY : e.getCodeParam().length == 0 ? StringUtils.EMPTY : e.getCodeParam()[0]);
                }else {
                    //退款记录
                    orderRefundRecord.addRecord(refundSn, payRecord, refundResult, retId, money, e.getCodeParam() == null ? StringUtils.EMPTY : e.getCodeParam().length == 0 ? StringUtils.EMPTY : e.getCodeParam()[0]);
                }
                throw e;
			}
		}
		if(!OrderConstant.PAY_CODE_COD.equals(order.getPayCode())){
            //交易记录
            tradesRecord.addRecord(money,order.getOrderSn(),order.getUserId(),TradesRecordService.TRADE_CONTENT_MONEY,RecordTradeEnum.TYPE_CASH_REFUND.val(),RecordTradeEnum.TRADE_FLOW_OUT.val(),TradesRecordService.TRADE_STATUS_ARRIVAL);
        }
		//记录
		refundAmountRecord.addRecord(order.getOrderSn(), order.getUserId(), RefundAmountRecordService.MONEY_PAID, money, retId);
	}

    /**
     * 	退款api
     * @param payCode 订单支付code(微信支付)
     * @param tradeNo 微信支付订单号
     * @param returnSn 退款流水号
     * @param totalFee 该笔支付总金额
     * @param money 此次退款金额
     * @return 非null成功，其他失败
     * @throws MpException
     */
    public WxPayRefundResult refundByApi(String payCode ,String tradeNo , String returnSn ,Integer totalFee, Integer money) throws MpException {
        //微信退款
        if(payCode.equals(OrderConstant.PAY_CODE_WX_PAY)){
            try {
                return mpPayment.refundByTransactionId(tradeNo, returnSn, totalFee, money);
            } catch (WxPayException e) {
                logger().warn("微信退款失败捕获WxPayException：{}", e.getMessage());
                throw new MpException(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_ERROR,e.getMessage(), e.getErrCodeDes());
            }
        }else {
            //TODO 将来扩展其他支付
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_ERROR);
        }
    }
	
	/**
	 * 虚拟订单微信退款
	 * @param order
	 * @param money
	 * @throws MpException 
	 */
	public void refundVirtualWx(VirtualOrderPayInfo order , BigDecimal money) throws MpException {
		if(OrderConstant.PAY_CODE_WX_PAY.equals(order.getPayCode())) {
            //退款流水号
            String refundSn = IncrSequenceUtil.generateOrderSn(OrderConstant.RETURN_SN_PREFIX);
            //支付记录
            PaymentRecordRecord payRecord = paymentRecord.getPaymentRecordByOrderSn(order.getOrderSn());
            if(payRecord == null) {
                logger().error("wxPayRefund 微信支付记录未找到 order_sn={}",order.getOrderSn());
                throw new MpException(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_NO_RECORD);
            }
            //微信金额单为为分需单位换算
            refundByApi(payRecord.getPayCode(), payRecord.getTradeNo(), refundSn, BigDecimalUtil.multiply(payRecord.getTotalFee(), new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue(), BigDecimalUtil.multiply(money, new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue());
            //TODO
            //addRecord();
		}
		//交易记录
		tradesRecord.addRecord(money,order.getOrderSn(),order.getUserId(),TradesRecordService.TRADE_CONTENT_MONEY,RecordTradeEnum.TYPE_CASH_REFUND.val(),RecordTradeEnum.TRADE_FLOW_OUT.val(),TradesRecordService.TRADE_STATUS_ARRIVAL);	
	}

    /**
     * 代付订单代付退款（mq消息推送）
     * @param orderSn 订单号
     * @param money 
     */
	public void returnSubOrder(String orderSn, BigDecimal money, Integer retId) {
        //异步消息队列退款
        logger().info("代付订单代付退款(mq消息推送),订单号:{},金额:{},退款订单id:{}", orderSn, money, retId);
        Integer shopId = getShopId();
        taskJobMainService.dispatchImmediately(new ReturnMqParam(orderSn, money, retId, shopId), ReturnMqParam.class.getName(), shopId, TaskJobsConstant.TaskJobEnum.RETURN_SUB_ORDER.getExecutionType());
    }

    /**
     * 代付订单代付退款
     * @param param
     */
    public void returnSubOrder(ReturnMqParam param) throws MpException {
        //异步消息队列退款
        logger().info("代付订单代付退款start,参数：{}", param);
        OrderInfoRecord mainOrder = orderInfo.getOrderByOrderSn(param.getOrderSn());
        if(mainOrder == null) {
            throw new MpException(JsonResultCode.CODE_ORDER_NOT_EXIST);
        }
        if(!mainOrder.getOrderPayWay().equals(OrderConstant.PAY_WAY_FRIEND_PAYMENT)) {
            logger().error("代付订单代付退款,非好友代付");
            return;
        }
        //可以退款的子单
        List<SubOrderInfoRecord> canReturn = subOrderService.getCanReturn(param.getOrderSn());
        if(CollectionUtils.isEmpty(canReturn)) {
            logger().error("代付订单代付退款,无可退子单");
            return;
        }
        while(BigDecimalUtil.compareTo(param.getMoney(), null) == 1) {
            ListIterator<SubOrderInfoRecord> iterator = canReturn.listIterator();
            while (iterator.hasNext()) {
                //当前退款子单
                SubOrderInfoRecord next = iterator.next();
                //当前子单可退金额
                BigDecimal currSubMoney = BigDecimalUtil.subtrac(next.getMoneyPaid(), next.getRefundMoney());
                //当前退款金额
                BigDecimal currMoney = BigDecimalUtil.compareTo(param.getMoney(), currSubMoney) == 1 ? currSubMoney : param.getMoney();
                //剩余退款金额
                BigDecimal surplusMoney = BigDecimalUtil.subtrac(param.getMoney(), currMoney);
                param.setMoney(surplusMoney);
                //退款
                WxPayRefundResult wxPayRefundResult = returnSubOrder(next.getSubOrderSn(), currMoney, next.getUserId(), param.getRetId());
                if(wxPayRefundResult == null) {
                    logger().error("子单退款失败,sn:{}", next.getSubOrderSn());
                    throw new MpException(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_ERROR, null);
                }
                subOrderService.updateBeforeReturn(next, currMoney);
                //消息模板
                sendMessage.sendInsteadPayReturn(currMoney, next, mainOrder.getOrderId());
                //当前子单退款完成需删除
                if(next.getOrderStatus().equals(OrderConstant.SubOrderConstant.SUB_ORDER_REFUND_SUCESS)) {
                    iterator.remove();
                }
            }
        }
        if(param.getRetId() != null && param.getRetId() != 0) {
            RefundParam refundParam = new RefundParam();
            refundParam.setIsMp(OrderConstant.IS_MP_MQ);
            returnService.finishUpdateInfo(mainOrder.into(OrderInfoVo.class), returnOrder.getByRetId(param.getRetId()), refundParam);
        }
    }

    private WxPayRefundResult returnSubOrder(String orderSn, BigDecimal money, Integer userId, Integer retId) throws MpException {
        logger().info("微信退款（returnSubOrder）start,退款订单号：{}退款金额：{}", orderSn, money);
        String refundSn = null;
        //支付记录
        PaymentRecordRecord payRecord =paymentRecord.getPaymentRecordByOrderSn(orderSn);
        //微信退款结果
        WxPayRefundResult refundResult = null;
        if(payRecord == null) {
            logger().error("returnSubOrder 微信支付记录未找到 order_sn:{}", orderSn);
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_NO_RECORD);
        }
        try {
            //退款流水号
            refundSn = IncrSequenceUtil.generateOrderSn(OrderConstant.RETURN_SN_PREFIX);
            //微信金额单为为分需单位换算
            refundResult = refundByApi(payRecord.getPayCode(), payRecord.getTradeNo(), refundSn,
                BigDecimalUtil.multiply(payRecord.getTotalFee(), new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue(),
                BigDecimalUtil.multiply(money, new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue());
            //退款记录
            orderRefundRecord.addRecord(refundSn, payRecord, refundResult, retId, money, StringUtils.EMPTY);
            logger().info("微信退款（returnSubOrder）end");
        } catch (MpException e) {
            logger().error("微信退款异常（returnSubOrder）,错误信息表ORDER_REFUND_RECORD");
            orderRefundRecord.addRecord(refundSn, payRecord, refundResult, retId, money, StringUtils.EMPTY);
        }
        //交易记录
        tradesRecord.addRecord(money,orderSn, userId,TradesRecordService.TRADE_CONTENT_MONEY,RecordTradeEnum.TYPE_CASH_REFUND.val(),RecordTradeEnum.TRADE_FLOW_OUT.val(),TradesRecordService.TRADE_STATUS_ARRIVAL);
        //记录
        refundAmountRecord.addRecord(orderSn, userId, RefundAmountRecordService.MONEY_PAID, money, retId);
        return refundResult;
    }
}

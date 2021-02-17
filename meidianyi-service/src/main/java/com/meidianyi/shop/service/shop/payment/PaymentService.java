package com.meidianyi.shop.service.shop.payment;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.dao.shop.order.InquiryOrderDao;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.payment.PayCode;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentRecordParam;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.activity.factory.OrderCreateMpProcessorFactory;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.order.inquiry.InquiryOrderService;
import com.meidianyi.shop.service.shop.order.action.InsteadPayService;
import com.meidianyi.shop.service.shop.order.action.PayService;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.store.StoreOrderService;
import com.meidianyi.shop.service.shop.order.sub.SubOrderService;
import com.meidianyi.shop.service.shop.order.virtual.CouponPackOrderService;
import com.meidianyi.shop.service.shop.order.virtual.MemberCardOrderService;
import com.meidianyi.shop.service.shop.order.virtual.VirtualOrderService;
import com.meidianyi.shop.service.shop.store.service.ServiceOrderService;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.Payment.PAYMENT;
import static com.meidianyi.shop.service.pojo.wxapp.store.StoreConstant.STORE_ORDER_SN_PREFIX;
import static com.meidianyi.shop.service.pojo.wxapp.store.StoreConstant.STORE_SERVICE_ORDER_SN_PREFIX;
import static com.meidianyi.shop.service.shop.store.service.ServiceOrderService.ORDER_STATUS_WAIT_PAY;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * @author lixinguo
 */
@Service
public class PaymentService extends ShopBaseService {

	@Autowired
	public PaymentRecordService record;

	@Autowired
	public OrderInfoService order;

	@Autowired
	public MpPaymentService mpPay;

    @Autowired
    public PayService pay;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private SubOrderService subOrderService;

    @Autowired
    private InsteadPayService insteadPayService;

    @Autowired
    private AtomicOperation atomicOperation;

	/**
	 * 营销活动processorFactory
	 */
	@Autowired
	private OrderCreateMpProcessorFactory marketProcessorFactory;

    /**
     * The Service order service.门店服务订单
     */
    @Autowired
    public ServiceOrderService serviceOrderService;

    @Autowired
    @Lazy
    private StoreOrderService storeOrder;

    @Autowired
    private CouponPackOrderService couponPackOrderService;
    @Autowired
    private MemberCardOrderService memberCardOrderService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private InquiryOrderService inquiryOrderService;
    @Autowired
    private InquiryOrderDao inquiryOrderDao;


	public PaymentVo getPaymentInfo(String payCode) {
		return db().select(PAYMENT.asterisk()).from(PAYMENT).where(PAYMENT.PAY_CODE.eq(payCode)).fetchOneInto(PaymentVo.class);
	}

	/**
	 * 设置支付方式开启状态
     *
	 * @param payCode 支付方式
	 * @param enabled 是否开启
	 */
	public void switchPayStatus(String payCode, Byte enabled) {
		db().update(PAYMENT).set(PAYMENT.ENABLED, enabled).where(PAYMENT.PAY_CODE.eq(payCode));
	}

	/**
	 * 得到所有支付方式
     *
	 * @return
	 */
	public Result<PaymentRecord> getPayment() {
		return db().fetch(PAYMENT);
	}

	/**
	 * 得到支持的支付方式
     *
	 * @return
	 */
	public Map<String, PaymentVo> getSupportPayment() {
        return db().selectFrom(PAYMENT)
            .where(PAYMENT.ENABLED.eq(PayCode.PAY_CODE_ENABLED))
            .orderBy(PAYMENT.PAY_CODE.desc())
            .fetchMap(PAYMENT.PAY_CODE, PaymentVo.class);
    }

	/**
	 * 统一订单支付回调
	 */
    public void unionPayNotify(PaymentRecordParam param) throws MpException, WxPayException {
        String orderSn = param.getOrderSn();
        String prefix = orderSn.substring(0,1);
        switch (prefix) {
            //TODO 订单根据前缀判断处理类型,将字面量替换为对应常量
            case STORE_SERVICE_ORDER_SN_PREFIX:
                //服务订单统一支付回调
                onPayNotifyService(param);
                break;
            case STORE_ORDER_SN_PREFIX:
                //门店买单订单统一支付回调
                onPayNotifyStore(param);
                break;
            case "C":
                //会员卡充值订单统一支付回调
                //todo
                break;
            case MemberCardOrderService.MEMBER_CARD_ORDER_SN_PREFIX:
                //购买会员卡虚拟订单统一支付回调
                onPayNotifyCardOrder(param);
                break;
            case CouponPackOrderService.COUPON_PACK_ORDER_SN_PREFIX:
                //购买优惠券礼包虚拟订单统一支付回调
                onPayNotifyCouponPack(param);
                break;
            case OrderConstant.INSTEAD_PAY_SN_PREFIX:
                //代付子订单统一支付回调
                onPayNotifySubOrder(param);
                break;
            case OrderConstant.ORDER_SN_PREFIX:
                //订单统一支付回调
                onPayNotify(param);
                break;
            case CardConstant.USER_CARD_RENEW_ORDER:
                //会员卡续费支付回调
                onPayNotifyCardRenew(param);
                break;
            case InquiryOrderConstant.INQUIRY_ORDER_SN_PREFIX:
                //问诊订单支付回调
                onPatNotifyInquiryOrder(param);
                break;
            default:
        }

	}

    /**
     * 订单统一支付回调业务处理
	 * @param param
	 * @throws WxPayException
	 */
	protected void onPayNotify(PaymentRecordParam param) throws MpException {

		String orderSn = param.getOrderSn().split("_")[0];
		param.setOrderSn(orderSn);

		OrderInfoRecord orderInfo = order.getOrderByOrderSn(orderSn);
		if (orderInfo == null) {
            logger().error("订单统一支付回调,未找到订单sn:{}", orderSn);
			throw new MpException(JsonResultCode.CODE_ORDER_NOT_EXIST, "orderSn " + orderSn + "not found");
		}
		String[] goodsTypes = OrderInfoService.orderTypeToArray(orderInfo.getGoodsType());
		BigDecimal totalFee = new BigDecimal(param.getTotalFee());

		// 全款支付，且金额不相同，则抛出异常
		if (!orderInfo.getMoneyPaid().equals(totalFee) && orderInfo.getOrderPayWay().equals(OrderConstant.PAY_WAY_FULL)) {
            logger().error("订单统一支付回调,全款支付但金额不相同,订单sn:{},参数金额:{},订单金额:{}",
                orderSn, totalFee, orderInfo.getMoneyPaid());
			throw new MpException(null, "onPayNotify orderSn " + orderSn + " pay amount  did not match");
		}

		// 订单状态已经是支付后状态，则直接返回
		if (orderInfo.getOrderStatus() >= OrderConstant.ORDER_WAIT_DELIVERY) {
			logger().info("onPayNotify orderSn {} has paied", orderSn);
			return;
		}

		// TODO: 如果为欧派或者寺库，订单推送，可以尝试消息队列

		// 补款订单，订单号为补款订单号
		if (OrderConstant.BK_PAY_FRONT == orderInfo.getBkOrderPaid()) {
			param.setOrderSn(orderInfo.getBkOrderSn());
		}

		// 添加支付记录（wx）
        PaymentRecordRecord paymentRecord = record.addPaymentRecord(param);

		// 订单状态处理
		if (Arrays.asList(goodsTypes).contains(String.valueOf(BaseConstant.ACTIVITY_TYPE_PRE_SALE))) {
			/**
			 * 预售单独处理,先支付定金，后支付尾款 1. 未支付时，如果为定金支付，则BK_ORDER_PAID置为1(定金已支付)，
			 * 否则为全款支付，则直接BK_ORDER_PAID置为2(尾款已支付)，状态变为待发货 最后修改相应预售商品数量销量库存 2.
			 * 已支付定金状态时，则直接BK_ORDER_PAID置为2(尾款已支付)，状态变为待发货
			 */
			if (orderInfo.getBkOrderPaid() == OrderConstant.BK_PAY_NO) {
				// 未支付时
				if (orderInfo.getOrderPayWay() == OrderConstant.PAY_WAY_DEPOSIT) {
					// 定金尾款支付方式时，先标记定金已支付
                    orderInfo.setBkOrderPaid(OrderConstant.BK_PAY_FRONT);
                    orderInfo.update();
                    if(orderInfo.getIsLock().equals(OrderConstant.NO)) {
                        //修改相应预售商品数量销量库存
                        marketProcessorFactory.processUpdateStock(pay.createOrderBeforeParam(orderInfo), orderInfo);
                        atomicOperation.updateStockAndSalesByLock(orderInfo, orderGoodsService.getByOrderId(orderInfo.getOrderId()).into(OrderGoodsBo.class), false);
                    }
				} else {
					// 全款支付方式时，则直接标记为尾款已支付
                    orderInfo.setBkOrderPaid(OrderConstant.BK_PAY_FINISH);
					//状态变为待发货
					pay.toWaitDeliver(orderInfo, paymentRecord);
				}
			} else {
				// 定金已支付，标记为尾款已支付
                orderInfo.setBkOrderPaid(OrderConstant.BK_PAY_FINISH);
				//状态变为待发货
                pay.toWaitDeliver(orderInfo, paymentRecord);
			}
		} else {
			//状态变为待发货
            pay.toWaitDeliver(orderInfo, paymentRecord);
		}

		/**
		 * TODO:POS推送订单
		 */

		/**
		 * TODO: 好友助力--支付完成修改助力进度
		 */

		/**
		 * TODO: 分销订单发送返利模板消息
		 */
	}

	/**
	 *  支付活动
	 * @param param
	 * @param orderInfo
	 * @throws MpException
	 */
	private void payAwardActivity(PaymentRecordParam param, OrderInfoRecord orderInfo) throws MpException {
		if (!orderInfo.getOrderStatus().equals(OrderConstant.ORDER_WAIT_DELIVERY)){
			return;
		}
		String[] strings = OrderInfoService.orderTypeToArray(orderInfo.getGoodsType());
		List<Byte> activityTypeList = Arrays.stream(strings).map(Byte::valueOf).collect(Collectors.toList());
		Byte activityType = OrderCreateMpProcessorFactory.SINGLENESS_ACTIVITY.stream().filter(activityTypeList::contains).findFirst().get();
		OrderBeforeParam orderBeforeParam =new OrderBeforeParam();
		orderBeforeParam.setActivityType(activityType);
		orderBeforeParam.setActivityId(orderInfo.getActivityId());
		orderBeforeParam.setDate(param.getCreated());
		orderBeforeParam.setGoods(new ArrayList<>());
		List<GoodsRecord> orderGoods = orderGoodsService.getGoodsInfoRecordByOrderSn(orderInfo.getOrderSn());
		orderGoods.forEach(orderGood->{
			OrderBeforeParam.Goods goods = new OrderBeforeParam.Goods();
			goods.setGoodsId(orderGood.getGoodsId());
			goods.setGoodsInfo(orderGood);
			orderBeforeParam.getGoods().add(goods);
		});
		marketProcessorFactory.processOrderEffective(orderBeforeParam,orderInfo);
	}

    /**
     * On pay notify store.门店买单订单统一支付回调
     *
     * @param param the param
     */
    private void onPayNotifyStore(PaymentRecordParam param) throws WxPayException {
        String orderSn = param.getOrderSn();
        StoreOrderRecord orderInfo = storeOrder.fetchStoreOrder(orderSn);
        if (Objects.isNull(orderInfo)) {
            logger().error("门店买单订单统一支付回调（onPayNotifyStore）：买单订单【订单号：{}】不存在！", orderSn);
            throw new WxPayException("onPayNotifyStore：orderSn 【" + orderSn + "】not found ！");
        }
        if (NumberUtils.createBigDecimal(param.getTotalFee()).compareTo(orderInfo.getMoneyPaid()) != INTEGER_ZERO) {
            logger().error("门店买单订单统一支付回调（onPayNotifyStore）：订单【订单号：{}】实付金额不符【系统计算金额：{} != 微信支付金额：{}】！", orderSn, orderInfo.getMoneyPaid(), param.getTotalFee());
            throw new WxPayException("onPayNotifyStore：orderSn 【 " + orderSn + "】 pay amount  did not match ！");
        }
        if (BYTE_ONE.equals(orderInfo.getOrderStatus())) {
            logger().info("门店买单订单统一支付回调（onPayNotifyStore）：订单【订单号：{}】已支付！", orderSn);
            return;
        }
        // 添加支付记录（wx）
        PaymentRecordRecord paymentRecord = record.addPaymentRecord(param);
        // 完成支付
        storeOrder.finishPayCallback(orderInfo, paymentRecord);
        logger().info("门店买单订单统一支付回调SUCCESS完成！");
        // 支付完送积分
        storeOrder.storePay2SendScore(orderInfo);
    }

    /**
     * On pay notify service.
     *
     * @param param the param
     * @throws WxPayException the wx pay exception
     */
    public void onPayNotifyService(PaymentRecordParam param) throws WxPayException {
        String orderSn = param.getOrderSn();
        ServiceOrderRecord orderInfo = serviceOrderService.getRecord(orderSn);
        if (Objects.isNull(orderInfo)) {
            logger().error("服务订单统一支付回调（onPayNotifyService）：订单【订单号：{}】不存在！", orderSn);
            throw new WxPayException("onPayNotifyStore：orderSn 【" + orderSn + "】not found ！");
        }
        if (NumberUtils.createBigDecimal(param.getTotalFee()).compareTo(orderInfo.getMoneyPaid()) != INTEGER_ZERO) {
            logger().error("服务订单统一支付回调（onPayNotifyService）：订单【订单号：{}】实付金额不符【系统计算金额：{} != 微信支付金额：{}】！", orderSn, orderInfo.getMoneyPaid(), param.getTotalFee());
            throw new WxPayException("onPayNotifyStore：orderSn 【 " + orderSn + "】 pay amount  did not match ！");
        }
        if (!ORDER_STATUS_WAIT_PAY.equals(orderInfo.getOrderStatus())) {
            logger().info("服务订单统一支付回调（onPayNotifyService）：订单【订单号：{}】已支付！", orderSn);
            return;
        }
        // 添加支付记录（wx）
        PaymentRecordRecord paymentRecord = record.addPaymentRecord(param);
        // 完成支付
        serviceOrderService.finishPayCallback(orderInfo, paymentRecord);
        logger().info("服务订单统一支付回调SUCCESS完成！");
    }

    /**
     * On pay notify service.
     *
     * @param param the param
     * @throws WxPayException the wx pay exception
     */
    public void onPayNotifyCouponPack(PaymentRecordParam param) throws WxPayException {
        String orderSn = param.getOrderSn();
        VirtualOrderRecord orderInfo = memberCardOrderService.getRecord(orderSn);
        if (Objects.isNull(orderInfo)) {
            logger().error("优惠券礼包订单统一支付回调（onPayNotifyCouponPack）：订单【订单号：{}】不存在！", orderSn);
            throw new WxPayException("onPayNotifyCouponPack：orderSn 【" + orderSn + "】not found ！");
        }
        if (NumberUtils.createBigDecimal(param.getTotalFee()).compareTo(orderInfo.getMoneyPaid()) != INTEGER_ZERO) {
            logger().error("优惠券礼包订单统一支付回调（onPayNotifyCouponPack）：订单【订单号：{}】实付金额不符【系统计算金额：{} != 微信支付金额：{}】！", orderSn, orderInfo.getMoneyPaid(), param.getTotalFee());
            throw new WxPayException("onPayNotifyStore：orderSn 【 " + orderSn + "】 pay amount  did not match ！");
        }
        if (!VirtualOrderService.ORDER_STATUS_WAIT_PAY.equals(orderInfo.getOrderStatus())) {
            logger().info("优惠券礼包订单统一支付回调（onPayNotifyCouponPack）：订单【订单号：{}】已支付！", orderSn);
            return;
        }
        // 添加支付记录（wx）
        PaymentRecordRecord paymentRecord = record.addPaymentRecord(param);
        // 完成支付
        couponPackOrderService.finishPayCallback(orderInfo, paymentRecord);
        logger().info("优惠券礼包订单统一支付回调SUCCESS完成！");
    }

    /**
     * On pay notify service.
     *  会员卡支付回调
     * @param param the param
     * @throws WxPayException the wx pay exception
     */
    public void onPayNotifyCardOrder(PaymentRecordParam param) throws WxPayException, MpException {
        String orderSn = param.getOrderSn();
        VirtualOrderRecord orderInfo = memberCardOrderService.getRecord(orderSn);
        if (Objects.isNull(orderInfo)) {
            logger().error("购买会员卡订单统一支付回调（onPayNotifyCardOrder）：订单【订单号：{}】不存在！", orderSn);
            throw new WxPayException("onPayNotifyCardOrder：orderSn 【" + orderSn + "】not found ！");
        }
        if (NumberUtils.createBigDecimal(param.getTotalFee()).compareTo(orderInfo.getMoneyPaid()) != INTEGER_ZERO) {
            logger().error("购买会员卡礼包订单统一支付回调（onPayNotifyCardOrder）：订单【订单号：{}】实付金额不符【系统计算金额：{} != 微信支付金额：{}】！", orderSn, orderInfo.getMoneyPaid(), param.getTotalFee());
            throw new WxPayException("onPayNotifyCardOrder：orderSn 【 " + orderSn + "】 pay amount  did not match ！");
        }
        if (!VirtualOrderService.ORDER_STATUS_WAIT_PAY.equals(orderInfo.getOrderStatus())) {
            logger().info("购买会员卡订单统一支付回调（onPayNotifyCardOrder）：订单【订单号：{}】已支付！", orderSn);
            return;
        }
        // 添加支付记录（wx）
        PaymentRecordRecord paymentRecord = record.addPaymentRecord(param);
        // 完成支付
        userCardService.finishPayCallback(orderInfo, paymentRecord);
        logger().info("购买会员卡订单统一支付回调SUCCESS完成！");
    }

    /**
     * 代付子单支付回调
     * @param param
     */
    private void  onPayNotifySubOrder(PaymentRecordParam param) throws MpException {
        logger().info("代付子单支付回调start");
        SubOrderInfoRecord order = subOrderService.get(param.getOrderSn());
        if (order == null) {
            logger().error("代付子单统一支付回调,未找到订单sn:{}", param.getOrderSn());
            throw new MpException(null, "orderSn " + param.getOrderSn() + "not found");
        }
        //参数金额
        BigDecimal totalFee = new BigDecimal(param.getTotalFee());
        if (!order.getMoneyPaid().equals(totalFee)) {
            logger().error("代付子统一支付回调,金额不相同,订单sn:{},参数金额:{},订单金额:{}",
                param.getOrderSn(), totalFee, order.getMoneyPaid());
            throw new MpException(null, "onPayNotify orderSn " + param.getOrderSn() + " pay amount  did not match");
        }
        if (OrderConstant.SubOrderConstant.SUB_ORDER_PAY_OK.equals(order.getOrderStatus())) {
            logger().info("代付子统一支付回调：{},已支付！", param.getOrderSn());
            return;
        }
        insteadPayService.businessLogic(param, order);
        logger().info("代付子单支付回调end");
    }

    /**
     * 会员卡续费支付回调
     * @param param
     */
    private void onPayNotifyCardRenew(PaymentRecordParam param) throws MpException {
        logger().info("会员卡续费支付回调start");
        CardRenewRecord order = userCardService.get(param.getOrderSn());
        if (order == null) {
            logger().error("会员卡续费支付回调,未找到订单sn:{}", param.getOrderSn());
            throw new MpException(null, "orderSn " + param.getOrderSn() + "not found");
        }
        //参数金额
        BigDecimal totalFee = new BigDecimal(param.getTotalFee());
        if (!order.getMoneyPaid().equals(totalFee)) {
            logger().error("会员卡续费支付回调,金额不相同,订单sn:{},参数金额:{},订单金额:{}",
                param.getOrderSn(), totalFee, order.getMoneyPaid());
            throw new MpException(null, "onPayNotify orderSn " + param.getOrderSn() + " pay amount  did not match");
        }
        if (CardConstant.CARD_RENEW_ORDER_STATUS_OK.equals(order.getOrderStatus())) {
            logger().info("会员卡续费支付回调：{},已支付！", param.getOrderSn());
            return;
        }
        // 添加支付记录（wx）
        PaymentRecordRecord paymentRecord = record.addPaymentRecord(param);
        userCardService.cardRenewFinish(order,paymentRecord);
        logger().info("会员卡续费支付回调end");
    }
    /**
     * 问诊订单支付回调
     * @param param
     */
    private void onPatNotifyInquiryOrder(PaymentRecordParam param)throws MpException,WxPayException{
        logger().info("问诊订单支付回调start");
        String orderSn = param.getOrderSn();
        InquiryOrderDo orderInfo=inquiryOrderDao.getByOrderSn(param.getOrderSn());
        if (Objects.isNull(orderInfo)) {
            logger().error("问诊订单订单统一支付回调（onPatNotifyInquiryOrder）：订单【订单号：{}】不存在！", orderSn);
            throw new WxPayException("onPayNotifyCardOrder：orderSn 【" + orderSn + "】not found ！");
        }
        if (NumberUtils.createBigDecimal(param.getTotalFee()).compareTo(orderInfo.getOrderAmount()) != INTEGER_ZERO) {
            logger().error("问诊订单订单统一支付回调（onPatNotifyInquiryOrder）：订单【订单号：{}】实付金额不符【系统计算金额：{} != 微信支付金额：{}】！", orderSn, orderInfo.getOrderAmount(), param.getTotalFee());
            throw new WxPayException("onPatNotifyInquiryOrder：orderSn 【 " + orderSn + "】 pay amount  did not match ！");
        }
        if (!InquiryOrderConstant.ORDER_TO_PAID.equals(orderInfo.getOrderStatus())) {
            logger().info("问诊订单订单统一支付回调（onPatNotifyInquiryOrder）：订单【订单号：{}】已支付！", orderSn);
            return;
        }
        //添加支付记录
        PaymentRecordRecord paymentRecord = record.addPaymentRecord(param);
        inquiryOrderService.inquiryOrderFinish(orderInfo,paymentRecord);
        logger().info("问诊订单支付回调start");
    }
}

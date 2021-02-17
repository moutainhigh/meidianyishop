package com.meidianyi.shop.service.shop.order.virtual;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.util.*;
import com.meidianyi.shop.service.pojo.shop.market.couponpack.CouponPackConstant;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountParam;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConsumpData;
import com.meidianyi.shop.service.pojo.shop.member.order.UserOrderBean;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.virtual.CouponPackOrderExportVo;
import com.meidianyi.shop.service.pojo.shop.order.virtual.CouponPackOrderPageParam;
import com.meidianyi.shop.service.pojo.shop.order.virtual.CouponPackOrderRefundParam;
import com.meidianyi.shop.service.pojo.shop.order.virtual.CouponPackOrderVo;
import com.meidianyi.shop.service.pojo.wxapp.coupon.pack.CouponPackOrderParam;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import jodd.util.StringUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.CouponPack.COUPON_PACK;
import static com.meidianyi.shop.db.shop.tables.CouponPackVoucher.COUPON_PACK_VOUCHER;
import static com.meidianyi.shop.db.shop.tables.CustomerAvailCoupons.CUSTOMER_AVAIL_COUPONS;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_TP_NORMAL;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.*;
import static com.meidianyi.shop.service.pojo.shop.payment.PayCode.PAY_CODE_BALANCE_PAY;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * @author huangronggang
 * @date 2019年8月1日
 */
@Service
public class CouponPackOrderService extends VirtualOrderService {
	/** 发放优惠劵的获取方式，0：发放，1：领取，2：礼包*/
	public static final Byte CUSTOMER_AVAIL_COUPONS_ACCESSMODE_PACK=2;

	public static final String COUPON_PACK_ORDER_SN_PREFIX = "Y";

    @Autowired
    public MemberService memberService;
    @Autowired
    public MpPaymentService mpPaymentService;

	/**
	 * 分页查询优惠劵礼包订单
	 * @param param
	 * @return
	 */
	public PageResult<CouponPackOrderVo> getPageList(CouponPackOrderPageParam param){
		SelectWhereStep<? extends Record> selectFrom = db()
            .select(VIRTUAL_ORDER.ORDER_ID, VIRTUAL_ORDER.VIRTUAL_GOODS_ID, COUPON_PACK.PACK_NAME,
                VIRTUAL_ORDER.ORDER_SN, VIRTUAL_ORDER.USER_ID, USER.USERNAME, USER.MOBILE, VIRTUAL_ORDER.MONEY_PAID, VIRTUAL_ORDER.USE_ACCOUNT, VIRTUAL_ORDER.USE_SCORE, VIRTUAL_ORDER.MEMBER_CARD_BALANCE, VIRTUAL_ORDER.CARD_NO, VIRTUAL_ORDER.PAY_CODE, VIRTUAL_ORDER.PAY_NAME, VIRTUAL_ORDER.PREPAY_ID, VIRTUAL_ORDER.PAY_SN, VIRTUAL_ORDER.ORDER_AMOUNT,
                VIRTUAL_ORDER.CREATE_TIME, VIRTUAL_ORDER.RETURN_FLAG, VIRTUAL_ORDER.RETURN_SCORE, VIRTUAL_ORDER.RETURN_ACCOUNT, VIRTUAL_ORDER.RETURN_MONEY, VIRTUAL_ORDER.RETURN_CARD_BALANCE,
                VIRTUAL_ORDER.RETURN_TIME, VIRTUAL_ORDER.CURRENCY,VIRTUAL_ORDER.PAY_TIME)
            .from(VIRTUAL_ORDER)
            .leftJoin(COUPON_PACK).on(VIRTUAL_ORDER.VIRTUAL_GOODS_ID.eq(COUPON_PACK.ID))
            .leftJoin(USER).on(VIRTUAL_ORDER.USER_ID.eq(USER.USER_ID));
		SelectConditionStep<? extends Record> select = buildOptions(selectFrom,param);
        select.orderBy(VIRTUAL_ORDER.CREATE_TIME.desc());
		PageResult<CouponPackOrderVo> pageResult = getPageResult(select,param.getCurrentPage(),param.getPageRows(), CouponPackOrderVo.class);
		List<CouponPackOrderVo> dataList = pageResult.dataList;
		if(dataList==null) {
			return pageResult;
		}
		for (CouponPackOrderVo couponPackOrderVo : dataList) {
			couponPackOrderVo.setSurplusAmount(getSurplusAmount(couponPackOrderVo.getVirtualGoodsId(), couponPackOrderVo.getOrderSn()));
			if(couponPackOrderVo.getReturnFlag().equals(REFUND_STATUS_SUCCESS) && couponPackOrderVo.getOrderAmount().compareTo(BigDecimal.ZERO) == 0 && couponPackOrderVo.getUseScore() > 0 && couponPackOrderVo.getUseScore() > couponPackOrderVo.getReturnScore()){
			    //积分支付的礼包，积分没有全退完时，标记成部分退款
                couponPackOrderVo.setReturnFlag((byte)3);
            }
			if(couponPackOrderVo.getReturnFlag().equals(REFUND_STATUS_SUCCESS) && couponPackOrderVo.getOrderAmount().compareTo(couponPackOrderVo.getReturnAccount().add(couponPackOrderVo.getReturnCardBalance()).add(couponPackOrderVo.getReturnMoney())) > 0){
                //现金支付的礼包，现金没有全退完时，标记成部分退款
                couponPackOrderVo.setReturnFlag((byte)3);
            }
			//超过一年不能退款
            if (couponPackOrderVo.getMoneyPaid().compareTo(BigDecimal.ZERO)>0&&couponPackOrderVo.getPayTime()!=null&& DateUtils.getLocalDateTime().after(DateUtils.getTimeStampPlus(couponPackOrderVo.getPayTime(),1, ChronoUnit.YEARS))){
                couponPackOrderVo.setCanReturn(BaseConstant.NO);
            }else {
                couponPackOrderVo.setCanReturn(BaseConstant.YES);
            }
		}
		return pageResult;
	}

	/**
	 * @param select
	 * @param param
	 * @return
	 */
	private  SelectConditionStep<? extends Record> buildOptions(SelectWhereStep<? extends Record> select, CouponPackOrderPageParam param) {
        SelectConditionStep<? extends Record> condition = select.where(VIRTUAL_ORDER.GOODS_TYPE.eq(GOODS_TYPE_COUPON_PACK))
            .and(VIRTUAL_ORDER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(VIRTUAL_ORDER.ORDER_AMOUNT.gt(BigDecimal.ZERO).or(VIRTUAL_ORDER.USE_SCORE.gt(0)));
        if (param.getUserId() != null && param.getUserId() > 0) {
            condition.and(VIRTUAL_ORDER.USER_ID.eq(param.getUserId()));
        }
        if (!StringUtils.isBlank(param.getPackName())) {
            condition.and(COUPON_PACK.PACK_NAME.like(this.likeValue(param.getPackName())));
        }
        if (!StringUtils.isBlank(param.getOrderSn())) {
            condition.and(VIRTUAL_ORDER.ORDER_SN.like(this.likeValue(param.getOrderSn())));
        }
        if (!StringUtils.isBlank(param.getUserInfo())) {
            condition.and(USER.USERNAME.like(likeValue(param.getUserInfo())).or(USER.MOBILE.like(likeValue(param.getUserInfo()))));
        }
        if (param.getStartTime() != null) {
            condition.and(VIRTUAL_ORDER.CREATE_TIME.gt(param.getStartTime()));
        }
        if (param.getEndTime() != null) {
            condition.and(VIRTUAL_ORDER.CREATE_TIME.le(param.getEndTime()));
        }
        if (StringUtil.isNotBlank(param.getPayCode())) {
            condition.and(VIRTUAL_ORDER.PAY_CODE.eq(param.getPayCode()));
        }
        if (null != param.getRefund()) {
            if (param.getRefund()) {
                condition.and(VIRTUAL_ORDER.RETURN_FLAG.eq(REFUND_STATUS_SUCCESS).or(VIRTUAL_ORDER.RETURN_FLAG.eq(REFUND_STATUS_FAILED)));
            }
        }
        return condition;
    }

	/**
	 * 返回一个优惠劵包里有多少个优惠劵
	 * @param couponPackId
	 * @return
	 */
	private int getTotalCouponNum(Integer couponPackId) {
		Record1<BigDecimal> fetchOne = db().select(DSL.sum(COUPON_PACK_VOUCHER.TOTAL_AMOUNT)).from(COUPON_PACK_VOUCHER)
					.where(COUPON_PACK_VOUCHER.ACT_ID.eq(couponPackId))
					.and(COUPON_PACK_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).fetchOne();
		return fetchOne.value1() == null ?0:fetchOne.value1().intValue();
	}
	/**
	 * 返回用户已经领取的优惠劵数量
	 * @param orderSn
	 * @return
	 */
	public int getVoucherAccessCount(String orderSn) {
		return db().selectCount().from(CUSTOMER_AVAIL_COUPONS)
			.where(CUSTOMER_AVAIL_COUPONS.ACCESS_ORDER_SN.eq(orderSn))
			.and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
			.and(CUSTOMER_AVAIL_COUPONS.ACCESS_MODE.eq(CUSTOMER_AVAIL_COUPONS_ACCESSMODE_PACK)).fetchOptionalInto(Integer.class).orElse(0);
	}
	/**
	 * 获取某一个订单 用户还剩余多少优惠劵未发放
	 * @param couponPackId
	 * @param orderSn
	 * @return
	 */
	private int getSurplusAmount(Integer couponPackId, String orderSn) {
		if(couponPackId== null||orderSn == null) {
			return 0;
		}
		return getTotalCouponNum(couponPackId)-getVoucherAccessCount(orderSn);
	}

	/**
	 * 手动退款
	 * @param
	 * @return
	 */
	public JsonResultCode refundCouponPackOrder(CouponPackOrderRefundParam param) throws MpException {
	    this.virtualOrderRefund(param.getVirtualOrderRefundParam());
        this.updateSendFlag(param.getStillSendFlag(), param.getOrderId());

        /** 操作记录 */
        saas().getShopApp(getShopId()).record.insertRecord(Arrays.asList(new Integer[] { RecordContentTemplate.ORDER_COUPON_PACK_ORDER_REFUND.code }), new String[] {param.getOrderSn()});
        return null;
	}

	public void updateSendFlag(Byte sendFlag,Integer orderId) {
		db().update(VIRTUAL_ORDER)
			.set(VIRTUAL_ORDER.STILL_SEND_FLAG,sendFlag)
			.where(VIRTUAL_ORDER.ORDER_ID.eq(orderId)).execute();
	}

    /**
     * 某个优惠券礼包活动的礼包销量
     * @param couponPackId
     * @return
     */
	public int getCouponPackIssueAmount(int couponPackId){
	    return db().selectCount().from(VIRTUAL_ORDER).
            where(VIRTUAL_ORDER.GOODS_TYPE.eq(GOODS_TYPE_COUPON_PACK)).
            and(VIRTUAL_ORDER.ORDER_STATUS.eq(ORDER_STATUS_FINISHED)).
            and(VIRTUAL_ORDER.VIRTUAL_GOODS_ID.eq(couponPackId)).
            fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 获得用户某个礼包购买数
     * @param packId
     * @param userId
     * @return
     */
    public int getUserCouponPackBuyCount(int packId,int userId){
        return db().selectCount().from(VIRTUAL_ORDER).where(VIRTUAL_ORDER.GOODS_TYPE.eq(GOODS_TYPE_COUPON_PACK)).and(VIRTUAL_ORDER.ORDER_STATUS.eq(ORDER_STATUS_FINISHED)).and(VIRTUAL_ORDER.VIRTUAL_GOODS_ID.eq(packId)).and(VIRTUAL_ORDER.USER_ID.eq(userId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 优惠券礼包下单
     * @param param
     * @param userId
     * @return
     */
    public WebPayVo createOrder(CouponPackOrderParam param,Integer userId,String clientIp){
        //用户的余额和积分
        UserRecord user = memberService.getUserRecordById(userId);
        if(param.getAccountDiscount() != null && param.getAccountDiscount().compareTo(user.getAccount()) > 0){
            throw new BusinessException(JsonResultCode.CODE_BALANCE_INSUFFICIENT);
        }
        if(param.getScoreDiscount() != null && param.getScoreDiscount() > 0 && param.getScoreDiscount() > user.getScore()){
            throw new BusinessException(JsonResultCode.CODE_SCORE_INSUFFICIENT);
        }
        if(param.getMemberCardBalance() != null && param.getMemberCardBalance().compareTo(BigDecimal.ZERO) > 0 && StringUtil.isNotBlank(param.getCardNo())){
            UserCardRecord userCard = memberService.card.getUserCardInfoByCardNo(param.getCardNo());
            if(userCard == null){
                throw new BusinessException(JsonResultCode.CODE_USER_CARD_NONE);
            }
            if(userCard.getMoney().compareTo(param.getMemberCardBalance()) < 0){
                throw new BusinessException(JsonResultCode.CODE_USER_CARD_BALANCE_INSUFFICIENT);
            }
        }

        CouponPackRecord couponPackRecord = db().fetchAny(COUPON_PACK,COUPON_PACK.ID.eq(param.getPackId()));

        BigDecimal moneyPaid = param.getOrderAmount() == null ? BigDecimal.ZERO : (param.getOrderAmount().subtract(param.getAccountDiscount() == null ? BigDecimal.ZERO : param.getAccountDiscount()).subtract(param.getMemberCardBalance() == null ? BigDecimal.ZERO : param.getMemberCardBalance()).setScale(2,BigDecimal.ROUND_HALF_UP));
        String payCode = moneyPaid.compareTo(BigDecimal.ZERO) > 0 ? OrderConstant.PAY_CODE_WX_PAY : (param.getScoreDiscount() > 0 ? OrderConstant.PAY_CODE_SCORE_PAY : OrderConstant.PAY_CODE_BALANCE_PAY);
        String orderSn = IncrSequenceUtil.generateOrderSn(COUPON_PACK_ORDER_SN_PREFIX);


        VirtualOrderRecord insertVirtualOrderRecord = db().newRecord(VIRTUAL_ORDER);
        insertVirtualOrderRecord.setOrderSn(orderSn);
        insertVirtualOrderRecord.setUserId(userId);
        insertVirtualOrderRecord.setVirtualGoodsId(param.getPackId());
        insertVirtualOrderRecord.setOrderStatus(ORDER_STATUS_WAIT_PAY);
        insertVirtualOrderRecord.setInvoiceId(param.getInvoiceId());
        insertVirtualOrderRecord.setInvoiceDetail(param.getInvoiceDetail());
        insertVirtualOrderRecord.setPayCode(payCode);
        insertVirtualOrderRecord.setMoneyPaid(moneyPaid);
        insertVirtualOrderRecord.setUseAccount(param.getAccountDiscount());
        insertVirtualOrderRecord.setUseScore(param.getScoreDiscount());
        insertVirtualOrderRecord.setMemberCardBalance(param.getMemberCardBalance());
        insertVirtualOrderRecord.setCardNo(param.getCardNo());
        insertVirtualOrderRecord.setOrderAmount(param.getOrderAmount() == null ? BigDecimal.ZERO : param.getOrderAmount());
        insertVirtualOrderRecord.setGoodsType(GOODS_TYPE_COUPON_PACK);
        insertVirtualOrderRecord.setAccessMode(couponPackRecord.getAccessMode());
        insertVirtualOrderRecord.setCurrency(saas().shop.getCurrency(getShopId()));
        insertVirtualOrderRecord.setSendCardNo("");

        insertVirtualOrderRecord.insert();
        WebPayVo vo = null;
        if(moneyPaid.compareTo(BigDecimal.ZERO) <= 0){
            this.finishPayCallback(insertVirtualOrderRecord,null);
            return null;
        }else{
            //微信支付接口
            try {
                vo = mpPaymentService.wxUnitOrder(clientIp, couponPackRecord.getPackName(), orderSn, moneyPaid, user.getWxOpenid());
            } catch (WxPayException e) {
                logger().error("微信预支付调用接口失败WxPayException，订单号：{},异常：{}", orderSn, e);
                throw new BusinessException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }catch (Exception e) {
                logger().error("微信预支付调用接口失败Exception，订单号：{},异常：{}", orderSn, e.getMessage());
                throw new BusinessException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }
            logger().debug("优惠券礼包-微信支付接口调用结果：{}", vo);
            // 更新记录微信预支付id：prepayid
            this.updatePrepayId(orderSn,vo.getResult().getPrepayId());

            return vo;
        }

    }

    /**
     * 支付完成的回调
     * @param orderRecord
     * @param paymentRecord
     * @return
     */
    public VirtualOrderRecord finishPayCallback(VirtualOrderRecord orderRecord, PaymentRecordRecord paymentRecord){
        db().update(VIRTUAL_ORDER).
            set(VIRTUAL_ORDER.ORDER_STATUS,ORDER_STATUS_FINISHED).
            set(VIRTUAL_ORDER.PAY_SN,(paymentRecord == null ? "" : paymentRecord.getPaySn())).
            set(VIRTUAL_ORDER.PAY_TIME, DateUtils.getLocalDateTime()).
            where(VIRTUAL_ORDER.ORDER_SN.eq(orderRecord.getOrderSn())).
            execute();
        orderRecord.refresh();
        db().update(COUPON_PACK).set(COUPON_PACK.ISSUED_AMOUNT, COUPON_PACK.ISSUED_AMOUNT.add(1)).where(COUPON_PACK.ID.eq(orderRecord.getVirtualGoodsId())).execute();

        if(orderRecord.getUseScore() != null && orderRecord.getUseScore() > 0){
            ScoreParam scoreParam = new ScoreParam();
            scoreParam.setScore(- orderRecord.getUseScore());
            scoreParam.setUserId(orderRecord.getUserId());
            scoreParam.setOrderSn(orderRecord.getOrderSn());
            scoreParam.setRemarkCode(RemarkTemplate.ORDER_MAKE.code);
            scoreParam.setRemarkData(orderRecord.getOrderSn());
            try {
                memberService.score.updateMemberScore(scoreParam, INTEGER_ZERO, TYPE_SCORE_PAY.val(), TRADE_FLOW_OUT.val());
            } catch (MpException e) {
                e.printStackTrace();
            }
        }
        if(BigDecimalUtil.greaterThanZero(orderRecord.getUseAccount())){
            AccountParam accountParam = new AccountParam() {{
                setUserId(orderRecord.getUserId());
                setAmount(orderRecord.getUseAccount().negate());
                setOrderSn(orderRecord.getOrderSn());
                setPayment(PAY_CODE_BALANCE_PAY);
                setIsPaid(UACCOUNT_CONSUMPTION.val());
                setRemarkId(RemarkTemplate.ORDER_MAKE.code);
                setRemarkData(orderRecord.getOrderSn());
            }};
            TradeOptParam tradeOptParam = TradeOptParam.builder()
                .tradeType(TYPE_CRASH_ACCOUNT_PAY.val())
                .tradeFlow(TRADE_FLOW_OUT.val())
                .build();
            try {
                memberService.account.updateUserAccount(accountParam,tradeOptParam);
            } catch (MpException e) {
                e.printStackTrace();
            }
        }
        if(BigDecimalUtil.greaterThanZero(orderRecord.getMemberCardBalance()) && StringUtil.isNotBlank(orderRecord.getCardNo())){
            CardConsumpData cardConsumpData = new CardConsumpData()
                .setUserId(orderRecord.getUserId())
                // 会员卡更新金额，区分正负号，这里是负号，意为扣减
                .setMoney(orderRecord.getMemberCardBalance().negate())
                .setCardNo(orderRecord.getCardNo())
                .setReason(orderRecord.getOrderSn())
                .setReasonId(RemarkTemplate.COUPON_PACK_ORDER.code)
                // 消费类型 :只支持普通卡0
                .setType(MCARD_TP_NORMAL);
            TradeOptParam tradeOpt = TradeOptParam
                .builder()
                .tradeFlow(TYPE_CRASH_MEMBER_CARD_PAY.val())
                .tradeFlow(TRADE_FLOW_OUT.val())
                .build();
            try {
                memberService.card.updateMemberCardAccount(cardConsumpData,tradeOpt);
            } catch (MpException e) {
                e.printStackTrace();
            }
        }

        //发券
        List<VirtualOrderRecord> list = new ArrayList<>();
        list.add(orderRecord);
        saas.getShopApp(getShopId()).couponPack.sendCouponPack(list);

        return orderRecord;
    }

    /**
     *
     * @param orderSn
     * @param prepayId
     */
    public void updatePrepayId(String orderSn,String prepayId){
        db().update(VIRTUAL_ORDER).set(VIRTUAL_ORDER.PREPAY_ID,prepayId).where(VIRTUAL_ORDER.ORDER_SN.eq(orderSn)).execute();
    }



    /**
     * 更新stillSendFlag
     * @param orderSn
     * @param stillSendFlag
     */
    public void updateStillSendFlag(String orderSn,Byte stillSendFlag){
        db().update(VIRTUAL_ORDER).set(VIRTUAL_ORDER.STILL_SEND_FLAG,stillSendFlag).where(VIRTUAL_ORDER.ORDER_SN.eq(orderSn)).execute();
    }

    /**
     * 获取待发放的优惠券礼包订单
     * @return
     */
    public List<VirtualOrderRecord> getCanGrantCouponOrderList(){
        return db().selectFrom(VIRTUAL_ORDER).
            where(VIRTUAL_ORDER.GOODS_TYPE.eq(GOODS_TYPE_COUPON_PACK)).
            and(VIRTUAL_ORDER.ORDER_STATUS.eq(ORDER_STATUS_FINISHED)).
            and(VIRTUAL_ORDER.STILL_SEND_FLAG.eq(CouponPackConstant.STILL_SEND_FLAG_CONTINUE)).
            fetch();
    }

    public Integer getExportRows(CouponPackOrderPageParam param) {
        SelectWhereStep<? extends Record> selectFrom = db()
            .selectCount()
            .from(VIRTUAL_ORDER)
            .leftJoin(COUPON_PACK).on(VIRTUAL_ORDER.VIRTUAL_GOODS_ID.eq(COUPON_PACK.ID))
            .leftJoin(USER).on(VIRTUAL_ORDER.USER_ID.eq(USER.USER_ID));
        SelectConditionStep<? extends Record> select = buildOptions(selectFrom, param);
        return select.fetchOptionalInto(int.class).orElse(0);
    }

    public Workbook exportOrderList(CouponPackOrderPageParam param, String lang) {
        SelectWhereStep<? extends Record> selectFrom = db()
            .select(VIRTUAL_ORDER.ORDER_ID, VIRTUAL_ORDER.ORDER_STATUS, VIRTUAL_ORDER.VIRTUAL_GOODS_ID, COUPON_PACK.PACK_NAME,
                VIRTUAL_ORDER.ORDER_SN, VIRTUAL_ORDER.USER_ID, USER.USERNAME, USER.MOBILE, VIRTUAL_ORDER.MONEY_PAID, VIRTUAL_ORDER.USE_ACCOUNT, VIRTUAL_ORDER.USE_SCORE, VIRTUAL_ORDER.MEMBER_CARD_BALANCE, VIRTUAL_ORDER.CARD_NO, VIRTUAL_ORDER.PAY_CODE, VIRTUAL_ORDER.PAY_NAME, VIRTUAL_ORDER.PREPAY_ID, VIRTUAL_ORDER.PAY_SN, VIRTUAL_ORDER.ORDER_AMOUNT,
                VIRTUAL_ORDER.CREATE_TIME, VIRTUAL_ORDER.RETURN_FLAG, VIRTUAL_ORDER.RETURN_SCORE, VIRTUAL_ORDER.RETURN_ACCOUNT, VIRTUAL_ORDER.RETURN_MONEY, VIRTUAL_ORDER.RETURN_CARD_BALANCE,
                VIRTUAL_ORDER.RETURN_TIME, VIRTUAL_ORDER.CURRENCY)
            .from(VIRTUAL_ORDER)
            .leftJoin(COUPON_PACK).on(VIRTUAL_ORDER.VIRTUAL_GOODS_ID.eq(COUPON_PACK.ID))
            .leftJoin(USER).on(VIRTUAL_ORDER.USER_ID.eq(USER.USER_ID));
        SelectConditionStep<? extends Record> select = buildOptions(selectFrom, param);
        select.orderBy(VIRTUAL_ORDER.CREATE_TIME.desc());
        List<CouponPackOrderExportVo> list = select.fetchInto(CouponPackOrderExportVo.class);

        list.forEach(o -> {
            if (o.getUseScore() != null && o.getUseScore() > 0) {
                o.setPrice(o.getUseScore().toString() + Util.translateMessage(lang, JsonResultMessage.UEXP_SCORE, "excel"));
            } else {
                String cny = "CNY";
                if (cny.equals(o.getCurrency())) {
                    o.setPrice("￥" + o.getOrderAmount().toString());
                } else {
                    o.setPrice("$" + o.getOrderAmount().toString());
                }
            }

            if (REFUND_STATUS_SUCCESS.equals(o.getReturnFlag())) {
                o.setOrderStatusName(Util.translateMessage(lang, JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_REFUNDED, "excel"));
            } else {
                o.setOrderStatusName(Util.translateMessage(lang, JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_PAYMENT_SUCCESSFUL, "excel"));
            }

        });

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(list, CouponPackOrderExportVo.class);
        return workbook;
    }

    /**
     * 用户下单统计
     *
     * @param userId
     * @return
     */
    public UserOrderBean getUserOrderStatistics(int userId) {
        return db().select(DSL.count(VIRTUAL_ORDER.ORDER_ID).as("orderNum"),
            DSL.sum(VIRTUAL_ORDER.MONEY_PAID.add(VIRTUAL_ORDER.USE_ACCOUNT).add(VIRTUAL_ORDER.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.ORDER_STATUS.eq(ORDER_STATUS_FINISHED))
            .and(VIRTUAL_ORDER.GOODS_TYPE.eq(GOODS_TYPE_COUPON_PACK))
            .and(VIRTUAL_ORDER.USER_ID.eq(userId))
            //  虚拟优惠券礼包免费领取不计入，需要计算花了余额或积分的
            .and(VIRTUAL_ORDER.ORDER_AMOUNT.gt(BigDecimal.ZERO).or(VIRTUAL_ORDER.USE_SCORE.gt(0)))
            .fetchAnyInto(UserOrderBean.class);
    }


}


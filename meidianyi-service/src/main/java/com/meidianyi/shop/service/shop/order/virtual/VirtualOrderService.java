package com.meidianyi.shop.service.shop.order.virtual;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.VirtualOrderRefundRecord;
import com.meidianyi.shop.db.shop.tables.records.VirtualOrderRecord;
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
import com.meidianyi.shop.service.pojo.shop.order.virtual.*;
import com.meidianyi.shop.service.shop.operation.RecordTradeService;
import com.meidianyi.shop.service.shop.order.refund.ReturnMethodService;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static com.meidianyi.shop.db.shop.tables.VirtualOrderRefundRecord.VIRTUAL_ORDER_REFUND_RECORD;

/**
 * @author: 王兵兵
 * @create: 2019-08-22 11:17
 **/
@Service
public class VirtualOrderService extends ShopBaseService {

	@Autowired
	private RecordTradeService recordMemberTrade;

	@Autowired
	private ReturnMethodService returnMethod;
    /** 虚拟商品类型：会员卡订单 */
    public static final Byte GOODS_TYPE_MEMBER_CARD = 0;
    /** 虚拟商品类型：优惠券礼包订单 */
    public static final Byte GOODS_TYPE_COUPON_PACK = 1;


    /** 订单状态：待付款 */
    public static final Byte ORDER_STATUS_WAIT_PAY = 0;
    /** 订单状态：已完成 */
    public static final Byte ORDER_STATUS_FINISHED = 1;

    /** 订单和退款记录表退款状态值相同 */
    /** 退款状态：未退款 **/
    public static final Byte REFUND_STATUS_NORMAL = 0;
    /** 退款状态：退款失败 **/
    public static final Byte REFUND_STATUS_FAILED = 1;
    /** 退款状态：退款成功 **/
    public static final Byte REFUND_STATUS_SUCCESS = 2;

    /**
     * 手动退款
     * @throws MpException
     */
    public void virtualOrderRefund(VirtualOrderRefundParam param) throws MpException {
    	//订单
    	VirtualOrderPayInfo payInfo = getOrderPayInfo(param.getOrderId());
    	//虚拟订单退款限制为1年
        if (payInfo.getMoneyPaid().compareTo(BigDecimal.ZERO)>0&&payInfo.getPayTime()!=null&& DateUtils.getLocalDateTime().after(DateUtils.getTimeStampPlus(payInfo.getPayTime(),1, ChronoUnit.YEARS))){
            throw new MpException(JsonResultCode.REFUND_REQUEST_PARAMETER_TIME_ONE_YEAR);
        }

        /** 是否退款成功 */
        boolean successFlag = true;
        try {
            processRefund(param, payInfo);

        }  catch (DataAccessException e) {
			logger().error("退款捕获DataAccessException异常", e);
			Throwable cause = e.getCause();
			if (cause instanceof MpException) {
				throw (MpException)cause;
			} else {
				throw new MpException(JsonResultCode.CODE_ORDER_RETURN_ROLLBACK_NO_MPEXCEPTION);
			}
		} catch (Exception e) {
			logger().error("退款捕获mp异常", e);
			throw new MpException(JsonResultCode.CODE_ORDER_RETURN_ROLLBACK_NO_MPEXCEPTION);
		}

        BigDecimal finalReturnMoney = payInfo.getReturnMoney().add(param.getMoney());
        BigDecimal finalReturnAccount = payInfo.getReturnAccount().add(param.getAccount());
        BigDecimal finalReturnMemberCardBalance = payInfo.getReturnCardBalance().add(param.getMemberCardBalance());
        Integer finalReturnScore = payInfo.getReturnScore() + param.getScore();

        byte isSuccess = successFlag ? REFUND_STATUS_SUCCESS : REFUND_STATUS_FAILED;

        // 记录退款信息
        insertRefundInfo(param, payInfo, isSuccess);

        if(successFlag){
            // 更新订单状态
            updateOrderState(param, finalReturnMoney, finalReturnAccount, finalReturnMemberCardBalance, finalReturnScore);
        }
    }

    private void processRefund(VirtualOrderRefundParam param, VirtualOrderPayInfo payInfo) {
        transaction(() -> {
            if (param.getAccount().compareTo(BigDecimal.ZERO) > 0) {
                /** TODO 余额退款服务 */
                AccountData accountData = AccountData.newBuilder().userId(payInfo.getUserId())
                        .orderSn(payInfo.getOrderSn()).
                // 退款金额
                amount(param.getAccount())
                        .remarkCode(RemarkTemplate.ORDER_VIRTUAL_RETURN.code)
                        .remarkData(String.format(payInfo.getOrderSn()))
                        .payment(OrderConstant.PAY_CODE_BALANCE_PAY).
                // 支付类型
                isPaid(RecordTradeEnum.UACCOUNT_RECHARGE.val()).
                // 后台处理时为操作人id为0
                adminUser(0).
                // 用户余额退款
                tradeType(RecordTradeEnum.TYPE_CRASH_MACCOUNT_REFUND.val()).
                // 资金流量-支出
                tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val()).build();
                // 调用退余额接口
                recordMemberTrade.updateUserEconomicData(accountData);
            }

            if (param.getMemberCardBalance().compareTo(BigDecimal.ZERO) > 0) {

                /**
                 * 交易记录信息
                 */
                TradeOptParam tradeOpt = TradeOptParam
                        .builder()
                        .adminUserId(0)
                        .tradeType(RecordTradeEnum.TYPE_CRASH_MCARD_ACCOUNT_REFUND.val())
                        .tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val())
                        .build();

                /** TODO 会员卡余额退款服务 */
                UserCardData userCardData = UserCardData.newBuilder().userId(payInfo.getUserId())
.cardNo(payInfo.getCardNo()).money(param.getMemberCardBalance()).reasonId(RemarkTemplate.ORDER_VIRTUAL_RETURN_DEFAULT.code).
// 普通会员卡
type(CardConstant.MCARD_TP_NORMAL).orderSn(param.getOrderSn()).
tradeOpt(tradeOpt).chargeType(CardConstant.CHARGE_TYPE_REFUND).build();
                // 调用退会员卡接口
                recordMemberTrade.updateUserEconomicData(userCardData);
            }

            if (param.getScore() > 0) {
                /** TODO 积分退款服务 */
                ScoreData scoreData = ScoreData.newBuilder().userId(payInfo.getUserId()).orderSn(payInfo.getOrderSn()).
                // 退款积分
                score(param.getScore()).
                remarkCode(RemarkTemplate.ORDER_VIRTUAL_RETURN_SCORE_ACCOUNT.code).
                remarkData(payInfo.getOrderSn()+","+param.getScore()).
                //remark(String.format("虚拟订单退款:%s，退积分：%s", payInfo.getOrderSn(), param.getScore())).
                // 后台处理时为操作人id为0
                adminUser(0).
                // 用户余额充值
                tradeType(RecordTradeEnum.TYPE_CRASH_POWER_MACCOUNT.val()).
                // 资金流量-支出
                tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val()).
                // 积分变动是否来自退款
                isFromRefund(NumberUtils.BYTE_ONE).build();
                // 调用退积分接口
                recordMemberTrade.updateUserEconomicData(scoreData);
            }

            if (param.getMoney().compareTo(BigDecimal.ZERO) > 0) {
                /** TODO 微信退款服务 */
                returnMethod.refundVirtualWx(payInfo, param.getMoney());
            }

        });
    }

    private void insertRefundInfo(VirtualOrderRefundParam param, VirtualOrderPayInfo payInfo, byte isSuccess) {
        db().insertInto(VIRTUAL_ORDER_REFUND_RECORD, VIRTUAL_ORDER_REFUND_RECORD.ORDER_SN, VIRTUAL_ORDER_REFUND_RECORD.USER_ID,
            VIRTUAL_ORDER_REFUND_RECORD.MONEY_PAID, VIRTUAL_ORDER_REFUND_RECORD.USE_ACCOUNT, VIRTUAL_ORDER_REFUND_RECORD.USE_SCORE,
            VIRTUAL_ORDER_REFUND_RECORD.MEMBER_CARD_BALANCE,
            VIRTUAL_ORDER_REFUND_RECORD.IS_SUCCESS)
            .values(payInfo.getOrderSn(),payInfo.getUserId(),param.getMoney(),param.getAccount(),param.getScore(),param.getMemberCardBalance(),isSuccess).execute();
    }

    private void updateOrderState(VirtualOrderRefundParam param, BigDecimal finalReturnMoney, BigDecimal finalReturnAccount, BigDecimal finalReturnMemberCardBalance, Integer finalReturnScore) {
        db().update(VIRTUAL_ORDER)
            .set(VIRTUAL_ORDER.RETURN_FLAG, REFUND_STATUS_SUCCESS)
            .set(VIRTUAL_ORDER.RETURN_MONEY, finalReturnMoney)
            .set(VIRTUAL_ORDER.RETURN_ACCOUNT, finalReturnAccount)
            .set(VIRTUAL_ORDER.RETURN_CARD_BALANCE, finalReturnMemberCardBalance)
            .set(VIRTUAL_ORDER.RETURN_SCORE, finalReturnScore)
            .set(VIRTUAL_ORDER.RETURN_TIME, DateUtils.getLocalDateTime())
            .where(VIRTUAL_ORDER.ORDER_SN.eq(param.getOrderSn())).execute();
    }

    private VirtualOrderPayInfo getOrderPayInfo(Integer orderId){
        return db().select(VIRTUAL_ORDER.ORDER_SN,VIRTUAL_ORDER.USER_ID,VIRTUAL_ORDER.PAY_CODE,VIRTUAL_ORDER.PAY_SN,VIRTUAL_ORDER.MONEY_PAID,VIRTUAL_ORDER.USE_ACCOUNT,VIRTUAL_ORDER.MEMBER_CARD_BALANCE,VIRTUAL_ORDER.USE_SCORE,VIRTUAL_ORDER.ORDER_AMOUNT,VIRTUAL_ORDER.PAY_TIME,VIRTUAL_ORDER.RETURN_FLAG,VIRTUAL_ORDER.RETURN_MONEY,VIRTUAL_ORDER.RETURN_ACCOUNT,VIRTUAL_ORDER.RETURN_CARD_BALANCE,VIRTUAL_ORDER.RETURN_SCORE,VIRTUAL_ORDER.CARD_NO,VIRTUAL_ORDER.RETURN_TIME).from(VIRTUAL_ORDER).where(VIRTUAL_ORDER.ORDER_ID.eq(orderId)).and(VIRTUAL_ORDER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).fetchOneInto(VirtualOrderPayInfo.class);
    }

    /**
     * 校验申请退款金额是否大于可退金额
     * @param param
     * @return
     */
    public Boolean checkVirtualOrderRefundParam(VirtualOrderRefundParam param){
        VirtualOrderPayInfo payInfo = getOrderPayInfo(param.getOrderId());
        if(param.getAccount().compareTo(payInfo.getUseAccount().subtract(payInfo.getReturnAccount())) > 0
            || param.getMoney().compareTo(payInfo.getMoneyPaid().subtract(payInfo.getReturnMoney())) > 0
            || param.getMemberCardBalance().compareTo(payInfo.getMemberCardBalance().subtract(payInfo.getReturnCardBalance())) > 0
            || param.getScore() > (payInfo.getUseScore() - payInfo.getReturnScore())){
            return false;
        }else{
            return true;
        }
    }

    public VirtualOrderRecord getInfoByNo(String cardNo) {
    	return  db().selectFrom(VIRTUAL_ORDER).where(VIRTUAL_ORDER.CARD_NO.eq(cardNo)).fetchAny();
    }

    /**
     * 获取虚拟订单最近下单时间
     */
    public Timestamp lastOrderTime(Integer userId) {
        logger().info("获取虚拟订单最近下单时间");
        return db().select(VIRTUAL_ORDER.CREATE_TIME)
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.USER_ID.eq(userId))
            .orderBy(VIRTUAL_ORDER.CREATE_TIME.desc())
            .fetchAnyInto(Timestamp.class);
    }

    /**
     * 图表分析的数据
     *
     * @param param
     * @return
     */
    public AnalysisVo getAnalysisData(AnalysisParam param, Byte goodsType) {
        AnalysisVo analysisVo = new AnalysisVo();
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate == null || endDate == null) {
            startDate = Timestamp.valueOf(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_BEGIN, DateUtils.getLocalDateTime()));
            param.setStartTime(startDate);
            endDate = DateUtils.getLocalDateTime();
            param.setEndTime(endDate);
        }
        Map<Date, List<VirtualOrderAnalysisBo>> orderGoodsMap = getAnalysisOrderMap(param, goodsType);

        Set<Integer> allUserIds = new HashSet<>();
        //填充
        while (Objects.requireNonNull(startDate).compareTo(endDate) <= 0) {
            Date k = new Date(startDate.getTime());
            List<VirtualOrderAnalysisBo> v = orderGoodsMap.get(k);
            Set<Integer> userIds = new HashSet<>();

            if (v != null) {
                /**支付金额 */
                BigDecimal paymentAmount = BigDecimal.ZERO;
                /**退款金额 */
                BigDecimal returnAmount = BigDecimal.ZERO;
                for (VirtualOrderAnalysisBo o : v) {
                    paymentAmount = BigDecimalUtil.add(paymentAmount, o.getOrderAmount());
                    userIds.add(o.getUserId());
                    returnAmount = BigDecimalUtil.addOrSubtrac(
                        BigDecimalUtil.BigDecimalPlus.create(returnAmount, BigDecimalUtil.Operator.add),
                        BigDecimalUtil.BigDecimalPlus.create(o.getReturnMoney(), BigDecimalUtil.Operator.add),
                        BigDecimalUtil.BigDecimalPlus.create(o.getReturnAccount(), BigDecimalUtil.Operator.add),
                        BigDecimalUtil.BigDecimalPlus.create(o.getReturnCardBalance(), BigDecimalUtil.Operator.add));
                }

                analysisVo.getDateList().add(k.toString());
                analysisVo.getPaidOrderNumber().add(v.size());
                analysisVo.getPaidUserNumber().add(userIds.size());
                analysisVo.getPaymentAmount().add(paymentAmount);
                analysisVo.getReturnAmount().add(returnAmount);
            } else {
                analysisVo.getDateList().add(k.toString());
                analysisVo.getPaidOrderNumber().add(0);
                analysisVo.getPaidUserNumber().add(0);
                analysisVo.getPaymentAmount().add(BigDecimal.ZERO);
                analysisVo.getReturnAmount().add(BigDecimal.ZERO);
            }
            allUserIds.addAll(userIds);
            startDate = Util.getEarlyTimeStamp(startDate, 1);
        }


        AnalysisVo.AnalysisTotalVo totalVo = new AnalysisVo.AnalysisTotalVo();
        totalVo.setTotalPaidOrderNumber(analysisVo.getPaidOrderNumber().stream().mapToInt(Integer::intValue).sum());
        totalVo.setTotalPaidUserNumber(allUserIds.size());
        totalVo.setTotalPaymentAmount(analysisVo.getPaymentAmount().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        totalVo.setTotalReturnAmount(analysisVo.getReturnAmount().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        analysisVo.setTotal(totalVo);
        return analysisVo;
    }

    /**
     * 效果要分析的订单数据
     *
     * @param param
     * @return
     */
    protected Map<Date, List<VirtualOrderAnalysisBo>> getAnalysisOrderMap(AnalysisParam param, Byte goodsType) {
        SelectConditionStep<?> select = db().select(DSL.date(VIRTUAL_ORDER.CREATE_TIME).as("createTime"), VIRTUAL_ORDER.ORDER_SN, VIRTUAL_ORDER.USER_ID, VIRTUAL_ORDER.ORDER_AMOUNT, VIRTUAL_ORDER.RETURN_MONEY, VIRTUAL_ORDER.RETURN_ACCOUNT, VIRTUAL_ORDER.RETURN_CARD_BALANCE)
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.CREATE_TIME.between(param.getStartTime(), param.getEndTime()))
            .and(VIRTUAL_ORDER.GOODS_TYPE.eq(goodsType))
            .and(VIRTUAL_ORDER.ORDER_STATUS.eq(ORDER_STATUS_FINISHED));
        if (GOODS_TYPE_COUPON_PACK.equals(goodsType)) {
            //优惠券礼包订单过滤掉免费领取的
            select.and(VIRTUAL_ORDER.ORDER_AMOUNT.gt(BigDecimal.ZERO).or(VIRTUAL_ORDER.USE_SCORE.gt(0)));
        }
        List<VirtualOrderAnalysisBo> list = select.fetchInto(VirtualOrderAnalysisBo.class);
        return list.stream().collect(Collectors.groupingBy(VirtualOrderAnalysisBo::getCreateTime));
    }

    /**
     * 获取用户退款订单统计
     * return <累计退款金额,累计退款订单数>
     */
    public Tuple2<BigDecimal, Integer> getUserReturnOrderStatistics(Integer userId) {
        VirtualOrderRefundRecord localTable = VIRTUAL_ORDER_REFUND_RECORD;
        Record2<Integer, BigDecimal> r = db().select(DSL.countDistinct(localTable.ORDER_SN).as("orderNum"),
            DSL.sum(localTable.MONEY_PAID
                .add(localTable.USE_ACCOUNT)
                .add(localTable.MEMBER_CARD_BALANCE)
            ).as("totalMoneyPaid")
        )
            .from(localTable)
            .where(localTable.IS_SUCCESS.eq(REFUND_STATUS_SUCCESS))
            .and(localTable.USER_ID.eq(userId))
            .fetchAny();
        return new Tuple2<>(r.value2(), r.value1());
    }
}

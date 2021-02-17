package com.meidianyi.shop.service.shop.order.store;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.StoreOrder;
import com.meidianyi.shop.db.shop.tables.records.PaymentRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreOrderRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.IncrSequenceUtil;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountParam;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConsumpData;
import com.meidianyi.shop.service.pojo.shop.member.card.ScoreJson;
import com.meidianyi.shop.service.pojo.shop.member.order.UserOrderBean;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.invoice.InvoiceVo;
import com.meidianyi.shop.service.pojo.shop.order.store.StoreOrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.store.StoreOrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.store.StoreOrderPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.pojo.wxapp.store.StoreConstant;
import com.meidianyi.shop.service.pojo.wxapp.store.StoreOrderTran;
import com.meidianyi.shop.service.pojo.wxapp.store.StorePayOrderInfo;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.member.*;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.payment.PaymentService;
import com.meidianyi.shop.service.shop.store.service.ServiceOrderService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.db.shop.tables.Invoice.INVOICE;
import static com.meidianyi.shop.db.shop.tables.MemberCard.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.tables.Store.STORE;
import static com.meidianyi.shop.db.shop.tables.StoreOrder.STORE_ORDER;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserCard.USER_CARD;
import static com.meidianyi.shop.db.shop.tables.UserScoreSet.USER_SCORE_SET;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.CONDITION_ONE;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.CONDITION_ZERO;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_TP_NORMAL;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.NO_USE_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.payment.PayCode.*;
import static com.meidianyi.shop.service.pojo.wxapp.store.StoreConstant.*;
import static com.meidianyi.shop.service.shop.member.ScoreCfgService.BUY;
import static com.meidianyi.shop.service.shop.member.ScoreCfgService.BUY_EACH;
import static java.math.BigDecimal.*;
import static org.apache.commons.lang3.math.NumberUtils.*;

/**
 * Table:TABLE
 *
 * @author 王帅
 */
@Slf4j
@Service
public class StoreOrderService extends ShopBaseService {
    /**
     * 门店
     */
    @Autowired
    public StoreService store;

    /**
     * The Order service.
     */
    @Autowired
    public ServiceOrderService orderService;

    /**
     * The Member card service.会员卡
     */
    @Autowired
    public MemberCardService memberCardService;

    /**
     * The Score service.积分
     */
    @Autowired
    public ScoreService scoreService;

    /**
     * The Score cfg service.积分配置
     */
    @Autowired
    public ScoreCfgService scoreCfgService;
    /**
     * The User card dao service.
     */
    @Autowired
    public UserCardDaoService userCardDaoService;

    /**
     * The Payment service.
     */
    @Autowired
    @Lazy
    public PaymentService paymentService;

    /**
     * The Account service.余额管理
     */
    @Autowired
    public AccountService accountService;

    /**
     * The Trade service.交易服务配置
     */
    @Autowired
    public TradeService tradeService;

    @Autowired
    public BaseScoreCfgService baseScoreCfgService;

    public final StoreOrder TABLE = STORE_ORDER;
    public static final BigDecimal HUNDRED = new BigDecimal(100);

    /**
     * 买单订单综合查询
     *
     * @param param
     * @return
     */
    public PageResult<StoreOrderListInfoVo> getPageList(StoreOrderPageListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(TABLE.ORDER_ID, TABLE.ORDER_SN, TABLE.ORDER_STATUS, TABLE.STORE_ID, TABLE.PAY_TIME, TABLE.MONEY_PAID, TABLE.PAY_CODE, TABLE.PAY_NAME, USER.USERNAME)
            .from(TABLE).leftJoin(USER)
            .on(USER.USER_ID.eq(TABLE.USER_ID));
        buildOptionsStore(select, param);
        PageResult<StoreOrderListInfoVo> result = getPageResult(select, param.getCurrentPage(), param.getPageRows(), StoreOrderListInfoVo.class);
        return result;
    }

    /**
     * 构造买单订单查询条件
     *
     * @param select
     * @param param
     * @return
     */
    public SelectWhereStep<?> buildOptionsStore(SelectWhereStep<?> select, StoreOrderPageListQueryParam param) {
        //自增id排序
        select.orderBy(TABLE.ORDER_ID);

        select.where(TABLE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));

        select.where(TABLE.ORDER_STATUS.in(OrderConstant.STORE_STATUS_PAY, OrderConstant.STORE_STATUS_RETURN));

        if (!StringUtils.isEmpty(param.getOrderSn())) {
            select.where(TABLE.ORDER_SN.like(param.getOrderSn()));
        }
        if (param.getUserName() != null) {
            select.where(USER.USERNAME.like(likeValue(param.getUserName())));
        }
        if (param.getPayTimeStart() != null) {
            select.where(TABLE.PAY_TIME.ge(param.getPayTimeStart()));
        }
        if (param.getPayTimeEnd() != null) {
            select.where(TABLE.PAY_TIME.le(param.getPayTimeEnd()));
        }
        if (param.getStoreId() != null) {
            select.where(TABLE.STORE_ID.eq(param.getStoreId()));
        }
        if (param.getOrderStatus() != null && param.getOrderStatus().length != 0) {
            select.where(TABLE.ORDER_STATUS.in(param.getOrderStatus()));
        }
        return select;
    }

    public StoreOrderInfoVo get(String orderSn) {
        return db().select(TABLE.ORDER_ID, TABLE.ORDER_SN, TABLE.ORDER_STATUS, TABLE.STORE_ID, TABLE.PAY_TIME, TABLE.MONEY_PAID, TABLE.PAY_CODE, TABLE.PAY_NAME,
            TABLE.MEMBER_CARD_BALANCE, TABLE.MEMBER_CARD_REDUCE, TABLE.SCORE_DISCOUNT, TABLE.USE_ACCOUNT
            , TABLE.ORDER_AMOUNT, TABLE.MONEY_PAID, TABLE.ADD_MESSAGE, TABLE.CURRENCY,
            USER.USERNAME,
            STORE.STORE_NAME, STORE.PROVINCE_CODE, STORE.CITY_CODE, STORE.DISTRICT_CODE, STORE.ADDRESS, STORE.LATITUDE, STORE.LONGITUDE,
            INVOICE.TYPE, INVOICE.TITLE, INVOICE.TAXNUMBER.as("taxNumber"), INVOICE.COMPANYADDRESS.as("companyAddress"))
            .from(TABLE)
            .leftJoin(USER).on(USER.USER_ID.eq(TABLE.USER_ID))
            .leftJoin(STORE).on(STORE.STORE_ID.eq(TABLE.STORE_ID))
            .leftJoin(INVOICE).on(INVOICE.USER_ID.eq(TABLE.USER_ID))
            .where(TABLE.ORDER_SN.eq(orderSn))
            .fetchOneInto(StoreOrderInfoVo.class);
    }

    /**
     * Fetch store order store order record.
     *
     * @param orderSn the order sn
     * @return the store order record
     */
    public StoreOrderRecord fetchStoreOrder(String orderSn) {
        return db().selectFrom(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetchOneInto(TABLE);
    }

    /**
     * 生成门店买单订单号
     */
    public String generateOrderSn() {
        return IncrSequenceUtil.generateOrderSn(STORE_ORDER_SN_PREFIX);
    }

    /**
     * Check before create store order tran.
     *
     * @param userInfo    the user info
     * @param invoiceInfo the invoice info
     * @param orderInfo   the order info
     * @return the store order tran
     */
    public StoreOrderTran checkBeforeCreate(UserRecord userInfo, InvoiceVo invoiceInfo, StorePayOrderInfo orderInfo) {
        // 订单编号
        String orderSn = generateOrderSn();
        log.debug("门店买单订单编号:{}", orderSn);
        StorePojo storePojo = store.getStore(orderInfo.getStoreId());
        Objects.requireNonNull(storePojo, "店铺不存在");
        if (storePojo.getDelFlag().equals(BYTE_ONE)) {
            // 该门店已删除
            throw new BusinessException(JsonResultCode.CODE_STORE_ALREADY_DEL);
        }
        // 订单金额
        BigDecimal orderAmount = orderInfo.getOrderAmount();
        // 应付金额
        BigDecimal moneyPaid = orderAmount;
        // 实际支付金额（支付金额=微信支付+会员卡余额支付+余额支付+积分支付）
        BigDecimal payAmount = ZERO;
        // 会员卡余额抵扣金额
        BigDecimal cardAmount = orderInfo.getCardAmount();
        // 会员卡折扣抵扣金额
        BigDecimal cardDisAmount = orderInfo.getCardDisAmount();
        // 积分抵扣金额
        BigDecimal scoreAmount = orderInfo.getScoreAmount();
        // 余额抵扣金额
        BigDecimal balanceAmount = orderInfo.getBalanceAmount();
        AccountParam accountParam = null;
        CardConsumpData cardConsumpData = null;
        ScoreParam scoreParam = null;
        String cardNo = orderInfo.getCardNo();
        // todo 目前门店买单只支持普通会员卡：cardType=0，会员卡折扣和余额均可使用
        if (org.apache.commons.lang3.StringUtils.isNotBlank(cardNo)) {
            // 验证会员卡有效性
            if (!userCardDaoService.checkStoreValidCard(userInfo.getUserId(), orderInfo.getStoreId(), cardNo)) {
                log.error("会员卡【{}】无效", cardNo);
                throw new BusinessException(JsonResultCode.CODE_ORDER_CARD_INVALID);
            }
            Record2<BigDecimal, BigDecimal> record2 = userCardDaoService.getCardBalAndDis(cardNo);
            // 会员卡折扣
            BigDecimal discount = record2.getValue(MEMBER_CARD.DISCOUNT);
            // 会员卡余额
            BigDecimal money = record2.getValue(USER_CARD.MONEY);
            if (BigDecimalUtil.greaterThanZero(discount)) {
                // 计算会员卡折扣金额
                BigDecimal cardDisAm = orderAmount.multiply((ONE.subtract(discount.divide(TEN, 2, RoundingMode.DOWN))));
                if (cardDisAm.compareTo(cardDisAmount) != 0) {
                    log.debug("会员卡折扣抵扣金额【{}】计算有误【前端计算结果为：{}】", cardDisAm, cardDisAmount);
                    throw new BusinessException(JsonResultCode.CODE_FAIL);
                }
                log.debug("会员卡折扣金额:{}", cardDisAmount);
                moneyPaid = moneyPaid.subtract(cardDisAmount).setScale(2, RoundingMode.UP);
            }
            payAmount = moneyPaid;
            // 会员卡余额抵扣金额
            if (BigDecimalUtil.greaterThanZero(cardAmount)) {
                if (cardAmount.compareTo(money) > 0) {
                    // 会员卡余额不足
                    log.error("会员卡余额[{}]不足(实际抵扣金额[{}])，无法下单", money, cardAmount);
                    throw new BusinessException(JsonResultCode.CODE_USER_CARD_BALANCE_INSUFFICIENT);
                }
                // 增加会员卡消费记录
                UserCardParam userCardParam = userCardDaoService.getUserCardInfo(cardNo);
                // 创建会员卡消费变动事件
                cardConsumpData = new CardConsumpData()
                    .setUserId(userInfo.getUserId())
                    // 会员卡更新金额，区分正负号，这里是负号，意为扣减
                    .setMoney(orderInfo.getCardAmount().negate())
                    .setCardNo(cardNo)
                    .setCardId(userCardParam.getCardId())
                    .setReasonId(RemarkTemplate.STORE_PAYMEMBT.code)
                    .setReason(RemarkTemplate.getMessageByCode(RemarkTemplate.STORE_PAYMEMBT.code))
                    // 消费类型 :门店只支持普通会员卡：cardType=0
                    .setType(MCARD_TP_NORMAL);
                log.debug("会员卡余额抵扣金额:{}", cardAmount);
                moneyPaid = moneyPaid.subtract(cardAmount).setScale(2, RoundingMode.UP);
            }
        } else {
            payAmount = moneyPaid;
        }
        // 余额抵扣金额
        InnerBalanceReduce innerBalanceReduce = new InnerBalanceReduce(userInfo, orderSn, moneyPaid, balanceAmount, accountParam).invoke();
        moneyPaid = innerBalanceReduce.getMoneyPaid();
        accountParam = innerBalanceReduce.getAccountParam();

        // 积分抵扣金额(积分数除以100就是积分抵扣金额数)todo 积分润换比暂时为固定的100积分=1元RMB，后续会改为可配置参数
        InnerScoreReduce innerScoreReduce = new InnerScoreReduce(userInfo, orderSn, moneyPaid, payAmount, scoreAmount, scoreParam).invoke();
        moneyPaid = innerScoreReduce.getMoneyPaid();
        scoreParam = innerScoreReduce.getScoreParam();
        // 应付金额
        log.debug("应付金额:{}", moneyPaid);
        if (Objects.isNull(orderInfo.getMoneyPaid()) || orderInfo.getMoneyPaid().compareTo(moneyPaid) != 0) {
            // 应付金额计算错误
            log.debug("应付金额【{}】计算有误【前端计算结果为：{}】", moneyPaid, orderInfo.getMoneyPaid());
            throw new BusinessException(JsonResultCode.CODE_AMOUNT_PAYABLE_CALCULATION_FAILED);
        }

        StoreOrderRecord orderRecord = getStoreOrderRecord(userInfo, invoiceInfo, orderInfo, orderSn, orderAmount,
            moneyPaid, cardAmount, cardDisAmount, scoreAmount, balanceAmount, cardNo);

        return StoreOrderTran.builder()
            .account(accountParam)
            .cardConsumpData(cardConsumpData)
            .scoreParam(scoreParam)
            .storeOrder(orderRecord)
            .build();
    }

    private StoreOrderRecord getStoreOrderRecord(UserRecord userInfo, InvoiceVo invoiceInfo, StorePayOrderInfo orderInfo, String orderSn, BigDecimal orderAmount, BigDecimal moneyPaid, BigDecimal cardAmount, BigDecimal cardDisAmount, BigDecimal scoreAmount, BigDecimal balanceAmount, String cardNo) {
        StoreOrderRecord orderRecord = new StoreOrderRecord();
        orderRecord.setStoreId(orderInfo.getStoreId());
        orderRecord.setOrderSn(orderSn);
        orderRecord.setUserId(userInfo.getUserId());
        orderRecord.setOrderStatus(WAIT_TO_PAY);
        orderRecord.setOrderStatusName(WAIT_TO_PAY_NAME);
        if (Objects.nonNull(invoiceInfo)) {
            orderRecord.setInvoiceId(Objects.nonNull(invoiceInfo.getId()) ? invoiceInfo.getId() : INTEGER_ZERO);
            orderRecord.setInvoiceDetail(Util.toJson(invoiceInfo));
        }
        orderRecord.setAddMessage(Objects.nonNull(orderInfo.getRemark()) ? orderInfo.getRemark() : StringUtils.EMPTY);
        orderRecord.setPayCode(moneyPaid.compareTo(ZERO) > 0 ? PAY_CODE_WX_PAY : PAY_CODE_BALANCE_PAY);
        orderRecord.setPayName(paymentService.getPaymentInfo(orderRecord.getPayCode()).getPayName());
        orderRecord.setMoneyPaid(moneyPaid);
        orderRecord.setMemberCardNo(Objects.nonNull(cardNo) ? cardNo : StringUtils.EMPTY);
        orderRecord.setMemberCardRedunce(BigDecimalUtil.null2Zero(cardDisAmount));
        orderRecord.setMemberCardBalance(BigDecimalUtil.null2Zero(cardAmount));
        orderRecord.setScoreDiscount(BigDecimalUtil.null2Zero(scoreAmount));
        orderRecord.setUseAccount(BigDecimalUtil.null2Zero(balanceAmount));
        orderRecord.setOrderAmount(orderAmount);
        orderRecord.setSellerRemark(StringUtils.EMPTY);
        orderRecord.setStarFlag(BYTE_ZERO);
        orderRecord.setDelFlag(BYTE_ZERO);
        orderRecord.setCardNo(Objects.nonNull(cardNo) ? cardNo : StringUtils.EMPTY);
        orderRecord.setAliTradeNo(StringUtils.EMPTY);
        orderRecord.setCurrency(saas().shop.getCurrency(getShopId()));
        return orderRecord;
    }

    /**
     * Create store order.创建门店买单订单
     *
     * @param param the param
     * @return the string
     */
    public String createStoreOrder(StoreOrderTran param) {
        AccountParam account = param.getAccount();
        CardConsumpData userCard = param.getCardConsumpData();
        ScoreParam scoreParam = param.getScoreParam();
        StoreOrderRecord order = param.getStoreOrder();
        // 创建门店买单订单记录
        db().executeInsert(order);
        // 创建余额，会员卡，积分消费记录
        if (Objects.nonNull(account)) {
            try {
                TradeOptParam tradeOpt = TradeOptParam.builder()
                    .adminUserId(INTEGER_ZERO)
                    .tradeType(RecordTradeEnum.TYPE_CRASH_ACCOUNT_PAY.val())
                    .tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val())
                    .build();
                accountService.updateUserAccount(account, tradeOpt);
            } catch (MpException e) {
                log.debug("余额抵扣失败：{}", e.getMessage());
                throw new BusinessException(JsonResultCode.CODE_FAIL);
            }
            log.debug("用户余额[{}]抵扣(实际抵扣金额[{}])成功!", account.getAccount(), order.getUseAccount());
        }
        if (Objects.nonNull(userCard)) {
            TradeOptParam optParam = TradeOptParam.builder()
                .tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val())
                .tradeType(RecordTradeEnum.TYPE_CRASH_MEMBER_CARD_PAY.val())
                .tradeSn(order.getOrderSn()).adminUserId(INTEGER_ZERO).build();
            try {
                memberCardService.updateMemberCardAccount(userCard, optParam);
            } catch (MpException e) {
                log.debug("会员卡余额抵扣失败：{}", e.getMessage());
                throw new BusinessException(JsonResultCode.CODE_FAIL);
            }
            log.debug("会员卡余额抵扣(实际抵扣金额[{}])成功!", userCard.getMoney());
        }
        if (Objects.nonNull(scoreParam)) {
            try {
                scoreService.updateMemberScore(scoreParam, INTEGER_ZERO, RecordTradeEnum.TYPE_SCORE_PAY.val(), RecordTradeEnum.TRADE_FLOW_OUT.val());
            } catch (MpException e) {
                log.debug("积分抵扣失败：{}", e.getMessage());
                throw new BusinessException(JsonResultCode.CODE_FAIL);
            }
            log.debug("积分抵扣(实际抵扣金额[{}])成功!", scoreParam.getScore());
        }
        return order.getOrderSn();
    }

    /**
     * Update prepay id by order sn.更新微信支付id
     *
     * @param orderSn  the order sn
     * @param prepayId the prepay id
     */
    public void updatePrepayIdByOrderSn(String orderSn, String prepayId) {
        db().update(TABLE).set(TABLE.PREPAY_ID, prepayId).where(TABLE.ORDER_SN.eq(orderSn)).execute();
    }

    /**
     * Update record.门店订单更新
     *
     * @param condition   the condition 更新条件
     * @param orderRecord the order record 更新内容
     */
    public void updateRecord(Condition condition, StoreOrderRecord orderRecord) {
        db().update(TABLE).set(orderRecord).where(condition).execute();
    }

    /**
     * Send score after pay done.支付完成送积分
     *
     * @param orderInfo the order info 门店订单信息
     */
    public void sendScoreAfterPayDone(StoreOrderRecord orderInfo) {
        String cardNo = orderInfo.getMemberCardNo();
        // 最终赠送积分值,为零不赠送
        int sendScore = 0;
        // 会员卡送积分逻辑
        if (org.apache.commons.lang3.StringUtils.isNotBlank(cardNo)) {
            log.debug("会员卡买单赠送积分");
            // 获取会员卡购物送积分策略json数据,例如: {"offset":0,"goodsMoney":[100,200],"getScores":[1000,2000],"perGoodsMoney":1000,"perGetScores":2000}
            String buyScoreConfig = memberCardService.getSendScoreStrategy(cardNo);
            // 策略为空,不赠送积分
            if (org.apache.commons.lang3.StringUtils.isBlank(buyScoreConfig)) {
                log.debug("会员卡赠送积分策略为空，不送积分！");
                return;
            }
            ScoreJson scoreJson = Util.json2Object(buyScoreConfig, ScoreJson.class, false);
            BigDecimal totalMoney = orderInfo.getMoneyPaid().add(orderInfo.getUseAccount()).add(orderInfo.getMemberCardRedunce()).setScale(2, RoundingMode.DOWN);
            //0：购物满多少送多少积分；1：购物每满多少送多少积分
            if (BYTE_ONE.equals(scoreJson.getOffset())) {
                if (scoreJson.getPerGetScores().compareTo(ZERO) > 0 && scoreJson.getPerGoodsMoney().compareTo(ZERO) > 0) {
                    sendScore = totalMoney.divide(scoreJson.getPerGoodsMoney(), 0, RoundingMode.DOWN).multiply(scoreJson.getPerGetScores()).intValue();
                    log.info("支付完成送积分:会员卡[{}],每满[{}]元,送[{}]积分;订单金额[{}],赠送积分[{}]", cardNo, scoreJson.getGoodsMoney(), scoreJson.getPerGetScores(), totalMoney, sendScore);
                }
            } else if (BYTE_ZERO.equals(scoreJson.getOffset())) {
                BigDecimal fullMoney = scoreJson.getGoodsMoney().stream().filter(e -> e.compareTo(totalMoney) < 0).max(Comparators.comparable()).orElse(BIGDECIMAL_ZERO);
                int index = scoreJson.getGoodsMoney().indexOf(fullMoney);
                sendScore = index < scoreJson.getGetScores().size() ? scoreJson.getGetScores().get(index).intValue() : INTEGER_ZERO;
                log.info("支付完成送积分:会员卡[{}],满[{}]元,送[{}]积分;订单金额[{}],赠送积分[{}]", cardNo, scoreJson.getGoodsMoney(), scoreJson.getPerGetScores(), totalMoney, sendScore);
            }
            // 送积分
            giftScore(orderInfo.getOrderSn(), sendScore, orderInfo.getUserId());
        } else {
            log.debug("非会员卡/普通买单赠送积分");
            // 非会员卡送积分逻辑
            BigDecimal totalMoney = orderInfo.getMoneyPaid().add(orderInfo.getUseAccount()).setScale(2, RoundingMode.DOWN);
//            购物送积分类型： 0： 购物满；1：购物每满
            byte scoreType = scoreCfgService.getScoreType();
//            购物满
            if (scoreType == CONDITION_ZERO) {
                Result<Record2<String, String>> record2s = scoreCfgService.getValFromUserScoreSet(BUY, totalMoney.toString());
                if(record2s != null){
                    // 满...金额
                    String setVal = record2s.getValue(0, USER_SCORE_SET.SET_VAL);
                    // 送...积分
                    String setVal2 = record2s.getValue(0, USER_SCORE_SET.SET_VAL2);
                    if (org.apache.commons.lang3.StringUtils.isBlank(setVal2)) {
                        return;
                    }
                    sendScore = Integer.parseInt(setVal2);
                    log.info("支付完成送积分:非会员卡满[{}]元,送[{}]积分;订单金额[{}],赠送积分[{}]", setVal, setVal2, totalMoney, sendScore);
                }
            } else if (scoreType == CONDITION_ONE) {
//                购物每满
                Result<Record2<String, String>> record2s = scoreCfgService.getValFromUserScoreSet(BUY_EACH);
                if(record2s != null){
                    // 每满...金额
                    String setVal = record2s.getValue(0, USER_SCORE_SET.SET_VAL);
                    // 送...积分
                    String setVal2 = record2s.getValue(0, USER_SCORE_SET.SET_VAL2);
                    if (org.apache.commons.lang3.StringUtils.isBlank(setVal) || org.apache.commons.lang3.StringUtils.isBlank(setVal2)) {
                        return;
                    }
                    sendScore = totalMoney.divide(NumberUtils.createBigDecimal(setVal), 0, RoundingMode.DOWN).multiply(NumberUtils.createBigDecimal(setVal2)).intValue();
                    log.info("支付完成送积分:非会员卡每满[{}]元,送[{}]积分;订单金额[{}],赠送积分[{}]", setVal, setVal2, totalMoney, sendScore);
                }

            } else {
                return;
            }
            giftScore(orderInfo.getOrderSn(), sendScore, orderInfo.getUserId());
        }
    }

    /**
     * Gift score.赠送积分
     *
     * @param orderSn the order sn订单编号
     * @param score   the score积分值
     * @param userId  the user id用户id
     *                {@value com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant#NO_USE_SCORE_STATUS}
     */
    public void giftScore(String orderSn, Integer score, Integer... userId) {
        if (score <= 0) {
            return;
        }
        try {
            scoreService.updateMemberScore(
                new ScoreParam() {
                    {
                        setUserId(userId[0]);
                        setScore(score);
                        setScoreStatus(NO_USE_SCORE_STATUS);
                        setDesc(RemarkTemplate.getMessageByCode(RemarkTemplate.ORDER_STORE_SCORE.code));
                        setOrderSn(orderSn);
                        setRemarkCode(RemarkTemplate.ORDER_STORE_SCORE.code);
                    }
                },
                INTEGER_ZERO,
                BYTE_ONE,
                BYTE_ZERO);
        } catch (MpException e) {
            log.error("门店买单支付赠送积分失败,原因如下:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Store pay 2 send score.支付完成送积分; 门店买单支付是否返送积分开关 on 1
     *
     * @param orderInfo the order info
     */
    @Async
    public void storePay2SendScore(StoreOrderRecord orderInfo) {
        if (BYTE_ONE.equals(scoreCfgService.getStoreScore())) {
            sendScoreAfterPayDone(orderInfo);
        }
    }

    /**
     * Finish pay callback.
     *
     * @param storeOrder the store order
     * @param payment    the payment
     */
    public void finishPayCallback(StoreOrderRecord storeOrder, PaymentRecordRecord payment) {
        // WxPayException
        PaymentVo paymentVo = paymentService.getPaymentInfo(payment.getPayCode());
        updateRecord(STORE_ORDER.ORDER_SN.eq(storeOrder.getOrderSn()), new StoreOrderRecord() {{
            setPayTime(Timestamp.valueOf(LocalDateTime.now()));
            setOrderStatus(PAY_SUCCESS);
            setOrderStatusName(PAY_SUCCESS_NAME);
            setPaySn(payment.getPaySn());
            setPayCode(payment.getPayCode());
            setPayName(Objects.nonNull(paymentVo) ? paymentVo.getPayName() : StringUtils.EMPTY);
        }});
    }

    /**
     * 获取用户门店买单信息
     */
    public UserOrderBean getUserOrderStatistics(Integer userId) {
        logger().info("获取用户门店买单信息");
        Record1<Timestamp> createTime = db().select(TABLE.CREATE_TIME).from(TABLE)
            .where(TABLE.ORDER_STATUS.ge(StoreConstant.PAY_SUCCESS))
            .and(TABLE.USER_ID.eq(userId))
            .orderBy(TABLE.CREATE_TIME.desc())
            .fetchAny();
        if (createTime != null) {
            UserOrderBean userOrderBean = db().select(DSL.count(TABLE.ORDER_ID).as("orderNum"),
                DSL.sum(TABLE.MONEY_PAID.add(TABLE.USE_ACCOUNT).add(TABLE.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(TABLE)
                .where(TABLE.ORDER_STATUS.ge(StoreConstant.PAY_SUCCESS))
                .and(TABLE.USER_ID.eq(userId))
                .fetchAnyInto(UserOrderBean.class);
            userOrderBean.setLastOrderTime(createTime.value1());
            userOrderBean.setUnitPrice(BigDecimalUtil.divide(userOrderBean.getTotalMoneyPaid(), new BigDecimal(userOrderBean.getOrderNum())));
            return userOrderBean;
        }
        return new UserOrderBean();
    }

    /**
     * 获取用户的门店买单退款订单信息
     * return <累计退款金额,累计退款订单数>
     */
    public Tuple2<BigDecimal, Integer> getUserReturnOrderStatistics(Integer userId) {
        logger().info("获取用户的门店服务退款订单信息");
        // TODO 等待门店买单退款业务功能添加  service_order_refund

        return new Tuple2<>(BigDecimal.ZERO, 0);
    }

    private class InnerScoreReduce {
        private UserRecord userInfo;
        private String orderSn;
        private BigDecimal moneyPaid;
        private BigDecimal payAmount;
        private BigDecimal scoreAmount;
        private ScoreParam scoreParam;

        public InnerScoreReduce(UserRecord userInfo, String orderSn, BigDecimal moneyPaid, BigDecimal payAmount, BigDecimal scoreAmount, ScoreParam scoreParam) {
            this.userInfo = userInfo;
            this.orderSn = orderSn;
            this.moneyPaid = moneyPaid;
            this.payAmount = payAmount;
            this.scoreAmount = scoreAmount;
            this.scoreParam = scoreParam;
        }

        public BigDecimal getMoneyPaid() {
            return moneyPaid;
        }

        public ScoreParam getScoreParam() {
            return scoreParam;
        }

        public InnerScoreReduce invoke() {
            // 积分抵扣金额(积分数除以100就是积分抵扣金额数)todo 积分润换比暂时为固定的100积分=1元RMB，后续会改为可配置参数
            if (BigDecimalUtil.greaterThanZero(scoreAmount)) {
                if (!tradeService.paymentIsEnabled(PAY_CODE_SCORE_PAY)) {
                    log.error("未开启积分支付");
                    throw new BusinessException(JsonResultCode.CODE_ORDER_PAY_WAY_NO_SUPPORT_SCORE);
                }
                //积分兑换比
                int scoreProportion = baseScoreCfgService.getScoreProportion();
                BigDecimal proportion = BigDecimal.valueOf(scoreProportion);
                // 积分使用上下限限制
                int scoreValue = scoreAmount.multiply(proportion).intValue();
                // 积分下限开关（0： 不限制使用下限值；1：限制）
                byte scorePayLimitSwitch = baseScoreCfgService.getScorePayLimit();
                if (scorePayLimitSwitch != 0) {
                    if (scoreValue < baseScoreCfgService.getScorePayNum()) {
                        log.debug("低于积分使用下限配置，不可使用积分支付");
                        throw new BusinessException(JsonResultCode.CODE_STORE_PAY_LOWER_SCORE_DOWN_CONFIG);
                    }
                }
                BigDecimal ratio = BigDecimal.valueOf(baseScoreCfgService.getScoreDiscountRatio()).divide(HUNDRED);
                if (scoreAmount.compareTo(payAmount.multiply(ratio)) > INTEGER_ZERO) {
                    log.debug("超过积分使用上限配置，不可使用积分支付");
                    throw new BusinessException(JsonResultCode.CODE_STORE_PAY_HIGHER_SCORE_UP_CONFIG);
                }
                if (scoreValue > userInfo.getScore()) {
                    // 积分不足，无法下单
                    log.error("积分[{}]不足(实际抵扣金额[{}]，金额积分兑换比为1:{})，无法下单", userInfo.getScore(), scoreAmount, scoreProportion);
                    throw new BusinessException(JsonResultCode.CODE_SCORE_INSUFFICIENT);
                }
                scoreParam = new ScoreParam() {{
                    setScoreDis(userInfo.getScore());
                    setUserId(userInfo.getUserId());
                    // 积分变动数额
                    setScore(-scoreValue);
                    setOrderSn(orderSn);
                    setRemarkCode(RemarkTemplate.STORE_PAYMEMBT.code);
                }};
                log.debug("积分抵扣金额:{}", scoreAmount);
                moneyPaid = moneyPaid.subtract(scoreAmount).setScale(2, RoundingMode.UP);
            }
            return this;
        }
    }

    private class InnerBalanceReduce {
        private UserRecord userInfo;
        private String orderSn;
        private BigDecimal moneyPaid;
        private BigDecimal balanceAmount;
        private AccountParam accountParam;

        public InnerBalanceReduce(UserRecord userInfo, String orderSn, BigDecimal moneyPaid, BigDecimal balanceAmount, AccountParam accountParam) {
            this.userInfo = userInfo;
            this.orderSn = orderSn;
            this.moneyPaid = moneyPaid;
            this.balanceAmount = balanceAmount;
            this.accountParam = accountParam;
        }

        public BigDecimal getMoneyPaid() {
            return moneyPaid;
        }

        public AccountParam getAccountParam() {
            return accountParam;
        }

        public InnerBalanceReduce invoke() {
            if (BigDecimalUtil.greaterThanZero(balanceAmount)) {
                if (!tradeService.paymentIsEnabled(PAY_CODE_BALANCE_PAY)) {
                    log.error("未开启余额支付");
                    throw new BusinessException(JsonResultCode.CODE_ORDER_PAY_WAY_NO_SUPPORT_ACCOUNT);
                }
                if (balanceAmount.compareTo(userInfo.getAccount()) > 0) {
                    // 余额不足，无法下单
                    log.error("用户余额[{}]不足(实际抵扣金额[{}])，无法下单", userInfo.getAccount(), balanceAmount);
                    throw new BusinessException(JsonResultCode.CODE_BALANCE_INSUFFICIENT);
                }
                // 创建会员余额变动事件
                accountParam = new AccountParam() {{
                    setAccount(userInfo.getAccount());
                    setUserId(userInfo.getUserId());
                    // 更新金额  区分正负号， 这里置为负号，表示扣减
                    setAmount(balanceAmount.negate());
                    setOrderSn(orderSn);
                    setPayment(PAY_CODE_BALANCE_PAY);
                    // 支付类型，0：充值，1：消费
                    setIsPaid(BYTE_ONE);
                    setRemarkId(RemarkTemplate.STORE_PAYMEMBT.code);
                }};
                log.debug("余额抵扣金额:{}", balanceAmount);
                moneyPaid = moneyPaid.subtract(balanceAmount).setScale(2, RoundingMode.UP);
            }
            return this;
        }
    }
}

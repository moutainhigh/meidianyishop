package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PayAwardPrizeRecord;
import com.meidianyi.shop.db.shop.tables.records.PayAwardRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.PrizeRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueBo;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardContentBo;
import com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardVo;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountParam;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.GoodsActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.PayAwardPromotion;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.shop.coupon.CouponGiveService;
import com.meidianyi.shop.service.shop.market.payaward.PayAwardRecordService;
import com.meidianyi.shop.service.shop.market.payaward.PayAwardService;
import com.meidianyi.shop.service.shop.market.prize.PrizeRecordService;
import com.meidianyi.shop.service.shop.member.AccountService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.*;
import static com.meidianyi.shop.db.shop.Tables.PAY_AWARD_RECORD;
import static com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant.COUPON_GIVE_SOURCE_PAY_AWARD;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.GIVE_TYPE_BALANCE;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.GIVE_TYPE_CUSTOM;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.GIVE_TYPE_GOODS;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.GIVE_TYPE_LOTTERY;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.GIVE_TYPE_NO_PRIZE;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.GIVE_TYPE_ORDINARY_COUPON;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.GIVE_TYPE_SCORE;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.GIVE_TYPE_SPLIT_COUPON;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.PAY_AWARD_GIVE_STATUS_NO_STOCK;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.PAY_AWARD_GIVE_STATUS_RECEIVED;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.PAY_AWARD_GIVE_STATUS_UNRECEIVED;
import static com.meidianyi.shop.service.pojo.shop.market.payaward.PayAwardConstant.REDIS_PAY_AWARD_JOIN_COUNT;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.NO_USE_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.TRADE_FLOW_IN;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.TYPE_CRASH_PAY_AWARD;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.TYPE_SCORE_PAY_AWARD;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.UACCOUNT_RECHARGE;
import static com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate.PAY_HAS_GIFT;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.ORDER_WAIT_DELIVERY;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.PAY_CODE_COD;
import static com.meidianyi.shop.service.pojo.shop.payment.PayCode.PAY_CODE_BALANCE_PAY;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_SOURCE_PAY_AWARD;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * 支付有礼活动 下单
 *
 * @author 孔德成
 * @date 2019/12/18 10:56
 */
@Service
public class PayAwardProcessor extends ShopBaseService implements Processor, CreateOrderProcessor,GoodsDetailProcessor {

    @Autowired
    private PayAwardService payAwardService;
    @Autowired
    private PayAwardRecordService payAwardRecordService;
    @Autowired
    private CouponGiveService couponGiveService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private PrizeRecordService prizeRecordService;
    @Autowired
    private JedisManager jedisManager;
    @Autowired
    private AtomicOperation atomicOperation;
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_BARGAIN_PRIORITY;
    }
    @Override
    public Byte getActivityType() {
        return ACTIVITY_TYPE_PAY_AWARD;
    }

    /*****************商品详情处理*******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        PayAwardPromotion payAwardPromotionInfo = payAwardService.getPayAwardPromotionInfo(capsule.getGoodsId(), capsule.getCatId(), capsule.getSortId(), DateUtils.getLocalDateTime());
        if (payAwardPromotionInfo == null) {
            return;
        }
        capsule.getPromotions().put(BaseConstant.ACTIVITY_TYPE_PAY_AWARD,Collections.singletonList(payAwardPromotionInfo));
    }

    /**
     * @param param
     * @throws MpException
     */
    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {

    }

    /**
     * 订单确定后调用
     * 判断保存支付有礼记录
     *
     * @param param
     * @param order
     * @throws MpException
     */
    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    /**
     * 发送奖品
     *
     * @param canSendAwardFlag
     * @param order
     * @param payAward
     * @param payAwardContentBo
     * @param payAwardRecordRecord
     * @throws MpException
     */
    private void sendAward(boolean canSendAwardFlag, OrderInfoRecord order, PayAwardVo payAward, PayAwardContentBo payAwardContentBo, PayAwardRecordRecord payAwardRecordRecord) throws MpException {
        switch (payAwardContentBo.getGiftType()) {
            case GIVE_TYPE_NO_PRIZE:
                logger().info("无奖励");
                payAwardRecordRecord.setSendData("");
                payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_NO_STOCK);
                payAwardRecordRecord.setAwardData("");
                break;
            case GIVE_TYPE_ORDINARY_COUPON:
                logger().info("奖品:优惠卷");
            case GIVE_TYPE_SPLIT_COUPON:
                awardCouponGive(canSendAwardFlag, order, payAward, payAwardContentBo, payAwardRecordRecord);
                break;
            case GIVE_TYPE_LOTTERY:
                logger().info("幸运大抽奖");
                if (canSendAwardFlag){
                    payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_UNRECEIVED);
                }else {
                    payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_NO_STOCK);
                }
                payAwardRecordRecord.setAwardData(payAwardContentBo.getLotteryId().toString());
                break;
            case GIVE_TYPE_BALANCE:
                awardUserAmount(canSendAwardFlag, order, payAwardContentBo, payAwardRecordRecord);
                break;
            case GIVE_TYPE_GOODS:
                awardPrize(canSendAwardFlag, order, payAward, payAwardContentBo, payAwardRecordRecord);
                break;
            case GIVE_TYPE_SCORE:
                awardScore(canSendAwardFlag, order, payAwardContentBo, payAwardRecordRecord);
                break;
            case GIVE_TYPE_CUSTOM:
                logger().info("自定义");
                payAwardRecordRecord.setSendData("");
                payAwardRecordRecord.setAwardData(Util.toJson(payAwardContentBo));
                if (canSendAwardFlag){
                    payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_UNRECEIVED);
                }else {
                    payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_NO_STOCK);
                }
                break;
            default:
        }
    }

    private void awardScore(boolean canSendAwardFlag, OrderInfoRecord order, PayAwardContentBo payAwardContentBo, PayAwardRecordRecord payAwardRecordRecord) throws MpException {
        logger().info("积分");
        if (canSendAwardFlag){
            ScoreParam scoreParam = new ScoreParam();
            scoreParam.setScore(payAwardContentBo.getScoreNumber());
            scoreParam.setUserId(order.getUserId());
            scoreParam.setOrderSn(order.getOrderSn());
            scoreParam.setScoreStatus(NO_USE_SCORE_STATUS);
            scoreParam.setRemarkCode(PAY_HAS_GIFT.code);
            scoreService.updateMemberScore(scoreParam, INTEGER_ZERO, TYPE_SCORE_PAY_AWARD.val(), TRADE_FLOW_IN.val());
            payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_RECEIVED);
            payAwardRecordRecord.setSendData(payAwardContentBo.getScoreNumber().toString());
        }else {
            payAwardRecordRecord.setSendData("");
            payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_NO_STOCK);
        }
        payAwardRecordRecord.setAwardData(payAwardContentBo.getScoreNumber().toString());
    }

    private void awardPrize(boolean canSendAwardFlag, OrderInfoRecord order, PayAwardVo payAward, PayAwardContentBo payAwardContentBo, PayAwardRecordRecord payAwardRecordRecord) throws MpException {
        logger().info("奖品");
        if (canSendAwardFlag){
            payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_UNRECEIVED);
        }else {
            payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_NO_STOCK);
        }
        payAwardRecordRecord.setAwardData(payAwardContentBo.getProductId().toString());
        payAwardRecordRecord.setKeepDays(payAwardContentBo.getKeepDays());
        payAwardRecordRecord.insert();
        //扣商品库存
        atomicOperation.updateStockAndSalesByLock(payAwardContentBo.getProduct().getGoodsId(),payAwardContentBo.getProductId(),1,true);
        PrizeRecordRecord prizeRecordRecord = prizeRecordService.savePrize(order.getUserId(), payAward.getId(), payAwardRecordRecord.getId(), PRIZE_SOURCE_PAY_AWARD, payAwardContentBo.getProductId(), payAwardContentBo.getKeepDays(),null);
        payAwardRecordRecord.setSendData(prizeRecordRecord.getId().toString());
    }

    private void awardUserAmount(boolean canSendAwardFlag, OrderInfoRecord order, PayAwardContentBo payAwardContentBo, PayAwardRecordRecord payAwardRecordRecord) throws MpException {
        logger().info("余额");
        if (canSendAwardFlag){
            AccountParam accountParam = new AccountParam() {{
                setUserId(order.getUserId());
                setAmount(payAwardContentBo.getAccountNumber());
                setOrderSn(order.getOrderSn());
                setPayment(PAY_CODE_BALANCE_PAY);
                setIsPaid(UACCOUNT_RECHARGE.val());
                setRemarkId(PAY_HAS_GIFT.code);
            }};
            TradeOptParam tradeOptParam = TradeOptParam.builder()
                    .tradeType(TYPE_CRASH_PAY_AWARD.val())
                    .tradeFlow(TRADE_FLOW_IN.val())
                    .build();
            accountService.updateUserAccount(accountParam, tradeOptParam);
            payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_RECEIVED);
            payAwardRecordRecord.setSendData(payAwardContentBo.getAccountNumber().toString());
        }else {
            logger().info("余额发放完成");
            payAwardRecordRecord.setSendData("");
            payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_NO_STOCK);
        }
        payAwardRecordRecord.setAwardData(payAwardContentBo.getAccountNumber().toString());
    }

    private void awardCouponGive(boolean canSendAwardFlag, OrderInfoRecord order, PayAwardVo payAward, PayAwardContentBo payAwardContentBo, PayAwardRecordRecord payAwardRecordRecord) {
        logger().info("奖品:分裂优惠卷");
        List<Integer> integers = Util.stringToList(payAwardContentBo.getCouponIds());
        String[] couponArray = new String[0];
        if (integers != null) {
            couponArray = integers.stream().map(Object::toString).toArray(String[]::new);
        }
        if (canSendAwardFlag){
            CouponGiveQueueParam couponGive = new CouponGiveQueueParam();
            couponGive.setUserIds(Collections.singletonList(order.getUserId()));
            couponGive.setCouponArray(couponArray);
            couponGive.setActId(payAward.getId());
            couponGive.setAccessMode((byte) 0);
            couponGive.setGetSource(COUPON_GIVE_SOURCE_PAY_AWARD);
            /**
             * 发送优惠卷
             */
            CouponGiveQueueBo sendData = couponGiveService.handlerCouponGive(couponGive);
            if (sendData.getCouponSet().size()>0){
                payAwardRecordRecord.setSendData(Util.listToString(new ArrayList<>(sendData.getCouponSet())));
                payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_RECEIVED);
                payAwardRecordRecord.setAwardData(payAwardContentBo.getCouponIds());
                return;
            }
        }
        payAwardRecordRecord.setSendData("");
        payAwardRecordRecord.setStatus(PAY_AWARD_GIVE_STATUS_NO_STOCK);
        payAwardRecordRecord.setAwardData(payAwardContentBo.getCouponIds());
    }


    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        try {
            logger().info("支付有礼活动校验");
            if (!order.getOrderStatus().equals(ORDER_WAIT_DELIVERY) || order.getPayCode().equals(PAY_CODE_COD)) {
                logger().info("不参与支付有礼活动");
                return;
            }
            //获取进行中的活动
            PayAwardVo payAward = payAwardService.getGoingPayAward(param.getDate());
            if (payAward == null) {
                logger().info("支付有礼活动为空!");
                return;
            }
            logger().info("校验支付金额是否合格");
            if (orderInfoService.getOrderFinalAmount(order.into(OrderListInfoVo.class), false).compareTo(payAward.getMinPayMoney()) < 0) {
                logger().info("支付金额不满足活动要求");
                return;
            }
            if (processSectionGoodsType(param, payAward)) {
                return;
            }
            logger().info("校验奖品配置");
            int payAwardSize = payAward.getAwardContentList().size();
            if (payAwardSize == 0) {
                logger().info("支付有礼没有配置奖品");
                return;
            }
            float joinAwardCount = jedisManager.getIncrValueAndSave(REDIS_PAY_AWARD_JOIN_COUNT +payAward.getId() +":"+order.getUserId(), 60000,
                    () -> payAwardRecordService.getJoinAwardCount(order.getUserId(), payAward.getId()).toString()).intValue()+1;
            logger().info("用户:{},参与次数:{}", order.getUserId(), joinAwardCount);
            double circleTimes = (int)Math.ceil(joinAwardCount / payAwardSize);
            int currentAward =  (int)joinAwardCount % payAwardSize;
            currentAward=currentAward==0?payAwardSize:currentAward;
            logger().info("当前第:{}轮,第:{}次", circleTimes,currentAward);
            if (payAward.getLimitTimes() > 0 && payAward.getLimitTimes()*payAwardSize < joinAwardCount) {
                jedisManager.delete(REDIS_PAY_AWARD_JOIN_COUNT +payAward.getId() +":"+order.getUserId());
                logger().info("参与次数到达上限:{}", payAward.getLimitTimes());
                return;
            }
            logger().info("当前的奖励层级:{}", currentAward);
            PayAwardContentBo payAwardContentBo = payAward.getAwardContentList().get(currentAward-1);
            logger().info("当前奖励:" + payAwardContentBo.toString());
            if (payAwardContentBo.getGiftType().equals(GIVE_TYPE_NO_PRIZE)) {
                logger().info("当前奖励无奖品");
            }
            logger().info("礼物数量校验");
            PayAwardPrizeRecord awardInfo = payAwardRecordService.getAwardInfo(payAward.getId(), payAwardContentBo.getId());
            boolean canSendAwardFlag =true;
            boolean hasAward = awardInfo != null && (awardInfo.getAwardNumber().equals(0) || awardInfo.getSendNum() < awardInfo.getAwardNumber());
            if (hasAward){
                int i = payAwardRecordService.updateAwardStock(payAward.getId(), payAwardContentBo.getId());
                if (i<1){
                    canSendAwardFlag =false;
                    logger().info("礼物已发完");
                }
            }else {
                canSendAwardFlag =false;
                logger().info("礼物已发完");
            }

            savePayWardRecord(order, payAward, currentAward, payAwardContentBo, canSendAwardFlag);

        } catch (Exception e) {
            logger().error("支付有礼活动异常");
            //获取进行中的活动
            PayAwardVo payAward = payAwardService.getGoingPayAward(param.getDate());
            if (payAward == null) {
                logger().info("支付有礼活动为空!");
                return;
            }
            jedisManager.delete(REDIS_PAY_AWARD_JOIN_COUNT +payAward.getId() +":"+order.getUserId());
            e.printStackTrace();

        }
    }

    private void savePayWardRecord(OrderInfoRecord order, PayAwardVo payAward, int currentAward, PayAwardContentBo payAwardContentBo, boolean canSendAwardFlag) throws MpException {
        PayAwardRecordRecord payAwardRecordRecord = db().newRecord(PAY_AWARD_RECORD);
        payAwardRecordRecord.setAwardId(payAward.getId());
        payAwardRecordRecord.setAwardTimes(currentAward);
        payAwardRecordRecord.setUserId(order.getUserId());
        payAwardRecordRecord.setOrderSn(order.getOrderSn());
        payAwardRecordRecord.setAwardPrizeId(payAwardContentBo.getId());
        payAwardRecordRecord.setGiftType(payAwardContentBo.getGiftType());
        // 定点杆添加支付有礼id
        order.setPayAwardId(payAward.getId());
        sendAward(canSendAwardFlag, order, payAward, payAwardContentBo, payAwardRecordRecord);
        if (payAwardRecordRecord.getId()!=null){
            payAwardRecordRecord.update();
        }else {
            payAwardRecordRecord.insert();
        }
    }

    private boolean processSectionGoodsType(OrderBeforeParam param, PayAwardVo payAward) {
        //活动商品
        if (payAward.getGoodsAreaType().equals(GOODS_AREA_TYPE_SECTION.intValue())) {
            boolean payAwardFlag = false;
            for (OrderBeforeParam.Goods goods : param.getGoods()) {
                boolean hasGoodsId = false;
                boolean hasCatId = false;
                boolean hasSortId = false;
                if (payAward.getGoodsIds()!=null){
                    hasGoodsId = Arrays.asList(payAward.getGoodsIds().split(",")).contains(goods.getGoodsInfo().getGoodsId().toString());
                }
                if (payAward.getGoodsCatIds()!=null){
                    hasCatId = Arrays.asList(payAward.getGoodsCatIds().split(",")).contains(goods.getGoodsInfo().getCatId().toString());
                }
                if (payAward.getGoodsSortIds()!=null){
                    hasSortId = Arrays.stream(payAward.getGoodsSortIds().split(",")).anyMatch(goods.getGoodsInfo().getSortId().toString()::equals);
                }
                if (hasGoodsId || hasCatId || hasSortId) {
                    GoodsActivityInfo activityInfo = new GoodsActivityInfo();
                    activityInfo.setActivityType(ACTIVITY_TYPE_PAY_AWARD);
                    activityInfo.setActivityId(payAward.getId());
                    payAwardFlag = true;
                }
            }
            if (!payAwardFlag) {
                logger().info("支付有礼没有商品查找");
                return true;
            }
        }
        return false;
    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) {

    }
}

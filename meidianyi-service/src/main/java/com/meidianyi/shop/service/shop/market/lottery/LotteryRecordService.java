package com.meidianyi.shop.service.shop.market.lottery;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.tables.records.LotteryPrizeRecord;
import com.meidianyi.shop.db.shop.tables.records.LotteryRecord;
import com.meidianyi.shop.db.shop.tables.records.LotteryRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.PrizeRecordRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueBo;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;
import com.meidianyi.shop.service.pojo.shop.market.lottery.JoinLottery;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant;
import com.meidianyi.shop.service.pojo.shop.market.lottery.prize.LotteryPrizeVo;
import com.meidianyi.shop.service.pojo.shop.market.lottery.record.LotteryRecordPageListParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.record.LotteryRecordPageListVo;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountParam;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.wxapp.market.lottery.LotteryListUserParam;
import com.meidianyi.shop.service.shop.coupon.CouponGiveService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.market.prize.PrizeRecordService;
import com.meidianyi.shop.service.shop.member.AccountService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import org.elasticsearch.common.Strings;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static com.meidianyi.shop.db.shop.tables.LotteryRecord.LOTTERY_RECORD;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant.COUPON_GIVE_SOURCE_LOTTERY_AWARD;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_PRIZE_STATUS_RECEIVED;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_PRIZE_STATUS_UNCLAIMED;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TYPE_BALANCE;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TYPE_COUPON;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TYPE_CUSTOM;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TYPE_GOODS;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TYPE_NULL;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TYPE_SCORE;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TYPE_SEND_OUT;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.NO_USE_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.*;
import static com.meidianyi.shop.service.pojo.shop.payment.PayCode.PAY_CODE_BALANCE_PAY;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_SOURCE_LOTTERY;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * 抽奖记录
 *
 * @author 孔德成
 * @date 2019/8/6 9:34
 */
@Service
public class LotteryRecordService extends ShopBaseService {

    @Autowired
    private MemberService member;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PrizeRecordService prizeRecordService;
    @Autowired
    private CouponGiveService couponGiveService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private AtomicOperation atomicOperation;

    /**
     * 抽奖记录查询
     *
     * @param param LotteryRecordPageListParam
     * @return LotteryRecordPageListVo
     */
    public PageResult<LotteryRecordPageListVo> getLotteryRecordList(LotteryRecordPageListParam param) {
        SelectOnConditionStep<Record> select = db().select(LOTTERY_RECORD.asterisk(), USER.USERNAME, USER.MOBILE)
                .from(LOTTERY_RECORD)
                .leftJoin(USER).on(USER.USER_ID.eq(LOTTERY_RECORD.USER_ID));
        buildSelect(select, param);
        select.orderBy(LOTTERY_RECORD.CREATE_TIME.desc());
        PageResult<LotteryRecordPageListVo> pageList = getPageResult(select, param.getCurrentPage(), param.getPageRows(), LotteryRecordPageListVo.class);
        pageList.getDataList().forEach(item -> {
            item.setLotteryPrize(Util.parseJson(item.getAwardInfo(), LotteryPrizeVo.class));
        });
        return pageList;
    }
    /**
     * 获取用户抽奖列表
     * @param param
     * @return
     */
    public PageResult<LotteryRecordPageListVo> lotteryListByParam(LotteryListUserParam param) {
            SelectConditionStep<Record> records = db()
                .select(LOTTERY_RECORD.asterisk(), USER.USERNAME, USER.MOBILE)
                .from(LOTTERY_RECORD).innerJoin(USER).on(USER.USER_ID.eq(LOTTERY_RECORD.USER_ID))
                .where(LOTTERY_RECORD.LOTTERY_ID.eq(param.getLotteryId()));
            if (param.getUserId()!=null){
                records.and(LOTTERY_RECORD.USER_ID.eq(param.getUserId()));
            }else {
                records.and(LOTTERY_RECORD.LOTTERY_GRADE.notEqual((byte) 0));
            }
            records.orderBy(LOTTERY_RECORD.CREATE_TIME.desc());
            PageResult<LotteryRecordPageListVo> pageResult = getPageResult(records, param, LotteryRecordPageListVo.class);
            pageResult.getDataList().forEach(item -> {
            if (!Strings.isEmpty(item.getAwardInfo())){
                item.setLotteryPrize(Util.parseJson(item.getAwardInfo(), LotteryPrizeVo.class));
                item.setAwardInfo(null);
            }
        });
        return pageResult;
    }

    private void buildSelect(SelectOnConditionStep<Record> select, LotteryRecordPageListParam param) {
        if (param.getLotteryId() != null) {
            select.where(LOTTERY_RECORD.LOTTERY_ID.eq(param.getLotteryId()));
        }
        if (param.getUsername() != null) {
            select.where(USER.USERNAME.like(likeValue(param.getUsername())));
        }
        if (param.getMobile() != null) {
            select.where(USER.MOBILE.like(prefixLikeValue(param.getMobile())));
        }
        if (param.getLotteryGrade() != null && param.getLotteryGrade() >= 0) {
            select.where(LOTTERY_RECORD.LOTTERY_GRADE.eq(param.getLotteryGrade()));
        }
        if (param.getLotterySource() != null && param.getLotterySource() >= 0) {
            select.where(LOTTERY_RECORD.LOTTERY_SOURCE.eq(param.getLotterySource()));
        }
        if (param.getChanceSource() != null && param.getChanceSource() >= 0) {
            select.where(LOTTERY_RECORD.CHANCE_SOURCE.eq(param.getChanceSource()));
        }
        if (param.getLotteryActId() != null) {
            select.where(LOTTERY_RECORD.LOTTERY_ACT_ID.eq(param.getLotteryActId()));

        }
    }


    /**
     * 获取用户已抽奖次数
     *
     * @param userId       会员id
     * @param lotteryId    活动id
     * @param chanceSource 1:free 2:share 3:score
     * @return 抽奖次数
     */
    public Integer getJoinLotteryNumber(Integer userId, Integer lotteryId, Byte chanceSource) {
        Condition condition = LOTTERY_RECORD.USER_ID.eq(userId)
                .and(LOTTERY_RECORD.LOTTERY_ID.eq(lotteryId));
        if (chanceSource.intValue() > 0) {
            condition = condition.and(LOTTERY_RECORD.CHANCE_SOURCE.eq(chanceSource));
        }
        return db().fetchCount(LOTTERY_RECORD, condition);
    }

    /**
     * 获取用户已抽奖次数
     *
     * @param userId       会员id
     * @param lotteryId    活动id
     * @param chanceSource 1:free 2:share 3:score
     * @param chanceType 0每人  1每人每天
     * @return 抽奖次数
     */
    public Integer getJoinLotteryNumber(Integer userId, Integer lotteryId, Byte chanceSource,Byte chanceType) {
        Condition condition = LOTTERY_RECORD.USER_ID.eq(userId)
                .and(LOTTERY_RECORD.LOTTERY_ID.eq(lotteryId));
        if (chanceSource.intValue() > 0) {
            condition = condition.and(LOTTERY_RECORD.CHANCE_SOURCE.eq(chanceSource));
        }
        if (LotteryConstant.CHANCE_TYPE_EVERYONE_DAY.equals(chanceType)){
            condition = condition.and(DslPlus.toDays(LOTTERY_RECORD.CREATE_TIME).eq(DslPlus.toDays(DSL.now())));
        }
        return db().fetchCount(LOTTERY_RECORD, condition);
    }


    /**
     * 发送奖品
     *
     * @param userId
     * @param joinValid JoinLottery
     */
    public void sendAwardPresent(Integer userId, JoinLottery joinValid) throws MpException {
        LotteryPrizeRecord lotteryPrizeRecord = joinValid.getLotteryPrize();
        LotteryRecord lotteryRecord = joinValid.getLottery();
        LotteryRecordRecord recordRecord = db().newRecord(LOTTERY_RECORD);
        recordRecord.setUserId(userId);
        recordRecord.setLotteryId(lotteryRecord.getId());
        recordRecord.setLotteryActId(lotteryRecord.getId());
        recordRecord.setLotteryType(joinValid.getResultsType());
        recordRecord.setChanceSource(joinValid.getChanceSource());
        recordRecord.setLotterySource(joinValid.getLotterySource());
        recordRecord.setPrdId(0);
        recordRecord.setLotteryGrade(lotteryPrizeRecord != null ? lotteryPrizeRecord.getLotteryGrade():0);
        if (lotteryPrizeRecord!=null){
            LotteryPrizeVo lotteryPrizeVo =lotteryPrizeRecord.into(LotteryPrizeVo.class);
            recordRecord.setAwardInfo(Util.toJson(lotteryPrizeVo));
        }
        joinValid.setPrizeImage(imageService.getImgFullUrl(lotteryPrizeRecord != null ? lotteryPrizeRecord.getIconImgsImage() : null));
        joinValid.setPrizeText(lotteryPrizeRecord!=null?lotteryPrizeRecord.getIconImgs():"");

        // 处理抽奖
        processLottery(userId, joinValid, lotteryPrizeRecord, lotteryRecord, recordRecord);

        if (recordRecord.getId() == null) {
            recordRecord.insert();
        }
    }

    /**
     * 处理抽奖
     *
     * @param userId
     * @param joinValid
     * @param lotteryPrizeRecord
     * @param lotteryRecord
     * @param recordRecord
     * @throws MpException
     */
    private void processLottery(Integer userId, JoinLottery joinValid, LotteryPrizeRecord lotteryPrizeRecord, LotteryRecord lotteryRecord, LotteryRecordRecord recordRecord) throws MpException {
        logger().info("抽奖结果:");
        //选择奖类型
        switch (joinValid.getResultsType()) {
            case LOTTERY_TYPE_NULL:
                logger().info("未中奖");
            case LOTTERY_TYPE_SEND_OUT:
                processSendOut(userId, joinValid, lotteryPrizeRecord, lotteryRecord, recordRecord);
                break;
            case LOTTERY_TYPE_SCORE:
                processTypeScore(userId, joinValid, lotteryPrizeRecord, recordRecord);

                break;
            case LOTTERY_TYPE_BALANCE:
                logger().info("用户余额");
                recordRecord.setPresentStatus(LOTTERY_PRIZE_STATUS_RECEIVED);
                recordRecord.setLotteryAward(lotteryPrizeRecord.getAwardAccount()+"余额");
                joinValid.setLotteryAward(lotteryPrizeRecord.getAwardAccount()+"余额");

                AccountParam accountParam = new AccountParam() {{
                    setUserId(userId);
                    setAmount(lotteryPrizeRecord.getAwardAccount());
                    setPayment(PAY_CODE_BALANCE_PAY);
                    setIsPaid(UACCOUNT_RECHARGE.val());
                    setRemarkId(RemarkTemplate.MSG_LOTTERY_GIFT.code);
                }};
                TradeOptParam tradeOptParam = TradeOptParam.builder()
                        .tradeType(TYPE_CRASH_LOTTERY.val())
                        .tradeFlow(TRADE_FLOW_IN.val())
                        .build();
                accountService.updateUserAccount(accountParam, tradeOptParam);
                break;
            case LOTTERY_TYPE_COUPON:
                logger().info("优惠卷");
                CouponView couponView = couponService.getCouponViewById(lotteryPrizeRecord.getCouponId());
                recordRecord.setPresentStatus(LOTTERY_PRIZE_STATUS_RECEIVED);
                recordRecord.setLotteryAward("优惠券:"+couponView.getActName());
                joinValid.setLotteryAward("优惠券:"+couponView.getActName());

                CouponGiveQueueParam couponGive = new CouponGiveQueueParam();
                couponGive.setUserIds(Collections.singletonList(userId));
                couponGive.setCouponArray(new String[]{String.valueOf(lotteryPrizeRecord.getCouponId())});
                couponGive.setActId(lotteryRecord.getId());
                couponGive.setAccessMode((byte) 0);
                couponGive.setGetSource(COUPON_GIVE_SOURCE_LOTTERY_AWARD);
                /**
                 * 发送优惠卷
                 */
                CouponGiveQueueBo sendData = couponGiveService.handlerCouponGive(couponGive);
                break;
            case LOTTERY_TYPE_GOODS:
                logger().info("赠品");
                GoodsView goodsView = goodsService.getGoodsViewByProductId(lotteryPrizeRecord.getPrdId());
                if (goodsView!=null){
                    //扣商品库存
                    atomicOperation.updateStockAndSalesByLock(goodsView.getGoodsId(),recordRecord.getPrdId(),1,true);
                    recordRecord.setPrdId(lotteryPrizeRecord.getPrdId());
                    recordRecord.setPresentStatus(LOTTERY_PRIZE_STATUS_UNCLAIMED);
                    recordRecord.setLotteryAward("赠品:"+goodsView.getGoodsName());
                    Timestamp timeStampPlus = DateUtils.getTimeStampPlus(lotteryPrizeRecord.getPrdKeepDays().intValue(), ChronoUnit.DAYS);
                    recordRecord.setLotteryExpiredTime(timeStampPlus);
                    recordRecord.insert();
                    PrizeRecordRecord prizeRecordRecord = prizeRecordService.savePrize(userId, lotteryRecord.getId(), recordRecord.getId(),
                            PRIZE_SOURCE_LOTTERY, lotteryPrizeRecord.getPrdId(), lotteryPrizeRecord.getPrdKeepDays().intValue(),null);
                    joinValid.setPrizeId(prizeRecordRecord.getId());
                    joinValid.setLotteryAward("赠品:"+goodsView.getGoodsName());
                    joinValid.setGoodsImage(goodsView.getGoodsImg());
                    joinValid.setGoodsId(goodsView.getGoodsId());
                    joinValid.setProductId(lotteryPrizeRecord.getPrdId());
                }else {

                }
                break;
            case LOTTERY_TYPE_CUSTOM:
                logger().info("自定义");
                joinValid.setLotteryAward(lotteryPrizeRecord.getLotteryDetail());
                recordRecord.setLotteryAward(lotteryPrizeRecord.getLotteryDetail());
                break;
            default:
        }
    }

    private void processTypeScore(Integer userId, JoinLottery joinValid, LotteryPrizeRecord lotteryPrizeRecord, LotteryRecordRecord recordRecord) throws MpException {
        logger().info("积分");
        recordRecord.setPresentStatus(LOTTERY_PRIZE_STATUS_RECEIVED);
        recordRecord.setLotteryAward(lotteryPrizeRecord.getIntegralScore()+"积分");
        joinValid.setLotteryAward(lotteryPrizeRecord.getIntegralScore()+"积分");

        ScoreParam scoreParam = new ScoreParam();
        scoreParam.setScore(lotteryPrizeRecord != null ? lotteryPrizeRecord.getIntegralScore() : 0);
        scoreParam.setUserId(userId);
        scoreParam.setScoreStatus(NO_USE_SCORE_STATUS);
        scoreParam.setRemarkCode(RemarkTemplate.MSG_LOTTERY_GIFT.code);
        scoreService.updateMemberScore(scoreParam, INTEGER_ZERO, TYPE_SCORE_LOTTERY.val(), TRADE_FLOW_IN.val());
    }

    private void processSendOut(Integer userId, JoinLottery joinValid, LotteryPrizeRecord lotteryPrizeRecord, LotteryRecord lotteryRecord, LotteryRecordRecord recordRecord) throws MpException {
        logger().info("已经发完了");
        recordRecord.setLotteryGrade((byte) 0);
        recordRecord.setPresentStatus(LOTTERY_PRIZE_STATUS_RECEIVED);
        if (lotteryPrizeRecord!=null){
            LotteryPrizeVo lotteryPrizeVo =lotteryPrizeRecord.into(LotteryPrizeVo.class);
            recordRecord.setAwardInfo(Util.toJson(lotteryPrizeVo==null?"":lotteryPrizeVo));
        }
        if (lotteryRecord.getNoAwardScore()!=null&&!lotteryRecord.getNoAwardScore().equals(0)){
            logger().info("有安慰奖");
            recordRecord.setLotteryAward("赠送积分"+lotteryRecord.getNoAwardScore());
            ScoreParam scoreParam = new ScoreParam();
            scoreParam.setScore(lotteryRecord.getNoAwardScore());
            scoreParam.setUserId(userId);
            scoreParam.setScoreStatus(NO_USE_SCORE_STATUS);
            scoreParam.setRemarkCode(RemarkTemplate.MSG_LOTTERY_GIFT.code);
            scoreService.updateMemberScore(scoreParam, INTEGER_ZERO, TYPE_SCORE_LOTTERY.val(), TRADE_FLOW_IN.val());
            joinValid.setLotteryAward("赠送积分"+lotteryRecord.getNoAwardScore());
        }else {
            logger().info("没有安慰奖");
            recordRecord.setLotteryAward(lotteryRecord.getNoAwardIcon());
            joinValid.setLotteryAward(lotteryRecord.getNoAwardIcon());
        }
        joinValid.setPrizeImage(imageService.getImgFullUrl(lotteryRecord.getNoAwardImage()));
        joinValid.setPrizeText(lotteryRecord.getNoAwardIcon());
    }


}

package com.meidianyi.shop.service.shop.market.lottery;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.LotteryPrizeRecord;
import com.meidianyi.shop.db.shop.tables.records.LotteryRecord;
import com.meidianyi.shop.db.shop.tables.records.LotteryShareRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.goods.spec.ProductSmallInfoVo;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.JoinLottery;
import com.meidianyi.shop.service.pojo.shop.market.lottery.JoinLotteryParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryByIdParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryPageListParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryPageListVo;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryVo;
import com.meidianyi.shop.service.pojo.shop.market.lottery.prize.LotteryPrizeVo;
import com.meidianyi.shop.service.pojo.shop.market.lottery.record.LotteryRecordPageListParam;
import com.meidianyi.shop.service.pojo.shop.market.lottery.record.LotteryRecordPageListVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberPageListParam;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.market.lottery.LotteryListUserParam;
import com.meidianyi.shop.service.pojo.wxapp.market.lottery.LotteryUserTimeInfo;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import org.jooq.AggregateFunction;
import org.jooq.Record4;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_STATUS_DISABLE;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_STATUS_NORMAL;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.NO;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.YES;
import static com.meidianyi.shop.db.shop.Tables.LOTTERY;
import static com.meidianyi.shop.db.shop.Tables.LOTTERY_PRIZE;
import static com.meidianyi.shop.db.shop.Tables.LOTTERY_RECORD;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TIME_ALL;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TIME_FREE;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TIME_SCORE;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.LOTTERY_TIME_SHARE;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.NO_USE_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.TRADE_FLOW_OUT;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.TYPE_SCORE_LOTTERY;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * @author 孔德成
 * @date 2019/8/5 10:55
 */
@Service
public class LotteryService extends ShopBaseService {


    @Autowired
    private LotteryRecordService lotteryRecordService;
    @Autowired
    private LotteryShareService lotteryShareService;
    @Autowired
    private LotteryPrizeService lotteryPrizeService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private QrCodeService qrCode;
    @Autowired
    private ScoreService scoreService;

    /**
     * 添加幸运抽奖
     *
     * @param param 参数
     * @return lotteryId
     */
    public Integer addLottery(LotteryParam param) {
        LotteryRecord record = db().newRecord(LOTTERY, param);
        record.setId(null);
        record.insert();
        param.getPrizeList().forEach(prize -> {
            LotteryPrizeRecord prizeRecord = db().newRecord(LOTTERY_PRIZE, prize);
            prizeRecord.setLotteryId(record.getId());
            prizeRecord.setAwardAccount(prize.getAccount());
            prizeRecord.setAwardTimes(0);
            prizeRecord.insert();
        });
        return record.getId();
    }

    /**
     * 更新幸运抽奖
     *
     * @param param 参数 id不为空
     * @return 1成功 0 失败
     */
    public Integer updateLottery(LotteryParam param) {
        if (param.getId() != null) {
            LotteryRecord record = db().newRecord(LOTTERY, param);
            record.update();
            List<Integer> prizeIdList = new ArrayList<>();
            param.getPrizeList().forEach(prize -> {
                LotteryPrizeRecord prizeRecord = db().newRecord(LOTTERY_PRIZE, prize);
                prizeRecord.setLotteryId(record.getId());
                prizeRecord.setAwardAccount(prize.getAccount());
                if (prizeRecord.getId() == null) {
                    prizeRecord.insert();
                } else {
                    prizeRecord.update();
                }
                prizeIdList.add(prizeRecord.getId());
            });
            db().update(LOTTERY_PRIZE)
                .set(LOTTERY_PRIZE.DEL_FLAG, DelFlag.DISABLE_VALUE)
                .where(LOTTERY_PRIZE.LOTTERY_ID.eq(record.getId()))
                .and(LOTTERY_PRIZE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(LOTTERY_PRIZE.ID.notIn(prizeIdList))
                .execute();
            return 1;
        }
        return 0;
    }

    /**
     * 停用或者启用
     *
     * @param lotteryId lotteryId
     * @return 1 成功 0失败
     */
    public Integer closeAndRestartById(Integer lotteryId) {
        LotteryRecord record = db().newRecord(LOTTERY);
        record.setId(lotteryId);
        record.refresh();
        if (ACTIVITY_STATUS_NORMAL.equals(record.getStatus())) {
            record.setStatus(ACTIVITY_STATUS_DISABLE);
        } else {
            record.setStatus(ACTIVITY_STATUS_NORMAL);
        }
        return record.update();
    }

    /**
     * 删除活动
     *
     * @param lotteryId lotteryId
     * @return 1成功 0 失败
     */
    public Integer deleteLottery(Integer lotteryId) {
        return db().update(LOTTERY).set(LOTTERY.DEL_FLAG, DelFlag.DISABLE_VALUE).where(LOTTERY.ID.eq(lotteryId)).execute();
    }

    /**
     * 抽奖活动列表
     *
     * @param param LotteryPageListParam
     * @return PageResult<LotteryPageListVo>
     */
    public PageResult<LotteryPageListVo> getLotteryList(LotteryPageListParam param) {
        AggregateFunction<Integer> awardNumber = DSL.count(DSL.when(LOTTERY_RECORD.LOTTERY_GRADE.gt((byte) 0), LOTTERY_RECORD.ID));
        SelectConditionStep<Record7<Integer, String, Timestamp, Timestamp, Byte, Integer, Integer>> select = db()
            .select(LOTTERY.ID, LOTTERY.LOTTERY_NAME, LOTTERY.START_TIME, LOTTERY.END_TIME, LOTTERY.STATUS,
                DSL.count(LOTTERY_RECORD.ID).as(LotteryPageListVo.JOIN_NUMBER),
                awardNumber.as(LotteryPageListVo.AWAED_NUMBER))
            .from(LOTTERY)
            .leftJoin(LOTTERY_RECORD).on(LOTTERY.ID.eq(LOTTERY_RECORD.LOTTERY_ID))
            .where(LOTTERY.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        switch (param.getState()) {
            case BaseConstant.NAVBAR_TYPE_ONGOING:
                select.and(LOTTERY.START_TIME.lt(nowTime))
                    .and(LOTTERY.END_TIME.gt(nowTime))
                    .and(LOTTERY.STATUS.eq(ACTIVITY_STATUS_NORMAL));
                break;
            case BaseConstant.NAVBAR_TYPE_NOT_STARTED:
                select.and(LOTTERY.STATUS.eq(ACTIVITY_STATUS_NORMAL))
                    .and(LOTTERY.START_TIME.gt(nowTime));
                break;
            case BaseConstant.NAVBAR_TYPE_FINISHED:
                select.and(LOTTERY.STATUS.eq(ACTIVITY_STATUS_NORMAL))
                    .and(LOTTERY.END_TIME.lt(nowTime));
                break;
            case BaseConstant.NAVBAR_TYPE_DISABLED:
                select.and(LOTTERY.STATUS.eq(ACTIVITY_STATUS_DISABLE));
                break;
            case BaseConstant.NAVBAR_TYPE_AVAILABLE:
                select.and(LOTTERY.STATUS.eq(ACTIVITY_STATUS_NORMAL))
                    .and(LOTTERY.END_TIME.gt(nowTime));
                break;
            default:
        }
        select.orderBy(LOTTERY.CREATE_TIME.desc());
        select.groupBy(LOTTERY.ID, LOTTERY.LOTTERY_NAME, LOTTERY.START_TIME, LOTTERY.END_TIME, LOTTERY.STATUS);
        PageResult<LotteryPageListVo> res = getPageResult(select, param.getCurrentPage(), param.getPageRows(), LotteryPageListVo.class);
        res.dataList.forEach(vo -> {
            vo.setCurrentState(Util.getActStatus(vo.getStatus(), vo.getStartTime(), vo.getEndTime()));
        });
        return res;
    }

    /**
     * 获取抽奖
     *
     * @param id id
     * @return record
     */
    public LotteryRecord getLotteryById(Integer id) {
        return db().selectFrom(LOTTERY).where(LOTTERY.ID.eq(id)).fetchOne();
    }

    /**
     * 获取活动及奖品初始化
     *
     * @param id 活动id
     * @return LotteryVo or null
     */
    public LotteryVo getLotteryVo(Integer id) {
        LotteryRecord lottery = getLotteryById(id);
        Result<LotteryPrizeRecord> lotteryPrizeList = getLotteryPrizeById(id);
        if (lottery == null || lotteryPrizeList.size() == 0) {
            return null;
        }
        LotteryVo lotteryVo = lottery.into(LotteryVo.class);
        List<LotteryPrizeVo> lotteryPrizeVoList = lotteryPrizeList.into(LotteryPrizeVo.class);
        lotteryPrizeVoList.forEach(lotteryPrizeVo -> {
            lotteryPrizeVo.setAccount(lotteryPrizeVo.getAwardAccount());
            if (lotteryPrizeVo.getLotteryType().equals(LotteryConstant.LOTTERY_TYPE_GOODS)) {
                ProductSmallInfoVo product = goodsService.getProductVoInfoByProductId(lotteryPrizeVo.getPrdId());
                lotteryPrizeVo.setProduct(product);
            }
        });
        lotteryVo.setPrizeList(lotteryPrizeVoList);
        return lotteryVo;
    }

    /**
     * Fetch available lottery lottery record.获取可用抽奖活动
     *
     * @param id the id
     * @return the lottery record
     */
    public LotteryRecord fetchAvailableLottery(Integer id) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        return db().selectFrom(LOTTERY)
            .where(LOTTERY.ID.eq(id))
            .and(LOTTERY.DEL_FLAG.eq(BYTE_ZERO))
            .and(LOTTERY.STATUS.eq(BYTE_ONE))
            .and(LOTTERY.END_TIME.greaterThan(now))
            .and(LOTTERY.START_TIME.lessThan(now))
            .fetchOne();
    }


    public Result<LotteryPrizeRecord> getLotteryPrizeById(Integer id) {
        return lotteryPrizeService.getPrizeByLotteryId(id);
    }

    /**
     * 抽奖记录
     *
     * @param param LotteryRecordPageListParam
     * @return PageResult<LotteryRecordPageListVo>
     */
    public PageResult<LotteryRecordPageListVo> getLotteryRecordList(LotteryRecordPageListParam param) {
        return lotteryRecordService.getLotteryRecordList(param);
    }

    /**
     * 获取新用户记录
     *
     * @param param MarketSourceUserListParam
     * @return PageResult<MemberInfoVo>
     */
    public PageResult<MemberInfoVo> getLotteryUserList(MarketSourceUserListParam param) {
        MemberPageListParam pageListParam = new MemberPageListParam();
        pageListParam.setCurrentPage(param.getCurrentPage());
        pageListParam.setPageRows(param.getPageRows());
        pageListParam.setMobile(param.getMobile());
        pageListParam.setUsername(param.getUserName());
        pageListParam.setInviteUserName(param.getInviteUserName());
        return saas().getShopApp(getShopId()).member.getSourceActList(pageListParam, MemberService.INVITE_SOURCE_LOTTERY, param.getActivityId());
    }

    /**
     * 参加抽奖
     *
     * @return JoinLottery
     */
    public JoinLottery joinLottery(JoinLotteryParam param) {
        try {
            //校验,扣积分
            JoinLottery joinValid = this.validJoinLottery(param);
            if (!joinValid.getFlag()) {
                return joinValid;
            }
            //抽奖
            lotteryPrizeService.joinLotteryAction(joinValid);
            //发送奖品 记录
            lotteryRecordService.sendAwardPresent(param.getUserId(), joinValid);
            return joinValid;
        } catch (MpException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 校验是否可以抽奖，及抽奖消耗
     *
     * @param param 入参
     * @return JoinLottery
     */
    public JoinLottery validJoinLottery(JoinLotteryParam param) throws MpException {
        Integer userId = param.getUserId();
        Integer lotteryId = param.getLotteryId();

        JoinLottery join = new JoinLottery();
        //获取活动
        LotteryRecord lottery = this.getLotteryById(lotteryId);
        Byte chanceType = lottery.getChanceType();
        join.setLottery(lottery);
        join.setLotterySource(param.getLotterySource());
        //活动不存在了
        if (lottery == null || DelFlag.DISABLE_VALUE.equals(lottery.getDelFlag())) {
            join.setResultMessage(ResultMessage.builder().jsonResultCode(JsonResultCode.LOTTERY_ACTIVITY_FAIL).build());
            return join;
        }
        //活动停止
        if (lottery.getStatus().equals(ACTIVITY_STATUS_DISABLE)) {
            join.setResultMessage(ResultMessage.builder().jsonResultCode(JsonResultCode.LOTTERY_ACTIVITY_STOP).build());
            return join;
        }
        //活动未开始
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        if (lottery.getStartTime().after(nowTime)) {
            join.setResultMessage(ResultMessage.builder().jsonResultCode(JsonResultCode.LOTTERY_ACTIVITY_NOT_BEGIN).build());
            return join;
        }
        //活动过期
        if (lottery.getEndTime().before(nowTime)) {
            join.setResultMessage(ResultMessage.builder().jsonResultCode(JsonResultCode.LOTTERY_ACTIVITY_OUT_DATE).build());
            return join;
        }
        //活动免费 null 0代表不限制 -1不能免费抽奖
        Integer userFreeTimes = lotteryRecordService.getJoinLotteryNumber(userId, lotteryId, LOTTERY_TIME_FREE, chanceType);
        Integer freeChances = lottery.getFreeChances() != null ? lottery.getFreeChances() : 0;
        if (freeChances.equals(0) || lottery.getFreeChances() > userFreeTimes) {
            join.setChanceSource(LOTTERY_TIME_FREE);
            join.setFlag(true);
            join.setCanUseTime(freeChances - userFreeTimes - 1);
            if (freeChances.equals(0)) {
                join.setTimeLimitType(NO);
            }
            return join;
        }
        if (checkShareLottery(userId, lotteryId, join, lottery, chanceType)) {
            return join;
        }

        if (checkScoreLottery(userId, lotteryId, join, lottery, chanceType)) {
            return join;
        }
        //抽奖次数用完
        join.setFlag(false);
        join.setResultMessage(ResultMessage.builder().jsonResultCode(JsonResultCode.LOTTERY_TIME_USE_UP).build());
        return join;
    }

    /**
     * 检查积分抽奖
     *
     * @param userId
     * @param lotteryId
     * @param join
     * @param lottery
     * @param chanceType
     * @return
     * @throws MpException
     */
    private boolean checkScoreLottery(Integer userId, Integer lotteryId, JoinLottery join, LotteryRecord lottery, Byte chanceType) throws MpException {
        //积分抽奖
        if (lottery.getCanUseScore() != null && lottery.getCanUseScore().equals(YES)) {
            Integer userScoreTimes = lotteryRecordService.getJoinLotteryNumber(userId, lotteryId, LOTTERY_TIME_SCORE, chanceType);
            Integer scoreChances = lottery.getScoreChances() != null ? lottery.getScoreChances() : 0;
            logger().info("积分抽奖-每人(每天)次数{},已使用{}", scoreChances, userScoreTimes);
            if (scoreChances.equals(0)) {
                join.setTimeLimitType(NO);
            }
            if (scoreChances.equals(0) || userScoreTimes < scoreChances) {
                join.setChanceSource(LOTTERY_TIME_SCORE);
                join.setFlag(true);
                join.setCanUseTime(scoreChances - userScoreTimes - 1);
                Integer lotteryScore = lottery.getScorePerChance();
                Integer userScore = memberService.getUserFieldById(userId, USER.SCORE);
                //积分不足
                if (userScore < lotteryScore) {
                    join.setResultMessage(ResultMessage.builder().jsonResultCode(JsonResultCode.LOTTERY_TIME_USE_UP).build());
                    join.setFlag(false);
                    return true;
                }
                ScoreParam scoreParam = new ScoreParam();
                scoreParam.setScore(lotteryScore);
                scoreParam.setUserId(userId);
                scoreParam.setRemarkCode(RemarkTemplate.MSG_LOTTERY_GIFT.code);
                scoreParam.setScoreStatus(NO_USE_SCORE_STATUS);
                scoreService.updateMemberScore(scoreParam, INTEGER_ZERO, TYPE_SCORE_LOTTERY.val(), TRADE_FLOW_OUT.val());
                return true;
            }
        }
        return false;
    }

    /**
     * 检查分享抽奖
     *
     * @param userId
     * @param lotteryId
     * @param join
     * @param lottery
     * @param chanceType
     * @return
     */
    private boolean checkShareLottery(Integer userId, Integer lotteryId, JoinLottery join, LotteryRecord lottery, Byte chanceType) {
        //分享抽奖
        if (lottery.getCanShare() != null && lottery.getCanShare().equals(YES)) {
            LotteryShareRecord shareRecord = lotteryShareService.getLotteryShareByUser(userId, lotteryId);
            //分享次数
            Integer usedShareRecordTimes = lotteryRecordService.getJoinLotteryNumber(userId, lotteryId, LOTTERY_TIME_SHARE, chanceType);
            Integer shareTimes = shareRecord != null ? shareRecord.getShareTimes() : 0;
            Integer usedShareTimes = shareRecord != null ? shareRecord.getUseShareTimes() : 0;
            Integer shareChances = lottery.getShareChances() != null ? lottery.getShareChances() : 0;
            logger().info("用户可以(每天)分享抽奖次数{},已分享次数{},共已使用{},(今天)使用{},", shareChances, shareTimes, usedShareTimes, usedShareRecordTimes);
            if (shareChances.equals(0)) {
                //不限制
                join.setTimeLimitType(NO);
            } else {
                join.setCanUseTime(shareChances - usedShareRecordTimes);
            }
            boolean isShareLottery = shareTimes > usedShareTimes && shareTimes > usedShareRecordTimes && (usedShareRecordTimes < shareChances || shareChances.equals(0));
            if (isShareLottery) {
                //分享抽奖
                join.setChanceSource(LOTTERY_TIME_SHARE);
                join.setCanUseTime(shareChances - usedShareRecordTimes - 1);
                join.setFlag(true);
                shareRecord.setUseShareTimes(shareRecord.getUseShareTimes() + 1);
                shareRecord.update();
                return true;
            }
            // 去分享
            if (shareChances.equals(0)) {
                join.setResultMessage(ResultMessage.builder().jsonResultCode(JsonResultCode.LOTTERY_SHARE_TIME_USE_UP)
                    .build());
                return true;
            } else if (usedShareRecordTimes < shareChances && shareTimes <= usedShareTimes) {
                //今天的抽奖次数<抽奖次数 抽奖次数<
                join.setResultMessage(ResultMessage.builder().jsonResultCode(JsonResultCode.LOTTERY_SHARE_TIME_USE_UP_TIME)
                    .message(shareChances - usedShareRecordTimes).build());
                return true;
            }
        }
        return false;
    }

    /**
     * 用户抽奖详情
     *
     * @param userId
     * @param lotteryId
     * @return
     */
    public LotteryUserTimeInfo getUserLotteryInfo(Integer userId, Integer lotteryId) {
        LotteryRecord lottery = getLotteryById(lotteryId);
        LotteryUserTimeInfo lotteryTimeInfo = new LotteryUserTimeInfo();
        Byte chanceType = lottery.getChanceType();
        //全部
        Integer usedAllTime = lotteryRecordService.getJoinLotteryNumber(userId, lotteryId, LOTTERY_TIME_ALL);
        lotteryTimeInfo.setUsedTime(usedAllTime);
        //免费
        lotteryTimeInfo.setFreeTime(lottery.getFreeChances());
        Integer usedFreeTime = lotteryRecordService.getJoinLotteryNumber(userId, lotteryId, LOTTERY_TIME_FREE, chanceType);
        lotteryTimeInfo.setUsedFreeTime(usedFreeTime);
        //分享
        if (lottery.getCanShare().equals(YES)) {
            LotteryShareRecord lotteryShareByUser = lotteryShareService.getLotteryShareByUser(userId, lotteryId);
            if (lotteryShareByUser != null) {
                Integer usedShareRecordTimes = lotteryRecordService.getJoinLotteryNumber(userId, lotteryId, LOTTERY_TIME_SHARE, chanceType);
                lotteryTimeInfo.setShareTime(lotteryShareByUser.getShareTimes() - lotteryShareByUser.getUseShareTimes() + usedShareRecordTimes);
                lotteryTimeInfo.setUsedShareTime(usedShareRecordTimes);
                lotteryTimeInfo.setScore(lotteryShareByUser.getUseScoreTimes());
            } else {
                lotteryTimeInfo.setShareTime(0);
                lotteryTimeInfo.setUsedShareTime(0);
            }
            lotteryTimeInfo.setShareMaximum(lottery.getShareChances());
        }
        //积分
        if (lottery.getCanUseScore().equals(YES)) {
            Integer userScoreTimes = lotteryRecordService.getJoinLotteryNumber(userId, lotteryId, LOTTERY_TIME_SCORE, chanceType);
            lotteryTimeInfo.setScoreMaximum(lottery.getScoreChances());
            lotteryTimeInfo.setUsedScoreTime(userScoreTimes);
            lotteryTimeInfo.setScore(lottery.getScorePerChance());
        }
        //滚动记录
        LotteryListUserParam param = new LotteryListUserParam();
        param.setLotteryId(lotteryId);
        PageResult<LotteryRecordPageListVo> lotteryRecordPageListVoPageResult = lotteryRecordService.lotteryListByParam(param);
        lotteryTimeInfo.setLotteryRecord(lotteryRecordPageListVoPageResult);
        return lotteryTimeInfo;
    }

    /**
     * 分享抽奖活动
     *
     * @param userId
     * @param lotteryId
     */
    public void shareLottery(Integer userId, Integer lotteryId) {
        lotteryShareService.addShareRecord(userId, lotteryId);
    }

    /**
     * 用户中奖列表
     *
     * @param param
     * @return
     */
    public PageResult<LotteryRecordPageListVo> lotteryListByUser(LotteryListUserParam param) {
        return lotteryRecordService.lotteryListByParam(param);
    }

    /**
     * 分享
     *
     * @param param
     * @return
     * @retuen
     */
    public ShareQrCodeVo getMpQrCode(LotteryByIdParam param) {
        Integer groupDrawId = param.getId();
        String pathParam = "lotteryId=" + groupDrawId + "&lotterySource=6";
        String imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.LOTTERY, pathParam);
        ShareQrCodeVo share = new ShareQrCodeVo();
        share.setImageUrl(imageUrl);
        share.setPagePath(QrCodeTypeEnum.LOTTERY.getPathUrl(pathParam));
        return share;
    }

    /**
     * 营销日历用id查询活动
     *
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
        return db().select(LOTTERY.ID, LOTTERY.LOTTERY_NAME.as(CalendarAction.ACTNAME), LOTTERY.START_TIME,
            LOTTERY.END_TIME).from(LOTTERY).where(LOTTERY.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     *
     * @param param
     * @return
     */
    public PageResult<MarketVo> getListNoEnd(MarketParam param) {
        SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
            .select(LOTTERY.ID, LOTTERY.LOTTERY_NAME.as(CalendarAction.ACTNAME), LOTTERY.START_TIME,
                LOTTERY.END_TIME)
            .from(LOTTERY)
            .where(LOTTERY.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(LOTTERY.STATUS
                .eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(LOTTERY.END_TIME.gt(DateUtils.getSqlTimestamp()))))
            .orderBy(LOTTERY.ID.desc());
        PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
            MarketVo.class);
        return pageResult;
    }
}

package com.meidianyi.shop.service.shop.market.sharereward;

import com.fasterxml.jackson.databind.JsonNode;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.ShareAwardRecordRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.market.sharereward.*;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.service.pojo.shop.market.form.FormConstant.MAPPER;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.*;
import static com.meidianyi.shop.service.pojo.shop.market.sharereward.ShareConstant.*;
import static com.meidianyi.shop.service.shop.market.sharereward.WxShareRewardService.*;
import static com.meidianyi.shop.service.shop.store.store.StoreWxService.BYTE_TWO;
import static com.meidianyi.shop.service.shop.task.overview.GoodsStatisticTaskService.USER_GR;
import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.jooq.impl.DSL.sum;

/**
 * The type Share reward service.
 *
 * @author liufei
 * @date 2019 /8/19
 */
@Slf4j
@Service
public class ShareRewardService extends BaseShopConfigService {
    @Autowired
    private CouponService couponService;

    /**
     * 分页查询分享有礼活动信息
     *
     * @param param 查询条件
     * @return 分页数据 page result
     */
    public PageResult<ShareRewardShowVo> selectByPage(ShareRewardShowParam param) {
        //已删除的分享有礼活动不参与查询
        Condition categoryConditon = AWARD.DEL_FLAG.eq(FLAG_ZERO);
        switch (param.getCategory()) {
            // 所有0
            case PURCHASE_ALL:
                break;
            // 已停用4
            case PURCHASE_TERMINATED:
                categoryConditon = categoryConditon.and(AWARD.STATUS.eq(FLAG_ONE));
                break;
            // 已过期3
            case PURCHASE_EXPIRED:
                categoryConditon = categoryConditon.and(AWARD.END_TIME.lessThan(Timestamp.valueOf(LocalDateTime.now())))
                    .and(AWARD.IS_FOREVER.eq(FLAG_ZERO)).and(AWARD.STATUS.eq(FLAG_ZERO));
                break;
            // 未开始2
            case PURCHASE_PREPARE:
                categoryConditon = categoryConditon.and(AWARD.START_TIME.greaterThan(Timestamp.valueOf(LocalDateTime.now())))
                    .and(AWARD.IS_FOREVER.eq(FLAG_ZERO)).and(AWARD.STATUS.eq(FLAG_ZERO));
                break;
            // 默认进行中1
            default:
                categoryConditon = categoryConditon.and(AWARD.IS_FOREVER.eq(FLAG_ONE).
                    or(AWARD.START_TIME.lessThan(Timestamp.valueOf(LocalDateTime.now()))
                        .and(AWARD.END_TIME.greaterThan(Timestamp.valueOf(LocalDateTime.now())))))
                    .and(AWARD.STATUS.eq(FLAG_ZERO));
                break;
        }
        Table<Record12<Integer, String, Byte, Integer, Byte, Timestamp, Timestamp, String, String, String, Integer, Byte>> conditionStep = db().
            select(AWARD.ID, AWARD.NAME, AWARD.CONDITION, AWARD.GOODS_PV, AWARD.IS_FOREVER, AWARD.START_TIME
                , AWARD.END_TIME, AWARD.FIRST_LEVEL_RULE, AWARD.SECOND_LEVEL_RULE, AWARD.THIRD_LEVEL_RULE, AWARD.PRIORITY, AWARD.STATUS)
            .from(AWARD).where(categoryConditon).orderBy(AWARD.PRIORITY.desc()).asTable("AWARD");

        Condition selectConditon = AWARD.ID.isNotNull();
        // TODO 页面筛选条件待定，此处省略筛选condition

        SelectConditionStep<Record12<Integer, String, Byte, Integer, Byte, Timestamp, Timestamp, String, String, String, Integer, Byte>> resultStep = db().
            select(AWARD.ID, AWARD.NAME, AWARD.CONDITION, AWARD.GOODS_PV, AWARD.IS_FOREVER
                , AWARD.START_TIME, AWARD.END_TIME, AWARD.FIRST_LEVEL_RULE
                , AWARD.SECOND_LEVEL_RULE, AWARD.THIRD_LEVEL_RULE, AWARD.PRIORITY, AWARD.STATUS)
            .from(conditionStep)
            .where(selectConditon);
        PageResult<ShareRewardShowVo> pageResult = this.getPageResult(resultStep.orderBy(AWARD.PRIORITY.desc()), param.getCurrentPage(), param.getPageRows(), ShareRewardShowVo.class);

        for (ShareRewardShowVo vo : pageResult.getDataList()) {
            if (vo.getIsForever() == 1) {
                vo.setValidityPeriod("1");
            } else {
                String validityperiod = String.format("%s---%s", vo.getStartTime(), vo.getEndTime());
                log.debug("分享有礼活动有效期格式为：[{}]", validityperiod);
                vo.setValidityPeriod(validityperiod);
            }
            //设置活动的实时具体状态，进行中，未开始，已过期，已停用
            if (PURCHASE_ALL == param.getCategory()) {
                vo.setPageStatus(getPageStatus(vo));
            } else {
                vo.setPageStatus(param.getCategory());
            }
            vo.setRewardType(getRewardType(vo.getFirstLevelRule(), vo.getSecondLevelRule(), vo.getThirdLevelRule()));
            vo.setShareNum(getShareNum(vo.getId()));
            vo.setInviteNum(getInviteNum(vo.getId()));
        }
        return pageResult;
    }

    /**
     * 当查询所有的活动时，需要判定每一个活动的实时具体状态
     */
    private Byte getPageStatus(ShareRewardShowVo vo) {
        // 永久有效的活动没有过期，未开始状态，一直处于进行中状态
        boolean foreverFlag = FLAG_ONE.equals(vo.getIsForever());
        if (FLAG_ONE.equals(vo.getStatus())) {
            //已停用状态
            return PURCHASE_TERMINATED;
        }
        if (!foreverFlag && vo.getEndTime().toLocalDateTime().isBefore(LocalDateTime.now())) {
            //已过期状态
            return PURCHASE_EXPIRED;
        }
        if (!foreverFlag && vo.getStartTime().toLocalDateTime().isAfter(LocalDateTime.now())) {
            //未开始状态
            return PURCHASE_PREPARE;
        }
        boolean isProcessing = foreverFlag || (vo.getStartTime().toLocalDateTime().isBefore(LocalDateTime.now()) && vo.getEndTime().toLocalDateTime().isAfter(LocalDateTime.now()));
        if (isProcessing) {
            //进行中状态
            return PURCHASE_PROCESSING;
        }
        return null;
    }

    /**
     * 根据分享规则获取分享后的奖励类型，包括积分，优惠券，幸运大抽奖等
     *
     * @param rules 分享规则，可以有多个
     */
    private Set<Byte> getRewardType(String... rules) {
        if (rules.length == 0) {
            return null;
        }
        return Arrays.stream(rules).filter(StringUtils::isNotBlank).map(rule -> {
            try {
                JsonNode node = MAPPER.readTree(rule);
                return Byte.valueOf(node.get(REWARD_TYPE).asText());
            } catch (IOException e) {
                log.debug("[{}] Serialization Exception !", rule);
                throw new RuntimeException("Serialization Exception !");
            }
        }).collect(Collectors.toSet());
    }

    /**
     * 分享人数
     *
     * @param shareId 分享活动id
     */
    private Integer getShareNum(Integer shareId) {
        return db().selectCount().from(AWARD_RECORD).where(AWARD_RECORD.SHARE_ID.eq(shareId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 邀请人数
     *
     * @param shareId 分享活动id
     */
    private Integer getInviteNum(Integer shareId) {
        return db().selectCount().from(ATTEND).where(ATTEND.SHARE_ID.eq(shareId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 添加分享有礼活动
     *
     * @param param 分享有礼活动详情参数
     */
    public void addShareReward(ShareRewardAddParam param) {
        com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord awardRecord = buildOptions(param);
        awardRecord.setId(null);
        db().executeInsert(awardRecord);
    }

    /**
     * 获取分享有礼活动详情
     *
     * @param shareId 分享有礼活动id
     * @return the share reward info
     */
    public ShareRewardInfoVo getShareRewardInfo(Integer shareId) {
        ShareRewardInfoVo shareRewardInfoVo = db().selectFrom(AWARD).where(AWARD.ID.eq(shareId)).fetchOneInto(ShareRewardInfoVo.class);
        List<ShareRule> shareRules = new ArrayList<>();
        try {
            if (StringUtils.isNotEmpty(shareRewardInfoVo.getFirstLevelRule())) {
                log.debug("deserialization first rule : {}", shareRewardInfoVo.getFirstLevelRule());
                shareRules.add(MAPPER.readValue(shareRewardInfoVo.getFirstLevelRule(), ShareRule.class));
                if (StringUtils.isNotEmpty(shareRewardInfoVo.getSecondLevelRule())) {
                    log.debug("deserialization second rule : {}", shareRewardInfoVo.getSecondLevelRule());
                    shareRules.add(MAPPER.readValue(shareRewardInfoVo.getSecondLevelRule(), ShareRule.class));
                    if (StringUtils.isNotEmpty(shareRewardInfoVo.getThirdLevelRule())) {
                        log.debug("deserialization third rule : {}", shareRewardInfoVo.getThirdLevelRule());
                        shareRules.add(MAPPER.readValue(shareRewardInfoVo.getThirdLevelRule(), ShareRule.class));
                    }
                }
            }
        } catch (IOException e) {
            log.error("ShareReward rules deserialization failed !");
            e.printStackTrace();
        }
        shareRewardInfoVo.setShareRules(shareRules);
        return shareRewardInfoVo;
    }

    /**
     * 更新分享有礼活动
     *
     * @param param 分享有礼活动详情参数
     */
    public void updateShareReward(ShareRewardAddParam param) {
        com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord awardRecord = buildOptions(param);
        db().executeUpdate(awardRecord);
    }

    /**
     * 构建添加/更新的record
     *
     * @param param 分享有礼活动详情参数
     * @return {@link com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord}
     */
    private com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord buildOptions(ShareRewardAddParam param) {
        com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord awardRecord = new com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord();
        // 有效期为永久有效时, 将有效起止时间置为null
        if (FLAG_ONE.equals(param.getIsForever())) {
            param.setStartTime(null);
            param.setEndTime(null);
        }
        // 根据不同的触发条件, 清理冗余数据
        if (CONDITION_ONE == param.getCondition()) {
            // 全部商品
            param.setGoodsIds(null);
            param.setGoodsPv(null);
        }
        if (CONDITION_TWO == param.getCondition()) {
            // 部分商品
            param.setGoodsPv(null);
        }
        if (CONDITION_THREE == param.getCondition()) {
            // 指定访问量低于N的商品
            param.setGoodsIds(null);
        }
        // 奖励规则不为空时进行校验，为空奖励奖品数量直接置为0
        param.setFirstAwardNum(getAwardNum(param.getFirstRule()));
        param.setSecondAwardNum(getAwardNum(param.getSecondRule()));
        param.setThirdAwardNum(getAwardNum(param.getThirdRule()));

        param.setFirstLevelRule(Util.toJson(dataClean(param.getFirstRule())));
        param.setSecondLevelRule(Util.toJson(dataClean(param.getSecondRule())));
        param.setThirdLevelRule(Util.toJson(dataClean(param.getThirdRule())));

        FieldsUtil.assignNotNull(param, awardRecord);
        return awardRecord;
    }

    /**
     * ShareRule奖励规则数据清洗
     * @param shareRule
     * @return
     */
    private ShareRule dataClean(ShareRule shareRule) {
        if (Objects.isNull(shareRule)) {
            return null;
        }
        switch (shareRule.getRewardType()) {
            case CONDITION_ONE:
                shareRule.setCoupon(null);
                shareRule.setCouponNum(null);
                shareRule.setLottery(null);
                shareRule.setLotteryNum(null);
                break;
            case CONDITION_TWO:
                shareRule.setScore(null);
                shareRule.setScoreNum(null);
                shareRule.setLottery(null);
                shareRule.setLotteryNum(null);
                break;
            case CONDITION_THREE:
                shareRule.setCoupon(null);
                shareRule.setCouponNum(null);
                shareRule.setScore(null);
                shareRule.setScoreNum(null);
                break;
            default:
                break;
        }
        return shareRule;
    }

    private Integer getAwardNum(ShareRule shareRule) {
        if (Objects.isNull(shareRule)) {
            return 0;
        }
        switch (shareRule.getRewardType()) {
            case CONDITION_ONE:
                return shareRule.getScoreNum();
            case CONDITION_TWO:
                CouponView couponView = couponService.getCouponViewById(shareRule.getCoupon());
                // 校验优惠券是否存在
                com.meidianyi.shop.service.foundation.exception.Assert.notNull(couponView, JsonResultCode.CODE_DATA_NOT_EXIST, "优惠券 " + shareRule.getCoupon());
                log.debug("分享有礼活动奖励规则，奖励奖项优惠券[id:{}]所剩库存为：{}", shareRule.getCoupon(), couponView.getSurplus());
                // 校验活动定义的奖励数量是否满足奖品的库存数量
                if (shareRule.getCouponNum() != null && shareRule.getCouponNum() > 0 && couponView.getUseConsumeRestrict().equals(CouponConstant.LIMIT_SURPLUS) && couponView.getSurplus() > 0 && couponView.getSurplus() < shareRule.getCouponNum()) {
                    log.error("优惠券[id:{}]库存数量 {} 小于分享有礼活动定义的奖励数量 {}！", couponView.getId(), couponView.getSurplus(), shareRule.getCouponNum());
                    com.meidianyi.shop.service.foundation.exception.Assert.isTrue(false, JsonResultCode.SHARE_REWARD_COUPON_NUM_LIMIT);
                }
                return shareRule.getCouponNum();
            case CONDITION_THREE:
                return shareRule.getLotteryNum();
            default:
                return 0;
        }
    }

    /**
     * 停用/启用/删除
     *
     * @param param 活动状态/删除标识              0启用，1停用，2删除
     */
    public void changeActivity(ShareRewardStatusParam param) {
        switch (param.getStatus()) {
            case CONDITION_ZERO:
                //启用
                db().update(AWARD).set(AWARD.STATUS, FLAG_ZERO).where(AWARD.ID.eq(param.getShareId())).execute();
                break;
            case CONDITION_ONE:
                //停用
                db().update(AWARD).set(AWARD.STATUS, FLAG_ONE).where(AWARD.ID.eq(param.getShareId())).execute();
                break;
            case CONDITION_TWO:
                //删除
                db().update(AWARD).set(AWARD.DEL_FLAG, FLAG_ONE).where(AWARD.ID.eq(param.getShareId())).execute();
                break;
            default:
                break;
        }
    }

    /**
     * 分享有礼活动奖励领取明细查询
     *
     * @param param 活动id和筛选条件
     * @return 分页数据 page result
     */
    public PageResult<ShareReceiveDetailVo> shareReceiveDetail(ShareReceiveDetailParam param) {
        SelectConditionStep<Record11<Integer, Integer, String, String, Integer, String, Byte, String, String, String, Timestamp>> conditionStep = db()
            .select(AWARD_RECEIVE.SHARE_ID, AWARD_RECEIVE.USER_ID, USER.USERNAME, USER.MOBILE, AWARD_RECEIVE.GOODS_ID, GOODS.GOODS_NAME, AWARD_RECEIVE.AWARD_LEVEL, AWARD.FIRST_LEVEL_RULE
                , AWARD.SECOND_LEVEL_RULE, AWARD.THIRD_LEVEL_RULE, AWARD_RECEIVE.CREATE_TIME)
            .from(AWARD_RECEIVE).leftJoin(AWARD).on(AWARD_RECEIVE.SHARE_ID.eq(AWARD.ID)).leftJoin(GOODS).on(AWARD_RECEIVE.GOODS_ID.eq(GOODS.GOODS_ID)).leftJoin(USER).on(AWARD_RECEIVE.USER_ID.eq(USER.USER_ID)).where(AWARD_RECEIVE.SHARE_ID.eq(param.getShareId()));

        if (StringUtils.isNotEmpty(param.getGoodsName())) {
            conditionStep = conditionStep.and(GOODS.GOODS_NAME.contains(param.getGoodsName()));
        }
        if (StringUtils.isNotEmpty(param.getMobile())) {
            conditionStep = conditionStep.and(USER.MOBILE.contains(param.getMobile()));
        }
        if (StringUtils.isNotEmpty(param.getUsername())) {
            conditionStep = conditionStep.and(USER.USERNAME.contains(param.getUsername()));
        }
        if (param.getRewardLevel() != null) {
            conditionStep = conditionStep.and(AWARD_RECEIVE.AWARD_LEVEL.eq(param.getRewardLevel()));
        }

        PageResult<ShareReceiveDetailVo> pageResult = this.getPageResult(conditionStep, param.getCurrentPage(), param.getPageRows(), ShareReceiveDetailVo.class);

        for (ShareReceiveDetailVo vo : pageResult.getDataList()) {
            try {
                Integer shareId = vo.getShareId();
                ShareAwardRecordRecord shareAwardRecordRecord = getShareAwardRecord(shareId, vo.getUserId(), vo.getGoodsId());
                vo.setInviteUserNum(getInviteUserNum(shareId, shareAwardRecordRecord.getId()));
                vo.setInviteNewUserNum(getInviteNewUserNum(shareId, shareAwardRecordRecord.getId()));
                switch (vo.getAwardLevel()) {
                    case CONDITION_ONE:
                        JsonNode fNode = MAPPER.readTree(vo.getFirstLevelRule());
                        vo.setRewardType(Byte.valueOf(fNode.get(REWARD_TYPE).asText()));
                        vo.setScore(fNode.get(SCORE).asInt());
                        if (vo.getInviteUserNum() > fNode.get(INVITE_NUM).asInt()) {
                            vo.setInviteUserNum(fNode.get(INVITE_NUM).asInt());
                        }
                        break;
                    case CONDITION_TWO:
                        JsonNode sNode = MAPPER.readTree(vo.getSecondLevelRule());
                        vo.setRewardType(Byte.valueOf(sNode.get(REWARD_TYPE).asText()));
                        vo.setScore(sNode.get(SCORE).asInt());
                        if (vo.getInviteUserNum() > sNode.get(INVITE_NUM).asInt()) {
                            vo.setInviteUserNum(sNode.get(INVITE_NUM).asInt());
                        }
                        break;
                    case CONDITION_THREE:
                        JsonNode tNode = MAPPER.readTree(vo.getThirdLevelRule());
                        vo.setRewardType(Byte.valueOf(tNode.get(REWARD_TYPE).asText()));
                        vo.setScore(tNode.get(SCORE).asInt());
                        if (vo.getInviteUserNum() > tNode.get(INVITE_NUM).asInt()) {
                            vo.setInviteUserNum(tNode.get(INVITE_NUM).asInt());
                        }
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                log.debug(e.getMessage());
                throw new RuntimeException("Serialization Exception !");
            }
        }
        return pageResult;
    }

    /**
     * 邀请用户总数
     *
     * @param shareId 分享活动id
     */
    private Integer getInviteUserNum(Integer shareId, Integer recordId) {
        return db().select(DSL.countDistinct(ATTEND.USER_ID)).from(ATTEND).where(ATTEND.SHARE_ID.eq(shareId)).and(ATTEND.RECORD_ID.eq(recordId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 邀请新用户数
     *
     * @param shareId 分享活动id
     */
    private Integer getInviteNewUserNum(Integer shareId, Integer recordId) {
        return db().select(DSL.countDistinct(ATTEND.USER_ID)).from(ATTEND).where(ATTEND.SHARE_ID.eq(shareId)).and(ATTEND.IS_NEW.eq(CONDITION_ONE)).and(ATTEND.RECORD_ID.eq(recordId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 更新每日用户可分享次数上限参数
     *
     * @param value 每日用户可分享次数上限值
     */
    public void updateDailyShareAward(Integer value) {
        this.set(DAILY_SHARE_AWARD, String.valueOf(value));
    }

    /**
     * 获取每日用户可分享次数上限参数
     *
     * @return 每日用户可分享次数上限值 daily share award value
     */
    public int getDailyShareAwardValue() {
        return this.get(DAILY_SHARE_AWARD, Integer.class, 0);
    }

    /**
     * Activity is exist boolean.活动是否存在
     *
     * @param id the id
     * @return the boolean
     */
    private boolean activityIsExist(Integer id) {
        return db().fetchExists(AWARD, AWARD.ID.eq(id));
    }

    /**
     * Activity is exist boolean.
     *
     * @param condition the condition
     * @return the boolean
     */
    public boolean activityIsExist(Condition condition) {
        return db().fetchExists(AWARD, condition);
    }

    /**
     * A record is exist boolean.
     *
     * @param condition the condition
     * @return the boolean
     */
    boolean aRecordIsExist(Condition condition) {
        return db().fetchExists(AWARD_RECORD, condition);
    }

    /**
     * Activity available.活动是否可用
     *
     * @param id     the id
     * @param goodId the good id
     * @return the com . vpu . mp . db . shop . tables . records . share award record
     */
    com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord activityAvailable(Integer id, Integer goodId) {
        if (!activityIsExist(id)) {
            log.info("分享有礼活动 {} 不存在", id);
            throw new BusinessException(JsonResultCode.CODE_DATA_NOT_EXIST, String.format("Activity:%s", id));
        }
        com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord record = getShareReward(id);
        if (BYTE_ONE.equals(record.getDelFlag())) {
            log.info("分享有礼活动 {} 已删除", id);
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }
        if (BYTE_ZERO.equals(record.getIsForever()) && LocalDateTime.now().isAfter(record.getEndTime().toLocalDateTime())) {
            log.info("分享有礼活动 {} 已过期", id);
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }
        if (BYTE_TWO.equals(record.getCondition()) && !Util.stringList2IntList(Arrays.asList(record.getGoodsIds().split(","))).contains(goodId)) {
            log.info("分享有礼活动 {} , 该分享商品 {} 不在活动涉及范围内！", id, goodId);
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }
        int goodPv = getGoodsPv(goodId);
        if (BYTE_THREE.equals(record.getCondition()) && goodPv > record.getGoodsPv()) {
            log.info("分享有礼活动 {} , 该分享商品 {} 的访问量 {} 不符合活动限制 {}！", id, goodId, goodPv, record.getGoodsPv());
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }
        return record;
    }

    /**
     * Gets share reward.
     *
     * @param id the id
     * @return the share reward
     */
    private com.meidianyi.shop.db.shop.tables.records.ShareAwardRecord getShareReward(Integer id) {
        return db().fetchSingle(AWARD, AWARD.ID.eq(id));
    }

    /**
     * Gets share award record.
     *
     * @param shareId the share id
     * @param userId  the user id
     * @param goodsId the goods id
     * @return the share award record
     */
    ShareAwardRecordRecord getShareAwardRecord(Integer shareId, Integer userId, Integer goodsId) {
        return db().fetchOne(AWARD_RECORD, AWARD_RECORD.USER_ID.eq(userId).and(AWARD_RECORD.SHARE_ID.eq(shareId)).and(AWARD_RECORD.GOODS_ID.eq(goodsId)));
    }

    /**
     * Gets share rules.
     *
     * @param id the id
     * @return the share rules
     */
    ShareRewardInfoVo getShareInfo(Integer id) {
        ShareRewardInfoVo shareReward = db().selectFrom(AWARD).where(AWARD.ID.eq(id)).fetchOneInto(ShareRewardInfoVo.class);
        int sum = INTEGER_ZERO;
        ShareRule first = Util.json2Object(shareReward.getFirstLevelRule(), ShareRule.class, false);
        setTheStock(first, shareReward.getFirstAwardNum());
        ShareRule second = Util.json2Object(shareReward.getSecondLevelRule(), ShareRule.class, false);
        setTheStock(second, shareReward.getSecondAwardNum());
        ShareRule third = Util.json2Object(shareReward.getThirdLevelRule(), ShareRule.class, false);
        setTheStock(third, shareReward.getThirdAwardNum());
        List<ShareRule> list = new ArrayList<ShareRule>() {{
            add(first);
            add(second);
            add(third);
        }};
        list = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Byte ruleLevel = BYTE_ONE;
        for (ShareRule shareRule : list) {
            shareRule.setRuleLevel(ruleLevel++);
        }
        log.info("分享有礼活动 {} 对应的分享规则为：{}！", id, Util.toJson(list));
        shareReward.setShareRules(list);
        return shareReward;
    }

    private void setTheStock(ShareRule rule, int stock) {
        if (Objects.nonNull(rule)) {
            rule.setStock(stock);
        }
    }

    /**
     * Gets goods pv.获取商品最近七天访问量
     *
     * @param goodsId the goods id
     * @return the goods pv
     */
    int getGoodsPv(Integer goodsId) {
        return db().select(sum(USER_GR.COUNT)).from(USER_GR).where(USER_GR.GOODS_ID.eq(goodsId))
            .and(USER_GR.CREATE_TIME.greaterOrEqual(Timestamp.valueOf(LocalDate.now().minusDays(7).atStartOfDay())))
            .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }

    /**
     * Autoincrement user num.
     *
     * @param id the id
     * @return the int
     */
    int autoincrementUserNum(Integer id) {
        db().update(AWARD_RECORD).set(AWARD_RECORD.USER_NUMBER, AWARD_RECORD.USER_NUMBER.add(INTEGER_ONE)).where(AWARD_RECORD.ID.eq(id)).execute();
        return db().select(AWARD_RECORD.USER_NUMBER).from(AWARD_RECORD).where(AWARD_RECORD.ID.eq(id)).fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }

	/**
	 * 营销日历用id查询活动
	 *
	 * @param id
	 * @return
	 */
	public MarketVo getActInfo(Integer id) {
		return db()
				.select(AWARD.ID, AWARD.NAME.as(CalendarAction.ACTNAME), AWARD.START_TIME, AWARD.END_TIME,
						AWARD.IS_FOREVER.as(CalendarAction.ISPERMANENT))
				.from(AWARD).where(AWARD.ID.eq(id)).fetchAnyInto(MarketVo.class);
	}

	/**
	 * 营销日历用查询目前正常的活动
	 *
	 * @param param
	 * @return
	 */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record5<Integer, String, Timestamp, Timestamp, Byte>, Integer> select = db()
				.select(AWARD.ID, AWARD.NAME.as(CalendarAction.ACTNAME), AWARD.START_TIME, AWARD.END_TIME,
						AWARD.IS_FOREVER.as(CalendarAction.ISPERMANENT))
				.from(AWARD).where(AWARD.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(AWARD.STATUS
						.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(AWARD.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(AWARD.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}
}

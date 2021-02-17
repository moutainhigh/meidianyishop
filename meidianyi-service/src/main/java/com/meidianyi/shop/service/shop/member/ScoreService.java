package com.meidianyi.shop.service.shop.member;

import static com.meidianyi.shop.db.shop.Tables.TRADES_RECORD;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_SCORE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.DAY;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MONTH;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.WEEK;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.NO_USE_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.REFUND_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.USED_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.TRADE_CONTENT_SCORE;
import static com.meidianyi.shop.service.shop.member.BaseScoreCfgService.SCORE_LT_NOW;
import static com.meidianyi.shop.service.shop.member.BaseScoreCfgService.SCORE_LT_YMD;
import static com.meidianyi.shop.service.shop.member.UserCardService.UPGRADE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.ShopCfgRecord;
import com.meidianyi.shop.db.shop.tables.records.TradesRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.db.shop.tables.records.UserScoreRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.RemarkUtil;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageTemplateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.score.CheckSignVo;
import com.meidianyi.shop.service.pojo.shop.member.score.ScorePageInfo;
import com.meidianyi.shop.service.pojo.shop.member.score.ScorePageListParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScorePageListVo;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreSignParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreSignVo;
import com.meidianyi.shop.service.pojo.shop.member.score.SignData;
import com.meidianyi.shop.service.pojo.shop.member.score.UserScoreSetValue;
import com.meidianyi.shop.service.pojo.shop.member.score.UserScoreVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.wxapp.score.ExpireVo;
import com.meidianyi.shop.service.shop.member.dao.ScoreDaoService;
import com.meidianyi.shop.service.shop.order.trade.TradesRecordService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;

/**
 * @author 黄壮壮
 * @Date: 2019年8月23日
 * @Description: 积分服务
 */
@Service
public class ScoreService extends ShopBaseService {

    @Autowired
    private TradesRecordService tradesRecord;
    @Autowired
    private ScoreDaoService scoreDao;

    @Autowired
    public UserCardService userCardService;
    @Autowired
    public ScoreCfgService score;
    @Autowired
    public ScoreCfgService scoreCfgService;
    @Autowired
    public MemberService member;
    @Autowired
    public TagService tagSvc;

    /**
     * 创建用户积分表,增加，消耗用户积分
     *
     * @param param     积分变动相关数据
     * @param adminUser 操作员id
     * @param tradeType 交易类型说明  类型{@link com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum }
     * @param tradeFlow 资金流向类型   {@link com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum }
     * @return JsonResultCode
     * @throws MpException
     */


    public void updateMemberScore(ScoreParam param, Integer adminUser, Byte tradeType,
                                  Byte tradeFlow) throws MpException {
        logger().info("用户积分更新");
        UserScoreRecord userScoreRecord = populateUserScoreRecord(param, adminUser);
        UserRecord dbUser = member.getUserRecordById(param.getUserId());

        if (dbUser == null) {
            throw new MpException(JsonResultCode.CODE_MEMEBER_NOT_EXIST);
        }

        try {
            this.transaction(() -> {
                Integer score = param.getScore();
                if (score < 0) {
                    // 消耗积分
                    consumeScore(param, userScoreRecord, dbUser, score);
                } else if (param.getIsFromRefund() != null &&
                    NumberUtils.BYTE_ONE.equals(param.getIsFromRefund())) {
                    // 退款处理积分
                    userScoreRecord.setStatus(REFUND_SCORE_STATUS);
                    UserScoreRecord userScore = getScoreRecordByOrderSn(param.getUserId(), param.getOrderSn());
                    if (userScore != null) {
                        userScoreRecord.setScore(Math.abs(userScore.getScore()));
                        userScoreRecord.setUsableScore(Math.abs(userScore.getScore()));
                        userScoreRecord.setExpireTime(userScore.getExpireTime());
                    }
                } else {
                    userScoreRecord.setStatus(NO_USE_SCORE_STATUS);
                }

                userScoreRecord.insert();
                Integer totalScore = updateUserScore(param);


                String subscribueRemark = null;
                if (RemarkTemplate.USER_INPUT_MSG.code.equals(param.getRemarkCode())) {
                    subscribueRemark = param.getRemarkData();
                } else {
                    String message = RemarkTemplate.getMessageByCode(param.getRemarkCode());
                    subscribueRemark = Util.translateMessage(null, message, "", "remark", param.getRemarkData());
                }

                // 订阅消息
                processSubscribeData(param, score, totalScore, subscribueRemark);

            });
            // TODO CRM

            insertTradesRecord(param, tradeType, tradeFlow);

            String userGrade = member.card.getUserGrade(param.getUserId());
            if (!CardConstant.LOWEST_GRADE.equals(userGrade)) {
                try {
                    // 升级
                    userCardService.updateGrade(param.getUserId(), null, UPGRADE);
                } catch (MpException e) {
                    logger().info("没有可升级的会员卡");
                }
            }

            if (adminUser == 0) {
                String strScore = (param.getScore() >= 0) ? "+" + param.getScore() : "" + param.getScore();
                saas().getShopApp(getShopId()).record.insertRecord(
                    Arrays.asList(new Integer[]{RecordContentTemplate.MEMBER_INTEGRALT.code}),
                    String.valueOf(dbUser.getUserId()), dbUser.getUsername(), strScore);
            }
        } catch (DataAccessException e) {
            logger().info("从事务抛出的DataAccessException中获取我们自定义的异常");
            Throwable cause = e.getCause();
            if (cause instanceof MpException) {
                MpException mpEx = (MpException) cause;
                if (mpEx.getErrorCode().getCode() == JsonResultCode.CODE_MEMBER_SCORE_EXPIRED.getCode()) {
                    logger().info("刷新用户积分，去掉过期积分");
                    updateUserScore(param);
                }
                throw mpEx;
            }
            throw e;
        }
    }

    private void processSubscribeData(ScoreParam param, Integer score, Integer totalScore, String subscribueRemark) {
        String[][] maData = new String[][]{
            {String.valueOf(Math.abs(score))},
            {String.valueOf(Math.abs(totalScore))},
            {subscribueRemark}
        };
        List<Integer> arrayList = Collections.<Integer>singletonList(param.getUserId());
        MaSubscribeData data = MaSubscribeData.builder().data307(maData).build();
        RabbitMessageParam param2 = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.AUDIT).data(data).build())
            .page("pages1/integral/integral").shopId(getShopId())
            .userIdList(arrayList)
            .type(MessageTemplateConfigConstant.POINTS_CONSUMPTION).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    private void consumeScore(ScoreParam param, UserScoreRecord userScoreRecord, UserRecord dbUser, Integer score) throws MpException {
        logger().info("当前需要消耗积分：" + Math.abs(score) + "用户目前拥有积分： " + dbUser.getScore());
        if (Math.abs(score) > dbUser.getScore()) {
            logger().info("消耗的积分超出可用积分:");
            throw new MpException(JsonResultCode.CODE_MEMBER_SCORE_ERROR);
        }

        if (param.getIsFromOverdue() == null ||
            NumberUtils.BYTE_ZERO.equals(param.getIsFromOverdue())) {
            /**过期不消耗积分 */
            boolean result = useUserScore(param.getUserId(), Math.abs(score),
                param.getOrderSn() != null ? param.getOrderSn() : "");
            if (!result) {
                logger().info("消耗积分异常,积分过期");
                throw new MpException(JsonResultCode.CODE_MEMBER_SCORE_EXPIRED);
            }
        }
        userScoreRecord.setStatus(USED_SCORE_STATUS);
    }

    /**
     * 更新用户积分
     *
     * @param param
     * @return
     */
    private Integer updateUserScore(ScoreParam param) {
        logger().info("更新用户积分");
        Integer totalScore = getTotalAvailableScoreById(param.getUserId());
        updateUserScore(param.getUserId(), totalScore);
        return totalScore;
    }


    private UserScoreRecord populateUserScoreRecord(ScoreParam param, Integer adminUser) {

        if (param.getRemarkCode() == null) {
            if (StringUtils.isBlank(param.getRemarkData())) {
                // 默认管理员操作
                param.setRemarkCode(RemarkTemplate.ADMIN_OPERATION.code);
            } else {
                // 用户输入
                param.setRemarkCode(RemarkTemplate.USER_INPUT_MSG.code);
            }
        }

        String orderSn = "";
        boolean canGetOrderSn = param.getChangeWay() == null || (
            param.getChangeWay().equals(60)
                || param.getChangeWay().equals(10));
        if (canGetOrderSn) {
            orderSn = param.getOrderSn();
        }

        UserScoreRecord userScoreRecord = db().newRecord(USER_SCORE);
        userScoreRecord.setAdminUser(String.valueOf(adminUser));
        userScoreRecord.setCreateTime(DateUtils.getLocalDateTime());
        userScoreRecord.setFlowNo(generateFlowNo());
        userScoreRecord.setUsableScore(param.getScore() > 0 ? param.getScore() : 0);
        // userScoreRecord.setScoreProportion(this.saas.getShopApp(getShopId()).score.getScoreProportion());

        if (param.getIsFromCrm() != null &&
            !NumberUtils.BYTE_ZERO.equals(param.getIsFromCrm())) {
            // TODO CRM
        } else {
            userScoreRecord.setExpireTime(getScoreExpireTime());
        }

        userScoreRecord.setScore(param.getScore());
        userScoreRecord.setUserId(param.getUserId());
        userScoreRecord.setRemarkId(String.valueOf(param.getRemarkCode()));
        userScoreRecord.setRemarkData(param.getRemarkData());
        if (orderSn != null) {
            userScoreRecord.setOrderSn(orderSn);
        }
        userScoreRecord.setStatus(param.getScoreStatus());
        if (param.getDesc() != null) {
            userScoreRecord.setDesc(param.getDesc());
        }
        if (param.getGoodsId() != null) {
            userScoreRecord.setGoodsId(param.getGoodsId());
        }
        userScoreRecord.setIdentityId(param.getIdentityId());
        return userScoreRecord;
    }


    private void insertTradesRecord(ScoreParam param, Byte tradeType, Byte tradeFlow) {
        TradesRecordRecord tradesRecord = db().newRecord(TRADES_RECORD);
        tradesRecord.setTradeTime(DateUtils.getLocalDateTime());
        tradesRecord.setTradeNum(BigDecimal.valueOf(Math.abs(param.getScore())));
        tradesRecord.setTradeSn(param.getOrderSn() != null ? param.getOrderSn() : "");
        tradesRecord.setUserId(param.getUserId());
        /** -这是更新积分的明细所以交易内容为积分 */
        tradesRecord.setTradeContent(TRADE_CONTENT_SCORE.val());
        tradesRecord.setTradeType(tradeType);
        tradesRecord.setTradeFlow(tradeFlow);
        tradesRecord.setTradeStatus(tradeFlow);

        /** -交易记录表-记录交易的数据信息  */
        tradesRecord.insert();
    }


    /**
     * 通过订单编号获取用户积分记录
     *
     * @param userId  用户id
     * @param orderSn 订单号
     * @return UserScoreRecord
     */
    public UserScoreRecord getScoreRecordByOrderSn(Integer userId, String orderSn) {
        return db().selectFrom(USER_SCORE).where(USER_SCORE.USER_ID.eq(userId))
            .and(USER_SCORE.ORDER_SN.eq(orderSn))
            .orderBy(USER_SCORE.CREATE_TIME)
            .fetchAnyInto(UserScoreRecord.class);
    }

    /**
     * 更新用户积分
     *
     * @param userId
     * @param totalScore
     */
    public void updateUserScore(Integer userId, Integer totalScore) {
        db().update(USER).set(USER.SCORE, totalScore).where(USER.USER_ID.eq(userId)).execute();
    }

    /**
     * 获取用户可用积分
     *
     * @param userId
     */
    public Integer getTotalAvailableScoreById(Integer userId) {
        return scoreDao.calculateAvailableScore(userId);
    }


    /**
     * 消耗积分记录
     *
     * @param userId     用户id
     * @param score      消耗积分的绝对值
     * @param identityId 关联其他属性：例如order_sn
     * @return true操作成功，false无可用积分
     */
    public boolean useUserScore(Integer userId, int score, String identityId) {
        while (true) {
            /** 1 获取最早一条可用记录 */
            UserScoreRecord userRecord = getEarlyUsableRecord(userId);

            if (userRecord == null) {
                logger().info("暂无可用积分");
                return false;
            }

            /** 更新 关联其他属性：例如order_sn */
            if (!StringUtils.isBlank(identityId)) {
                userRecord.setIdentityId(userRecord.getIdentityId() + "," + identityId);
            }

            /** 2. 处理消耗score值 */
            if (score < userRecord.getUsableScore()) {
                int usableScore = userRecord.getUsableScore() - score;
                userRecord.setUsableScore(usableScore);
                /** 3. 更新记录 */
                updateUserScoreRecord(userRecord);
                break;
            } else {
                /** 4. 更新要插入的数据值,设置记录状态为已使用 并且 可用积分为0  */
                score = score - userRecord.getUsableScore();
                userRecord.setStatus(USED_SCORE_STATUS);
                userRecord.setUsableScore(0);
                updateUserScoreRecord(userRecord);
                if (score <= 0) {
                    break;
                }
            }
        }
        return true;
    }

    /**
     * 更新用户积分记录表b2c_user_score
     *
     * @param record
     * @return int 执行结果
     */
    private int updateUserScoreRecord(UserScoreRecord record) {

        int result = db().update(USER_SCORE).set(USER_SCORE.USABLE_SCORE, record.getUsableScore())
            .set(USER_SCORE.STATUS, record.getStatus()).where(USER_SCORE.USER_ID.eq(record.getUserId()))
            .and(USER_SCORE.FLOW_NO.eq(record.getFlowNo())).execute();

        return result;
    }

    /**
     * 获取一条用户可用的最早积分记录
     *
     * @param userId 用户id
     * @return UserScoreRecord
     */
    public UserScoreRecord getEarlyUsableRecord(Integer userId) {
        return scoreDao.getTheEarliestUsableUserScoreRecord(userId);
    }

    /**
     * 生成流水号
     *
     * @return string 流水号
     */
    private String generateFlowNo() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String result = null;
        Random random = new Random();
        while (true) {
            LocalDateTime now = LocalDateTime.now();
            /** 生成四位随机数字 */
            int num = random.nextInt(9000) + 1000;
            result = "S" + now.format(f) + num;

            /** 确保流水号的唯一性 */
            int count = db().fetchCount(USER_SCORE, USER_SCORE.FLOW_NO.eq(result));

            if (count == 0) {
                break;
            }
        }
        return result;
    }


    /**
     * 分页查询会员用户积分详情
     *
     * @param param
     * @return PageResult<ScorePageListVo>
     */
    public PageResult<ScorePageListVo> getPageListOfScoreDetails(ScorePageListParam param, String language) {
        SelectJoinStep<? extends Record> select = db()
            .select(USER.USERNAME, USER.MOBILE, USER_SCORE.ORDER_SN, USER_SCORE.SCORE, USER_SCORE.CREATE_TIME, USER_SCORE.EXPIRE_TIME
                , USER_SCORE.REMARK_DATA, USER_SCORE.REMARK_ID)
            .from(USER_SCORE.join(USER).on(USER.USER_ID.eq(USER_SCORE.USER_ID)));


        buildOptions(select, param);
        select.orderBy(USER_SCORE.CREATE_TIME.desc());

        PageResult<ScorePageInfo> resultBefore = getPageResult(select, param.getCurrentPage(), param.getPageRows(), ScorePageInfo.class);

        return scoreInfoToScoreVo(language, resultBefore);

    }

    /**
     * 数据类型转换
     */
    public PageResult<ScorePageListVo> scoreInfoToScoreVo(String language, PageResult<ScorePageInfo> resultBefore) {
        PageResult<ScorePageListVo> resultAfter = new PageResult<>();
        List<ScorePageListVo> dataList = new ArrayList<>();
        for (ScorePageInfo scoreItem : resultBefore.dataList) {
            ScorePageListVo scoreVo = new ScorePageListVo();
            BeanUtils.copyProperties(scoreItem, scoreVo);
            String remark = RemarkUtil.remarkI18N(language, scoreItem.getRemarkId(), scoreItem.getRemarkData());
            scoreVo.setRemark(remark);
            dataList.add(scoreVo);
        }
        resultAfter.setPage(resultBefore.getPage());
        resultAfter.setDataList(dataList);
        return resultAfter;
    }


    /**
     * 分页查询用户积分明细-积分明细时其他查询条件
     *
     * @param select
     * @param param
     */
    private void buildOptions(
        SelectJoinStep<? extends Record> select,
        ScorePageListParam param) {
        logger().info("正在构建查询条件");

        /** 会员id-会员昵称，优先id */
        if (param.getUserId() != null) {
            select.where(USER_SCORE.USER_ID.eq(param.getUserId()));
        } else if (!StringUtils.isBlank(param.getUserName())) {
            String likeValue = likeValue(param.getUserName());
            /** 查询出所有符合昵称的会员id */
            List<Integer> ids = db().select(USER.USER_ID).from(USER).where(USER.USERNAME.like(likeValue)).fetchInto(Integer.class);
            select.where(USER_SCORE.USER_ID.in(ids));
        }

        /** 订单号 */
        if (!StringUtils.isEmpty(param.getOrderSn())) {
            select.where(USER_SCORE.ORDER_SN.like(likeValue(param.getOrderSn())));
        }

        /** 时间范围 */
        /** 开始时间 */
        if (param.getStartTime() != null) {
            select.where(USER_SCORE.CREATE_TIME.ge(param.getStartTime()));
        }
        /** 结束时间 */
        if (param.getEndTime() != null) {
            select.where(USER_SCORE.CREATE_TIME.le(param.getEndTime()));
        }

        /**类型 */
        if ("wxapp".equals(param.getType())) {
            if (param.getUserId() != null) {
                select.where(USER.USER_ID.eq(param.getUserId()));
            }
        }
    }


    /**
     * 累计积分
     *
     * @return 返回用户累计积分，没有则为0
     */
    public Integer getAccumulationScore(Integer userId) {
        logger().info("获取用户的所有累积积分");
        return scoreDao.calculateAccumulationScore(userId);
    }

    /**
     * 查询今天是否有某种积分
     *
     * @param userId
     * @param desc
     * @return
     */
    public UserScoreRecord getScoreInDay(Integer userId, String desc) {
        return db()
            .selectFrom(
                USER_SCORE)
            .where(USER_SCORE.USER_ID.eq(userId).and(
                USER_SCORE.DESC.eq(desc).and((dateFormat(USER_SCORE.CREATE_TIME, DateUtils.DATE_MYSQL_SIMPLE))
                    .eq(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE)))))
            .fetchAny();
    }

    /**
     * 创建用户积分记录，发返1 为成功
     *
     * @param data
     * @param adminUser
     * @param tradeType
     * @param tradeFlow
     */
    @Deprecated
    public Integer addUserScore(UserScoreVo data, String adminUser, Byte tradeType, Byte tradeFlow) {
        // 生成新的充值记录
        // 验证现有积分跟提交的积分是否一致
        // 销毁score_dis
        int insert = 0;
        try {

            if (data.getRemarkCode() == null) {
                //data.setRemark("管理员操作");
                data.setRemarkCode(RemarkTemplate.ADMIN_OPERATION.code);
            }
            UserScoreRecord record = db().newRecord(USER_SCORE);
            FieldsUtil.assignNotNull(data, record);
            record.setRemarkId(String.valueOf(data.getRemarkCode()));
            record.setAdminUser(adminUser);
            record.setFlowNo(generateFlowNo());
            record.setUsableScore(data.getScore() > 0 ? data.getScore() : 0);
            if (data.getIsFromCrm()) {
                record.setExpireTime(getScoreExpireTime());
            }
            if (data.getScore() < 0) {
                if (data.getIsFromOverdue()) {
                    boolean useUserScore = useUserScore(data.getUserId(), Math.abs(data.getScore()), data.getOrderSn());
                    if (!useUserScore) {
                        logger().info("消耗积分异常：" + data.toString());
                    }
                }
            } else if (data.getIsFromRefund()) {
                // 退款处理积分
                logger().info("退款处理积分");
                record.setStatus(REFUND_SCORE_STATUS);
                UserScoreRecord scoreRecordByOrderSn = getScoreRecordByOrderSn(data.getUserId(), data.getOrderSn());
                record.setExpireTime(scoreRecordByOrderSn.getExpireTime() != null ? scoreRecordByOrderSn.getExpireTime() : data.getExpireTime());
            }
            //TODO  UserScore.php $crmResult = shop($this->getShopId())->serviceRequest->crmApi->init();

            logger().info("开始插入");
            insert = record.insert();
            if (insert <= 0) {
                logger().info("插入失败");
            }
            //TODO    shop($this->getShopId())->serviceRequest->crmApi->syncUserScore($data['user_id'], $data);
        } catch (Exception e) {
            logger().error("addUserScore error, message:");
            logger().error(e.getMessage(), e);
            return -2;
        }
        //更新用户积分
        Integer canUseScore = getTotalAvailableScoreById(data.getUserId());
        db().update(USER).set(USER.SCORE, canUseScore).where(USER.USER_ID.eq(data.getUserId())).execute();
        if (data.getScore() < 0) {
            tradeFlow = 0;
        }
        tradesRecord.addRecord(new BigDecimal(Math.abs(data.getScore())), data.getOrderSn() == null ? "" : data.getOrderSn(), data.getUserId(), (byte) 1, tradeType, tradeFlow, tradeFlow);
        String userGrade = member.card.getUserGrade(data.getUserId());
        if (!userGrade.equals(CardConstant.LOWEST_GRADE)) {
            try {
                // 升级
                userCardService.updateGrade(data.getUserId(), null, UPGRADE);
            } catch (MpException e) {
                logger().info("没有可升级的会员卡");
                return -1;
            }
        }
        return insert;
    }


    /**
     * 获取积分过期时间
     *
     * @return
     */
    public Timestamp getScoreExpireTime() {
        logger().info("获取积分过期时间");
        Timestamp expireTime = null;
        Byte scoreLimit = scoreCfgService.getScoreLimit();
        LocalDate date = LocalDate.now();

        if (SCORE_LT_YMD.equals(scoreLimit)) {
            logger().info("根据年月日计算过期时间");
            Integer scoreYear = scoreCfgService.getScoreYear();
            Integer scoreMonth = scoreCfgService.getScoreMonth();
            Integer scoreDay = scoreCfgService.getScoreDay();
            LocalDateTime localDateTime = LocalDateTime.of(date.getYear() + scoreYear, scoreMonth, scoreDay, 23, 59, 59);
            expireTime = Timestamp.valueOf(localDateTime);
        }

        if (SCORE_LT_NOW.equals(scoreLimit)) {
            logger().info("根据领取之日起计算有效时间");
            Integer scoreLimitNumber = scoreCfgService.getScoreLimitNumber();
            Integer scorePeriod = scoreCfgService.getScorePeriod();

            LocalDateTime localDateTime = LocalDateTime.now();

            if (DAY.equals(scorePeriod)) {
                localDateTime = localDateTime.plusDays(scoreLimitNumber);
            } else if (WEEK.equals(scorePeriod)) {
                localDateTime = localDateTime.plusWeeks(scoreLimitNumber);
            } else if (MONTH.equals(scorePeriod)) {
                localDateTime = localDateTime.plusMonths(scoreLimitNumber);
            }
            expireTime = Timestamp.valueOf(localDateTime);
        }
        logger().info("获取积分过期时间" + expireTime);
        return expireTime;
    }

    /**
     * 检查签到送积分
     */
    public CheckSignVo checkSignInScore(Integer userId) {
        logger().info("进入检查签到送积分");
        UserScoreSetValue signInScore = scoreCfgService.getScoreValueThird("sign_in_score");
        int days = 0;
        int scoreValue = 0;
        int isSignIn = 0;
        int day = 0;
        int isOpenSign = 0;
        SignData signData = new SignData();
        if (signInScore != null) {
            if (signInScore.getScore() != null) {
                for (String value : signInScore.getScore()) {
                    days++;
                    scoreValue += Integer.parseInt(value);
                }
            }
            String receiveScore = null;
            if (signInScore.getEnable() == (byte) 1) {
                isOpenSign = 1;
                Byte signInRules = signInScore.getSignInRules() == null ? (byte) 0 : signInScore.getSignInRules();
                if (checkUserIsSign(userId)) {
                    // 未签到
                    logger().info("未签到");
                    isSignIn = 0;
                    // 判断当前是第几天领取
                    day = checkDayByUserSignIn(userId, false);
                    if (days != 0 && day > days) {
                        if (signInRules.equals(NumberUtils.BYTE_ONE)) {
                            logger().info("未签到-循环签到判断");
                            //当签到天数超过设置的天数时，取余当做当前签到天数
                            day = day % days;
                            if (day == 0) {
                                day++;
                            }
                        }
                    }
                    // 今天领取多少积分
                    String[] score2 = signInScore.getScore();
                    receiveScore = getReceiveScore(score2, day, 1);
                } else {
                    // 已签到
                    logger().info("已签到");
                    isSignIn = 1;
                    day = checkDayByUserSignIn(userId, true) - 1;
                    UserScoreRecord scoreInDay = getScoreInDay(userId, "sign_score");
                    if (scoreInDay == null) {
                        logger().error("已签到时表中为空，请检查日志");
                        receiveScore = "0";
                    } else {
                        receiveScore = String.valueOf(scoreInDay.getScore());
                    }
                    if (signInRules.equals(NumberUtils.BYTE_ONE)) {
                        logger().info("已签到-循环签到判断");
                        //当签到天数超过设置的天数时，取余当做当前签到天数
                        day = day % days;
                        if (day == 0) {
                            day++;
                        }
                    }
                }
                signData.setIsSignIn(isSignIn);
                signData.setDay(day);
                signData.setReceiveScore(receiveScore);
                signData.setTomoroowReceive(getReceiveScore(signInScore.getScore(), day, 0));
                signData.setMaxSignDay(days);
                signData.setScoreValue(scoreValue);
                signData.setSignInRules(signInRules);

            } else {
                isOpenSign = 0;
            }
        }
        logger().info("signInScore是空的");
        CheckSignVo vo = new CheckSignVo();
        vo.setIsOpenSign(isOpenSign);
        if (signInScore != null) {
            vo.setSignData(signData);
            vo.setSignRule(signInScore.getScore().length <= 0 ? new String[0] : signInScore.getScore());
        } else {
            vo.setSignData(null);
            vo.setSignRule(new String[0]);
        }
        logger().info("进入检查签到送积分结束");
        return vo;
    }

    private String getReceiveScore(String[] score2, Integer day, Integer option) {
        String receiveScore = null;
        if (score2.length <= 0) {
            receiveScore = "0";
        } else {
            if (day < 1 || day >= score2.length) {
                receiveScore = score2[score2.length - 1];
            } else {
                receiveScore = score2[day - option];
            }
        }
        return receiveScore;
    }

    /**
     * 检查用户是否已经签到  true: 未签到
     *
     * @param userId
     * @return
     */
    public boolean checkUserIsSign(Integer userId) {
        UserScoreRecord record = db().selectFrom(USER_SCORE).where(USER_SCORE.USER_ID.eq(userId))
            .and(USER_SCORE.DESC.eq("sign_score")).orderBy(USER_SCORE.ID.desc()).fetchAny();
        if (record != null) {
            if (DateUtils.timestampIsNowDay(record.getCreateTime())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断用户是第几天签到
     *
     * @param userId
     * @param signOk
     * @return
     */
    public int checkDayByUserSignIn(Integer userId, Boolean signOk) {
        Result<UserScoreRecord> list = db().selectFrom(USER_SCORE).where(USER_SCORE.USER_ID.eq(userId))
            .and(USER_SCORE.DESC.eq("sign_score")).orderBy(USER_SCORE.ID.desc()).fetch();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime localDateTime = LocalDateTime.now();
        int day = 1;
        String date = signOk ? df.format(localDateTime) : df.format(localDateTime.minusDays(1L));
        int flag = 0;
        for (UserScoreRecord record : list) {
            if (flag == 1) {
                break;
            }
            if (date.equals(df.format(record.getCreateTime().toLocalDateTime()))) {
                ++day;
            } else {
                flag = 1;
            }
            date = df.format(localDateTime.minusDays(1L));
        }
        return day;
    }

    /**
     * 获取即将过期积分
     */
    public Integer getExpireScore(Integer userId, Timestamp endTime) {
        logger().info("获取即将过期积分");

        if (endTime == null) {
            // endTime为空，那么就默设置为一年后
            LocalDateTime oneYearLater = LocalDateTime.now().plusYears(1L);
            endTime = Timestamp.valueOf(oneYearLater);
        }
        return scoreDao.calculateWillExpireSoonScore(endTime, userId);
    }

    /**
     * 获取用户积分数据
     *
     * @param userId
     * @return
     */
    public ExpireVo getUserScoreCfg(Integer userId) {
        ShopCfgRecord scoreLimitRecord = scoreCfgService.getScoreNum("score_limit");
        ExpireVo vo = new ExpireVo();
        if (scoreLimitRecord != null) {
            String scoreLimitOn = "1";
            if (scoreLimitRecord.getV().equals(scoreLimitOn)) {
                int scoreYear = Integer.parseInt(scoreCfgService.getScoreNum("score_year").getV());
                int year = LocalDateTime.now().getYear();
                year = year + scoreYear;
                String month = scoreCfgService.getScoreNum("score_month").getV();
                String day = scoreCfgService.getScoreNum("score_day").getV();
                if (Integer.parseInt(month) < 10) {
                    month = "0" + month;
                }
                if (Integer.parseInt(day) < 10) {
                    day = "0" + day;
                }
                vo.setTime(String.valueOf(year) + "-" + month + "-" + day + " 23:59:59");
                vo.setScore(getExpireScore(userId, Timestamp.valueOf(vo.getTime())));
            }
            String scoreLimitOff = "0";
            if (scoreLimitRecord.getV().equals(scoreLimitOff)) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_SIMPLE);
                LocalDateTime localDateTime = LocalDateTime.now();
                vo.setTime(df.format(localDateTime));
                vo.setScore(-1);
            }
        }
        return vo;
    }

    /**
     * 用户表里的积分
     *
     * @param userId
     * @return
     */
    public int getUserScore(int userId) {
        return db().select(USER.SCORE).from(USER).where(USER.USER_ID.eq(userId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 获取用户签到积分
     */
    public PageResult<ScoreSignVo> userSign(ScoreSignParam param) {
        logger().info("获取用户连续签到相关信息");
        PageResult<ScoreSignVo> res = new PageResult<>();
        PageResult<? extends Record> signList = scoreDao.getAllSignList(param);
        res.setPage(signList.getPage());
        res.setDataList(new ArrayList<ScoreSignVo>());

        // 一次性查询用户所拥有的标签
        List<Integer> userIds = signList.dataList.stream().map(r -> r.get(USER_SCORE.USER_ID)).distinct().collect(Collectors.toList());
        Map<Integer, List<TagVo>> tagMap = tagSvc.getUserTag(userIds);

        for (Record record : signList.dataList) {
            ScoreSignVo vo = record.into(ScoreSignVo.class);
            // 处理连续签到的天数和积分数
            Map<String, Integer> map = scoreDao.checkDays(record.get(USER_SCORE.USER_ID), record.get(USER_SCORE.CREATE_TIME), record.get(USER_SCORE.USABLE_SCORE));
            vo.setContinueDays(map.get(ScoreDaoService.SIGN_DAY));
            vo.setTotalScore(map.get(ScoreDaoService.SIGN_SCORE));
            // 处理用户标签
            List<TagVo> tags = tagMap.get(record.get(USER_SCORE.USER_ID));
            if (tags != null && tags.size() > 0) {
                StringBuilder userTag = new StringBuilder(), userShowTag = new StringBuilder();
                for (TagVo tag : tags) {
                    int i = 0;
                    userTag.append(tag.getTagName()).append(";");
                    if (i <= 5) {
                        userShowTag.append(tag.getTagName()).append(";");
                    } else if (i == 6) {
                        // showTag最多显示个标签
                        userShowTag.append("...");
                    }
                }
                vo.setUserTag(userTag.toString());
                vo.setUserShowTag(userShowTag.toString());
            }
            res.dataList.add(vo);
        }
        return res;
    }

}

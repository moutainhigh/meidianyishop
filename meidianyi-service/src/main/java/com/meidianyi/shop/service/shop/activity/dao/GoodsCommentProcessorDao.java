package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.CommentDetailVo;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record11;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * @author 李晓冰
 * @date 2019年11月04日
 */
@Service
@Slf4j
public class GoodsCommentProcessorDao extends BaseShopConfigService {

    /**
     * 评价审核控制
     */
    final private static String K_COMMENT = "comment";
    /**
     * 设置前端是否显示未填写心得的评价
     */
    final private static String K_COMMENT_STATE = "comment_state";
    /**
     * 审核配置对应码  0不审，1先发后审，2先审后发
     */
    private static final Byte NOT_AUDIT = 0;
    private static final Byte PUBLISH_FIRST = 1;
    /**
     * 显示未填写心得评论配置值
     */
    private static final Byte SHOW_STATE = 1;

    /***商品评价状态对应码*/
    public static final Byte PASS_AUDIT = 1;
    public static final Byte NOT_PASS_AUDIT = 2;

    /**
     * 获取集合内商品评级数量
     * 评价状态为 0:未审批,1:审批通过,2:审批未通过
     * 配置项为0不用审核：用户发表评价时评价状态自动为1,查询数据时查询状态为0和1的记录
     * 配置项为1先发后审: 用户发表评价时状态为0，查询数据时查询状态为0和1的记录
     * 配置项为2先审后发：用户发表评价时状态为1，查询数据时查询状态为1的记录
     * 是否显示未填写心得评价开关：K_COMMENT_STATE 0:不展示未填写心得的评论 1:显示未填写心得的评论 （心得：COMMENT_GOODS：comm_note）
     *
     * @param goodsIds 商品id集合
     */
    public Map<Integer, Long> getGoodsCommentNumInfo(List<Integer> goodsIds) {
        Byte commentConfig = get(K_COMMENT, Byte.class, (byte) 0);
        Byte commentStateConfig = get(K_COMMENT_STATE, Byte.class, (byte) 1);

        Condition auditCondition = DSL.noCondition();
        if (NOT_AUDIT.equals(commentConfig) || PUBLISH_FIRST.equals(commentConfig)) {
            auditCondition = auditCondition.and(COMMENT_GOODS.FLAG.ne(NOT_PASS_AUDIT));
        } else {
            auditCondition = auditCondition.and(COMMENT_GOODS.FLAG.eq(PASS_AUDIT));
        }

        if (SHOW_STATE.equals(commentStateConfig)) {
            auditCondition = auditCondition.and(COMMENT_GOODS.COMM_NOTE.isNotNull());
        }

        Map<Integer, Long> commentNumMap = db().select(COMMENT_GOODS.GOODS_ID).from(COMMENT_GOODS)
            .where(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(COMMENT_GOODS.GOODS_ID.in(goodsIds))
            .and(auditCondition)
            .fetch().stream().collect(Collectors.groupingBy(x -> x.get(COMMENT_GOODS.GOODS_ID), Collectors.counting()));

        return commentNumMap;
    }


    public CommentDetailVo getGoodsCommentInfoForDetail(Integer goodsId) {
        Byte commentConfig = get(K_COMMENT, Byte.class, (byte) 0);
        Byte commentStateConfig = get(K_COMMENT_STATE, Byte.class, (byte) 1);

        Condition condition;
        // 通过店铺审核配置情况设置过滤条件，如果是不审核或者先审后发则可查询0或1状态
        if (NOT_AUDIT.equals(commentConfig) || PUBLISH_FIRST.equals(commentConfig)) {
            condition = COMMENT_GOODS.FLAG.ne(NOT_PASS_AUDIT);
        } else {
            condition = COMMENT_GOODS.FLAG.eq(PASS_AUDIT);
        }
        if (SHOW_STATE.equals(commentStateConfig)) {
            condition = condition.and(COMMENT_GOODS.COMM_NOTE.isNotNull());
        }
        // 获取最近的一次评级详情
        log.debug("商品详情-查询商品最近一次评价详情信息");
        Record11<Integer, Byte, String, Byte, String, Timestamp, String, String, String, String, String> record = db().select(COMMENT_GOODS.ID, COMMENT_GOODS.ANONYMOUSFLAG, COMMENT_GOODS.COMM_NOTE, COMMENT_GOODS.COMMSTAR, COMMENT_GOODS.COMM_IMG,
            COMMENT_GOODS.CREATE_TIME, USER_DETAIL.USERNAME, USER_DETAIL.USER_AVATAR,
            COMMENT_GOODS.BOGUS_USERNAME, COMMENT_GOODS.BOGUS_USER_AVATAR, ORDER_GOODS.GOODS_ATTR)
            .from(COMMENT_GOODS).leftJoin(ORDER_GOODS).on(COMMENT_GOODS.ORDER_SN.eq(ORDER_GOODS.ORDER_SN))
            .leftJoin(USER_DETAIL).on(COMMENT_GOODS.USER_ID.eq(USER_DETAIL.USER_ID))
            .where(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(COMMENT_GOODS.GOODS_ID.eq(goodsId)).and(condition).orderBy(COMMENT_GOODS.CREATE_TIME.desc())
            .fetchAny();

        if (record == null) {
            log.debug("商品详情-无评价信息");
            return null;
        }

        CommentDetailVo.CommentInfo commentInfo = record.into(CommentDetailVo.CommentInfo.class);
        commentInfo.setCommStar(record.get(COMMENT_GOODS.COMMSTAR));
        commentInfo.setUserName(record.get(USER_DETAIL.USERNAME));

        if (commentInfo.getUserName() == null) {
            commentInfo.setUserName(record.get(COMMENT_GOODS.BOGUS_USERNAME));
        }
        if (commentInfo.getUserAvatar() == null) {
            commentInfo.setUserAvatar(record.get(COMMENT_GOODS.BOGUS_USER_AVATAR));
        }
        final Byte anonymous = 1;
        commentInfo.setAnonymousFlag(record.get(COMMENT_GOODS.ANONYMOUSFLAG));
        if (anonymous.equals(commentInfo.getAnonymousFlag())) {
            commentInfo.setUserAvatar(null);
        }
        if (StringUtils.isNotBlank(record.get(COMMENT_GOODS.COMM_IMG))) {
            String[] imgs = record.get(COMMENT_GOODS.COMM_IMG).split(",");
            List<String> imgList = Arrays.stream(imgs).collect(Collectors.toList());
            commentInfo.setCommImgs(imgList);
        } else {
            commentInfo.setCommImgs(new ArrayList<>());
        }

        Record2<Integer, String> answerRecord = db().select(COMMENT_GOODS_ANSWER.ANSWER_ID, COMMENT_GOODS_ANSWER.CONTENT).from(COMMENT_GOODS_ANSWER).where(COMMENT_GOODS_ANSWER.COMMENT_ID.eq(commentInfo.getId()))
            .and(COMMENT_GOODS_ANSWER.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(COMMENT_GOODS_ANSWER.CREATE_TIME.desc()).fetchAny();
        if (answerRecord != null) {
            commentInfo.setAnswerId(answerRecord.get(COMMENT_GOODS_ANSWER.ANSWER_ID));
            commentInfo.setAnswer(answerRecord.get(COMMENT_GOODS_ANSWER.CONTENT));
        }
        CommentDetailVo vo = new CommentDetailVo();
        vo.setCommentInfo(commentInfo);
        log.debug("商品详情-统计商品所有评价数量");
        List<CommentDetailVo.CommentLevelInfo> commentLevelInfos = calculateGoodsCommentNumInfo(goodsId, commentConfig, commentStateConfig);
        vo.setCommentLevelsInfo(commentLevelInfos);
        return vo;
    }

    /**
     * @param goodsId            商品id
     * @param commentConfig      用户评价配置
     * @param commentStateConfig 是否显示未留言评价
     */
    public List<CommentDetailVo.CommentLevelInfo> calculateGoodsCommentNumInfo(Integer goodsId, Byte commentConfig, Byte commentStateConfig) {
        Condition condition = COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode());
        if (SHOW_STATE.equals(commentStateConfig)) {
            condition = condition.and(COMMENT_GOODS.COMM_NOTE.isNotNull());
        }
        if (NOT_AUDIT.equals(commentConfig) || PUBLISH_FIRST.equals(commentConfig)) {
            condition = condition.and(COMMENT_GOODS.FLAG.ne(NOT_PASS_AUDIT));
        } else {
            condition = condition.and(COMMENT_GOODS.FLAG.eq(PASS_AUDIT));
        }

        ArrayList<Record3<Integer, Byte, String>> recordsList = new ArrayList<>(db().select(COMMENT_GOODS.ID, COMMENT_GOODS.COMMSTAR, COMMENT_GOODS.COMM_IMG).from(COMMENT_GOODS)
            .where(condition).and(COMMENT_GOODS.GOODS_ID.eq(goodsId)).fetch());

        if (recordsList.size() == 0) {
            return null;
        }
        // 各个星级
        Map<Byte, Long> numMap = recordsList.stream().collect(Collectors.groupingBy(x -> x.get(COMMENT_GOODS.COMMSTAR), Collectors.counting()));
        Byte level0 = 0, level1 = 1, level2 = 2, level3 = 3, level4 = 4, level5 = 5;
        long level0Num = numMap.getOrDefault(level0,0L);
        long level1Num = numMap.getOrDefault(level1,0L);
        long level2Num = numMap.getOrDefault(level2,0L);
        long level3Num = numMap.getOrDefault(level3,0L);
        long level4Num = numMap.getOrDefault(level4,0L);
        long level5Num = numMap.getOrDefault(level5,0L);

        List<CommentDetailVo.CommentLevelInfo> rets = new ArrayList<>();
        CommentDetailVo.CommentLevelInfo good = new CommentDetailVo.CommentLevelInfo(CommentDetailVo.GOODS_LEVEL,level5Num+level4Num);
        rets.add(good);
        CommentDetailVo.CommentLevelInfo normal = new CommentDetailVo.CommentLevelInfo(CommentDetailVo.NORMAL_LEVEL,level3Num+level2Num);
        rets.add(normal);
        CommentDetailVo.CommentLevelInfo bad = new CommentDetailVo.CommentLevelInfo(CommentDetailVo.BAD_LEVEL,level1Num+level0Num);
        rets.add(bad);

        Long imgNum = recordsList.stream().filter(x -> StringUtils.isNotBlank(x.get(COMMENT_GOODS.COMM_IMG))&&!"[]".equals(x.get(COMMENT_GOODS.COMM_IMG))).count();
        CommentDetailVo.CommentLevelInfo imgLevel = new CommentDetailVo.CommentLevelInfo(CommentDetailVo.IMG_LEVEL,imgNum);
        rets.add(imgLevel);

        return rets;
    }
}

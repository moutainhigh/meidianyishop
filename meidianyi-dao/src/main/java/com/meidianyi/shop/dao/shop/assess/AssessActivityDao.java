package com.meidianyi.shop.dao.shop.assess;

import static com.meidianyi.shop.db.shop.Tables.ASSESS_ACTIVITY;
import static com.meidianyi.shop.db.shop.Tables.ASSESS_RECORD;
import static com.meidianyi.shop.db.shop.Tables.ASSESS_RESULT;
import static com.meidianyi.shop.db.shop.Tables.ASSESS_TOPIC;

import java.sql.Timestamp;

import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.AssessActivityRecord;
import com.meidianyi.shop.db.shop.tables.records.AssessResultRecord;
import com.meidianyi.shop.db.shop.tables.records.AssessTopicRecord;
import com.meidianyi.shop.service.pojo.shop.assess.AssessActivityListParam;
import com.meidianyi.shop.service.pojo.shop.assess.AssessActivityListVo;
import com.meidianyi.shop.service.pojo.shop.assess.AssessActivityOneParam;
import com.meidianyi.shop.service.pojo.shop.assess.AssessResultOneParam;
import com.meidianyi.shop.service.pojo.shop.assess.AssessTopicOneParam;

/**
 * @author lixinguo
 */
@Repository
public class AssessActivityDao extends ShopBaseDao {
    /**
     * 测评活动列表
     *
     * @param param
     * @return
     */
    public PageResult<AssessActivityListVo> getActivityList(AssessActivityListParam param) {
        SelectJoinStep<? extends Record> select = db()
            .select(ASSESS_ACTIVITY.ID, ASSESS_ACTIVITY.ACT_NAME, ASSESS_ACTIVITY.CREATE_TIME,
                ASSESS_ACTIVITY.START_TIME, ASSESS_ACTIVITY.END_TIME, ASSESS_ACTIVITY.PUB_FLAG)
            .from(ASSESS_ACTIVITY);
        buildOptions(select, param);
        PageResult<AssessActivityListVo> activityList = this.getPageResult(select, param.getCurrentPage(),
            param.getPageRows(), AssessActivityListVo.class);
        return activityList;
    }

    /**
     * 反馈数量
     *
     * @param id
     */
    public int countRecords(Integer id) {
        return db().selectCount().from(ASSESS_RECORD).where(ASSESS_RECORD.ASSESS_ID.eq(id)).fetchOne()
            .into(Integer.class);
    }

    /**
     * 测评结果数
     *
     * @param id
     * @return
     */
    public int countResults(Integer id) {
        int resultNum = db().selectCount().from(ASSESS_RESULT).where(ASSESS_RESULT.ASSESS_ID.eq(id)).fetchOne()
            .into(Integer.class);
        return resultNum;
    }

    /**
     * 测评题目数
     *
     * @param id
     * @return
     */
    public int countTopics(Integer id) {
        int topicNum = db().selectCount().from(ASSESS_TOPIC).where(ASSESS_TOPIC.ASSESS_ID.eq(id)).fetchOne()
            .into(Integer.class);
        return topicNum;
    }

    /**
     * 测评活动列表条件查询
     *
     * @param select
     * @param param
     */
    protected void buildOptions(SelectJoinStep<? extends Record> select, AssessActivityListParam param) {
        Timestamp nowDate = new Timestamp(System.currentTimeMillis());
        if (param.getNav() != null) {
            switch (param.getNav()) {
                // 进行中
                case 1:
                    select.where(ASSESS_ACTIVITY.START_TIME.le(nowDate)).and(ASSESS_ACTIVITY.END_TIME.ge(nowDate));
                    break;
                // 未开始
                case 2:
                    select.where(ASSESS_ACTIVITY.START_TIME.ge(nowDate));
                    break;
                // 已过期
                case 3:
                    select.where(ASSESS_ACTIVITY.END_TIME.le(nowDate));
                    break;
                // 已停用
                case 4:
                    select.where(ASSESS_ACTIVITY.IS_BLOCK.eq((byte) 1));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获取一条测评活动信息
     *
     * @param assessId
     * @return
     */
    public AssessActivityOneParam getOneInfo(Integer assessId) {
        AssessActivityOneParam info = db().select().from(ASSESS_ACTIVITY).where(ASSESS_ACTIVITY.ID.eq(assessId))
            .fetchOne().into(AssessActivityOneParam.class);
        return info;
    }

    /**
     * 编辑保存
     *
     * @param param
     * @return
     */
    public int updateAssess(AssessActivityOneParam param) {
        AssessActivityRecord record = new AssessActivityRecord();
        assign(param, record);
        return db().executeUpdate(record);
    }

    /**
     * 添加测评活动信息
     *
     * @param param
     * @return
     */
    public int insertAssess(AssessActivityOneParam param) {
        AssessActivityRecord record = new AssessActivityRecord();
        assign(param, record);
        return db().executeInsert(record);
    }

    /**
     * 更新活动状态
     *
     * @param assessId
     * @param block    1停用 0启用
     * @return
     */
    public int updateAssessBlock(Integer assessId, Byte block) {
        int res = db().update(ASSESS_ACTIVITY).set(ASSESS_ACTIVITY.IS_BLOCK, block)
            .where(ASSESS_ACTIVITY.ID.eq(assessId)).execute();
        return res;
    }


    /**
     * 更新发布状态
     *
     * @param assessId
     * @param pubFlag  1 发布
     * @return
     */
    public int updateAssessPubFlag(Integer assessId, Byte pubFlag) {
        int res = db().update(ASSESS_ACTIVITY).set(ASSESS_ACTIVITY.PUB_FLAG, pubFlag)
            .where(ASSESS_ACTIVITY.ID.eq(assessId)).execute();
        return res;
    }

    /**
     * 添加测评题目
     *
     * @param param
     * @return
     */
    public int insertAssessTopic(AssessTopicOneParam param) {
        AssessTopicRecord record = new AssessTopicRecord();
        assign(param, record);
        return db().executeInsert(record);
    }

    /**
     * 获取单条测评题目信息
     *
     * @param id
     * @return
     */
    public AssessTopicOneParam getAssessTopicOne(Integer id) {
        AssessTopicOneParam res = db().select().from(ASSESS_TOPIC).where(ASSESS_TOPIC.ID.eq(id)).fetchOne()
            .into(AssessTopicOneParam.class);
        return res;
    }

    /**
     * 测评题目编辑保存
     *
     * @param param
     * @return
     */
    public int updateAssessTopic(AssessTopicOneParam param) {
        AssessTopicRecord record = new AssessTopicRecord();
        assign(param, record);
        return db().executeUpdate(record);
    }

    /**
     * 删除测评题目
     *
     * @param id
     * @return
     */
    public int deleteAssessTopic(Integer id) {
        int res = db().update(ASSESS_TOPIC).set(ASSESS_TOPIC.DEL_FLAG, (byte) 1).where(ASSESS_TOPIC.ID.eq(id))
            .execute();
        return res;
    }

    /**
     * 添加测评结果
     *
     * @param param
     * @return
     */
    public int insertAssessResult(AssessResultOneParam param) {
        AssessResultRecord record = new AssessResultRecord();
        assign(param, record);
        return db().executeInsert(record);
    }
}

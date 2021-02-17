package com.meidianyi.shop.dao.shop.session;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.ImSessionConstant;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.ImSessionDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.ImSessionRecord;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.condition.ImSessionCondition;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionPageListParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.vo.ImSessionListVo;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.IM_SESSION;
import static com.meidianyi.shop.db.shop.Tables.INQUIRY_ORDER;

/**
 * 会话处理Dao
 * @author 李晓冰
 * @date 2020年07月21日
 */
@Repository
public class ImSessionDao extends ShopBaseDao {

    /**
     * 新增会话
     * @param imSessionDo 会话信息
     */
    public void insert(ImSessionDo imSessionDo) {
        ImSessionRecord imSessionRecord = db().newRecord(IM_SESSION);
        FieldsUtil.assign(imSessionDo, imSessionRecord);
        imSessionRecord.insert();
        imSessionDo.setId(imSessionRecord.getId());
    }

    /**
     * 修改会话
     * @param imSessionDo 会话信息
     */
    public void update(ImSessionDo imSessionDo) {
        ImSessionRecord imSessionRecord = db().newRecord(IM_SESSION);
        FieldsUtil.assign(imSessionDo, imSessionRecord);
        imSessionRecord.update();
    }

    /**
     * 批量修改会话
     * @param imSessionDos
     */
    public void batchUpdate(List<ImSessionDo> imSessionDos) {
        List<ImSessionRecord> imSessionRecords = new ArrayList<>();
        for (ImSessionDo imSessionDo : imSessionDos) {
            ImSessionRecord imSessionRecord = new ImSessionRecord();
            FieldsUtil.assign(imSessionDo, imSessionRecord);
            imSessionRecords.add(imSessionRecord);
        }
        db().batchUpdate(imSessionRecords).execute();
    }

    /**
     * 找相关的end状态会话信息
     * @param doctorId
     * @param userId
     * @param patientId
     * @return
     */
    public ImSessionDo getByAllInfo(Integer doctorId, Integer userId, Integer patientId) {
        return db().selectFrom(IM_SESSION)
            .where(IM_SESSION.DOCTOR_ID.eq(doctorId).and(IM_SESSION.USER_ID.eq(userId)).and(IM_SESSION.PATIENT_ID.eq(patientId)).and(IM_SESSION.SESSION_STATUS.eq(ImSessionConstant.SESSION_END)))
            .fetchAnyInto(ImSessionDo.class);
    }

    /**
     * 根据id获取对应的会话信息
     * @param id
     * @return
     */
    public ImSessionDo getById(Integer id) {
        return db().selectFrom(IM_SESSION).where(IM_SESSION.ID.eq(id).and(IM_SESSION.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchAnyInto(ImSessionDo.class);
    }

    public Byte getStatus(Integer id){
        return db().select(IM_SESSION.SESSION_STATUS).from(IM_SESSION).where(IM_SESSION.ID.eq(id).and(IM_SESSION.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchAny(IM_SESSION.SESSION_STATUS);
    }

    /**
     * 根据orderSn获取对应的会话信息
     * @param orderSn
     * @return
     */
    public ImSessionDo getByOrderSn(String orderSn) {
        return db().selectFrom(IM_SESSION).where(IM_SESSION.ORDER_SN.eq(orderSn).and(IM_SESSION.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchAnyInto(ImSessionDo.class);
    }

    /**
     * 关闭会话session
     * @param imSessionId
     */
    public void updateSessionStatus(Integer imSessionId, Byte status, Byte weightFactor) {
        db().update(IM_SESSION).set(IM_SESSION.SESSION_STATUS, status)
            .set(IM_SESSION.WEIGHT_FACTOR, weightFactor)
            .where(IM_SESSION.ID.eq(imSessionId))
            .execute();
    }


    public void batchUpdateSessionEvaluateStatus(List<Integer> imSessionIds, Byte status) {
        db().update(IM_SESSION).set(IM_SESSION.EVALUATE_STATUS, status)
            .where(IM_SESSION.ID.in(imSessionIds))
            .execute();
    }

    public void batchUpdateSessionEvaluateStatus(List<Integer> imSessionIds, Byte newStatus, Byte oldStatus) {
        db().update(IM_SESSION)
            .set(IM_SESSION.EVALUATE_STATUS, newStatus)
            .where(IM_SESSION.ID.in(imSessionIds).and(IM_SESSION.EVALUATE_STATUS.eq(oldStatus)))
            .execute();
    }

    /**
     * 批量更新状态
     * @param imSessionIds 会话ids
     * @param status       状态
     */
    public void batchUpdateSessionStatus(List<Integer> imSessionIds, Byte status, Byte weightFactor) {
        db().update(IM_SESSION).set(IM_SESSION.SESSION_STATUS, status)
            .set(IM_SESSION.WEIGHT_FACTOR, weightFactor)
            .where(IM_SESSION.ID.in(imSessionIds))
            .execute();
    }

    /**
     * 分页查询会话列表信息
     * @param pageListParam 分页信息
     * @return 分页结果
     */
    public PageResult<ImSessionListVo> pageList(ImSessionPageListParam pageListParam) {
        Condition condition = IM_SESSION.IS_DELETE.eq(DelFlag.NORMAL_VALUE);
        if (pageListParam.getDoctorId() != null) {
            condition = condition.and(IM_SESSION.DOCTOR_ID.eq(pageListParam.getDoctorId()));
        }
        if (pageListParam.getSessionStatus() != null) {
            condition = condition.and(IM_SESSION.SESSION_STATUS.in(pageListParam.getSessionStatus()));
        }
        if (pageListParam.getUserId() != null) {
            condition = condition.and(IM_SESSION.USER_ID.eq(pageListParam.getUserId()));
        }

        SelectSeekStep2<Record, Byte, Timestamp> select = db().select(INQUIRY_ORDER.ORDER_AMOUNT,IM_SESSION.asterisk()).from(IM_SESSION)
            .leftJoin(INQUIRY_ORDER).on(INQUIRY_ORDER.ORDER_SN.eq(IM_SESSION.ORDER_SN))
            .where(condition)
            .orderBy(IM_SESSION.WEIGHT_FACTOR.desc(), IM_SESSION.CREATE_TIME.desc());
        return getPageResult(select, pageListParam.getCurrentPage(), pageListParam.getPageRows(), ImSessionListVo.class);
    }

    /**
     * 获取当前sessionId相关的，其它会话id集合
     * 相关指：医师id，用户id，患者id相同
     * @param baseSessionId
     * @return
     */
    public List<Integer> getRelevantSessionIds(Integer baseSessionId) {
        final String tempTableName = "TEMP_TABLE";

        Table<Record3<Integer, Integer, Integer>> tempTable = db().select(IM_SESSION.DOCTOR_ID, IM_SESSION.USER_ID, IM_SESSION.PATIENT_ID).from(IM_SESSION)
            .where(IM_SESSION.ID.eq(baseSessionId)).asTable(tempTableName);

        List<Integer> sessionIds = db().select(IM_SESSION.ID).from(IM_SESSION).innerJoin(tempTable)
            .on(IM_SESSION.DOCTOR_ID.eq(tempTable.field("doctor_id", Integer.class))
                .and(IM_SESSION.USER_ID.eq(tempTable.field("user_id", Integer.class)))
                .and(IM_SESSION.PATIENT_ID.eq(tempTable.field("patient_id", Integer.class))))
            .fetch(IM_SESSION.ID);

        return sessionIds;
    }

    /**
     * 列出符合条件的会话
     * @param imSessionCondition 会话过滤条件
     * @return 会话列表
     */
    public List<ImSessionDo> listImSession(ImSessionCondition imSessionCondition) {
        Condition condition = IM_SESSION.IS_DELETE.eq(DelFlag.NORMAL_VALUE);
        if (imSessionCondition.getStatus() != null) {
            condition = condition.and(IM_SESSION.SESSION_STATUS.eq(imSessionCondition.getStatus()));
        }

        if (imSessionCondition.getLessCreateTime() != null) {
            condition = condition.and(IM_SESSION.CREATE_TIME.le(imSessionCondition.getLessCreateTime()));
        }

        if (imSessionCondition.getLimitTime() != null) {
            condition = condition.and(IM_SESSION.LIMIT_TIME.le(imSessionCondition.getLimitTime()));
        }

        if (imSessionCondition.getOrderSns() != null) {
            condition = condition.and(IM_SESSION.ORDER_SN.in(imSessionCondition.getOrderSns()));
        }

        if (imSessionCondition.getDoctorId() != null) {
            condition = condition.and(IM_SESSION.DOCTOR_ID.eq(imSessionCondition.getDoctorId()));
        }

        if (imSessionCondition.getUserId() != null) {
            condition = condition.and(IM_SESSION.USER_ID.eq(imSessionCondition.getUserId()));
        }

        return db().selectFrom(IM_SESSION).where(condition).fetchInto(ImSessionDo.class);
    }

    /**
     * 根据会话id查询会话状态，订单号
     * @param sessionId
     * @return
     */
    public ImSessionDo getImSession(Integer sessionId) {
        return db().select().from(IM_SESSION)
            .where(IM_SESSION.ID.eq(sessionId)).fetchAnyInto(ImSessionDo.class);
    }

    /**
     * 获取医师接诊平均响应时间
     * @param doctorId 医师id
     * @return 平均响应时间 秒
     */
    public Integer getSessionReadyToOnAckAvgTime(Integer doctorId) {
        Record1<BigDecimal> bigDecimalRecord1 = db().select(DSL.avg(IM_SESSION.READY_TO_ON_AKC_TIME)).from(IM_SESSION)
            .where(IM_SESSION.DOCTOR_ID.eq(doctorId)
                .and(IM_SESSION.SESSION_STATUS.notIn(ImSessionConstant.SESSION_READY_TO_START, ImSessionConstant.SESSION_CANCEL)))
            .fetchAny();
        if (bigDecimalRecord1 == null) {
            return null;
        }
        BigDecimal bigDecimal = bigDecimalRecord1.get(0, BigDecimal.class);
        return bigDecimal.intValue();
    }

    public Integer getSessionCount(Integer doctorId) {
        return db().fetchCount(IM_SESSION, IM_SESSION.DOCTOR_ID.eq(doctorId)
            .and(IM_SESSION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .and(IM_SESSION.SESSION_STATUS.notIn(ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS, ImSessionConstant.SESSION_EVALUATE_CAN_STATUS, ImSessionConstant.SESSION_CANCEL)));
    }

    public BigDecimal getSessionTotalMoney(Integer doctorId) {
        return db().select(DSL.sum(INQUIRY_ORDER.ORDER_AMOUNT).as("consultation_money")).from(IM_SESSION)
            .leftJoin(INQUIRY_ORDER).on(INQUIRY_ORDER.ORDER_SN.eq(IM_SESSION.ORDER_SN))
            .where(IM_SESSION.DOCTOR_ID.eq(doctorId))
            .and(IM_SESSION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .and(IM_SESSION.SESSION_STATUS.notIn(ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS, ImSessionConstant.SESSION_EVALUATE_CAN_STATUS, ImSessionConstant.SESSION_CANCEL))
            .fetchAnyInto(BigDecimal.class);
    }
}

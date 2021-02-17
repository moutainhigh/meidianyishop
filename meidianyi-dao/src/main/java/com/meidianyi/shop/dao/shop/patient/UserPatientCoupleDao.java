package com.meidianyi.shop.dao.shop.patient;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.UserDo;
import com.meidianyi.shop.common.pojo.shop.table.UserPatientCoupleDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.UserPatientCoupleRecord;
import com.meidianyi.shop.service.pojo.shop.patient.PatientConstant;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientDetailVo;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientParam;
import org.jooq.Condition;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * @author chenjie
 */
@Repository
public class UserPatientCoupleDao  extends ShopBaseDao {

    /**
     * 获取默认患者 没有为0
     * @param userId
     * @return
     */
    public Integer defaultPatientIdByUser(Integer userId) {
        Integer patientId = db().select(USER_PATIENT_COUPLE.PATIENT_ID).from(USER_PATIENT_COUPLE).where(USER_PATIENT_COUPLE.USER_ID.eq(userId).and(USER_PATIENT_COUPLE.IS_DEFAULT.eq(PatientConstant.DEFAULT)).and(USER_PATIENT_COUPLE.IS_DELETE.eq((byte) 0)))
            .fetchOptional(USER_PATIENT_COUPLE.PATIENT_ID, int.class).orElse(0);
        return patientId;
    }

    /**
     * 获取默认患者 没有为null
     * @param userId
     * @return
     */
    public UserPatientParam defaultPatientByUser(Integer userId) {
        return db().select(USER_PATIENT_COUPLE.PATIENT_ID,USER_PATIENT_COUPLE.USER_ID,USER_PATIENT_COUPLE.IS_FETCH)
                .from(USER_PATIENT_COUPLE)
                .where(USER_PATIENT_COUPLE.USER_ID.eq(userId).and(USER_PATIENT_COUPLE.IS_DEFAULT.eq(PatientConstant.DEFAULT)).and(USER_PATIENT_COUPLE.IS_DELETE.eq((byte) 0)))
            .fetchOptionalInto(UserPatientParam.class).orElse(null);
    }

    /**
     * 获取用户绑定患者信息集合
     * @param userId
     * @return
     */
    public List<PatientOneParam> listPatientIdsByUser(Integer userId) {
        return db().select(USER_PATIENT_COUPLE.asterisk(),PATIENT.MOBILE,PATIENT.IDENTITY_CODE,PATIENT.IDENTITY_TYPE,PATIENT.NAME).from(USER_PATIENT_COUPLE)
            .leftJoin(PATIENT).on(PATIENT.ID.eq(USER_PATIENT_COUPLE.PATIENT_ID))
            .where(USER_PATIENT_COUPLE.USER_ID.eq(userId).and(PATIENT.IS_DELETE.eq((byte) 0)))
            .fetchInto(PatientOneParam.class);
    }

    /**
     * 设置默认患者 没有为0
     * @param userPatient
     * @return
     */
    public int setDefaultPatient(UserPatientParam userPatient) {
        int id = db().update(USER_PATIENT_COUPLE).set(USER_PATIENT_COUPLE.IS_DEFAULT,(byte) 1).where(USER_PATIENT_COUPLE.USER_ID.eq(userPatient.getUserId()).and(USER_PATIENT_COUPLE.PATIENT_ID.eq(userPatient.getPatientId())))
            .execute();
        return id;
    }

    /**
     * 将用户的患者全置为非默认
     * @param userId
     * @return
     */
    public int initDefaultUserPatient(Integer userId) {
        int id = db().update(USER_PATIENT_COUPLE).set(USER_PATIENT_COUPLE.IS_DEFAULT,(byte) 0).where(USER_PATIENT_COUPLE.USER_ID.eq(userId))
            .execute();
        return id;
    }
    /**
     * 新增userPatientCoupleDo
     * @param
     * @return
     */
    public int save(UserPatientCoupleDo userPatientCoupleDo){
        UserPatientCoupleRecord record=db().newRecord(USER_PATIENT_COUPLE,userPatientCoupleDo);
        record.insert();
        userPatientCoupleDo.setId(record.getId());
        return record.getId();
    }

    /**
     * 编辑保存
     *
     * @param param
     * @return
     */
    public void updateUserPatient(UserPatientCoupleDo param) {
        UserPatientCoupleRecord record = new UserPatientCoupleRecord();
        FieldsUtil.assign(param, record);
        db().executeUpdate(record);
    }

    /**
     * 用户和患者是否绑定（用户患者是否存在）
     * @param param
     * @return
     */
    public boolean isExistUserPatient(UserPatientParam param) {
        Condition condition = USER_PATIENT_COUPLE.USER_ID.eq(param.getUserId()).and(USER_PATIENT_COUPLE.PATIENT_ID.eq(param.getPatientId())).and(USER_PATIENT_COUPLE.IS_DELETE.eq((byte) 0));
        int count = db().fetchCount(USER_PATIENT_COUPLE, condition);
        return count>0;
    }

    /**
     * 解除用户患者关联
     * @param param
     */
    public void deleteUserPatient(UserPatientParam param) {
        db().delete(USER_PATIENT_COUPLE).where(USER_PATIENT_COUPLE.USER_ID.eq(param.getUserId())).and(USER_PATIENT_COUPLE.PATIENT_ID.eq(param.getPatientId()))
            .execute();
    }

    /**
     * 根据用户患者Id获取用户患者
     * @param param
     * @return
     */
    public UserPatientParam getUserPatient(UserPatientParam param) {
        return db().select().from(USER_PATIENT_COUPLE)
            .where(USER_PATIENT_COUPLE.USER_ID.eq(param.getUserId()))
            .and(USER_PATIENT_COUPLE.PATIENT_ID.eq(param.getPatientId()))
            .fetchAnyInto(UserPatientParam.class);
    }

    /**
     * 获取用户患者Id
     * @param param
     * @return
     */
    public Integer getUserPatientId(UserPatientParam param) {
        Condition condition = USER_PATIENT_COUPLE.USER_ID.eq(param.getUserId()).and(USER_PATIENT_COUPLE.PATIENT_ID.eq(param.getPatientId())).and(USER_PATIENT_COUPLE.IS_DELETE.eq((byte) 0));
        return db().select(USER_PATIENT_COUPLE.ID).from(USER_PATIENT_COUPLE).where(condition).fetchAnyInto(Integer.class);
    }

    /**
     * 根据用户患者Id获取用户患者详情
     * @param param
     * @return
     */
    public UserPatientDetailVo getUserPatientInfo(UserPatientParam param) {
        return db().select(USER_PATIENT_COUPLE.asterisk(),PATIENT.NAME,PATIENT.MOBILE,PATIENT.IDENTITY_CODE,PATIENT.BIRTHDAY).from(USER_PATIENT_COUPLE)
            .leftJoin(PATIENT).on(PATIENT.ID.eq(USER_PATIENT_COUPLE.PATIENT_ID))
            .where(USER_PATIENT_COUPLE.USER_ID.eq(param.getUserId()))
            .and(USER_PATIENT_COUPLE.PATIENT_ID.eq(param.getPatientId()))
            .fetchAnyInto(UserPatientDetailVo.class);
    }
    /**
     * 根据用户患者Id获取用户患者详情
     * @param userId
     * @param patientId
     * @return
     */
    public UserPatientDetailVo getUserPatientInfo(Integer userId, Integer patientId) {
        return db().select(USER_PATIENT_COUPLE.asterisk(),PATIENT.NAME,PATIENT.MOBILE,PATIENT.IDENTITY_CODE,PATIENT.BIRTHDAY).from(USER_PATIENT_COUPLE)
            .leftJoin(PATIENT).on(PATIENT.ID.eq(USER_PATIENT_COUPLE.PATIENT_ID))
            .where(USER_PATIENT_COUPLE.USER_ID.eq(userId))
            .and(USER_PATIENT_COUPLE.PATIENT_ID.eq(patientId))
            .fetchAnyInto(UserPatientDetailVo.class);
    }

    /**
     * 获取用户绑定患者Id集合
     * @param userId
     * @return
     */
    public List<Integer> listPatientIdsByUserId(Integer userId) {
        return db().select(USER_PATIENT_COUPLE.PATIENT_ID).from(USER_PATIENT_COUPLE)
            .where(USER_PATIENT_COUPLE.USER_ID.eq(userId).and(PATIENT.IS_DELETE.eq((byte) 0)))
            .fetchInto(Integer.class);
    }

    /**
     * 获取患者绑定用户集合
     * @param patientId 患者id
     * @return List<UserDo>
     */
    public List<UserDo> listUsersByPatientId(Integer patientId) {
        return db().select(USER.USER_ID, USER.USERNAME).from(USER)
            .leftJoin(USER_PATIENT_COUPLE)
            .on(USER_PATIENT_COUPLE.USER_ID.eq(USER.USER_ID))
            .where(USER_PATIENT_COUPLE.PATIENT_ID.eq(patientId)).fetchInto(UserDo.class);
    }
}

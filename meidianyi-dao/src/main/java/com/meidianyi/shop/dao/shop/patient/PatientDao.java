package com.meidianyi.shop.dao.shop.patient;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.*;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.PatientRecord;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorExternalRequestParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorQueryPatientParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorQueryPatientVo;
import com.meidianyi.shop.service.pojo.shop.patient.*;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.InquiryOrder.INQUIRY_ORDER;

/**
 * @author chenjie
 */
@Repository
public class PatientDao extends ShopBaseDao{

    private static final String PRESCRIPTION_AUDIT_FAILED = "处方审核未通过";

    private static final String PRESCRIPTION_AUDIT_NOT_PASSED = "处方未审核";

    private static final String OTC = "OTC";

    private static final Byte PRESCRIPTION_AUDIT_FAILED_CODE = 2;

    /**
     * 患者列表
     * @param param
     * @return
     */
    public PageResult<PatientOneParam> getPatientList(PatientListParam param) {
        SelectJoinStep<? extends Record> select = db()
            .select(PATIENT.asterisk())
            .from(PATIENT);
        select.where(PATIENT.IS_DELETE.eq((byte) 0));
        buildOptions(select, param);
        select.orderBy(PATIENT.ID.asc());
        return this.getPageResult(select, param.getCurrentPage(),
            param.getPageRows(), PatientOneParam.class);
    }

    /**
     * 患者搜索查询
     *
     * @param select
     * @param param
     */
    protected void buildOptions(SelectJoinStep<? extends Record> select, PatientListParam param) {
        if (param.getName() != null) {
            select.where(PATIENT.NAME.like(likeValue(param.getName())));
        }
        if (param.getMobile() != null) {
            select.where(PATIENT.MOBILE.like(likeValue(param.getMobile())));
        }
        if (param.getUserId()>0) {
            select.where(PATIENT.ID.in(param.getPatientIds()));
        }
        if (param.getStartTime() != null || param.getEndTime() != null) {
            select.where(PATIENT.CREATE_TIME.ge(param.getStartTime()))
                .and(PATIENT.CREATE_TIME.le(param.getEndTime()));
        }
    }

    /**
     * 获取一条患者的信息
     *
     * @param patientId
     * @return
     */
    public PatientOneParam getOneInfo(Integer patientId) {
        PatientOneParam info = db().select().from(PATIENT).where(PATIENT.ID.eq(patientId))
            .fetchOneInto(PatientOneParam.class);
        return info;
    }


    /**
     * 编辑保存
     *
     * @param param
     * @return
     */
    public void updatePatient(PatientDo param) {
        PatientRecord record = new PatientRecord();
        FieldsUtil.assign(param, record);
        db().executeUpdate(record);
    }

    /**
     * 添加患者
     *
     * @param param
     * @return
     */
    public int insertPatient(PatientDo param) {
        PatientRecord record = db().newRecord(PATIENT);
        FieldsUtil.assign(param, record);
        record.insert();
        param.setId(record.getId());
        return record.getId();
    }

    /**
     * 删除
     *
     * @param patientId
     * @return
     */
    public int deletePatient(Integer patientId) {
        int res = db().update(PATIENT).set(PATIENT.IS_DELETE, (byte) 1).where(PATIENT.ID.eq(patientId))
            .execute();
        return res;
    }

    public List<PatientOneParam> listPatientByIds (List<Integer> patientIds) {
        List<PatientOneParam> patientList = db().select().from(PATIENT).where(PATIENT.ID.in(patientIds))
            .fetch().into(PatientOneParam.class);
        return patientList;
    }

    /**
     * 查询患者
     *
     * @param patientInfoParam
     * @return
     */
    public PatientOneParam getPatientByNameAndMobile(UserPatientOneParam patientInfoParam){
        SelectConditionStep<? extends Record> select= db().select().from(PATIENT)
            .where(PATIENT.NAME.eq(patientInfoParam.getName()))
            .and(PATIENT.MOBILE.eq(patientInfoParam.getMobile())).and(PATIENT.IDENTITY_CODE.eq(patientInfoParam.getIdentityCode()));
        return select.fetchOneInto(PatientOneParam.class);
    }

    /**
     * 根据患者姓名，手机号查询当前患者
     * @param userPatientOneParam 患者入参
     * @return PatientOneParam
     */
    public PatientOneParam getPatientByName(UserPatientOneParam userPatientOneParam) {
        return  db().select().from(PATIENT)
            .where(PATIENT.NAME.eq(userPatientOneParam.getName()))
            .and(PATIENT.MOBILE.eq(userPatientOneParam.getMobile()))
            .fetchOneInto(PatientOneParam.class);
    }

    /**
     * 根据患者姓名，手机号查询当前患者
     * @param userPatientOneParam 患者入参
     * @return PatientOneParam
     */
    public PatientOneParam getPatientByName(UserPatientFetchParam userPatientOneParam) {
        return  db().select().from(PATIENT)
            .where(PATIENT.NAME.eq(userPatientOneParam.getName()))
            .and(PATIENT.MOBILE.eq(userPatientOneParam.getMobile()))
            .fetchOneInto(PatientOneParam.class);
    }



    /**
     * 获取患者信息
     * @param patientIds id集合
     * @return
     */
    public List<PatientSimpleInfoVo> listPatientInfo(List<Integer> patientIds){
        return db().select(PATIENT.ID,PATIENT.NAME).from(PATIENT).where(PATIENT.ID.in(patientIds).and(PATIENT.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(PatientSimpleInfoVo.class);
    }


    /**
     * 患者是否存在，用来新增检查
     * @param param
     * @return
     */
    public Integer getPatientExist(PatientExternalRequestParam param) {
        Condition condition = PATIENT.NAME.eq(param.getName()).and(PATIENT.MOBILE.eq(param.getMobile())).and(PATIENT.IDENTITY_TYPE.eq((byte) 1)).and(PATIENT.IDENTITY_CODE.eq(param.getIdentityCode()));
        Integer patientId = db().select(PATIENT.ID).from(PATIENT).where(condition).fetchAnyInto(Integer.class);
        return patientId;
    }

    /**
     * 根据身份证查询患者id
     * @param identityCode 身份证
     * @return Integer
     */
    public Integer getPatientIdByIdentityCode(String identityCode) {
        return db().select(PATIENT.ID)
            .from(PATIENT)
            .where(PATIENT.IDENTITY_CODE.eq(identityCode)).fetchAnyInto(Integer.class);
    }

    /**
     * 用户绑定的患者数
     * @param userId
     * @return
     */
    public Integer countPatientByUser(Integer userId) {
        return db().fetchCount(USER_PATIENT_COUPLE, USER_PATIENT_COUPLE.USER_ID.eq(userId).and(USER_PATIENT_COUPLE.IS_DELETE.eq((byte) 0)));
    }

    /**
     * 查询患者购药记录
     * @param patientMedicineParam 患者id
     * @return PageResult<PatientMedicineVo> 患者购药记录出参
     */
    public PageResult<PatientMedicineVo> getPatientMedicine(PatientMedicineParam patientMedicineParam) {
        SelectOnConditionStep<? extends Record> select = db().select(
              ORDER_GOODS.GOODS_NAME.as("goodsCommonName")
            , ORDER_GOODS.GOODS_QUALITY_RATIO
            , ORDER_GOODS.GOODS_PRODUCTION_ENTERPRISE
            , ORDER_GOODS.GOODS_APPROVAL_NUMBER
            , ORDER_GOODS.GOODS_NUMBER
            , ORDER_GOODS.GOODS_PRICE
            , ORDER_GOODS.PRESCRIPTION_OLD_CODE
            , ORDER_GOODS.PRESCRIPTION_CODE
            , ORDER_GOODS.MEDICAL_AUDIT_TYPE
            , ORDER_GOODS.MEDICAL_AUDIT_STATUS
            , ORDER_GOODS.ORDER_SN
            , ORDER_GOODS.DISCOUNTED_TOTAL_PRICE
            , ORDER_GOODS.CREATE_TIME).from(ORDER_GOODS)
            .leftJoin(ORDER_INFO)
            .on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN));
        patientMedicineBuildOptions(select, patientMedicineParam);
        PageResult<PatientMedicineVo> pageResult = this.getPageResult(select, patientMedicineParam.getCurrentPage(),
            patientMedicineParam.getPageRows(), PatientMedicineVo.class);
        pageResult.getDataList().forEach(patientMedicineVo -> {

            if (patientMedicineVo.getMedicalAuditStatus() == 0 && patientMedicineVo.getMedicalAuditType() != 0) {
                    patientMedicineVo.setPrescriptionCode(PRESCRIPTION_AUDIT_NOT_PASSED);
                    patientMedicineVo.setIsCanSkipPrescription((byte) 0);

            }
            if (patientMedicineVo.getMedicalAuditType() == 1 && PRESCRIPTION_AUDIT_FAILED_CODE.equals(patientMedicineVo.getMedicalAuditStatus())) {
                patientMedicineVo.setPrescriptionCode(PRESCRIPTION_AUDIT_FAILED);
                patientMedicineVo.setIsCanSkipPrescription((byte) 0);
            }
            if (patientMedicineVo.getMedicalAuditType() == 0) {
                patientMedicineVo.setPrescriptionCode(OTC);
                patientMedicineVo.setIsCanSkipPrescription((byte) 0);
            }
        });
        return pageResult;
    }

    /**
     * 患者购药记录条件查询筛选
     * @param select 查询实体
     * @param patientMedicineParam 患者条件查询入参
     */
    private void patientMedicineBuildOptions(SelectOnConditionStep<? extends Record> select, PatientMedicineParam patientMedicineParam) {
        if (patientMedicineParam.getGoodsCommonName() != null && patientMedicineParam.getGoodsCommonName().trim().length() > 0) {
            select.where(ORDER_GOODS.GOODS_NAME.like(likeValue(patientMedicineParam.getGoodsCommonName().trim())));
        }
        if (patientMedicineParam.getGoodsApprovalNumber() != null && patientMedicineParam.getGoodsApprovalNumber().trim().length() > 0) {
            select.where(ORDER_GOODS.GOODS_APPROVAL_NUMBER.like(likeValue(patientMedicineParam.getGoodsApprovalNumber())));
        }
        if (patientMedicineParam.getGoodsProductionEnterprise() != null && patientMedicineParam.getGoodsProductionEnterprise().trim().length() > 0) {
            select.where(ORDER_GOODS.GOODS_PRODUCTION_ENTERPRISE.like(likeValue(patientMedicineParam.getGoodsProductionEnterprise())));
        }
        if (patientMedicineParam.getStartTime() != null || patientMedicineParam.getEndTime() != null) {
            select.where(ORDER_GOODS.CREATE_TIME.ge(patientMedicineParam.getStartTime()))
                .and(ORDER_GOODS.CREATE_TIME.le(patientMedicineParam.getEndTime()));
        }
        select.where(ORDER_INFO.PATIENT_ID.eq(patientMedicineParam.getPatientId()))
            .orderBy(ORDER_GOODS.CREATE_TIME.desc());
    }

    /**
     * 根据患者id查询关联处方
     * @param patientPrescriptionParam 查询处方入参
     * @return
     */
    public PageResult<PatientPrescriptionVo> getPatientPrescription(PatientPrescriptionParam patientPrescriptionParam) {
        SelectConditionStep<Record> where = db()
            .select(PRESCRIPTION.asterisk(), ORDER_INFO.ORDER_SN.as("orderSnByOrderInfo"))
            .from(PRESCRIPTION)
            .leftJoin(ORDER_GOODS)
            .on(ORDER_GOODS.PRESCRIPTION_CODE.eq(PRESCRIPTION.PRESCRIPTION_CODE))
            .leftJoin(ORDER_INFO)
            .on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN))
            .where(PRESCRIPTION.PATIENT_ID.eq(patientPrescriptionParam.getPatientId()));
        patientPrescriptionBuildOptions(patientPrescriptionParam, where);
        patientPrescriptionParam.setPageRows(5);
        return this.getPageResult(where, patientPrescriptionParam.getCurrentPage(),
            patientPrescriptionParam.getPageRows(), PatientPrescriptionVo.class);
    }

    /**
     * 患者查询关联处方条件查询
     * @param patientPrescriptionParam 查询关联处方条件
     * @param select 条件入参
     */
    private void patientPrescriptionBuildOptions(PatientPrescriptionParam patientPrescriptionParam, SelectConditionStep<Record> select) {
        if (patientPrescriptionParam.getDoctorName() != null && patientPrescriptionParam.getDoctorName().trim().length() > 0) {
            select.and(PRESCRIPTION.DOCTOR_NAME.like(likeValue(patientPrescriptionParam.getDoctorName().trim())));
        }
        if (patientPrescriptionParam.getDepartmentName() != null && patientPrescriptionParam.getDepartmentName().trim().length() > 0) {
            select.and(PRESCRIPTION.DEPARTMENT_NAME.like(likeValue(patientPrescriptionParam.getDepartmentName().trim())));
        }
        if (patientPrescriptionParam.getPrescriptionType() != null) {
            select.and(PRESCRIPTION.AUDIT_TYPE.eq(patientPrescriptionParam.getPrescriptionType()));
        }
        if (patientPrescriptionParam.getStartTime() != null || patientPrescriptionParam.getEndTime() != null) {
            select.and(PRESCRIPTION.CREATE_TIME.ge(patientPrescriptionParam.getStartTime()))
                .and(PRESCRIPTION.CREATE_TIME.le(patientPrescriptionParam.getEndTime()));
        }
        select.orderBy(PRESCRIPTION.CREATE_TIME.desc());
    }

    /**
     * 查询患者关联问诊
     * @param patientPrescriptionParam 患者关联问诊入参
     * @return PageResult<InquiryOrderDo>
     */
    public PageResult<PatientInquiryVo> getPatientInquiry(PatientPrescriptionParam patientPrescriptionParam) {
        SelectConditionStep<Record> where = db().select(INQUIRY_ORDER.asterisk(), DOCTOR.HOSPITAL_CODE.as("doctorCode"))
            .from(INQUIRY_ORDER)
            .leftJoin(DOCTOR)
            .on(DOCTOR.ID.eq(INQUIRY_ORDER.DOCTOR_ID))
            .where(INQUIRY_ORDER.PATIENT_ID.eq(patientPrescriptionParam.getPatientId()));
        if (patientPrescriptionParam.getDoctorName() != null && patientPrescriptionParam.getDoctorName().trim().length() > 0) {
            where.and(INQUIRY_ORDER.DOCTOR_NAME.like(likeValue(patientPrescriptionParam.getDoctorName().trim())));
        }
        if (patientPrescriptionParam.getStartTime() != null || patientPrescriptionParam.getEndTime() != null) {
            where.and(INQUIRY_ORDER.CREATE_TIME.ge(patientPrescriptionParam.getStartTime()))
                .and(INQUIRY_ORDER.CREATE_TIME.le(patientPrescriptionParam.getEndTime()));
        }
        where.orderBy(INQUIRY_ORDER.CREATE_TIME.desc());
        patientPrescriptionParam.setPageRows(5);
        return this.getPageResult(where, patientPrescriptionParam.getCurrentPage(),
            patientPrescriptionParam.getPageRows(), PatientInquiryVo.class);
    }

    /**
     * 根据患者id查询关联医师信息
     * @param patientQueryDoctorParam 用户查询关联医师入参
     * @return PageResult<PatientQueryDoctorVo>
     */
    public PageResult<PatientQueryDoctorVo> getPatientQueryDoctor(PatientQueryDoctorParam patientQueryDoctorParam) {
        SelectConditionStep<Record6<String, String, String, Integer, BigDecimal, Integer>> select = db().select(
            PRESCRIPTION.DOCTOR_CODE
            , PRESCRIPTION.DOCTOR_NAME.as("doctorName")
            , PRESCRIPTION.DEPARTMENT_NAME.as("departmentName")
            , DOCTOR.ID.as("doctorId")
            , DSL.sum(PRESCRIPTION.TOTAL_PRICE).as("prescriptionConsumptionAmount")
            , DSL.count(PRESCRIPTION.DOCTOR_NAME).as("prescriptionNumber"))
            .from(DOCTOR)
            .join(PRESCRIPTION)
            .on(PRESCRIPTION.DOCTOR_CODE.eq(DOCTOR.HOSPITAL_CODE))
            .where(PRESCRIPTION.PATIENT_ID.eq(patientQueryDoctorParam.getPatientId()));
        patientPrescriptionBuildOptions(select, patientQueryDoctorParam);
        select.groupBy(PRESCRIPTION.PATIENT_ID
            , PRESCRIPTION.DOCTOR_CODE
            , PRESCRIPTION.DOCTOR_NAME
            , PRESCRIPTION.DEPARTMENT_NAME
            , DOCTOR.ID)
            .orderBy(PRESCRIPTION.CREATE_TIME.desc());
        patientQueryDoctorParam.setPageRows(5);
        return this.getPageResult(select, patientQueryDoctorParam.getCurrentPage(),
            patientQueryDoctorParam.getPageRows(), PatientQueryDoctorVo.class);
    }

    private void patientPrescriptionBuildOptions(SelectConditionStep<Record6<String, String, String, Integer, BigDecimal, Integer>> select, PatientQueryDoctorParam patientQueryDoctorParam) {
        if (patientQueryDoctorParam.getDoctorName() != null && patientQueryDoctorParam.getDoctorName().trim().length() > 0) {
            select.and(PRESCRIPTION.DOCTOR_NAME.like(likeValue(patientQueryDoctorParam.getDoctorName().trim())));
        }
        if (patientQueryDoctorParam.getDepartmentName() != null && patientQueryDoctorParam.getDepartmentName().trim().length() > 0) {
            select.and(PRESCRIPTION.DEPARTMENT_NAME.like(likeValue(patientQueryDoctorParam.getDepartmentName().trim())));
        }
    }

    /**
     * 查询医师关联患者处方信息
     * @param doctorQueryPatientParam 医师查询关联患者列表入参
     * @return PageResult<DoctorQueryPatientVo>
     */
    public List<DoctorQueryPatientVo> getDoctorQueryPatientWithPrescription(DoctorQueryPatientParam doctorQueryPatientParam) {
        SelectConditionStep<? extends Record> where = db().select(PATIENT.NAME.as("patientName")
            , PATIENT.ID.as("patientId")
            , USER.USERNAME.as("patientNickName")
            , DSL.count(PATIENT.NAME).as("prescriptionNum")
            , DSL.sum(PRESCRIPTION.TOTAL_PRICE).as("medicineCost"))
            .from(PRESCRIPTION)
            .leftJoin(PATIENT)
            .on(PRESCRIPTION.PATIENT_ID.eq(PATIENT.ID))
            .leftJoin(USER_PATIENT_COUPLE)
            .on(USER_PATIENT_COUPLE.PATIENT_ID.eq(PATIENT.ID))
            .leftJoin(USER)
            .on(USER_PATIENT_COUPLE.USER_ID.eq(USER.USER_ID))
            .where(PRESCRIPTION.DOCTOR_CODE.eq(doctorQueryPatientParam.getDoctorCode()));
        if (doctorQueryPatientParam.getStartTime() != null || doctorQueryPatientParam.getEndTime() != null) {
            where.and(PRESCRIPTION.CREATE_TIME.ge(doctorQueryPatientParam.getStartTime()))
                .and(PRESCRIPTION.CREATE_TIME.le(doctorQueryPatientParam.getEndTime()));
        }
        if (doctorQueryPatientParam.getPatientName() != null && doctorQueryPatientParam.getPatientName().trim().length() > 0) {
            where.and(PRESCRIPTION.PATIENT_NAME.like(likeValue(doctorQueryPatientParam.getPatientName())));
        }
        where.groupBy(PATIENT.ID, USER.USERNAME, PATIENT.NAME);
        return where.fetchInto(DoctorQueryPatientVo.class);
    }

    /**
     * 查询医师关联患者问诊信息
     * @param doctorQueryPatientParam 医师查询关联患者列表入参
     * @return PageResult<DoctorQueryPatientVo>
     */
    public List<DoctorQueryPatientVo> getDoctorQueryPatientWithInquiry(DoctorQueryPatientParam doctorQueryPatientParam) {
        SelectConditionStep<? extends Record> where = db().select(PATIENT.NAME.as("patientName")
            , USER.USERNAME.as("patientNickName")
            , DSL.count(PATIENT.NAME).as("inquiryNum")
            , DSL.sum(INQUIRY_ORDER.ORDER_AMOUNT).as("inquiryCost"))
            .from(INQUIRY_ORDER)
            .leftJoin(PATIENT)
            .on(INQUIRY_ORDER.PATIENT_ID.eq(PATIENT.ID))
            .leftJoin(USER_PATIENT_COUPLE)
            .on(USER_PATIENT_COUPLE.PATIENT_ID.eq(PATIENT.ID))
            .leftJoin(USER)
            .on(USER_PATIENT_COUPLE.USER_ID.eq(USER.USER_ID))
            .where(INQUIRY_ORDER.DOCTOR_ID.eq(doctorQueryPatientParam.getDoctorId()));
        if (doctorQueryPatientParam.getStartTime() != null || doctorQueryPatientParam.getEndTime() != null) {
            where.and(INQUIRY_ORDER.CREATE_TIME.ge(doctorQueryPatientParam.getStartTime()))
                .and(INQUIRY_ORDER.CREATE_TIME.le(doctorQueryPatientParam.getEndTime()));
        }
        if (doctorQueryPatientParam.getPatientName() != null && doctorQueryPatientParam.getPatientName().trim().length() > 0) {
            where.and(INQUIRY_ORDER.PATIENT_NAME.like(likeValue(doctorQueryPatientParam.getPatientName())));
        }
        where.groupBy(PATIENT.ID, USER.USERNAME, PATIENT.NAME);
        return where.fetchInto(DoctorQueryPatientVo.class);
    }
}

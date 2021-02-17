package com.meidianyi.shop.dao.shop.prescription;

import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.PrescriptionRecord;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorDetailPerformanceVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorQueryPrescriptionParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorQueryPrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit.DoctorAuditedPrescriptionParam;
import com.meidianyi.shop.service.pojo.shop.patient.PatientConstant;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientParam;
import com.meidianyi.shop.service.pojo.shop.prescription.FetchPrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionDoctorVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionInfoVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionListParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionListVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionPatientListParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionSimpleVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import org.elasticsearch.common.Strings;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR;
import static com.meidianyi.shop.db.shop.Tables.PATIENT;
import static com.meidianyi.shop.db.shop.Tables.PRESCRIPTION;
import static com.meidianyi.shop.db.shop.Tables.PRESCRIPTION_ITEM;
import static com.meidianyi.shop.service.pojo.shop.doctor.DoctorQueryPrescriptionParam.PRESCRIPTION_SOURCE_IS_HIS;
import static com.meidianyi.shop.service.pojo.shop.doctor.DoctorQueryPrescriptionParam.PRESCRIPTION_TYPE_IS_HIS_FETCH;

/**
 * 处方
 *
 * @author 孔德成
 * @date 2020/7/2 14:17
 */
@Repository
public class PrescriptionDao extends ShopBaseDao {

    /**
     * 添加
     *
     * @param param
     * @return
     */
    public int save(PrescriptionDo param) {
        PrescriptionRecord record = db().newRecord(PRESCRIPTION, param);
        return record.insert();
    }

    /**
     * @description his系统拉取处方信息入库
     * @author zhaoxiaodong
     * @create 2020-7-16 9:40
     */
    /**
     * 新增处方
     * @param fetchPrescriptionVo 处方入参
     */
    public void addHisPrescription(FetchPrescriptionVo fetchPrescriptionVo){
        PrescriptionRecord prescriptionRecord = db().newRecord(PRESCRIPTION, fetchPrescriptionVo);
        prescriptionRecord.insert();
    }


    /**
     * 获取一条记录
     *
     * @param id
     * @return
     */
    public PrescriptionDo getById(Integer id) {
        return db().selectFrom(PRESCRIPTION).where(PRESCRIPTION.ID.eq(id)).fetchAnyInto(PrescriptionDo.class);
    }
    /**
     *
     * 获取
     * @param prescriptionCode
     * @return PrescriptionInfoVo
     */
    public PrescriptionInfoVo getInfoByPrescriptionNo(String prescriptionCode) {
         return db().select(PRESCRIPTION.PRESCRIPTION_CODE,PRESCRIPTION.PATIENT_TREATMENT_CODE,PRESCRIPTION.DOCTOR_ADVICE,
                 PRESCRIPTION.PATIENT_NAME,PRESCRIPTION.PATIENT_AGE,PRESCRIPTION.PATIENT_SEX,PRESCRIPTION.DEPARTMENT_NAME,
                 PRESCRIPTION.DIAGNOSIS_NAME,PRESCRIPTION.PHARMACIST_NAME,PRESCRIPTION.PRESCRIPTION_CREATE_TIME,
             PRESCRIPTION.DOCTOR_NAME,PRESCRIPTION.DIAGNOSIS_DETAIL,PRESCRIPTION.IS_VALID,PRESCRIPTION.IS_USED,
                 PRESCRIPTION.PRESCRIPTION_EXPIRE_TIME,PRESCRIPTION.EXPIRE_TYPE,PRESCRIPTION.DOCTOR_SIGNATURE,PRESCRIPTION.PHARMACIST_SIGNATURE)
                 .from(PRESCRIPTION)
                 .where(PRESCRIPTION.PRESCRIPTION_CODE.eq(prescriptionCode))
                 .fetchAnyInto(PrescriptionInfoVo.class);
    }
    /**
     *
     * 获取
     * @param prescriptionCode
     * @return
     */
    public PrescriptionVo getDoByPrescriptionNo(String prescriptionCode){
        return db().select().from(PRESCRIPTION)
                .where(PRESCRIPTION.PRESCRIPTION_CODE.eq(prescriptionCode))
                .fetchAnyInto(PrescriptionVo.class);
    }

    /**
     * 获取一条记录
     *
     * @param id
     * @return
     */
    public <E> E getById(Integer id, Class<? extends E> type) {
        return db().selectFrom(PRESCRIPTION).where(PRESCRIPTION.ID.eq(id)).fetchAnyInto(type);
    }

    /**
     * 分页
     * @param param
     * @return
     */
    public PageResult<PrescriptionListVo> listPageResult(PrescriptionListParam param) {
        SelectOnConditionStep<? extends Record> record = db().select(PRESCRIPTION.asterisk(),
                PATIENT.NAME,PATIENT.ID.as("patientId"),DOCTOR.ID.as("doctorId")).from(PRESCRIPTION)
                .leftJoin(PATIENT).on(PATIENT.ID.eq(PRESCRIPTION.PATIENT_ID))
                .leftJoin(DOCTOR).on(DOCTOR.HOSPITAL_CODE.eq(PRESCRIPTION.DOCTOR_CODE));
        buildSelect(param, record);
        record.orderBy(PRESCRIPTION.DIAGNOSE_TIME.desc());
        return getPageResult(record, param, PrescriptionListVo.class);
    }

    private void buildSelect(PrescriptionListParam param, SelectOnConditionStep<? extends Record> record) {
        if (param.getPatientId()!=null&&param.getPatientId()>0){
            record.where(PRESCRIPTION.PATIENT_ID.eq(param.getPatientId()));
        }
        if (param.getPatientName()!=null&&param.getPatientName().trim().length()> 0){
            record.where(PRESCRIPTION.PATIENT_NAME.like(likeValue(param.getPatientName().trim())));
        }
        if (param.getDoctorCode()!=null&&param.getDoctorCode().trim().length()>0){
            record.where(PRESCRIPTION.DOCTOR_CODE.eq(param.getDoctorCode().trim()));
        }
        if (param.getPatientMobile()!=null&&param.getPatientMobile().trim().length()>0){
            record.where(PATIENT.MOBILE.eq(param.getPatientMobile()));
        }
        if (param.getPrescriptionCode()!=null&&param.getPrescriptionCode().trim().length()>0){
            record.where(PRESCRIPTION.PRESCRIPTION_CODE.eq(param.getPrescriptionCode().trim()));
        }
        if (param.getOrderSn()!=null&&param.getOrderSn().trim().length()>0){
            record.where(PRESCRIPTION.ORDER_SN.eq(param.getOrderSn().trim()));
        }
        if (param.getDepartmentName()!=null&&param.getDepartmentName().trim().length()>0){
            record.where(PRESCRIPTION.DEPARTMENT_NAME.eq(param.getDepartmentName()));
        }
        if (param.getDoctorName()!=null&&param.getDoctorName().trim().length()>0){
            record.where(PRESCRIPTION.DOCTOR_NAME.like(likeValue(param.getDoctorName().trim())));
        }
        if (param.getDiagnosisName()!=null&&!Strings.isEmpty(param.getDiagnosisName().trim())){
            record.where(PRESCRIPTION.DIAGNOSIS_NAME.eq(param.getDiagnosisName().trim()));
        }
        if (param.getDiagnoseEndTime()!=null&&param.getDiagnoseStartTime()!=null){
            record.where(PRESCRIPTION.DIAGNOSE_TIME.ge(param.getDiagnoseStartTime()))
                    .and(PRESCRIPTION.DIAGNOSE_TIME.le(param.getDiagnoseEndTime()));
        }
        if (param.getCreateEndTime()!=null&&param.getCreateStartTime()!=null){
            record.where(PRESCRIPTION.DIAGNOSE_TIME.ge(param.getCreateStartTime()))
                    .and(PRESCRIPTION.DIAGNOSE_TIME.le(param.getCreateEndTime()));
        }
        if (param.getAuditType()!=null){
            record.where(PRESCRIPTION.AUDIT_TYPE.eq(param.getAuditType()));
        }
        if (param.getIsUsed()!=null){
            record.where(PRESCRIPTION.IS_USED.eq(param.getIsUsed()));
        }
        if (param.getIsValid()!=null){
            record.where(PRESCRIPTION.IS_VALID.eq(param.getIsValid()));
        }
        if (param.getSettlementFlag()!=null){
            record.where(PRESCRIPTION.SETTLEMENT_FLAG.eq(param.getSettlementFlag()));
        }
        if (param.getExpireType()!=null){
            record.where(PRESCRIPTION.EXPIRE_TYPE.eq(param.getExpireType()));
        }
    }

    /**
     * 分页
      @param param
     * @return
     */
    public PageResult<PrescriptionListVo> listPatientPageResult(PrescriptionPatientListParam param) {
        SelectConditionStep<Record> and = db().select().from(PRESCRIPTION)
                .where(PRESCRIPTION.PATIENT_ID.eq(param.getPatientId()))
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE));
        if (param.getPrescriptionNos() != null) {
            and.and(PRESCRIPTION.PRESCRIPTION_CODE.in(param.getPrescriptionNos()));
        }
        if (!Strings.isEmpty(param.getPatientName())){
            and.and(PRESCRIPTION.PATIENT_NAME.like(likeValue(param.getPatientName())));
        }
        if (!Strings.isEmpty(param.getDoctorCode())){
            and.and(PRESCRIPTION.DOCTOR_CODE.eq(param.getDoctorCode()));
        }
        if (!Strings.isEmpty(param.getDoctorName())){
            and.and(PRESCRIPTION.DOCTOR_NAME.eq(likeValue(param.getDoctorName())));
        }
        if (param.getDiagnoseEndTime()!=null&&param.getDiagnoseStartTime()!=null){
            and.and(PRESCRIPTION.DIAGNOSE_TIME.ge(param.getDiagnoseStartTime()))
                    .and(PRESCRIPTION.DIAGNOSE_TIME.le(param.getDiagnoseEndTime()));
        }
        if (param.getAuditType()!=null){
            and.and(PRESCRIPTION.AUDIT_TYPE.eq(param.getAuditType()));
        }
        and.orderBy(PRESCRIPTION.CREATE_TIME.desc());
        return getPageResult(and, param, PrescriptionListVo.class);
    }

    /**
     * 小程序分页
     * @param param
     * @return
     */
    public PageResult<PrescriptionSimpleVo> listPageResultWx(PrescriptionPatientListParam param) {
        SelectConditionStep<Record> and = db().select().from(PRESCRIPTION)
                .where(PRESCRIPTION.PATIENT_ID.eq(param.getUserPatientParam().getPatientId()))
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE));
        if (PatientConstant.UN_FETCH.equals(param.getUserPatientParam().getIsFetch())) {
            and.and(PRESCRIPTION.USER_ID.eq(param.getUserPatientParam().getUserId()));
        }
        and.orderBy(PRESCRIPTION.CREATE_TIME.desc());
        return getPageResult(and, param, PrescriptionSimpleVo.class);
    }

    /**
     * 小程序分页
     * @return
     */
    public <E> List<E> listPrescriptionByCode(Collection<String> codeList, Class<? extends E> type) {
       return db().select().from(PRESCRIPTION)
                .where(PRESCRIPTION.PRESCRIPTION_CODE.in(codeList))
               .fetchInto(type);
    }
    /**
     * 小程序分页
     * @return
     */
    public <E> Map<String, E> mapPrescriptionByCode(Collection<String> codeList, Class<? extends E> type) {
       return db().select().from(PRESCRIPTION)
                .where(PRESCRIPTION.PRESCRIPTION_CODE.in(codeList))
               .fetchMap(PRESCRIPTION.PRESCRIPTION_CODE,type);
    }

    /**
     * 根据处方号查询处方信息
     * @return
     */
    public List<PrescriptionVo> listPrescriptionList(Collection<String> codeList){
       return db().select(PRESCRIPTION.asterisk()).from(PRESCRIPTION).where(PRESCRIPTION.PRESCRIPTION_CODE.in(codeList)).fetchInto(PrescriptionVo.class);
    }

    /**
     * *****
     * 获取有效处方通过商品id
     * @param goodsId 商品id
     * @param prescriptionNos
     * @return
     */
    public List<PrescriptionVo> getValidByGoodsId(Integer goodsId, List<String> prescriptionNos) {
        return db().select(PRESCRIPTION.asterisk(),PRESCRIPTION_ITEM.PRESCRIPTION_DETAIL_CODE)
                .from(PRESCRIPTION)
                .leftJoin(PRESCRIPTION_ITEM).on(PRESCRIPTION.PRESCRIPTION_CODE.eq(PRESCRIPTION_ITEM.PRESCRIPTION_CODE))
                .where(PRESCRIPTION_ITEM.GOODS_ID.eq(goodsId))
                .and(PRESCRIPTION.PRESCRIPTION_CODE.in(prescriptionNos))
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
                .orderBy(PRESCRIPTION.PRESCRIPTION_CREATE_TIME.desc())
                .fetchInto(PrescriptionVo.class);
    }



    /**
     * *****
     * 通用有效名查询处方明细
     *
     * @param prescriptionNos
     * @param goodsCommonName 商品名称
     * @return
     */
    public List<PrescriptionVo>  getValidByCommonName( List<String> prescriptionNos, String goodsCommonName){
        return db().select(PRESCRIPTION.asterisk(),PRESCRIPTION_ITEM.PRESCRIPTION_DETAIL_CODE)
                .from(PRESCRIPTION)
                .leftJoin(PRESCRIPTION_ITEM).on(PRESCRIPTION.PRESCRIPTION_CODE.eq(PRESCRIPTION_ITEM.PRESCRIPTION_CODE))
                .where(PRESCRIPTION_ITEM.GOODS_COMMON_NAME.eq(goodsCommonName))
                .and(PRESCRIPTION.PRESCRIPTION_CODE.in(prescriptionNos))
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
                .orderBy(PRESCRIPTION.PRESCRIPTION_CREATE_TIME.desc())
                .fetchInto(PrescriptionVo.class);
    }

    /**
     * *****
     * 通过有效通用名和规格系数查询处方明细
     * @param prescriptionNos
     * @param goodsCommonName 通用名
     * @param goodsQualityRatio 规格系数
     */
    public List<PrescriptionVo>  getValidByCommonNameAndQualityRatio( List<String> prescriptionNos , String goodsCommonName, String goodsQualityRatio) {
        return db().select(PRESCRIPTION.asterisk(),PRESCRIPTION_ITEM.PRESCRIPTION_DETAIL_CODE)
                .from(PRESCRIPTION)
                .leftJoin(PRESCRIPTION_ITEM).on(PRESCRIPTION.PRESCRIPTION_CODE.eq(PRESCRIPTION_ITEM.PRESCRIPTION_CODE))
                .where(PRESCRIPTION_ITEM.GOODS_COMMON_NAME.eq(goodsCommonName))
                .and(PRESCRIPTION.PRESCRIPTION_CODE.in(prescriptionNos))
                .and(PRESCRIPTION_ITEM.GOODS_QUALITY_RATIO.eq(goodsQualityRatio))
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
                .orderBy(PRESCRIPTION.PRESCRIPTION_CREATE_TIME.desc())
                .fetchInto(PrescriptionVo.class);
    }
    /**
     * *****
     * 通过有效通用名和规格系数查询处方明细
     * @param goodsCommonName 通用名
     * @param goodsQualityRatio 规格系数
     * @param productionEnterprise 生产企业
     */
    public List<PrescriptionVo>  getValidByCommonNameAndQualityRatio(List<String> prescriptionNos,String goodsCommonName, String goodsQualityRatio,String productionEnterprise) {
        return db().select(PRESCRIPTION.asterisk(),PRESCRIPTION_ITEM.PRESCRIPTION_DETAIL_CODE)
                .from(PRESCRIPTION)
                .leftJoin(PRESCRIPTION_ITEM).on(PRESCRIPTION.PRESCRIPTION_CODE.eq(PRESCRIPTION_ITEM.PRESCRIPTION_CODE))
                .where(PRESCRIPTION_ITEM.GOODS_COMMON_NAME.eq(goodsCommonName))
                .and(PRESCRIPTION.PRESCRIPTION_CODE.in(prescriptionNos))
                .and(PRESCRIPTION_ITEM.GOODS_PRODUCTION_ENTERPRISE.eq(productionEnterprise))
                .and(PRESCRIPTION_ITEM.GOODS_QUALITY_RATIO.eq(goodsQualityRatio))
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
                .orderBy(PRESCRIPTION.PRESCRIPTION_CREATE_TIME.desc())
                .fetchInto(PrescriptionVo.class);
    }


    /**
     * *****
     * 患者失效的历史处方no(所有)
     * @param patientId
     * @return
     */
    public List<String> getValidPrescriptionByPatient(Integer patientId) {
        return db().select(PRESCRIPTION.PRESCRIPTION_CODE).from(PRESCRIPTION)
            .where(PRESCRIPTION.PATIENT_ID.eq(patientId))
                .and(PRESCRIPTION.IS_VALID.eq(BaseConstant.YES)
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(String.class);
    }

    /**
     * *****
     * 患者失效的历史处方no（仅用户自己）
     * @param param
     * @return
     */
    public List<String> getValidPrescriptionByUserPatient(UserPatientParam param) {
        return db().select(PRESCRIPTION.PRESCRIPTION_CODE).from(PRESCRIPTION)
            .where(PRESCRIPTION.PATIENT_ID.eq(param.getPatientId()))
                .and(PRESCRIPTION.USER_ID.eq(param.getUserId()))
                    .and(PRESCRIPTION.IS_VALID.eq(BaseConstant.YES)
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(String.class);
    }

    /**
     * 患者未过期的历史处方列表
     * @param patientId
     * @return
     */
    public List<PrescriptionInfoVo> listValidPrescriptionInfosByPatient(Integer patientId) {
        return db().select().from(PRESCRIPTION)
            .where(PRESCRIPTION.PATIENT_ID.eq(patientId)
                .and(PRESCRIPTION.PRESCRIPTION_EXPIRE_TIME.gt(DateUtil.date().toTimestamp()))
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(PrescriptionInfoVo.class);
    }

    /**
     * 获取患者的历史疾病
     * @param patientId 患者id
     * @return
     */
    public List<PrescriptionDo> listDiagnosis(Integer patientId) {
        return db().select(PRESCRIPTION.DIAGNOSIS_NAME,PRESCRIPTION.PATIENT_TREATMENT_CODE)
                .from(PRESCRIPTION)
                .where(PRESCRIPTION.PATIENT_ID.eq(patientId))
                .fetchInto(PrescriptionDo.class);
    }



    /**
     * 更新处方
     * @param fetchPrescriptionVo 处方入参
     */
    public void updateHisPrescription(FetchPrescriptionVo fetchPrescriptionVo){
        PrescriptionRecord prescriptionRecord = db().select().from(PRESCRIPTION)
            .where(PRESCRIPTION.PRESCRIPTION_CODE.eq(fetchPrescriptionVo.getPrescriptionCode()))
            .fetchOneInto(PrescriptionRecord.class);
        FieldsUtil.assign(fetchPrescriptionVo, prescriptionRecord);
        prescriptionRecord.update();
        fetchPrescriptionVo.setId(prescriptionRecord.getId());
    }

    /**
     * 根据上次打开已开具页面查询是否有未读消息
     * @param timestamp 上次打开已开具页面时间
     * @return Boolean
     */
    public Boolean isExistAlreadyReadPrescription(String hospitalCode, Timestamp timestamp){
        Integer timestamps = db().selectCount().from(PRESCRIPTION)
            .where(PRESCRIPTION.CREATE_TIME.gt(timestamp))
            .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .and(PRESCRIPTION.IS_VALID.eq(DelFlag.NORMAL_VALUE))
            .and(PRESCRIPTION.SOURCE.eq(PrescriptionConstant.SOURCE_MP_SYSTEM))
            .and(PRESCRIPTION.AUDIT_TYPE.eq(PrescriptionConstant.PRESCRIPTION_AUDIT_TYPE_PRESCRIBE))
            .and(PRESCRIPTION.DOCTOR_CODE.eq(hospitalCode))
            .fetchAnyInto(Integer.class);
        return timestamps != 0;
    }

    /**
     * 查询是否有医师未读已续方
     * @param hospitalCode 医师院内编码
     * @param timestamp 上次打开页面时间
     * @return Boolean
     */
    public Boolean isExistAlreadyReadContinuedPrescription(String hospitalCode, Timestamp timestamp){
        Integer timestamps = db().selectCount().from(PRESCRIPTION)
            .where(PRESCRIPTION.CREATE_TIME.gt(timestamp))
            .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .and(PRESCRIPTION.IS_VALID.eq(DelFlag.NORMAL_VALUE))
            .and(PRESCRIPTION.SOURCE.eq(PrescriptionConstant.SOURCE_MP_SYSTEM))
            .and(PRESCRIPTION.AUDIT_TYPE.eq(PrescriptionConstant.PRESCRIPTION_AUDIT_TYPE_AUDIT))
            .and(PRESCRIPTION.DOCTOR_CODE.eq(hospitalCode))
            .fetchAnyInto(Integer.class);
        return timestamps != 0;
    }


    /**
     * 医生获取处方
     * @return
     */
    public PageResult<PrescriptionParam> listAuditedByDoctor(DoctorAuditedPrescriptionParam param) {
        SelectConditionStep<Record> where = db().select()
                .from(PRESCRIPTION)
                .where(PRESCRIPTION.DOCTOR_CODE.eq(param.getDoctorCode()));
        switch (param.getType()){
            case 0:
                //全部
                break;
            case 1:
                //审核
                where.and(PRESCRIPTION.AUDIT_TYPE.eq(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT));
                break;
            case 2:
                //已开具
                where.and(PRESCRIPTION.AUDIT_TYPE.eq(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE)
                        .or(PRESCRIPTION.AUDIT_TYPE.eq(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_PRESCRIPTION)));
                break;
            case 3:
                //开方
                where.and(PRESCRIPTION.AUDIT_TYPE.eq(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_PRESCRIPTION));
                break;
            default:
        }
         where.orderBy(PRESCRIPTION.CREATE_TIME.desc());
        return getPageResult(where,param,PrescriptionParam.class);
    }

    /**
     * 获取所有过期的处方
     * @return
     */
    public List<PrescriptionDo> getAllExpiredPrescription(){
        return db().select().from(PRESCRIPTION)
            .where(PRESCRIPTION.PRESCRIPTION_EXPIRE_TIME.lt(DateUtil.date().toTimestamp()))
            .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .fetchInto(PrescriptionDo.class);
    }

    /**
     * 更新处方使用状态和订单号
     * @param prescriptionCode
     * @param orderSn
     * @return
     */
    public int updatePrescriprionIsUseredAndOrderSn(String prescriptionCode, String orderSn) {
      return  db().update(PRESCRIPTION).set(PRESCRIPTION.IS_USED,BaseConstant.YES)
              .set(PRESCRIPTION.ORDER_SN,orderSn)
              .where(PRESCRIPTION.PRESCRIPTION_CODE.eq(prescriptionCode)).execute();
    }

    /**
     * 更新处方为未使用状态
     * @param prescriptionCode
     * @return
     */
    public int updatePrescriprionIsUnUsered(String prescriptionCode) {
      return  db().update(PRESCRIPTION).set(PRESCRIPTION.IS_USED,BaseConstant.NO).where(PRESCRIPTION.PRESCRIPTION_CODE.eq(prescriptionCode)).execute();
    }

    /**
     * 患者关联的处方数
     * @param patientId
     * @return
     */
    public Integer countPrescriptionByPatient(Integer patientId) {
        return db().fetchCount(PRESCRIPTION, PRESCRIPTION.PATIENT_ID.eq(patientId));
    }

    public void updateSettlementFlag(String prescriptionCode,Byte settlementFlag){
        db().update(PRESCRIPTION).set(PRESCRIPTION.SETTLEMENT_FLAG,settlementFlag).where(PRESCRIPTION.PRESCRIPTION_CODE.eq(prescriptionCode))
        .execute();
    }

    /**
     * 医师时间段内的处方数量
     * @param doctorCode 医师编号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public Integer countDateByDoctor(String doctorCode, Timestamp startTime, Timestamp endTime) {
        return db().selectCount().from(PRESCRIPTION)
                .where(PRESCRIPTION.DOCTOR_CODE.eq(doctorCode))
                .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
                .and(PRESCRIPTION.IS_VALID.eq(BaseConstant.YES))
                .and(PRESCRIPTION.CREATE_TIME.between(startTime,endTime))
                .fetchAnyInto(Integer.class);
    }

    /**
     * 医师时间段内的处方数量,金额
     * @param doctorCode
     * @param startTime
     * @param endTime
     * @return
     */
    public DoctorDetailPerformanceVo countSumDateByDoctor(String doctorCode, Timestamp startTime, Timestamp endTime) {
        return db().select(
            DSL.ifnull(DSL.count(PRESCRIPTION.PRESCRIPTION_CODE),0).as("prescriptionNum"),
            DSL.ifnull(DSL.sum(PRESCRIPTION.TOTAL_PRICE),0).as("prescriptionMoney")
        ).from(PRESCRIPTION)
            .where(PRESCRIPTION.DOCTOR_CODE.eq(doctorCode))
            .and(PRESCRIPTION.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .and(PRESCRIPTION.IS_VALID.eq(BaseConstant.YES))
            .and(PRESCRIPTION.CREATE_TIME.between(startTime,endTime))
            .fetchAnyInto(DoctorDetailPerformanceVo.class);
    }
    /**
     * 查询用户关联医师处方
     * @param doctorCode 医师code
     * @param userId 用户id
     * @return List<PrescriptionDo>
     */
    public PrescriptionDoctorVo getDoctorPrescription(String doctorCode, Integer userId) {
        return db().select(
              DSL.ifnull(DSL.count(PRESCRIPTION.TOTAL_PRICE), 0).as("totalCount")
            , DSL.ifnull(DSL.sum(PRESCRIPTION.TOTAL_PRICE), new BigDecimal(0)).as("totalPrice"))
            .from(PRESCRIPTION)
            .where(PRESCRIPTION.DOCTOR_CODE.eq(doctorCode))
            .and(PRESCRIPTION.USER_ID.eq(userId))
            .fetchAnyInto(PrescriptionDoctorVo.class);
    }

    /**
     * 更新药师签名
     * @param prescriptionCode
     * @param pharmacistSignature
     */
    public void updatePharmacistSignature(String prescriptionCode,String pharmacistSignature){
        db().update(PRESCRIPTION).set(PRESCRIPTION.PHARMACIST_SIGNATURE,pharmacistSignature)
            .where(PRESCRIPTION.PRESCRIPTION_CODE.eq(prescriptionCode)).execute();
    }

    /**
     * 根据医师id查询关联处方
     * @param doctorQueryPrescriptionParam 查询处方入参
     * @return PageResult<DoctorQueryPrescriptionVo>
     */
    public PageResult<DoctorQueryPrescriptionVo> getDoctorQueryPrescription(DoctorQueryPrescriptionParam doctorQueryPrescriptionParam) {
        SelectConditionStep<? extends Record> where = db()
            .select(PRESCRIPTION.PRESCRIPTION_CODE,
                PRESCRIPTION.DEPARTMENT_NAME,
                PRESCRIPTION.PATIENT_NAME,
                PRESCRIPTION.AUDIT_TYPE,
                PRESCRIPTION.ORDER_SN,
                PRESCRIPTION.TOTAL_PRICE,
                PRESCRIPTION.CREATE_TIME.as("treatmentTime"))
            .from(PRESCRIPTION)
            .where(PRESCRIPTION.DOCTOR_CODE.eq(doctorQueryPrescriptionParam.getDoctorCode()));
        doctorQueryPrescriptionBuildOption(doctorQueryPrescriptionParam, where);
        return this.getPageResult(where, doctorQueryPrescriptionParam.getCurrentPage(),
            doctorQueryPrescriptionParam.getPageRows(), DoctorQueryPrescriptionVo.class);
    }

    /**
     * 医师查询关联处方条件查询
     * @param doctorQueryPrescriptionParam 医师查询处方条件
     * @param select 查询实体
     */
    private void doctorQueryPrescriptionBuildOption(DoctorQueryPrescriptionParam doctorQueryPrescriptionParam, SelectConditionStep<? extends Record> select) {
        if (doctorQueryPrescriptionParam.getPatientName() != null && doctorQueryPrescriptionParam.getPatientName().trim().length() > 0) {
            select.and(PRESCRIPTION.PATIENT_NAME.like(likeValue(doctorQueryPrescriptionParam.getPatientName().trim())));
        }
        if (doctorQueryPrescriptionParam.getDepartmentName() != null && doctorQueryPrescriptionParam.getDepartmentName().trim().length() > 0) {
            select.and(PRESCRIPTION.DEPARTMENT_NAME.like(likeValue(doctorQueryPrescriptionParam.getDepartmentName().trim())));
        }
        if (doctorQueryPrescriptionParam.getAuditType() != null && PRESCRIPTION_TYPE_IS_HIS_FETCH.equals(doctorQueryPrescriptionParam.getAuditType())) {
            select.and(PRESCRIPTION.SOURCE.eq(PRESCRIPTION_SOURCE_IS_HIS));
        } else if (doctorQueryPrescriptionParam.getAuditType() != null) {
            select.and(PRESCRIPTION.AUDIT_TYPE.eq(doctorQueryPrescriptionParam.getAuditType()));
        }
        if (doctorQueryPrescriptionParam.getStartTime() != null || doctorQueryPrescriptionParam.getEndTime() != null) {
            select.and(PRESCRIPTION.CREATE_TIME.ge(doctorQueryPrescriptionParam.getStartTime()))
                .and(PRESCRIPTION.CREATE_TIME.le(doctorQueryPrescriptionParam.getEndTime()));
        }
        select.orderBy(PRESCRIPTION.CREATE_TIME.desc());
    }
}

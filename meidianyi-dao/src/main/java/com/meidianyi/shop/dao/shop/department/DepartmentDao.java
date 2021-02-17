package com.meidianyi.shop.dao.shop.department;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DepartmentRecord;
import com.meidianyi.shop.service.pojo.shop.department.*;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderConstant;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * @author chenjie
 */
@Repository
public class DepartmentDao extends ShopBaseDao {
    public static final Integer ROOT_ID = 0;
    public static final String SCORE = "score";
    public static final String DOCTOR_NUMBER = "doctor_number";
    public static final String DOCTOR_NUM_SCORE = "doctor_num_score";

    /**
     * 科室列表
     *
     * @param param
     * @return
     */
    public PageResult<DepartmentListVo> getDepartmentList(DepartmentListParam param) {
        SelectHavingStep<Record2<Integer, Integer>> doctorTable = getDoctorNumberTableAdmin();
        SelectHavingStep<Record6<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> departmentDataTable = getDepartmentStatisticTable();
        SelectJoinStep<? extends Record> select = db()
            .select(DEPARTMENT.ID, DEPARTMENT.CODE, DEPARTMENT.CREATE_TIME,
                DEPARTMENT.NAME, DEPARTMENT.PARENT_ID, DEPARTMENT.PARENT_IDS,DEPARTMENT.LEVEL,DEPARTMENT.IS_LEAF
                ,doctorTable.field("doctor_number"),departmentDataTable.field("consultation_number")
                ,departmentDataTable.field("inquiry_money"),departmentDataTable.field("inquiry_number")
                ,departmentDataTable.field("prescription_money"),departmentDataTable.field("prescription_num"))
            .from(DEPARTMENT)
            .leftJoin(doctorTable).on(doctorTable.field(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID).eq(DEPARTMENT.ID))
            .leftJoin(departmentDataTable).on(departmentDataTable.field(DEPARTMENT_SUMMARY_TREND.DEPARTMENT_ID).eq(DEPARTMENT.ID));
//        SelectHavingStep<Record6<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> statisticTable = getDepartmentStatisticTable();
        select.where(DEPARTMENT.IS_DELETE.eq((byte) 0)).and(DEPARTMENT.LEVEL.eq((Integer) 1));
        buildOptions(select, param);
        select.orderBy(DEPARTMENT.ID.desc());
        PageResult<DepartmentListVo> departmentList = this.getPageResult(select, param.getCurrentPage(),
            param.getPageRows(), DepartmentListVo.class);
        return departmentList;
    }

    /**
     * 科室搜索查询
     *
     * @param select
     * @param param
     */
    protected void buildOptions(SelectJoinStep<? extends Record> select, DepartmentListParam param) {
        Timestamp nowDate = new Timestamp(System.currentTimeMillis());
    }

    /**
     * 获取一条科室的信息
     *
     * @param departmentId
     * @return
     */
    public DepartmentOneParam getOneInfo(Integer departmentId) {
        DepartmentOneParam info = db().select().from(DEPARTMENT).where(DEPARTMENT.ID.eq(departmentId))
            .fetchOneInto(DepartmentOneParam.class);
        return info;
    }

    /**
     * 编辑保存
     *
     * @param param
     * @return
     */
    public int updateDepartment(DepartmentOneParam param) {
        DepartmentRecord record = new DepartmentRecord();
        FieldsUtil.assign(param, record);
        return db().executeUpdate(record);
    }

    /**
     * 添加测评活动信息
     *
     * @param param
     * @return
     */
    public void insertDepartment(DepartmentOneParam param) {
        DepartmentRecord record = db().newRecord(DEPARTMENT);
        FieldsUtil.assign(param, record);
        record.insert();
        param.setId(record.getId());
    }

    /**
     * 删除
     *
     * @param departmentId
     * @return
     */
    public int deleteDepartment(Integer departmentId) {
        int res = db().update(DEPARTMENT).set(DEPARTMENT.IS_DELETE, (byte) 1).where(DEPARTMENT.ID.eq(departmentId))
            .execute();
        return res;
    }

    /**
     *  更新科室叶子节点状态
     *
     * @param departmentId
     * @param isLeaf 1叶子节点 0非叶子节点
     * @return
     */
    public int updateDepartmentIsLeaf(Integer departmentId,Byte isLeaf) {
        int res = db().update(DEPARTMENT).set(DEPARTMENT.IS_LEAF, isLeaf)
            .where(DEPARTMENT.ID.eq(departmentId)).execute();
        return res;
    }

    /**
     * 获取科室列表By父节点
     *
     * @param departmentId
     * @return
     */
    public List<DepartmentListVo> listDepartmentByParentId(Integer departmentId) {

        if (ROOT_ID.equals(departmentId)) {
            List<DepartmentListVo> departmentList = db().select().from(DEPARTMENT).where(DEPARTMENT.LEVEL.eq(1).and(DEPARTMENT.IS_DELETE.eq((byte)0)))
                .fetchInto(DepartmentListVo.class);
            return departmentList;
        } else {
            List<DepartmentListVo> departmentList = db().select().from(DEPARTMENT).where(DEPARTMENT.PARENT_ID.eq(departmentId).and(DEPARTMENT.IS_DELETE.eq((byte)0)))
                .fetchInto(DepartmentListVo.class);
            return departmentList;
        }
    }

    /**
     * 科室子节点数目
     *
     * @param departmentId
     * @return
     */
    public int countDepartment(Integer departmentId) {
        int departmentNum = db().selectCount().from(DEPARTMENT).where(DEPARTMENT.PARENT_ID.eq(departmentId).and(DEPARTMENT.IS_DELETE.eq((byte)0))).fetchOneInto(int.class);
        return departmentNum;
    }

    /**
     * 科室是否存在，用来新增检查
     * @param departmentId 科室ID
     * @param name 科室名称
     * @return true 存在 false 不存在
     */
    public boolean isNameExist(Integer departmentId,String name) {
        Condition condition = DEPARTMENT.NAME.eq(name);
        if (departmentId != null) {
            condition = condition.and(DEPARTMENT.ID.ne(departmentId));
        }
        int count = db().fetchCount(DEPARTMENT, condition);
        return count>0;
    }

    /**
     *
     * @return
     */
    public List<DepartmentOneParam> getListByIds(List<Integer> departmentIds) {
        return db().select(DEPARTMENT.ID, DEPARTMENT.NAME).from(DEPARTMENT).where(DEPARTMENT.ID.in(departmentIds).and(DEPARTMENT.IS_DELETE.eq((byte) 0))).fetchInto(DepartmentOneParam.class);
    }

    public List<Integer> getDepartmentIdsByName(String name) {
        return db().select(DEPARTMENT.ID).from(DEPARTMENT).where(DEPARTMENT.IS_DELETE.eq((byte) 0).and(DEPARTMENT.NAME.like(likeValue(name)))).fetchInto(Integer.class);
    }

    /**
     * 获取一条科室的信息(根据code)
     *
     * @param code
     * @return
     */
    public DepartmentOneParam getDepartmentByCode(String code) {
        return db().select().from(DEPARTMENT).where(DEPARTMENT.CODE.eq(code))
            .fetchAnyInto(DepartmentOneParam.class);
    }

    /**
     * 科室子节点
     *
     * @param departmentId
     * @return
     */
    public List<DepartmentOneParam> getChildDepartment(Integer departmentId) {
        List<DepartmentOneParam> departments = db().select().from(DEPARTMENT).where(DEPARTMENT.PARENT_ID.eq(departmentId)).fetchInto(DepartmentOneParam.class);
        return departments;
    }

    public void updateDepartmentLevel(Integer id, Integer level) {
        db().update(DEPARTMENT)
            .set(DEPARTMENT.LEVEL, level)
            .where(DEPARTMENT.ID.eq(id))
            .execute();
    }

    /**
     * 获取子节点科室信息
     * @param name
     * @return
     */
    public List<DepartmentOneParam> listDepartmentsByName(String name) {
        Condition condition = DEPARTMENT.IS_DELETE.eq((byte) 0).and(DEPARTMENT.IS_LEAF.eq(DepartmentConstant.LEAF));
        if (name != null && name != "") {
            condition = condition.and(DEPARTMENT.NAME.like(likeValue(name)));
        }
        return db().select().from(DEPARTMENT).where(condition).fetchInto(DepartmentOneParam.class);
    }

    /**
     * 获取科室列表信息（小程序）
     * @return
     */
    public List<DepartmentOneParam> listDepartmentsForWx() {
        Condition condition = DEPARTMENT.IS_DELETE.eq((byte) 0).and(DEPARTMENT.IS_LEAF.eq(DepartmentConstant.LEAF));

        return db().select().from(DEPARTMENT).where(condition).fetchInto(DepartmentOneParam.class);
    }

    /**
     * 根据id集合查询对应信息
     * @param departmentIds id集合
     * @return 科室信息集合
     */
    public List<DepartmentSimpleVo> listDepartmentInfo(List<Integer> departmentIds) {
        return db().select(DEPARTMENT.ID,DEPARTMENT.NAME).from(DEPARTMENT).where(DEPARTMENT.ID.in(departmentIds).and(DEPARTMENT.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(DepartmentSimpleVo.class);
    }

    /**
     * 从统计表取每个科室的总的统计数据子查询（5项）
     * @return
     */
    public SelectHavingStep<Record6<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> getDepartmentStatisticTable(){
        return db().select(DEPARTMENT_SUMMARY_TREND.DEPARTMENT_ID
            ,DSL.sum(DEPARTMENT_SUMMARY_TREND.CONSULTATION_NUMBER).as("consultation_number")
            ,DSL.sum(DEPARTMENT_SUMMARY_TREND.INQUIRY_MONEY).as("inquiry_money"),DSL.sum(DEPARTMENT_SUMMARY_TREND.INQUIRY_NUMBER).as("inquiry_number")
            ,DSL.sum(DEPARTMENT_SUMMARY_TREND.PRESCRIPTION_MONEY).as("prescription_money"),DSL.sum(DEPARTMENT_SUMMARY_TREND.PRESCRIPTION_NUM).as("prescription_num")
        )
            .from(DEPARTMENT_SUMMARY_TREND)
            .where(DEPARTMENT_SUMMARY_TREND.TYPE.eq(StatisticConstant.TYPE_YESTODAY))
            .groupBy(DEPARTMENT_SUMMARY_TREND.DEPARTMENT_ID);
    }

    public DepartmentStatisticSelectVo getDepartmentSelect(){
        return DepartmentStatisticSelectVo.builder().consultationTable(getConsultationTable())
            .doctorNumberTable(getDoctorNumberTable())
            .prescriptionTable(getPrescriptionTable())
            .inquiryTable(getInquiryTable()).build();
    }

    /**
     * 科室医师数量子查询（小程序）
     * @return
     */
    public SelectHavingStep<Record2<Integer, Integer>> getDoctorNumberTable(){
        return db().select(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID,DSL.count(DOCTOR.ID).as("doctor_number"))
            .from(DOCTOR_DEPARTMENT_COUPLE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID))
            .where(DOCTOR.IS_DELETE.eq((byte) 0))
            .and(DOCTOR.STATUS.eq((byte) 1))
            .and(DOCTOR.CAN_CONSULTATION.eq((byte) 1))
            .and(DOCTOR.USER_ID.gt(0))
            .groupBy(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID);
    }

    /**
     * 科室医师数量子查询（小程序）
     * @return
     */
    public SelectHavingStep<Record2<Integer, Integer>> getDoctorNumberTableAdmin(){
        return db().select(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID,DSL.count(DOCTOR.ID).as("doctor_number"))
            .from(DOCTOR_DEPARTMENT_COUPLE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID))
            .where(DOCTOR.IS_DELETE.eq((byte) 0))
            .groupBy(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID);
    }

    public SelectHavingStep<Record2<Integer, BigDecimal>> getConsultationTable(){
        return db().select(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID,DSL.sum(DOCTOR.CONSULTATION_NUMBER).as("consultation_number"))
            .from(DOCTOR_DEPARTMENT_COUPLE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID))
            .groupBy(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID);
    }


    public SelectHavingStep<Record3<Integer, Integer, BigDecimal>> getPrescriptionTable(){
        return db().select(DEPARTMENT.ID,DSL.countDistinct(PRESCRIPTION.ID).as("presciption_num"),DSL.sum(PRESCRIPTION_ITEM.MEDICINE_PRICE).as("prescription_money"))
            .from(DEPARTMENT)
            .leftJoin(PRESCRIPTION ).on(PRESCRIPTION.DEPARTMENT_CODE.eq(DEPARTMENT.CODE))
            .leftJoin(PRESCRIPTION_ITEM).on(PRESCRIPTION_ITEM.PRESCRIPTION_CODE.eq(PRESCRIPTION.PRESCRIPTION_CODE))
            .where(PRESCRIPTION.STATUS.eq(PrescriptionConstant.STATUS_PASS))
            .groupBy(DEPARTMENT.ID);
    }

    public SelectHavingStep<Record3<Integer, Integer, BigDecimal>> getInquiryTable(){
        return db().select(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID,DSL.count(INQUIRY_ORDER.ORDER_ID).as("inquiry_number"),DSL.sum(INQUIRY_ORDER.ORDER_AMOUNT).as("inquiry_money"))
            .from(DOCTOR_DEPARTMENT_COUPLE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID))
            .leftJoin(INQUIRY_ORDER).on(INQUIRY_ORDER.DOCTOR_ID.eq(DOCTOR.ID))
            .where(INQUIRY_ORDER.ORDER_STATUS.notIn(InquiryOrderConstant.ORDER_TO_PAID,InquiryOrderConstant.ORDER_CANCELED))
            .groupBy(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID);
    }
    /**
     * 获取所有科室的信息
     *d
     * @return
     */
    public List<DepartmentOneParam> getAllDepartment() {
        return db().select().from(DEPARTMENT).where(DEPARTMENT.IS_LEAF.eq(DepartmentConstant.LEAF))
            .fetchInto(DepartmentOneParam.class);
    }

    /**
     * 获取科室处方统计数据
     * @param param
     * @return
     */
    public DepartmentStatisticOneParam getDepartmentInquiryData(DepartmentStatisticParam param) {
        return db().select(DSL.countDistinct(INQUIRY_ORDER.ORDER_ID).as("inquiry_number"),DSL.sum(INQUIRY_ORDER.ORDER_AMOUNT).as("inquiry_money"))
            .from(DOCTOR_DEPARTMENT_COUPLE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID))
            .leftJoin(INQUIRY_ORDER).on(INQUIRY_ORDER.DOCTOR_ID.eq(DOCTOR.ID))
            .where(INQUIRY_ORDER.ORDER_STATUS.notIn(InquiryOrderConstant.ORDER_TO_PAID,InquiryOrderConstant.ORDER_CANCELED))
            .and(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID.eq(param.getDepartmentId()))
            .and(INQUIRY_ORDER.CREATE_TIME.ge(param.getStartTime()))
            .and(INQUIRY_ORDER.CREATE_TIME.le(param.getEndTime()))
            .fetchAnyInto(DepartmentStatisticOneParam.class);
    }

    /**
     * 获取科室接诊统计数据
     * @param param
     * @return
     */
    public Integer getDepartmentConsultationData(DepartmentStatisticParam param) {
        return db().select(DSL.countDistinct(IM_SESSION.ID).as("consultation_number"))
            .from(DOCTOR_DEPARTMENT_COUPLE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID))
            .leftJoin(IM_SESSION).on(IM_SESSION.DOCTOR_ID.eq(DOCTOR.ID))
            .where(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID.eq(param.getDepartmentId()))
            .and(IM_SESSION.RECEIVE_START_TIME.ge(param.getStartTime()))
            .and(IM_SESSION.RECEIVE_START_TIME.le(param.getEndTime()))
            .fetchAnyInto(Integer.class);
    }

    /**
     * 获取科室处方统计数据
     * @param param
     * @return
     */
    public DepartmentStatisticOneParam getDepartmentPrescriptionData(DepartmentStatisticParam param) {
        return db().select(DSL.countDistinct(PRESCRIPTION.ID).as("prescription_num"),DSL.sum(PRESCRIPTION_ITEM.MEDICINE_PRICE).as("prescription_money"))
            .from(DEPARTMENT)
            .leftJoin(PRESCRIPTION ).on(PRESCRIPTION.DEPARTMENT_CODE.eq(DEPARTMENT.CODE))
            .leftJoin(PRESCRIPTION_ITEM).on(PRESCRIPTION_ITEM.PRESCRIPTION_CODE.eq(PRESCRIPTION.PRESCRIPTION_CODE))
            .where(DEPARTMENT.ID.eq(param.getDepartmentId()))
//            .and(PRESCRIPTION.STATUS.eq(PrescriptionConstant.STATUS_PASS))
            .and(PRESCRIPTION.CREATE_TIME.ge(param.getStartTime()))
            .and(PRESCRIPTION.CREATE_TIME.le(param.getEndTime()))
            .fetchAnyInto(DepartmentStatisticOneParam.class);
    }

    /**
     * 查询医师所属科室名称
     * @param doctorId 医师id
     * @return List<String>
     */
    public List<String> getDepartmentNameByDoctor(Integer doctorId) {
        return db().select(DEPARTMENT.NAME)
            .from(DEPARTMENT)
            .leftJoin(DOCTOR_DEPARTMENT_COUPLE)
            .on(DEPARTMENT.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID))
            .where(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID.eq(doctorId)).fetchInto(String.class);
    }

    /**
     * 科室评分查询
     * @return
     */
    public SelectHavingStep<Record2<Integer, BigDecimal>> getDepartmentStatisticScore(Integer departmentRecommendType, Integer consultationRate,Integer inquiryRate,Integer doctorRate){
        LocalDateTime today = LocalDate.now().atStartOfDay();
        Date refDate = Date.valueOf(today.minusDays(1).toLocalDate());
        SelectHavingStep<Record2<Integer, BigDecimal>> doctorNumberScoreTable = getDoctorScoreTable();
        if (doctorNumberScoreTable == null) {
            return db().select(DEPARTMENT_SUMMARY_TREND.DEPARTMENT_ID
                ,DEPARTMENT_SUMMARY_TREND.CONSULTATION_SCORE.multiply(consultationRate).add(DEPARTMENT_SUMMARY_TREND.INQUIRY_SCORE.multiply(inquiryRate)).as(SCORE))
                .from(DEPARTMENT_SUMMARY_TREND)
                .where(DEPARTMENT_SUMMARY_TREND.REF_DATE.eq(refDate))
                .and(DEPARTMENT_SUMMARY_TREND.TYPE.eq((byte)departmentRecommendType.intValue()));
        }
        return db().select(DEPARTMENT_SUMMARY_TREND.DEPARTMENT_ID
            ,DEPARTMENT_SUMMARY_TREND.CONSULTATION_SCORE.multiply(consultationRate).add(DEPARTMENT_SUMMARY_TREND.INQUIRY_SCORE.multiply(inquiryRate)).add(doctorNumberScoreTable.field(DOCTOR_NUM_SCORE).multiply(doctorRate)).as(SCORE))
            .from(DEPARTMENT_SUMMARY_TREND)
            .leftJoin(doctorNumberScoreTable).on(doctorNumberScoreTable.field(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID).eq(DEPARTMENT_SUMMARY_TREND.DEPARTMENT_ID))
            .where(DEPARTMENT_SUMMARY_TREND.REF_DATE.eq(refDate))
            .and(DEPARTMENT_SUMMARY_TREND.TYPE.eq((byte)departmentRecommendType.intValue()));
    }

    /**
     * 科室医师数量子查询（小程序）
     * @return
     */
    public SelectHavingStep<Record2<Integer, BigDecimal>> getDoctorScoreTable(){
        List<Integer> departmentDoctorNumber = getDepartmentDoctorNumber();
        Integer max =  Collections.max(departmentDoctorNumber);
        Integer min =  Collections.min(departmentDoctorNumber);
        BigDecimal minDecimal = new BigDecimal(min);
        Integer differ = max-min;
        BigDecimal differDecimal = new BigDecimal(differ);
        if (BigDecimal.ZERO.equals(differDecimal)) {
            return null;
        }
        return db().select(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID
            ,(DSL.count(DOCTOR.ID).cast(SQLDataType.DECIMAL(10,2)).sub(minDecimal)).divide(differDecimal).as(DOCTOR_NUM_SCORE))
            .from(DOCTOR_DEPARTMENT_COUPLE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID))
            .where(DOCTOR.IS_DELETE.eq((byte) 0))
            .and(DOCTOR.STATUS.eq((byte) 1))
            .and(DOCTOR.CAN_CONSULTATION.eq((byte) 1))
            .groupBy(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID);
    }

    public List<Integer> getDepartmentDoctorNumber(){
        return db().select(DSL.count(DOCTOR.ID).as(DOCTOR_NUMBER))
            .from(DOCTOR_DEPARTMENT_COUPLE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID))
            .where(DOCTOR.IS_DELETE.eq((byte) 0))
            .and(DOCTOR.STATUS.eq((byte) 1))
            .and(DOCTOR.CAN_CONSULTATION.eq((byte) 1))
            .groupBy(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID)
            .fetchInto(Integer.class);
    }


    /**
     * 获取子节点科室信息
     * @param param
     * @return
     */
    public List<DepartmentOneParam> listDepartmentsByOptions(DepartmentListParam param) {
        Condition condition = DEPARTMENT.IS_DELETE.eq((byte) 0).and(DEPARTMENT.IS_LEAF.eq(DepartmentConstant.LEAF));
        if (param.getKeyword() != null && param.getKeyword() != "") {
            condition = condition.and(DEPARTMENT.NAME.like(likeValue(param.getKeyword())));
        }
        if (param.getDepartmentIds() != null) {
            condition = condition.and(DEPARTMENT.ID.in(param.getDepartmentIds()));
        }
        LocalDateTime today = LocalDate.now().atStartOfDay();
        SelectHavingStep<Record2<Integer, Integer>> doctorTable = getDoctorNumberTable();
        SelectHavingStep<Record2<Integer, BigDecimal>> departmentScoreTable = getDepartmentStatisticScore(param.getDepartmentRecommendType(),param.getConsultationRate(),param.getInquiryRate(),param.getDoctorRate());
        SelectJoinStep<? extends Record> select = db().select(DEPARTMENT.ID,DEPARTMENT.NAME,DEPARTMENT.ID,DEPARTMENT.CODE,doctorTable.field("doctor_number")).from(DEPARTMENT)
            .leftJoin(departmentScoreTable).on(departmentScoreTable.field(DEPARTMENT_SUMMARY_TREND.DEPARTMENT_ID).eq(DEPARTMENT.ID))
            .leftJoin(doctorTable).on(doctorTable.field(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID).eq(DEPARTMENT.ID));
        select.where(condition)
            .and(doctorTable.field("doctor_number",Integer.class).gt(0))
            .orderBy(DEPARTMENT.FIRST.desc(),departmentScoreTable.field(SCORE).desc());
        if (param.getLimitNum() != null){
            select.limit(param.getLimitNum());
        }
        return select.fetchInto(DepartmentOneParam.class);
    }
}

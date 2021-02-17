package com.meidianyi.shop.dao.shop.order;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.Tables;
import com.meidianyi.shop.db.shop.tables.records.InquiryOrderRecord;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorDetailPerformanceVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorQueryInquiryParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorQueryInquiryVo;
import com.meidianyi.shop.service.pojo.shop.patient.PatientInquiryOrderVo;
import com.meidianyi.shop.service.pojo.shop.patient.PatientInquiryVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionDoctorVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderStatisticsParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.statistics.InquiryOrderStatistics;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderStatisticsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderTotalVo;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR;
import static com.meidianyi.shop.db.shop.Tables.IM_SESSION;
import static com.meidianyi.shop.db.shop.tables.InquiryOrder.INQUIRY_ORDER;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.currentTimestamp;
import static org.jooq.impl.DSL.date;
import static org.jooq.impl.DSL.sum;

/**
 * @author yangpengcheng
 */
@Repository
public class InquiryOrderDao extends ShopBaseDao {


    public PageResult<PatientInquiryVo> getInquiryOrderList(InquiryOrderListParam param){
        SelectJoinStep<? extends Record> select = db()
            .select(INQUIRY_ORDER.asterisk(), DOCTOR.HOSPITAL_CODE.as("doctorCode"))
            .from(INQUIRY_ORDER)
            .leftJoin(DOCTOR)
            .on(DOCTOR.ID.eq(INQUIRY_ORDER.DOCTOR_ID));
        select.where(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE));
        select = buildOptions(select, param);
        select.orderBy(INQUIRY_ORDER.CREATE_TIME.desc());
        return this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), PatientInquiryVo.class);
    }

    /**
     *
     * @param select
     * @param param
     */
    protected SelectJoinStep<? extends Record> buildOptions(SelectJoinStep<? extends Record> select, InquiryOrderListParam param) {
        Timestamp nowDate = new Timestamp(System.currentTimeMillis());
        if(param.getDoctorId()!=null) {
            select.where(INQUIRY_ORDER.DOCTOR_ID.eq(param.getDoctorId()));
        }
        if(param.getOrderStatus()!=null) {
            select.where(INQUIRY_ORDER.ORDER_STATUS.eq(param.getOrderStatus()));
        }
        if(param.getDepartmentId()!=null) {
            select.where(INQUIRY_ORDER.DEPARTMENT_ID.eq(param.getDepartmentId()));
        }
        if(StringUtils.isNotBlank(param.getDoctorName())) {
            select.where(INQUIRY_ORDER.DOCTOR_NAME.like(this.likeValue(param.getDoctorName())));
        }
        if(StringUtils.isNotBlank(param.getPatientName())) {
            select.where(INQUIRY_ORDER.PATIENT_NAME.like(this.likeValue(param.getPatientName())));
        }
        if (param.getStartTime()!=null&&param.getEndTime()!=null){
            select.where(INQUIRY_ORDER.CREATE_TIME.ge(param.getStartTime()))
                    .and(INQUIRY_ORDER.CREATE_TIME.le(param.getEndTime()));
        }
        return select;
    }

    /**
     * 新增
     *
     * @param inquiryOrderDo
     * @return
     */
    public int save(InquiryOrderDo inquiryOrderDo){
        InquiryOrderRecord inquiryOrderRecord=db().newRecord(INQUIRY_ORDER);
        FieldsUtil.assign(inquiryOrderDo,inquiryOrderRecord);
        inquiryOrderRecord.insert();
        return inquiryOrderRecord.getOrderId();
    }

    /**
     * 更新微信支付id
     *
     * @param orderSn
     * @param prepayId
     */
    public void updatePrepayId(String orderSn ,String prepayId){
        db().update(INQUIRY_ORDER).set(INQUIRY_ORDER.PREPAY_ID,prepayId).where(INQUIRY_ORDER.ORDER_SN.eq(orderSn)).execute();
    }


    /**
     * getByOrderSn
     *
     * @param orderSn
     * @return
     */
    public InquiryOrderDo getByOrderSn(String orderSn){
        return db().select().from(INQUIRY_ORDER).where(INQUIRY_ORDER.ORDER_SN.eq(orderSn)).and(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchOneInto(InquiryOrderDo.class);

    }

    /**
     * getByOrderId
     * @param orderId
     * @return
     */
    public InquiryOrderDo getByOrderId(Integer orderId){
        return db().select().from(INQUIRY_ORDER).where(INQUIRY_ORDER.ORDER_ID.eq(orderId)).and(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchOneInto(InquiryOrderDo.class);
    }

    /**
     * @param inquiryOrderDo
     */
    public void update(InquiryOrderDo inquiryOrderDo){
        InquiryOrderRecord record=new InquiryOrderRecord();
        FieldsUtil.assign(inquiryOrderDo,record);
        record.setUpdateTime(DateUtils.getLocalDateTime());
        db().executeUpdate(record);
    }

    /**
     * 获得待支付的超时问诊订单
     * @return
     */
    public List<InquiryOrderDo> getCanceledToPaidCloseOrder(){
        return db().selectFrom(INQUIRY_ORDER).where(INQUIRY_ORDER.ORDER_STATUS.eq(InquiryOrderConstant.ORDER_TO_PAID)).and(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .and(INQUIRY_ORDER.CREATE_TIME.le(DateUtils.getTimeStampPlus(0-InquiryOrderConstant.EXPIRY_TIME_HOUR, ChronoUnit.HOURS))).fetchInto(InquiryOrderDo.class);
    }

    /**
     * 获得待接诊的超时未接诊的问诊订单
     *
     * @return
     */
    public List<InquiryOrderDo>  getCanceledToWaitingCloseOrder(){
        return db().selectFrom(INQUIRY_ORDER).where(INQUIRY_ORDER.ORDER_STATUS.eq(InquiryOrderConstant.ORDER_TO_RECEIVE)).and(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .and(INQUIRY_ORDER.CREATE_TIME.le(DateUtils.getTimeStampPlus(0-InquiryOrderConstant.EXPIRY_TIME_HOUR, ChronoUnit.HOURS))).fetchInto(InquiryOrderDo.class);
    }

    /**
     *获取接诊中超时自动结束的问诊订单
     * @return
     */
    public List<InquiryOrderDo> getFinishedCloseOrder(){
        return db().selectFrom(INQUIRY_ORDER).where(INQUIRY_ORDER.ORDER_STATUS.eq(InquiryOrderConstant.ORDER_RECEIVING)).and(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .and(INQUIRY_ORDER.LIMIT_TIME.le(currentTimestamp())).fetchInto(InquiryOrderDo.class);
    }
    /**
     * @param param
     * @return
     */
    public List<InquiryOrderDo> getOrderByParams(InquiryOrderParam param){
        List<InquiryOrderDo> list= db().select().from(INQUIRY_ORDER).where(INQUIRY_ORDER.USER_ID.eq(param.getUserId())
            .and(INQUIRY_ORDER.PATIENT_ID.eq(param.getPatientId()))
            .and(INQUIRY_ORDER.DOCTOR_ID.eq(param.getDoctorId())
        )).fetchInto(InquiryOrderDo.class);
        return list;
    }

    /**
     * 创建时间起止时间查询
     * @param startTime
     * @param endTime
     * @return
     */
    public List<InquiryOrderDo> getListByCreateTime(Timestamp startTime,Timestamp endTime){
        return db().select().from(INQUIRY_ORDER).where(INQUIRY_ORDER.CREATE_TIME.ge(startTime))
            .and(INQUIRY_ORDER.CREATE_TIME.le(endTime)).fetchInto(InquiryOrderDo.class);
    }

    /**
     * 增量查询
     * @param startTime
     * @param endTime
     * @return
     */
    public List<InquiryOrderDo> getListByUpdateTime(Timestamp startTime,Timestamp endTime){
        return db().select().from(INQUIRY_ORDER).where(INQUIRY_ORDER.UPDATE_TIME.ge(startTime))
            .and(INQUIRY_ORDER.UPDATE_TIME.le(endTime)).fetchInto(InquiryOrderDo.class);
    }
    /**
     *问诊订单统计报表详情分页查询
     * @param param
     * @return
     */
    public PageResult<InquiryOrderStatisticsVo> orderStatisticsPage(InquiryOrderStatisticsParam param){
        SelectJoinStep<? extends Record> select=selectOptions(param);
        select=buildOptions(select,param);
        select.groupBy(INQUIRY_ORDER.DOCTOR_ID,INQUIRY_ORDER.DOCTOR_NAME,date(INQUIRY_ORDER.CREATE_TIME))
        .orderBy(INQUIRY_ORDER.CREATE_TIME.desc());
        PageResult<InquiryOrderStatisticsVo> result=this.getPageResult(select,param.getCurrentPage(),param.getPageRows(),InquiryOrderStatisticsVo.class);
        return result;
    }
    public SelectJoinStep<? extends Record> selectOptions(InquiryOrderStatisticsParam param){
        SelectJoinStep<? extends Record> select=db().select(
            date((INQUIRY_ORDER.CREATE_TIME)).as(InquiryOrderStatistics.CREAT_TIME),
            //咨询单数
            count((INQUIRY_ORDER.ORDER_ID)).as(InquiryOrderStatistics.AMOUNT),
            //咨询总金额
            sum(INQUIRY_ORDER.ORDER_AMOUNT).as(InquiryOrderStatistics.AMOUNT_PRICE),
            //咨询单次价格
            avg(INQUIRY_ORDER.ORDER_AMOUNT).as(InquiryOrderStatistics.ONE_PRICE),
            //医师id
            INQUIRY_ORDER.DOCTOR_ID.as(InquiryOrderStatistics.DOCTOR_ID),
            //医师name
            INQUIRY_ORDER.DOCTOR_NAME.as(InquiryOrderStatistics.DOCTOR_NAME)
        ).from(INQUIRY_ORDER);
        return select;
    }
    public SelectJoinStep<? extends Record> buildOptions(SelectJoinStep<? extends Record> selectJoinStep,InquiryOrderStatisticsParam param){
        selectJoinStep.where(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE));
        selectJoinStep.where(INQUIRY_ORDER.ORDER_STATUS.gt(InquiryOrderConstant.ORDER_TO_PAID));
        selectJoinStep.where(INQUIRY_ORDER.ORDER_STATUS.ne(InquiryOrderConstant.ORDER_CANCELED));
        if(StringUtils.isNotBlank(param.getDoctorName())){
            selectJoinStep.where(INQUIRY_ORDER.DOCTOR_NAME.like(likeValue(param.getDoctorName())));
        }
        if(param.getStartTime()!=null){
            selectJoinStep.where(INQUIRY_ORDER.CREATE_TIME.ge(param.getStartTime()));
        }
        if(param.getEndTime()!=null){
            selectJoinStep.where(INQUIRY_ORDER.CREATE_TIME.le(param.getEndTime()));
        }
        return selectJoinStep;
    }

    /**
     * 问诊订单统计报表详情查询
     * @param param
     * @return
     */
    public List<InquiryOrderStatisticsVo> orderStatistics(InquiryOrderStatisticsParam param){
        SelectJoinStep<? extends Record> select=selectOptions(param);
        select=buildOptions(select,param);
        select.groupBy(INQUIRY_ORDER.DOCTOR_ID,INQUIRY_ORDER.DOCTOR_NAME,date(INQUIRY_ORDER.CREATE_TIME))
            .orderBy(INQUIRY_ORDER.CREATE_TIME.desc());
        List<InquiryOrderStatisticsVo> list=select.fetchInto(InquiryOrderStatisticsVo.class);
        return list;
    }

    /**
     * 咨询报表总数total查询
     * @param param
     * @return
     */
    public InquiryOrderTotalVo orderStatisticsTotal(InquiryOrderStatisticsParam param){
        SelectJoinStep<? extends Record> select=db().select(
            //咨询单数
            count((INQUIRY_ORDER.ORDER_ID)).as(InquiryOrderStatistics.AMOUNT_TOTAL),
            //咨询总金额
            sum(INQUIRY_ORDER.ORDER_AMOUNT).as(InquiryOrderStatistics.AMOUNT_PRICE_TOTAL),
            //咨询单次价格
            avg(INQUIRY_ORDER.ORDER_AMOUNT).as(InquiryOrderStatistics.ONE_PRICE_TOTAL)
        ).from(INQUIRY_ORDER);
        select=buildOptions(select,param);
        InquiryOrderTotalVo inquiryOrderTotalVo=select.fetchOneInto(InquiryOrderTotalVo.class);
        return inquiryOrderTotalVo;
    }

    /**
     * 查询患者问诊数量
     * @param patientId 患者id
     * @return Integer
     */
    public PatientInquiryOrderVo getInquiryNumberByPatientId(Integer patientId, Integer doctorId) {
        return db().select(
            DSL.sum(INQUIRY_ORDER.ORDER_AMOUNT).as("totalAmount"),
            DSL.count(INQUIRY_ORDER.ORDER_AMOUNT).as("inquiryCount"))
            .from(INQUIRY_ORDER)
            .where(INQUIRY_ORDER.PATIENT_ID.eq(patientId))
            .and(INQUIRY_ORDER.DOCTOR_ID.eq(doctorId))
            .and(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .groupBy(INQUIRY_ORDER.PATIENT_ID)
            .fetchAnyInto(PatientInquiryOrderVo.class);
    }

    /**
     * 医师时间段内接诊数量
     * @param doctorId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer countByDateDoctorId(Integer doctorId, Timestamp startTime, Timestamp endTime) {
       return db().selectCount().from(INQUIRY_ORDER)
                .innerJoin(IM_SESSION).on(IM_SESSION.ORDER_SN.eq(INQUIRY_ORDER.ORDER_SN))
                .where(INQUIRY_ORDER.DOCTOR_ID.eq(doctorId))
                .and(IM_SESSION.RECEIVE_START_TIME.between(startTime,endTime))
                .and(INQUIRY_ORDER.ORDER_STATUS.in(InquiryOrderConstant.ORDER_RECEIVING,InquiryOrderConstant.ORDER_FINISHED,InquiryOrderConstant.ORDER_REFUND,
                        InquiryOrderConstant.ORDER_TO_REFUND,InquiryOrderConstant.ORDER_PART_REFUND))
                .fetchAnyInto(Integer.class);
    }

    /**
     * 医师时间段内咨询单数数量，金额
     * @param doctorId
     * @param startTime
     * @param endTime
     * @return
     */
    public DoctorDetailPerformanceVo getCountNumByDateDoctorId(Integer doctorId, Timestamp startTime, Timestamp endTime) {
        return db().select(
            DSL.ifnull(DSL.count(INQUIRY_ORDER.ORDER_ID),0).as("inquiryNumber"),
            DSL.ifnull(DSL.sum(INQUIRY_ORDER.ORDER_AMOUNT),0).as("inquiryMoney")
        ).from(INQUIRY_ORDER)
            .where(INQUIRY_ORDER.DOCTOR_ID.eq(doctorId))
            .and(INQUIRY_ORDER.CREATE_TIME.between(startTime,endTime))
            .and(INQUIRY_ORDER.ORDER_STATUS.in(InquiryOrderConstant.ORDER_RECEIVING,InquiryOrderConstant.ORDER_FINISHED,InquiryOrderConstant.ORDER_REFUND,
                InquiryOrderConstant.ORDER_TO_REFUND,InquiryOrderConstant.ORDER_PART_REFUND))
            .fetchAnyInto(DoctorDetailPerformanceVo.class);
    }
    /**
     * 查询用户关联医师处方
     * @param doctorId 医师id
     * @param userId 用户id
     * @return PrescriptionDoctorVo
     */
    public PrescriptionDoctorVo getDoctorInquiry(Integer doctorId, Integer userId){
        return db().select(DSL.ifnull(DSL.count(INQUIRY_ORDER.ORDER_AMOUNT), 0).as("totalCount")
        , DSL.ifnull(DSL.sum(INQUIRY_ORDER.ORDER_AMOUNT), new BigDecimal(0)).as("totalPrice"))
            .from(INQUIRY_ORDER)
            .where(INQUIRY_ORDER.DOCTOR_ID.eq(doctorId))
            .and(INQUIRY_ORDER.USER_ID.eq(userId))
            .fetchAnyInto(PrescriptionDoctorVo.class);
    }

    /**
     * 查询医师关联问诊信息
     * @param doctorQueryInquiryParam 医师查询关联问诊列表入参
     * @return PageResult<DoctorQueryInquiryVo>
     */
    public PageResult<DoctorQueryInquiryVo> getDoctorQueryInquiry(DoctorQueryInquiryParam doctorQueryInquiryParam) {
        SelectConditionStep<? extends Record> select = db().select(
            Tables.INQUIRY_ORDER.PATIENT_NAME,
            Tables.INQUIRY_ORDER.PATIENT_ID,
            Tables.INQUIRY_ORDER.ORDER_SN,
            Tables.INQUIRY_ORDER.CREATE_TIME.as("inqTime"),
            Tables.INQUIRY_ORDER.ORDER_AMOUNT.as("inquiryCost"))
            .from(Tables.INQUIRY_ORDER)
            .where(Tables.INQUIRY_ORDER.DOCTOR_ID.eq(doctorQueryInquiryParam.getDoctorId()));
        if (doctorQueryInquiryParam.getPatientName() != null && doctorQueryInquiryParam.getPatientName().trim().length() > 0) {
            select.and(Tables.INQUIRY_ORDER.PATIENT_NAME.like(likeValue(doctorQueryInquiryParam.getPatientName())));
        }
        if (doctorQueryInquiryParam.getStartTime() != null || doctorQueryInquiryParam.getEndTime() != null) {
            select.and(Tables.INQUIRY_ORDER.CREATE_TIME.ge(doctorQueryInquiryParam.getStartTime()))
                .and(Tables.INQUIRY_ORDER.CREATE_TIME.le(doctorQueryInquiryParam.getEndTime()));
        }
        return this.getPageResult(select, doctorQueryInquiryParam.getCurrentPage(),
            doctorQueryInquiryParam.getPageRows(), DoctorQueryInquiryVo.class);
    }


}

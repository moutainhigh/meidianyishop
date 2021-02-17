package com.meidianyi.shop.dao.shop.doctor;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.DoctorSummaryTrendDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DoctorSummaryTrendRecord;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticListVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticParam;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticConstant;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR;
import static com.meidianyi.shop.db.shop.Tables.DOCTOR_SUMMARY_TREND;

/**
 * @author chenjie
 * @date 2020年09月15日
 */
@Repository
public class DoctorSummaryTrendDao extends ShopBaseDao {

    /**
     * 最大最小
     */
    public static final String MIN_CONSULTATION = "min_consultation";
    public static final String MAX_CONSULTATION = "max_consultation";
    public static final String MIN_INQUIRY_MONEY = "min_inquiry_money";
    public static final String MAX_INQUIRY_MONEY = "max_inquiry_money";
    public static final Integer INTEGER_ZERO = 0;

    /**
     * 前台传入的控制排序方向
     */
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    /**
     * 待排序字段
     */
    public static final String CONSULTATION_NUMBER = "consultation_number";
    public static final String INQUIRY_MONEY = "inquiry_money";
    public static final String INQUIRY_NUMBER = "inquiry_number";
    public static final String PRESCRIPTION_MONEY = "prescription_money";
    public static final String PRESCRIPTION_NUM = "prescription_num";
    public static final String CONSUME_MONEY = "consume_money";
    /**
     * 添加记录
     *
     * @param param
     * @return
     */
    public void insertDoctorStatistic(DoctorSummaryTrendDo param) {
        DoctorSummaryTrendRecord record = db().newRecord(DOCTOR_SUMMARY_TREND);
        FieldsUtil.assign(param, record);
        record.insert();
    }

    /**
     * 更新记录
     *
     * @param param
     * @return
     */
    public void updateDoctorStatistic(DoctorSummaryTrendDo param) {
        DoctorSummaryTrendRecord record = db().newRecord(DOCTOR_SUMMARY_TREND);
        FieldsUtil.assign(param, record);
        record.update();
    }

    /**
     * 查询记录
     *
     * @param param
     * @return
     */
    public DoctorSummaryTrendDo getDoctorStatistic(DoctorStatisticParam param) {
        return db().selectFrom(DOCTOR_SUMMARY_TREND)
            .where(DOCTOR_SUMMARY_TREND.DOCTOR_ID.eq(param.getDoctorId()))
            .and(DOCTOR_SUMMARY_TREND.TYPE.eq(param.getType()))
            .and(DOCTOR_SUMMARY_TREND.REF_DATE.eq(param.getRefDate()))
            .fetchAnyInto(DoctorSummaryTrendDo.class);
    }

    /**
     * 医师业绩列表(自定义时间段)
     *
     * @param param
     * @return
     */
    public PageResult<DoctorStatisticListVo> getDoctorListForCustomize(DoctorStatisticParam param) {
        SelectJoinStep<? extends Record> select = db()
            .select(DOCTOR_SUMMARY_TREND.DOCTOR_ID,DOCTOR.NAME
                , DSL.sum(DOCTOR_SUMMARY_TREND.CONSULTATION_NUMBER).as(CONSULTATION_NUMBER)
                ,DSL.sum(DOCTOR_SUMMARY_TREND.INQUIRY_MONEY).as(INQUIRY_MONEY),DSL.sum(DOCTOR_SUMMARY_TREND.INQUIRY_NUMBER).as(INQUIRY_NUMBER)
                ,DSL.sum(DOCTOR_SUMMARY_TREND.PRESCRIPTION_MONEY).as(PRESCRIPTION_MONEY),DSL.sum(DOCTOR_SUMMARY_TREND.PRESCRIPTION_NUM).as(PRESCRIPTION_NUM)
                ,DSL.sum(DOCTOR_SUMMARY_TREND.CONSUME_MONEY).as(CONSUME_MONEY))
            .from(DOCTOR_SUMMARY_TREND)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_SUMMARY_TREND.DOCTOR_ID));
        Condition condition = DOCTOR_SUMMARY_TREND.TYPE.eq(StatisticConstant.TYPE_YESTODAY);
        if (param.getStartTime() != null) {
            Date startDate = new Date(param.getStartTime().getTime());
            condition = condition.and(DOCTOR_SUMMARY_TREND.REF_DATE.ge(startDate));
        }
        if (param.getEndTime() != null) {
            Date endDate = new Date(param.getEndTime().getTime());
            condition = condition.and(DOCTOR_SUMMARY_TREND.REF_DATE.le(endDate));
        }
        select.where(condition);
        select.groupBy(DOCTOR_SUMMARY_TREND.DOCTOR_ID,DOCTOR.NAME);
        buildOptions(select, param);
        if (param.getOrderField() != null) {
            doctorSummaryFiledSorted(select, param);
        }
        return this.getPageResult(select, param.getCurrentPage(),
            param.getPageRows(), DoctorStatisticListVo.class);
    }

    /**
     * 医师搜索查询
     *
     * @param select
     * @param param
     */
    protected void buildOptions(SelectJoinStep<? extends Record> select, DoctorStatisticParam param) {
        if (param.getDoctorName() != null) {
            select.where(DOCTOR.NAME.like(likeValue(param.getDoctorName())));
        }
        if (param.getDepartmentId() != null) {
            select.where(DOCTOR_SUMMARY_TREND.DOCTOR_ID.in(param.getDoctorIds()));
        }
        if(param.getDoctorId()!=null){
            select.where(DOCTOR.ID.eq(param.getDoctorId()));
        }
    }

    /**
     * 医师业绩列表(自定义时间段)
     *
     * @param param
     * @return
     */
    public PageResult<DoctorStatisticListVo> getDoctorListForType(DoctorStatisticParam param) {
        SelectJoinStep<? extends Record> select = db()
            .select(DOCTOR_SUMMARY_TREND.DOCTOR_ID,DOCTOR.NAME
                , DOCTOR_SUMMARY_TREND.CONSULTATION_NUMBER
                ,DOCTOR_SUMMARY_TREND.INQUIRY_MONEY,DOCTOR_SUMMARY_TREND.INQUIRY_NUMBER
                ,DOCTOR_SUMMARY_TREND.PRESCRIPTION_MONEY,DOCTOR_SUMMARY_TREND.PRESCRIPTION_NUM
                ,DOCTOR_SUMMARY_TREND.CONSUME_MONEY)
            .from(DOCTOR_SUMMARY_TREND)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_SUMMARY_TREND.DOCTOR_ID));
        select.where(DOCTOR_SUMMARY_TREND.TYPE.eq(param.getType()));
        buildOptions(select, param);
        if (param.getOrderField() != null) {
            doctorSummaryFiledSorted(select, param);
        }
        return this.getPageResult(select, param.getCurrentPage(),
            param.getPageRows(), DoctorStatisticListVo.class);
    }

    /**
     * 对商品统计按指定字段进行排序
     * @param select 查询实体
     * @param param 排序参数
     */
    private void doctorSummaryFiledSorted(SelectJoinStep<? extends Record> select, DoctorStatisticParam param) {
        if (ASC.equals(param.getOrderDirection())) {
            switch (param.getOrderField()) {
                case CONSULTATION_NUMBER:
                    select.orderBy(DSL.field(CONSULTATION_NUMBER).asc());
                    break;
                case INQUIRY_MONEY:
                    select.orderBy(DSL.field(INQUIRY_MONEY).asc());
                    break;
                case INQUIRY_NUMBER:
                    select.orderBy(DSL.field(INQUIRY_NUMBER).asc());
                    break;
                case PRESCRIPTION_MONEY:
                    select.orderBy(DSL.field(PRESCRIPTION_MONEY).asc());
                    break;
                case PRESCRIPTION_NUM:
                    select.orderBy(DSL.field(PRESCRIPTION_NUM).asc());
                    break;
                case CONSUME_MONEY:
                    select.orderBy(DSL.field(CONSUME_MONEY).asc());
                    break;
                default:
                    break;
            }
        } else {
            switch (param.getOrderField()) {
                case CONSULTATION_NUMBER:
                    select.orderBy(DSL.field(CONSULTATION_NUMBER).desc());
                    break;
                case INQUIRY_MONEY:
                    select.orderBy(DSL.field(INQUIRY_MONEY).desc());
                    break;
                case INQUIRY_NUMBER:
                    select.orderBy(DSL.field(INQUIRY_NUMBER).desc());
                    break;
                case PRESCRIPTION_MONEY:
                    select.orderBy(DSL.field(PRESCRIPTION_MONEY).desc());
                    break;
                case PRESCRIPTION_NUM:
                    select.orderBy(DSL.field(PRESCRIPTION_NUM).desc());
                    break;
                case CONSUME_MONEY:
                    select.orderBy(DSL.field(CONSUME_MONEY).desc());
                    break;
                default:
                    break;
            }
        }
    }

    public DoctorStatisticMinMaxVo getMinMaxStatisticData(Date refDate, Byte type){
        return db().select(DSL.min(DOCTOR_SUMMARY_TREND.CONSULTATION_NUMBER).as(MIN_CONSULTATION),DSL.max(DOCTOR_SUMMARY_TREND.CONSULTATION_NUMBER).as(MAX_CONSULTATION)
                ,DSL.min(DOCTOR_SUMMARY_TREND.INQUIRY_MONEY).as(MIN_INQUIRY_MONEY),DSL.max(DOCTOR_SUMMARY_TREND.INQUIRY_MONEY).as(MAX_INQUIRY_MONEY)
            ).from(DOCTOR_SUMMARY_TREND)
            .where(DOCTOR_SUMMARY_TREND.TYPE.eq(type))
            .and(DOCTOR_SUMMARY_TREND.REF_DATE.eq(refDate))
            .fetchAnyInto(DoctorStatisticMinMaxVo.class);
    }

    public DoctorSummaryTrendDo getOneInfo(Date refDate, Byte type, Integer doctorId){
        return db().selectFrom(DOCTOR_SUMMARY_TREND)
            .where(DOCTOR_SUMMARY_TREND.TYPE.eq(type))
            .and(DOCTOR_SUMMARY_TREND.REF_DATE.eq(refDate))
            .and(DOCTOR_SUMMARY_TREND.DOCTOR_ID.eq(doctorId))
            .fetchAnyInto(DoctorSummaryTrendDo.class);
    }

    public void updateDoctorStatisticInquiryScore(Byte type, Date refDate,DoctorStatisticMinMaxVo doctorStatisticMinMax) {
        BigDecimal differ = doctorStatisticMinMax.getMaxInquiryMoney().subtract(doctorStatisticMinMax.getMinInquiryMoney());
        if(BigDecimal.ZERO.compareTo(differ) == 0) {
            db().update(DOCTOR_SUMMARY_TREND)
                .set(DOCTOR_SUMMARY_TREND.INQUIRY_SCORE, BigDecimal.ZERO)
                .where(DOCTOR_SUMMARY_TREND.TYPE.eq(type))
                .and(DOCTOR_SUMMARY_TREND.REF_DATE.eq(refDate))
                .execute();
        } else {
            db().update(DOCTOR_SUMMARY_TREND)
                .set(DOCTOR_SUMMARY_TREND.INQUIRY_SCORE,
                    (DOCTOR_SUMMARY_TREND.INQUIRY_MONEY.sub(doctorStatisticMinMax.getMinInquiryMoney())).divide(differ))
                .where(DOCTOR_SUMMARY_TREND.TYPE.eq(type))
                .and(DOCTOR_SUMMARY_TREND.REF_DATE.eq(refDate))
                .execute();
        }

    }

    public void updateDoctorStatisticConsultationScore(Byte type, Date refDate,DoctorStatisticMinMaxVo doctorStatisticMinMax) {
        Integer differ = doctorStatisticMinMax.getMaxConsultation() - doctorStatisticMinMax.getMinConsultation();
        BigDecimal minConsultation = new BigDecimal(doctorStatisticMinMax.getMinConsultation());
        BigDecimal differDecimal = new BigDecimal(differ);
        if(INTEGER_ZERO.equals(differ)) {
            db().update(DOCTOR_SUMMARY_TREND)
                .set(DOCTOR_SUMMARY_TREND.CONSULTATION_SCORE, BigDecimal.ZERO)
                .where(DOCTOR_SUMMARY_TREND.TYPE.eq(type))
                .and(DOCTOR_SUMMARY_TREND.REF_DATE.eq(refDate))
                .execute();
        } else {
            db().update(DOCTOR_SUMMARY_TREND)
                .set(DOCTOR_SUMMARY_TREND.CONSULTATION_SCORE,
                    (DOCTOR_SUMMARY_TREND.CONSULTATION_NUMBER.cast(SQLDataType.DECIMAL(10,2)).sub(minConsultation)).divide(differDecimal))
                .where(DOCTOR_SUMMARY_TREND.TYPE.eq(type))
                .and(DOCTOR_SUMMARY_TREND.REF_DATE.eq(refDate))
                .execute();
        }

    }
}

package com.meidianyi.shop.dao.shop.department;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.DepartmentSummaryTrendDo;
import com.meidianyi.shop.common.pojo.shop.table.StoreOrderSummaryTrendDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DepartmentSummaryTrendRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreOrderSummaryTrendRecord;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentStatisticParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticParam;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;

import static com.meidianyi.shop.db.shop.Tables.*;
/**
 * @author chenjie
 * @date 2020年09月09日
 */
@Repository
public class DepartmentSummaryTrendDao extends ShopBaseDao {

    public static final String MIN_CONSULTATION = "min_consultation";
    public static final String MAX_CONSULTATION = "max_consultation";
    public static final String MIN_INQUIRY_MONEY = "min_inquiry_money";
    public static final String MAX_INQUIRY_MONEY = "max_inquiry_money";
    public static final Integer INTEGER_ZERO = 0;
    /**
     * 添加记录
     *
     * @param param
     * @return
     */
    public void insertDepartmentStatistic(DepartmentSummaryTrendDo param) {
        DepartmentSummaryTrendRecord record = db().newRecord(DEPARTMENT_SUMMARY_TREND);
        FieldsUtil.assign(param, record);
        record.insert();
    }

    /**
     * 更新记录
     *
     * @param param
     * @return
     */
    public void updateDepartmentStatistic(DepartmentSummaryTrendDo param) {
        DepartmentSummaryTrendRecord record = db().newRecord(DEPARTMENT_SUMMARY_TREND);
        FieldsUtil.assign(param, record);
        record.update();
    }

    /**
     * 查询记录
     *
     * @param param
     * @return
     */
    public DepartmentSummaryTrendDo getDepartmentStatistic(DepartmentStatisticParam param) {
        return db().selectFrom(DEPARTMENT_SUMMARY_TREND)
            .where(DEPARTMENT_SUMMARY_TREND.DEPARTMENT_ID.eq(param.getDepartmentId()))
            .and(DEPARTMENT_SUMMARY_TREND.TYPE.eq(param.getType()))
            .and(DEPARTMENT_SUMMARY_TREND.REF_DATE.eq(param.getRefDate()))
            .fetchAnyInto(DepartmentSummaryTrendDo.class);
    }

    public DoctorStatisticMinMaxVo getMinMaxStatisticData(Date refDate, Byte type){
        return db().select(DSL.min(DEPARTMENT_SUMMARY_TREND.CONSULTATION_NUMBER).as(MIN_CONSULTATION),DSL.max(DEPARTMENT_SUMMARY_TREND.CONSULTATION_NUMBER).as(MAX_CONSULTATION)
            ,DSL.min(DEPARTMENT_SUMMARY_TREND.INQUIRY_MONEY).as(MIN_INQUIRY_MONEY),DSL.max(DEPARTMENT_SUMMARY_TREND.INQUIRY_MONEY).as(MAX_INQUIRY_MONEY)
        ).from(DEPARTMENT_SUMMARY_TREND)
            .where(DEPARTMENT_SUMMARY_TREND.TYPE.eq(type))
            .and(DEPARTMENT_SUMMARY_TREND.REF_DATE.eq(refDate))
            .fetchAnyInto(DoctorStatisticMinMaxVo.class);
    }

    public void updateDepartmentStatisticInquiryScore(Byte type, Date refDate,DoctorStatisticMinMaxVo doctorStatisticMinMax) {
        BigDecimal differ = doctorStatisticMinMax.getMaxInquiryMoney().subtract(doctorStatisticMinMax.getMinInquiryMoney());
        if(BigDecimal.ZERO.equals(differ)) {
            db().update(DEPARTMENT_SUMMARY_TREND)
                .set(DEPARTMENT_SUMMARY_TREND.INQUIRY_SCORE, BigDecimal.ZERO)
                .where(DEPARTMENT_SUMMARY_TREND.TYPE.eq(type))
                .and(DEPARTMENT_SUMMARY_TREND.REF_DATE.eq(refDate))
                .execute();
        } else {
            db().update(DEPARTMENT_SUMMARY_TREND)
                .set(DEPARTMENT_SUMMARY_TREND.INQUIRY_SCORE,
                    (DEPARTMENT_SUMMARY_TREND.INQUIRY_MONEY.sub(doctorStatisticMinMax.getMinInquiryMoney())).multiply(differ))
                .where(DEPARTMENT_SUMMARY_TREND.TYPE.eq(type))
                .and(DEPARTMENT_SUMMARY_TREND.REF_DATE.eq(refDate))
                .execute();
        }

    }

    public void updateDepartmentStatisticConsultationScore(Byte type, Date refDate,DoctorStatisticMinMaxVo doctorStatisticMinMax) {
        Integer differ = doctorStatisticMinMax.getMaxConsultation() - doctorStatisticMinMax.getMinConsultation();
        BigDecimal minConsultation = new BigDecimal(doctorStatisticMinMax.getMinConsultation());
        BigDecimal differDecimal = new BigDecimal(differ);
        if(INTEGER_ZERO.equals(differ)) {
            db().update(DEPARTMENT_SUMMARY_TREND)
                .set(DEPARTMENT_SUMMARY_TREND.CONSULTATION_SCORE, BigDecimal.ZERO)
                .where(DEPARTMENT_SUMMARY_TREND.TYPE.eq(type))
                .and(DEPARTMENT_SUMMARY_TREND.REF_DATE.eq(refDate))
                .execute();
        } else {
            db().update(DEPARTMENT_SUMMARY_TREND)
                .set(DEPARTMENT_SUMMARY_TREND.CONSULTATION_SCORE,
                    (DEPARTMENT_SUMMARY_TREND.CONSULTATION_NUMBER.cast(SQLDataType.DECIMAL(10,2)).sub(minConsultation)).multiply(differDecimal))
                .where(DEPARTMENT_SUMMARY_TREND.TYPE.eq(type))
                .and(DEPARTMENT_SUMMARY_TREND.REF_DATE.eq(refDate))
                .execute();
        }

    }
}

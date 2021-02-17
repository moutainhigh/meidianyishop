package com.meidianyi.shop.dao.shop.rebate;

import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.PrescriptionRebateRecord;
import com.meidianyi.shop.service.pojo.shop.rebate.*;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.PrescriptionRebate.PRESCRIPTION_REBATE;

/**
 * @author yangpengcheng
 * @date 2020/8/26
 **/
@Repository
public class PrescriptionRebateDao extends ShopBaseDao {

    private final static String REBATE_MONEY  ="rebateMoney";

    /**
     * 处方返利入库
     * @param param
     */
    public void addPrescriptionRebate(PrescriptionRebateParam param){
        PrescriptionRebateRecord record=db().newRecord(PRESCRIPTION_REBATE);
        FieldsUtil.assign(param,record);
        record.insert();
    }

    /**
     * 更改返利状态
     * @param prescriptionCode
     * @param status
     */
    public void updateStatus(String prescriptionCode,Byte status,String reason){
        UpdateSetMoreStep<PrescriptionRebateRecord> update= db().update(PRESCRIPTION_REBATE).set(PRESCRIPTION_REBATE.STATUS,status);
        if(PrescriptionRebateConstant.REBATED.equals(status)){
            update.set(PRESCRIPTION_REBATE.REBATE_TIME, DateUtils.getLocalDateTime());
        }
        if(StringUtils.isNotBlank(reason)){
            update.set(PRESCRIPTION_REBATE.REASON,reason);
        }
        update.where(PRESCRIPTION_REBATE.PRESCRIPTION_CODE.eq(prescriptionCode)).execute();
    }

    /**
     * 根据处方号获取
     * @param prescriptionCode
     * @return
     */
    public PrescriptionRebateParam getRebateByPrescriptionCode(String prescriptionCode){
        return db().select().from(PRESCRIPTION_REBATE).where(PRESCRIPTION_REBATE.PRESCRIPTION_CODE.eq(prescriptionCode)).fetchAnyInto(PrescriptionRebateParam.class);
    }

    /**
     * 更新实际返利金额
     * @param prescriptionCode
     * @param realRebateMoney
     */
    public void updateRealRebateMoney(String prescriptionCode, BigDecimal realRebateMoney,BigDecimal platformRealRebateMoney){
        db().update(PRESCRIPTION_REBATE).set(PRESCRIPTION_REBATE.REAL_REBATE_MONEY,realRebateMoney)
            .set(PRESCRIPTION_REBATE.PLATFORM_REAL_REBATE_MONEY,platformRealRebateMoney)
            .where(PRESCRIPTION_REBATE.PRESCRIPTION_CODE.eq(prescriptionCode))
            .execute();
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public PageResult<PrescriptionRebateVo> getPageList(PrescriptionRebateListParam param){
        SelectJoinStep<? extends Record> select = selectOptions();
        select=buildOptions(select,param);
        select.orderBy(PRESCRIPTION_REBATE.CREATE_TIME.desc());
        PageResult<PrescriptionRebateVo> result=this.getPageResult(select,param.getCurrentPage(),param.getPageRows(),PrescriptionRebateVo.class);
        return result;

    }
    public SelectJoinStep<? extends Record> selectOptions(){
        SelectJoinStep<? extends Record> select=db().select(DOCTOR.NAME.as("doctorName"),DOCTOR.MOBILE,USER.USERNAME.as("userName"),PRESCRIPTION_REBATE.asterisk())
            .from(PRESCRIPTION_REBATE);
        select.leftJoin(DOCTOR).on(DOCTOR.ID.eq(PRESCRIPTION_REBATE.DOCTOR_ID))
            .leftJoin(PRESCRIPTION).on(PRESCRIPTION.PRESCRIPTION_CODE.eq(PRESCRIPTION_REBATE.PRESCRIPTION_CODE))
            .leftJoin(USER).on(USER.USER_ID.eq(PRESCRIPTION.USER_ID));
        return select;
    }
    protected SelectJoinStep<? extends Record> buildOptions(SelectJoinStep<? extends Record> select,PrescriptionRebateListParam param){
        select.where(PRESCRIPTION_REBATE.IS_DELETE.eq(DelFlag.NORMAL_VALUE));
        if(StringUtils.isNotBlank(param.getDoctorName())){
            select.where(DOCTOR.NAME.like(this.likeValue(param.getDoctorName())));
        }
        if(param.getDoctorId()!=null){
            select.where(PRESCRIPTION_REBATE.DOCTOR_ID.eq(param.getDoctorId()));
        }
        if(param.getStatus()!=null){
            select.where(PRESCRIPTION_REBATE.STATUS.eq(param.getStatus()));
        }
        if(param.getStartTime()!=null){
            select.where(PRESCRIPTION_REBATE.CREATE_TIME.ge(DateUtil.beginOfDay(param.getStartTime()).toTimestamp()));
        }
        if(param.getEndTime()!=null){
            select.where(PRESCRIPTION_REBATE.CREATE_TIME.le(DateUtil.endOfDay(param.getEndTime()).toTimestamp()));
        }
        return select;
    }

    /**
     * 获取List
     * @param param
     * @return
     */
    public List<PrescriptionRebateReportVo> getList(PrescriptionRebateListParam param){
        SelectJoinStep<? extends Record> select = selectOptions();
        select=buildOptions(select,param);
        select.orderBy(PRESCRIPTION_REBATE.CREATE_TIME.desc());
        return select.fetchInto(PrescriptionRebateReportVo.class);
    }

    /**
     * 获取指定时间段内的处方返利
     * @param doctorId
     * @param startTime
     * @param endTime
     * @return
     */
    public BigDecimal getRealRebateByDoctorDate(Integer doctorId, Timestamp startTime, Timestamp endTime) {
        return db().select(DSL.ifnull(DSL.sum(PRESCRIPTION_REBATE.REAL_REBATE_MONEY),BigDecimal.ZERO).as(REBATE_MONEY)).from(PRESCRIPTION_REBATE).where(PRESCRIPTION_REBATE.DOCTOR_ID.eq(doctorId))
                .and(PRESCRIPTION_REBATE.STATUS.eq(PrescriptionRebateConstant.REBATE_FAIL))
                .and(PRESCRIPTION_REBATE.REBATE_TIME.between(startTime,endTime))
                .fetchAnyInto(BigDecimal.class);
    }
}

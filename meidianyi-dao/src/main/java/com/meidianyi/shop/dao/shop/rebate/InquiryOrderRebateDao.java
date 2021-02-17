package com.meidianyi.shop.dao.shop.rebate;

import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.InquiryOrderRebateRecord;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateListParam;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateParam;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateReportVo;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateVo;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR;
import static com.meidianyi.shop.db.shop.Tables.INQUIRY_ORDER;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.tables.InquiryOrderRebate.INQUIRY_ORDER_REBATE;


/**
 * @author yangpengcheng
 * @date 2020/8/24
 **/
@Repository
public class InquiryOrderRebateDao extends ShopBaseDao {

    private final static String REBATE_MONEY ="rebateMoney";

    /**
     * 问诊返利add
     * @param param
     */
    public void addInquiryOrderRebate(InquiryOrderRebateParam param){
        InquiryOrderRebateRecord rebateRecord=db().newRecord(INQUIRY_ORDER_REBATE);
        FieldsUtil.assign(param,rebateRecord);
        rebateRecord.insert();
    }

    /**
     * 更改返利状态
     * @param orderSn
     */
    public void updateStatus(String orderSn,Byte status,String reason){
        UpdateSetMoreStep<InquiryOrderRebateRecord> update= db().update(INQUIRY_ORDER_REBATE).set(INQUIRY_ORDER_REBATE.STATUS, status);
        if(StringUtils.isNotBlank(reason)){
            update.set(INQUIRY_ORDER_REBATE.REASON,reason);
        }
        if(InquiryOrderRebateConstant.REBATED.equals(status)){
            update.set(INQUIRY_ORDER_REBATE.REBATE_TIME, DateUtils.getLocalDateTime());
        }
        update.where(INQUIRY_ORDER_REBATE.ORDER_SN.eq(orderSn)).execute();
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public PageResult<InquiryOrderRebateVo> getPageList(InquiryOrderRebateListParam param){
        SelectJoinStep<? extends Record> select =selectOptions();
        select=buildOptions(select,param);
        select.orderBy(INQUIRY_ORDER_REBATE.CREATE_TIME.desc());
        PageResult<InquiryOrderRebateVo> result=this.getPageResult(select,param.getCurrentPage(),param.getPageRows(),InquiryOrderRebateVo.class);
        return result;
    }
    public SelectJoinStep<? extends Record> selectOptions(){
        SelectJoinStep<? extends Record> select = db()
            .select(DOCTOR.NAME.as("doctorName"),INQUIRY_ORDER.ORDER_STATUS,INQUIRY_ORDER.PATIENT_NAME,DOCTOR.MOBILE,USER.USERNAME.as("userName"),INQUIRY_ORDER_REBATE.asterisk())
            .from(INQUIRY_ORDER_REBATE);
        select.leftJoin(DOCTOR).on(DOCTOR.ID.eq(INQUIRY_ORDER_REBATE.DOCTOR_ID))
            .leftJoin(INQUIRY_ORDER).on(INQUIRY_ORDER.ORDER_SN.eq(INQUIRY_ORDER_REBATE.ORDER_SN))
            .leftJoin(USER).on(USER.USER_ID.eq(INQUIRY_ORDER.USER_ID));
        return select;
    }
    protected SelectJoinStep<? extends Record> buildOptions(SelectJoinStep<? extends Record> select,InquiryOrderRebateListParam param){
        select.where(INQUIRY_ORDER_REBATE.IS_DELETE.eq(DelFlag.NORMAL_VALUE));
        if(StringUtils.isNotBlank(param.getDoctorName())){
            select.where(DOCTOR.NAME.like(this.likeValue(param.getDoctorName())));
        }
        if(param.getDoctorId()!=null){
            select.where(INQUIRY_ORDER_REBATE.DOCTOR_ID.eq(param.getDoctorId()));
        }
        if(param.getStatus()!=null){
            select.where(INQUIRY_ORDER_REBATE.STATUS.eq(param.getStatus()));
        }
        if(param.getStartTime()!=null){
            select.where(INQUIRY_ORDER_REBATE.CREATE_TIME.ge(DateUtil.beginOfDay(param.getStartTime()).toTimestamp()));
        }
        if(param.getEndTime()!=null){
            select.where(INQUIRY_ORDER_REBATE.CREATE_TIME.le(DateUtil.endOfDay(param.getEndTime()).toTimestamp()));
        }
        return select;
    }

    /**
     * 获取list
     * @param param
     * @return
     */
    public List<InquiryOrderRebateReportVo> getList(InquiryOrderRebateListParam param){
        SelectJoinStep<? extends Record> select =selectOptions();
        select=buildOptions(select,param);
        select.orderBy(INQUIRY_ORDER_REBATE.CREATE_TIME.desc());
        List<InquiryOrderRebateReportVo> list=select.fetchInto(InquiryOrderRebateReportVo.class);
        return list;
    }

    /**
     * 根据订单号获取
     * @param orderSn
     * @return
     */
    public InquiryOrderRebateVo getRebateByOrderSn(String orderSn){
        return db().select(DOCTOR.NAME.as("doctorName"),DOCTOR.MOBILE,INQUIRY_ORDER_REBATE.asterisk()).from(INQUIRY_ORDER_REBATE)
            .leftJoin(DOCTOR).on(DOCTOR.ID.eq(INQUIRY_ORDER_REBATE.DOCTOR_ID))
            .where(INQUIRY_ORDER_REBATE.ORDER_SN.eq(orderSn)).fetchOneInto(InquiryOrderRebateVo.class);
    }

    /**
     * 获取指定时间段内的问诊返利
     * @param doctorId
     * @param startTime
     * @param endTime
     * @return
     */
    public BigDecimal getRealRebateByDoctorDate(Integer doctorId, Timestamp startTime, Timestamp endTime) {
       return db().select(DSL.ifnull(DSL.sum(INQUIRY_ORDER_REBATE.TOTAL_REBATE_MONEY),BigDecimal.ZERO).as(REBATE_MONEY)).from(INQUIRY_ORDER_REBATE)
                .where(INQUIRY_ORDER_REBATE.DOCTOR_ID.eq(doctorId))
                .and(INQUIRY_ORDER_REBATE.STATUS.eq(InquiryOrderRebateConstant.REBATED))
                .and(INQUIRY_ORDER_REBATE.REBATE_TIME.between(startTime,endTime))
                .fetchAnyInto(BigDecimal.class);
    }
}

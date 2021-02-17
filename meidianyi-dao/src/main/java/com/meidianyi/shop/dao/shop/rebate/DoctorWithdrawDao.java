package com.meidianyi.shop.dao.shop.rebate;

import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DoctorWithdrawRecord;
import com.meidianyi.shop.service.pojo.shop.rebate.*;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.InquiryOrderRebate.INQUIRY_ORDER_REBATE;
import static org.jooq.impl.DSL.*;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@Repository
public class DoctorWithdrawDao extends ShopBaseDao {

    /**
     * 添加提现申请
     * @param param
     */
    public void addDoctorWithdraw(DoctorWithdrawParam param){
        DoctorWithdrawRecord record=db().newRecord(DOCTOR_WITHDRAW);
        FieldsUtil.assign(param,record);
        record.insert();
    }

    /**
     * 更新状态
     * @param id
     * @param status
     * @param refuseDesc
     */
    public  void update(Integer id,Byte status,String refuseDesc){
        UpdateSetFirstStep<DoctorWithdrawRecord> update=db().update(DOCTOR_WITHDRAW);
        switch (status){
            case (byte)2:
                update.set(DOCTOR_WITHDRAW.STATUS,status).set(DOCTOR_WITHDRAW.REFUSE_TIME, DateUtils.getSqlTimestamp());
                break;
            case (byte)3:
                update.set(DOCTOR_WITHDRAW.STATUS,status).set(DOCTOR_WITHDRAW.CHECK_TIME, DateUtils.getSqlTimestamp());
                break;
            case (byte)4:
                update.set(DOCTOR_WITHDRAW.STATUS,status).set(DOCTOR_WITHDRAW.BILLING_TIME, DateUtils.getSqlTimestamp());
                break;
            case (byte)5:
                update.set(DOCTOR_WITHDRAW.STATUS,status).set(DOCTOR_WITHDRAW.FAIL_TIME, DateUtils.getSqlTimestamp());
                break;
                default:
        }
        update.set(DOCTOR_WITHDRAW.REFUSE_DESC, refuseDesc).where(DOCTOR_WITHDRAW.ID.eq(id)).execute();
    }

    /**
     * 根据提现单号获取
     * @param orderSn
     * @return
     */
    public DoctorWithdrawVo getWithdrawByOrderSn(String orderSn){
        return db().select().from(DOCTOR_WITHDRAW).where(DOCTOR_WITHDRAW.ORDER_SN.eq(orderSn)).fetchAnyInto(DoctorWithdrawVo.class);
    }

    /**
     * 数目
     * @param doctorId
     * @return
     */
    public int count(Integer doctorId){
        if(doctorId==null){
            return db().selectCount().from(DOCTOR_WITHDRAW).fetchAnyInto(Integer.class);
        }
        return db().selectCount().from(DOCTOR_WITHDRAW).where(DOCTOR_WITHDRAW.DOCTOR_ID.eq(doctorId)).fetchAnyInto(Integer.class);
    }
    /**
     * 提现记录
     * @param param
     * @return
     */
    public PageResult<DoctorWithdrawVo> getPageList(DoctorWithdrawListParam param){
        SelectJoinStep<? extends Record> select=db().select(DOCTOR.NAME.as("doctorName"),DOCTOR.MOBILE,DOCTOR_WITHDRAW.asterisk()).from(DOCTOR_WITHDRAW);
        select.leftJoin(DOCTOR).on(DOCTOR.ID.eq(DOCTOR_WITHDRAW.DOCTOR_ID));
        select=buildOptions(select,param);
        select.orderBy(DOCTOR_WITHDRAW.CREATE_TIME.desc());
        PageResult<DoctorWithdrawVo> result=this.getPageResult(select,param.getCurrentPage(),param.getPageRows(),DoctorWithdrawVo.class);
        return result;

    }

    public SelectJoinStep<? extends Record> buildOptions(SelectJoinStep<? extends Record> select,DoctorWithdrawListParam param){
        if(param.getDoctorId()!=null){
            select.where(DOCTOR_WITHDRAW.DOCTOR_ID.eq(param.getDoctorId()));
        }
        if(StringUtils.isNotBlank(param.getDoctorName())){
            select.where(DOCTOR.NAME.like(this.likeValue(param.getDoctorName())));
        }
        if(param.getStatus()!=null){
            select.where(DOCTOR_WITHDRAW.STATUS.eq(param.getStatus()));
        }
        if(param.getStartTime()!=null){
            select.where(DOCTOR_WITHDRAW.CREATE_TIME.ge(DateUtil.beginOfDay(param.getStartTime()).toTimestamp()));
        }
        if(param.getEndTime()!=null){
            select.where(DOCTOR_WITHDRAW.CREATE_TIME.le(DateUtil.endOfDay(param.getEndTime()).toTimestamp()));
        }
        return select;
    }

    /**
     * 获取累计提现金额总数
     * @param doctorId
     * @param status
     * @return
     */
    public BigDecimal getWithdrawCashSum(Integer doctorId, Byte status, Timestamp startTime,Timestamp endTime){
        SelectConditionStep<? extends Record> step=db().select(sum(DOCTOR_WITHDRAW.WITHDRAW_CASH)).from(DOCTOR_WITHDRAW).where(DOCTOR_WITHDRAW.DOCTOR_ID.eq(doctorId));
        if(status!=null){
            step.and(DOCTOR_WITHDRAW.STATUS.eq(status));
        }
        if(startTime!=null){
            step.and(DOCTOR_WITHDRAW.CREATE_TIME.ge(startTime));
        }
        if(endTime!=null){
            step.and(DOCTOR_WITHDRAW.CREATE_TIME.le(endTime));
        }
        return step.fetchAnyInto(BigDecimal.class);
    }

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    public DoctorWithdrawDetailVo getWithdrawDetailById(Integer id){
        DoctorWithdrawDetailVo detail=db().select(DOCTOR_WITHDRAW.asterisk(),USER.USERNAME.as("userName"),DOCTOR.NAME.as("doctorName"),DOCTOR.MOBILE)
            .from(DOCTOR_WITHDRAW)
            .leftJoin(DOCTOR).on(DOCTOR_WITHDRAW.DOCTOR_ID.eq(DOCTOR.ID))
            .leftJoin(USER).on(DOCTOR.USER_ID.eq(USER.USER_ID))
            .where(DOCTOR_WITHDRAW.ID.eq(id)).fetchOneInto(DoctorWithdrawDetailVo.class);
        return detail;
    }

    /**
     *
     * @param param
     * @return
     */
    public int updateDoctorWithdrawDesc( DoctorWithdrawDescParam param){
        return db().update(DOCTOR_WITHDRAW).set(DOCTOR_WITHDRAW.DESC,param.getDesc())
            .where(DOCTOR_WITHDRAW.ID.eq(param.getId())).execute();
    }

}

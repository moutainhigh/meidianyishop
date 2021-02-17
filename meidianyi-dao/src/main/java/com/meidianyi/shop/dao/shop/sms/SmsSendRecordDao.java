package com.meidianyi.shop.dao.shop.sms;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.SmsSendRecordRecord;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorListParam;
import com.meidianyi.shop.service.pojo.shop.sms.ResponseMsgVo;
import com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordAdminParam;
import com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordAdminVo;
import com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordParam;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordConstant.SMS_SEND_STATUS_SUCCESS;
import static com.meidianyi.shop.service.pojo.shop.sms.SmsSendRecordConstant.SMS_SEND_TYPE_INDUSTRY;

/**
 * @author 孔德成
 * @date 2020/7/24 9:22
 */
@Repository
public class SmsSendRecordDao extends ShopBaseDao {


    /**
     * 保存短信发送记录
     * @param param
     * @return
     */
    public int save(SmsSendRecordParam param) {
        ResponseMsgVo parse = Util.json2Object(param.getResponseMsg(), ResponseMsgVo.class, false);
        assert parse != null;
        param.setResponseMsgCode(String.valueOf(parse.getCode()));
        SmsSendRecordRecord record = db().newRecord(SMS_SEND_RECORD, param);
        return record.insert();
    }

    /**
     * @author 赵晓东
     * @create 2020-07-27 11:55:27
     * @description admin端查询短信发送情况
     */
    /**
     * admin端查询短信情况
     * @param smsSendRecordAdminParam admin端短信查询入参
     * @return PageResult<SmsSendRecordAdminVo>
     */
    public PageResult<SmsSendRecordAdminVo> selectSmsSendRecordAdmin(SmsSendRecordAdminParam smsSendRecordAdminParam) {
        SelectJoinStep<? extends Record> select = db().select().from(SMS_SEND_RECORD);
        buildOptions(select, smsSendRecordAdminParam);
        return super.getPageResult(select, smsSendRecordAdminParam.getCurrentPage(),
            smsSendRecordAdminParam.getPageRows(), SmsSendRecordAdminVo.class);
    }

    /**
     * admin短信条件查询
     * @param select 查询SQL
     * @param smsSendRecordAdminParam 查询入参
     */
    protected void buildOptions(SelectJoinStep<? extends Record> select, SmsSendRecordAdminParam smsSendRecordAdminParam) {
        if (smsSendRecordAdminParam.getAccountName() != null) {
            select.where(SMS_SEND_RECORD.ACCOUNT_NAME.like(smsSendRecordAdminParam.getAccountName()));
        }
        if (smsSendRecordAdminParam.getUserId() != null) {
            select.where(SMS_SEND_RECORD.USER_ID.eq(smsSendRecordAdminParam.getUserId()));
        }
        if (smsSendRecordAdminParam.getResponseCode() != null) {
            select.where(SMS_SEND_RECORD.RESPONSE_CODE.like(smsSendRecordAdminParam.getResponseCode()));
        }
        if (smsSendRecordAdminParam.getExt() != null && !"".equals(smsSendRecordAdminParam.getExt())) {
            select.where(SMS_SEND_RECORD.EXT.like(smsSendRecordAdminParam.getExt()));
        }
        if (smsSendRecordAdminParam.getStartCreateTime() != null) {
            select.where(SMS_SEND_RECORD.CREATE_TIME.gt(smsSendRecordAdminParam.getStartCreateTime()));
        }
        if (smsSendRecordAdminParam.getEndCreateTime() != null) {
            select.where(SMS_SEND_RECORD.CREATE_TIME.lt(smsSendRecordAdminParam.getEndCreateTime()));
        }
        if (smsSendRecordAdminParam.getResponseMsgCode() != null
            && !"".equals(smsSendRecordAdminParam.getResponseMsgCode())
            && SMS_SEND_STATUS_SUCCESS.equals(smsSendRecordAdminParam.getResponseMsgCode())) {
            select.where(SMS_SEND_RECORD.RESPONSE_MSG_CODE.eq(smsSendRecordAdminParam.getResponseMsgCode()));
        }
        if (smsSendRecordAdminParam.getResponseMsgCode() != null
            && !"".equals(smsSendRecordAdminParam.getResponseMsgCode())
            && !SMS_SEND_STATUS_SUCCESS.equals(smsSendRecordAdminParam.getResponseMsgCode())) {
            select.where(SMS_SEND_RECORD.RESPONSE_MSG_CODE.ne(SMS_SEND_STATUS_SUCCESS));
        }
        select.orderBy(SMS_SEND_RECORD.RESPONSE_TIME.desc());
    }


    /**
     * 获取该用户今天接收短信数量
     * @param userId 用户id
     * @param ext 短信类型
     * @return Integer
     */
    public Integer selectTodaySms(Integer userId, String ext) {
        // 获取当前时间
        long current = System.currentTimeMillis();
        //今天零点零分零秒的秒数
        // 获取当天凌晨0点0分0秒Date
        Calendar todayZero = Calendar.getInstance();
        todayZero.set(todayZero.get(Calendar.YEAR), todayZero.get(Calendar.MONTH), todayZero.get(Calendar.DAY_OF_MONTH),
            0, 0, 0);
        Date beginOfDate = todayZero.getTime();
        // 获取当天23点59分59秒Date
        Calendar todayTwelve = Calendar.getInstance();
        todayTwelve.set(todayTwelve.get(Calendar.YEAR), todayTwelve.get(Calendar.MONTH), todayTwelve.get(Calendar.DAY_OF_MONTH),
            23, 59, 59);
        Date endOfDate = todayTwelve.getTime();
        // 获取当前的接收信息数
        return db().selectCount().from(SMS_SEND_RECORD)
            .where(SMS_SEND_RECORD.USER_ID.eq(userId))
            .and(SMS_SEND_RECORD.EXT.eq(ext))
            .and((SMS_SEND_RECORD.RESPONSE_TIME.gt(new Timestamp(beginOfDate.getTime()))))
            .and(SMS_SEND_RECORD.RESPONSE_TIME.lt(new Timestamp(endOfDate.getTime())))
            .fetchAnyInto(Integer.class);
    }
}

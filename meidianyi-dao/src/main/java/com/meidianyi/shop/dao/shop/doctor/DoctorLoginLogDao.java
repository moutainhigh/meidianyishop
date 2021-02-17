package com.meidianyi.shop.dao.shop.doctor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.DoctorLoginLogDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorAttendanceListParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorAttendanceOneParam;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR;
import static com.meidianyi.shop.db.shop.Tables.DOCTOR_LOGIN_LOG;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.date;

/**
 * @author 孔德成
 * @date 2020/9/16 14:07
 */
@Repository
public class DoctorLoginLogDao extends ShopBaseDao {


    public final static String  NAME ="name";
    public final static String  LAST_TIME ="last_time";
    public final static String  LOGIN_DAYS ="login_days";
    public final static String  DATE ="date";
    public final static String  VALUE ="value";
    public final static Byte  THIS_MONTH = 1;
    public final static Integer  INTEGER_ZERO = 0;
    /**
     * 医师的出勤天数
     * @param doctorId 医师id
     * @return
     */
    public Integer getDoctorAttendanceDayNum(Integer doctorId, Timestamp startTime, Timestamp endTime) {
        List<Integer> integers = db().selectCount().from(DOCTOR_LOGIN_LOG)
                .where(DOCTOR_LOGIN_LOG.DOCTOR_ID.eq(doctorId))
                .and(DOCTOR_LOGIN_LOG.CREATE_TIME.between(startTime, endTime))
                .groupBy(date(DOCTOR_LOGIN_LOG.CREATE_TIME))
                .fetchInto(Integer.class);
        return integers.size();

    }

    /**
     * 保存
     * @param param
     * @return
     */
    public Integer save(DoctorLoginLogDo param) {
        return db().newRecord(DOCTOR_LOGIN_LOG, param).insert();
    }

    /**
     * 医师出勤率
     * @param param
     * @return
     */
    public PageResult<DoctorAttendanceOneParam> getDoctorAttendancePage(DoctorAttendanceListParam param) {
        Timestamp startTime = getStartTime(param.getType());
        LocalDateTime localDateTime = startTime.toLocalDateTime();
        Date now = Date.valueOf(LocalDate.now());

        Integer days =  LocalDateTime.now().getDayOfYear() - localDateTime.getDayOfYear();

        Condition doctorJoinLoginTableCondition = DOCTOR.ID.eq(DOCTOR_LOGIN_LOG.DOCTOR_ID).and(DOCTOR.AUTH_TIME.le(DOCTOR_LOGIN_LOG.CREATE_TIME)).and(DOCTOR_LOGIN_LOG.CREATE_TIME.ge(startTime));

        Field<Integer> loginNum = DSL.countDistinct(DSL.date(DOCTOR_LOGIN_LOG.CREATE_TIME)).as("login_days");
        Field<Integer> neededNum = DSL.iif(DOCTOR.AUTH_TIME.ge(startTime), DSL.dateDiff(now, date(DOCTOR.AUTH_TIME)), days).as("needed_num");
        Field<BigDecimal> dengLuLv = DSL.countDistinct(DSL.date(DOCTOR_LOGIN_LOG.CREATE_TIME)).cast(SQLDataType.DECIMAL(10,2)).divide(DSL.iif(DOCTOR.AUTH_TIME.ge(startTime), DSL.dateDiff(now, date(DOCTOR.AUTH_TIME)).add(1), days+1).cast(SQLDataType.DECIMAL(10,2))).as("login_rate");
        Field<Timestamp> lastTime = DSL.max(DOCTOR_LOGIN_LOG.CREATE_TIME).as(LAST_TIME);
        SelectSeekStep1<Record6<Integer, Integer, Integer, BigDecimal, String, Timestamp>, BigDecimal> select = db().select(DOCTOR.ID.as("doctor_id"), loginNum, neededNum, dengLuLv, DOCTOR.NAME, lastTime).from(DOCTOR).leftJoin(DOCTOR_LOGIN_LOG)
            .on(doctorJoinLoginTableCondition)
            .groupBy(DOCTOR.ID, DOCTOR.AUTH_TIME, DOCTOR.NAME)
            .orderBy(dengLuLv.desc());

        return this.getPageResult(select, param.getCurrentPage(), 5, DoctorAttendanceOneParam.class);
    }

    public Timestamp getStartTime(Byte type) {
        if (THIS_MONTH.equals(type)) {
            LocalDateTime today = LocalDate.now().atStartOfDay();
            DateTime mouthStart = DateUtil.beginOfMonth(Date.valueOf(today.toLocalDate()));
            Timestamp mouthStartTime = new Timestamp(mouthStart.getTime());
            return mouthStartTime;
        }
        LocalDateTime today = LocalDate.now().atStartOfDay();
        return Timestamp.valueOf(today.minusDays(30));
    }

    public List<Integer> getDoctorIds(BigDecimal min, BigDecimal max,Byte type){
        Timestamp startTime = getStartTime(type);
        LocalDateTime localDateTime = startTime.toLocalDateTime();
        Date now = Date.valueOf(LocalDate.now());

        Integer days =  LocalDateTime.now().getDayOfYear() - localDateTime.getDayOfYear();

        Condition doctorJoinLoginTableCondition = DOCTOR.ID.eq(DOCTOR_LOGIN_LOG.DOCTOR_ID).and(DOCTOR.AUTH_TIME.le(DOCTOR_LOGIN_LOG.CREATE_TIME)).and(DOCTOR_LOGIN_LOG.CREATE_TIME.ge(startTime));
        return db().select(DOCTOR.ID).from(DOCTOR).leftJoin(DOCTOR_LOGIN_LOG)
            .on(doctorJoinLoginTableCondition)
            .groupBy(DOCTOR.ID, DOCTOR.AUTH_TIME)
            .having(
                DSL.countDistinct(DSL.date(DOCTOR_LOGIN_LOG.CREATE_TIME)).cast(SQLDataType.DECIMAL(10,2)).divide(DSL.iif(DOCTOR.AUTH_TIME.ge(startTime), DSL.dateDiff(now, date(DOCTOR.AUTH_TIME)).add(1), days+1).cast(SQLDataType.DECIMAL(10,2))).ge(min)
                .and(DSL.countDistinct(DSL.date(DOCTOR_LOGIN_LOG.CREATE_TIME)).cast(SQLDataType.DECIMAL(10,2)).divide(DSL.iif(DOCTOR.AUTH_TIME.ge(startTime), DSL.dateDiff(now, date(DOCTOR.AUTH_TIME)).add(1), days+1).cast(SQLDataType.DECIMAL(10,2))).lt(max))
            ).fetchInto(Integer.class);
    }

    /**
     * 根据医师id获取出勤信息
     * @param doctorId
     * @param startTime
     * @param endTime
     * @return
     */
    public DoctorAttendanceOneParam getDoctorAttend(Integer doctorId,Timestamp startTime,Timestamp endTime){
        SelectJoinStep<? extends Record> select = db().select(DOCTOR_LOGIN_LOG.DOCTOR_ID,DSL.countDistinct(date(DOCTOR_LOGIN_LOG.CREATE_TIME)).as(LOGIN_DAYS)
            , DSL.max(DOCTOR_LOGIN_LOG.CREATE_TIME).as(LAST_TIME),DOCTOR.NAME)
            .from(DOCTOR)
            .leftJoin(DOCTOR_LOGIN_LOG).on(DOCTOR.ID.eq(DOCTOR_LOGIN_LOG.DOCTOR_ID));
        select.where(DOCTOR.ID.eq(doctorId));
        if(startTime!=null){
            select.where(DOCTOR_LOGIN_LOG.CREATE_TIME.ge(startTime));
        }
        if(endTime!=null){
            select.where(DOCTOR_LOGIN_LOG.CREATE_TIME.le(endTime));
        }
        select.groupBy(DOCTOR_LOGIN_LOG.DOCTOR_ID,DOCTOR.NAME).orderBy(DSL.countDistinct(date(DOCTOR_LOGIN_LOG.CREATE_TIME)).desc());
        return select.fetchAnyInto(DoctorAttendanceOneParam.class);
    }

    /**
     * 医师出勤排名
     * @param
     * @return
     */
    public Integer getDoctorAttendanceRank(BigDecimal loginRate,Byte type) {

        Timestamp startTime = getStartTime(type);
        LocalDateTime localDateTime = startTime.toLocalDateTime();
        Date now = Date.valueOf(LocalDate.now());

        Integer days =  LocalDateTime.now().getDayOfYear() - localDateTime.getDayOfYear();

        Condition doctorJoinLoginTableCondition = DOCTOR.ID.eq(DOCTOR_LOGIN_LOG.DOCTOR_ID).and(DOCTOR.AUTH_TIME.le(DOCTOR_LOGIN_LOG.CREATE_TIME)).and(DOCTOR_LOGIN_LOG.CREATE_TIME.ge(startTime));
        List<Integer> doctorIds = db().select(DOCTOR.ID).from(DOCTOR).leftJoin(DOCTOR_LOGIN_LOG)
            .on(doctorJoinLoginTableCondition)
            .groupBy(DOCTOR.ID, DOCTOR.AUTH_TIME)
            .having(
                DSL.countDistinct(DSL.date(DOCTOR_LOGIN_LOG.CREATE_TIME)).cast(SQLDataType.DECIMAL(10,6)).divide(DSL.iif(DOCTOR.AUTH_TIME.ge(startTime), DSL.dateDiff(now, date(DOCTOR.AUTH_TIME)).add(1), days+1).cast(SQLDataType.DECIMAL(10,6))).gt(loginRate)
            ).fetchInto(Integer.class);
        return (doctorIds == null) ? 1:(doctorIds.size()+1);
    }
}

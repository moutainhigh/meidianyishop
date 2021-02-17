package com.meidianyi.shop.service.shop.summary.visit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.MpDailyRetainRecord;
import com.meidianyi.shop.db.shop.tables.records.MpMonthlyRetainRecord;
import com.meidianyi.shop.db.shop.tables.records.MpWeeklyRetainRecord;
import com.meidianyi.shop.service.pojo.shop.summary.visit.AccessRetain;
import com.meidianyi.shop.service.pojo.shop.summary.visit.AccessRetainVo;
import com.meidianyi.shop.service.pojo.shop.summary.visit.RetainItem;
import com.meidianyi.shop.service.pojo.shop.summary.visit.VisitStatisticsParam;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Result;
import org.jooq.TableField;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.meidianyi.shop.db.shop.tables.MpDailyRetain.MP_DAILY_RETAIN;
import static com.meidianyi.shop.db.shop.tables.MpMonthlyRetain.MP_MONTHLY_RETAIN;
import static com.meidianyi.shop.db.shop.tables.MpWeeklyRetain.MP_WEEKLY_RETAIN;
import static com.meidianyi.shop.service.pojo.shop.summary.visit.AccessRetainVo.ACTION_NEW_RETAIN;
import static com.meidianyi.shop.service.pojo.shop.summary.visit.AccessRetainVo.ACTION_RETAIN;

/**
 * 留存统计
 *
 * @author 郑保乐
 */
@Service
public class RetainService extends BaseVisitService {

    private static final int GRADING_DAY = 1;
    private static final int GRADING_WEEK = 7;
    private static final int GRADING_MONTH = 30;
    /** 日期标识符 */
    private static final Integer CUSTOM_DAYS = 0;
    /**
     *得到之前的某一天(字符串类型)
     *@param days N天前
     *@return preDay(String)
     */
    public String getDate(Integer days) {
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //获取当前时间
        Calendar c = Calendar.getInstance();
        //计算指定日期
        c.add(Calendar.DATE, - days);
        Date time = c.getTime();
        //返回格式化后的String日期
        return sdf.format(time);
    }
    public AccessRetainVo getAccessRetain(VisitStatisticsParam param) {
        //得到时间
        if (!param.getType().equals(CUSTOM_DAYS)){
            param.setStartDate(getDate(param.getType()));
            param.setEndDate(getDate(NumberUtils.INTEGER_ZERO));
        }
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        int action = param.getAction();
        Result<?> retainResult;
        AccessRetainVo vo = new AccessRetainVo();
        List<AccessRetain> retains;
        Integer grading = param.getGrading();
        switch (action) {
            case ACTION_NEW_RETAIN:
                retainResult = getNewRetainResult(startDate, endDate, grading);
                retains = getRetainList(retainResult, MP_DAILY_RETAIN.VISIT_UV_NEW);
                break;
            case ACTION_RETAIN:
                retainResult = getRetainResult(startDate, endDate, grading);
                retains = getRetainList(retainResult, MP_DAILY_RETAIN.VISIT_UV);
                break;
            default:
                throw new IllegalStateException("Unexpected action: " + action);
        }
        vo.setData(retains);
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);
        return vo;
    }

    private List<AccessRetain> getRetainList(
            Result<?> retainResult, TableField<MpDailyRetainRecord, String> field) {
        List<AccessRetain> retains = new LinkedList<>();
        retainResult.forEach(r -> {
            /* one day */
            String refDate = r.get(MP_DAILY_RETAIN.REF_DATE);
            String itemsString = r.get(field);
            Map<Integer,Integer> items = Util.parseJson(itemsString, new TypeReference<Map<Integer,Integer>>() {});
            Integer sum = 0;
            for (Integer value : items.values()){
                sum += value;
            }
            AccessRetain day = new AccessRetain();
            day.setRefDate(refDate);
            day.setData(Objects.requireNonNull(items));
            day.setSum(sum);
            retains.add(day);
        });
        return retains;
    }

    /**
     * 新增留存
     */
    private Result<?> getNewRetainResult(String startDate, String endDate, Integer grading) {
        switch (grading) {
            case GRADING_DAY:
                return getDaysResult(startDate, endDate, MP_DAILY_RETAIN.VISIT_UV_NEW);
            case GRADING_WEEK:
                return getWeeksResult(startDate, endDate, MP_WEEKLY_RETAIN.VISIT_UV_NEW);
            case GRADING_MONTH:
                return getMonthsResult(startDate, endDate, MP_MONTHLY_RETAIN.VISIT_UV_NEW);
            default:
                throw new IllegalStateException("Unexpected grading: " + grading);
        }
    }

    /**
     * 活跃留存
     */
    private Result<?> getRetainResult(String startDate, String endDate, Integer grading) {
        switch (grading) {
            case GRADING_DAY:
                return getDaysResult(startDate, endDate, MP_DAILY_RETAIN.VISIT_UV);
            case GRADING_WEEK:
                return getWeeksResult(startDate, endDate, MP_WEEKLY_RETAIN.VISIT_UV);
            case GRADING_MONTH:
                return getMonthsResult(startDate, endDate, MP_MONTHLY_RETAIN.VISIT_UV);
            default:
                throw new IllegalStateException("Unexpected grading: " + grading);
        }
    }

    /**
     * 查询（粒度：日）
     */
    private Result<MpDailyRetainRecord> getDaysResult(
            String startDate, String endDate, TableField<MpDailyRetainRecord, String> field) {
        return db().select(MP_DAILY_RETAIN.REF_DATE, field)
                .from(MP_DAILY_RETAIN)
                /* ref_date 在 start 和 end 之间的 */
                .where(MP_DAILY_RETAIN.REF_DATE.between(startDate).and(endDate))
                .orderBy(MP_DAILY_RETAIN.REF_DATE.desc())
                .fetch().into(MP_DAILY_RETAIN);
    }

    /**
     * 查询（粒度：周）
     */
    private Result<MpWeeklyRetainRecord> getWeeksResult(
            String startDate, String endDate, TableField<MpWeeklyRetainRecord, String> field) {
        return db().select(MP_WEEKLY_RETAIN.REF_DATE, field)
                .from(MP_WEEKLY_RETAIN)
                /* ref_date 中的开始日期 >= start 且 ref_date 中的结束日期 <= end */
                .where(MP_WEEKLY_RETAIN.REF_DATE.substring(1, 8).greaterOrEqual(startDate)
                        .and(MP_WEEKLY_RETAIN.REF_DATE.substring(10, 8).lessOrEqual(endDate)))
                .orderBy(MP_WEEKLY_RETAIN.REF_DATE.desc())
                .fetch().into(MP_WEEKLY_RETAIN);
    }

    /**
     * 查询（粒度：月）
     */
    private Result<MpMonthlyRetainRecord> getMonthsResult(
            String startDate, String endDate, TableField<MpMonthlyRetainRecord, String> field) {
        return db().select(MP_MONTHLY_RETAIN.REF_DATE, field)
                .from(MP_MONTHLY_RETAIN)
                /* ref_date 在 start 的年月和 end 的年月之间 */
                .where(MP_MONTHLY_RETAIN.REF_DATE.between(yearAndMonthOf(startDate)).and(yearAndMonthOf(endDate)))
                .orderBy(MP_MONTHLY_RETAIN.REF_DATE.desc())
                .fetch().into(MP_MONTHLY_RETAIN);
    }

    /**
     * 截取日期字符串获取年月
     */
    private String yearAndMonthOf(String rawDateString) {
        return rawDateString.substring(0, 6);
    }
}

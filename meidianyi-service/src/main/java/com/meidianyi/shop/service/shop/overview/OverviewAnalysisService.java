package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.common.foundation.util.PropertiesUtil;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.overview.analysis.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.meidianyi.shop.db.shop.Tables.*;


/**
 * 概况统计
 * 
 * @author liangchen
 * @date 2019年7月15日
 */
@Service

public class OverviewAnalysisService extends ShopBaseService {
    /** 自定义常量 */
    private static final Integer ONE_DAY = 1;
    private static final Integer TWO_DAYS = 2;
    private static final Integer EIGHT_DAYS = 8;
    private static final Integer THIRTY_ONE_DAYS = 31;
    /** 日期标识符 */
    private static final Integer CUSTOM_DAYS = 0;
    /** 活动标识符 */
    private static final Integer VISIT_TOTAL = 1;
    private static final Integer SESSION_CNT= 2;
    private static final Integer VISIT_PV= 3;
    private static final Integer VISIT_UV= 4;
    private static final Integer VISIT_UV_NEW= 5;
    private static final Integer SHARE_PV= 6;
    private static final Integer SHARE_UV= 7;
    private static final Integer STAY_TIME_UV= 8;
    private static final Integer STAY_TIME_SESSION= 9;
    /** 页面统计 */
    private static final String PAGE_OTHER = "page.other";

    /**
	 * 查询昨日概况
	 * 
	 * @return 昨日概况详情(基础信息+变化率)
	 */
	
	public List<YesterdayStatisticsVo> yesterdayAnalysis() {
		//得到昨日、前一日、前一周、前一月的时间
	    String basicTime = getDate(ONE_DAY);
		String oneDayAgoTime = getDate(TWO_DAYS);
		String oneWeekAgoTime = getDate(EIGHT_DAYS);
		String oneMonthAgoTime = getDate(THIRTY_ONE_DAYS);
        //得到昨日的数据
        YesterdayVo yesterdayVo = db().select(MP_DAILY_VISIT.SESSION_CNT,MP_DAILY_VISIT.VISIT_PV,
            MP_DAILY_VISIT.VISIT_UV, MP_DAILY_VISIT.VISIT_UV_NEW,MP_SUMMARY_TREND.SHARE_PV,MP_SUMMARY_TREND.SHARE_UV)
            .from(MP_DAILY_VISIT)
            .leftJoin(MP_SUMMARY_TREND).on(MP_DAILY_VISIT.REF_DATE.eq(MP_SUMMARY_TREND.REF_DATE))
            .where(MP_DAILY_VISIT.REF_DATE.eq(basicTime))
            .fetchOptionalInto(YesterdayVo.class)
            .orElse(new YesterdayVo());
        //得到一天前的数据
        OneDayAgoVo oneDayAgoVo = db().select(MP_DAILY_VISIT.SESSION_CNT,MP_DAILY_VISIT.VISIT_PV,
            MP_DAILY_VISIT.VISIT_UV, MP_DAILY_VISIT.VISIT_UV_NEW,MP_SUMMARY_TREND.SHARE_PV,MP_SUMMARY_TREND.SHARE_UV)
            .from(MP_DAILY_VISIT)
            .leftJoin(MP_SUMMARY_TREND).on(MP_DAILY_VISIT.REF_DATE.eq(MP_SUMMARY_TREND.REF_DATE))
            .where(MP_DAILY_VISIT.REF_DATE.eq(oneDayAgoTime))
            .fetchOptionalInto(OneDayAgoVo.class)
            .orElse(new OneDayAgoVo());
        //得到一周前的数据
        OneWeekAgoVo oneWeekAgoVo = db().select(MP_DAILY_VISIT.SESSION_CNT,MP_DAILY_VISIT.VISIT_PV,
            MP_DAILY_VISIT.VISIT_UV, MP_DAILY_VISIT.VISIT_UV_NEW,MP_SUMMARY_TREND.SHARE_PV,MP_SUMMARY_TREND.SHARE_UV)
            .from(MP_DAILY_VISIT)
            .leftJoin(MP_SUMMARY_TREND).on(MP_DAILY_VISIT.REF_DATE.eq(MP_SUMMARY_TREND.REF_DATE))
            .where(MP_DAILY_VISIT.REF_DATE.eq(oneWeekAgoTime))
            .fetchOptionalInto(OneWeekAgoVo.class)
            .orElse(new OneWeekAgoVo());
        //得到一月前的数据
        OneMonthAgoVo oneMonthAgoVo = db().select(MP_DAILY_VISIT.SESSION_CNT,MP_DAILY_VISIT.VISIT_PV,
            MP_DAILY_VISIT.VISIT_UV, MP_DAILY_VISIT.VISIT_UV_NEW,MP_SUMMARY_TREND.SHARE_PV,MP_SUMMARY_TREND.SHARE_UV)
            .from(MP_DAILY_VISIT)
            .leftJoin(MP_SUMMARY_TREND).on(MP_DAILY_VISIT.REF_DATE.eq(MP_SUMMARY_TREND.REF_DATE))
            .where(MP_DAILY_VISIT.REF_DATE.eq(oneMonthAgoTime))
            .fetchOptionalInto(OneMonthAgoVo.class)
            .orElse(new OneMonthAgoVo());
        //打开次数
        YesterdayStatisticsVo sessionCount = getYesterdayDetail("sessionCount",yesterdayVo.getSessionCnt(),oneDayAgoVo.getSessionCnt(),oneWeekAgoVo.getSessionCnt(),oneMonthAgoVo.getSessionCnt());
        //访问次数
        YesterdayStatisticsVo visitPv = getYesterdayDetail("visitPv",yesterdayVo.getVisitPv(),oneDayAgoVo.getVisitPv(),oneWeekAgoVo.getVisitPv(),oneMonthAgoVo.getVisitPv());
        //访问人数
        YesterdayStatisticsVo visitUv = getYesterdayDetail("visitUv",yesterdayVo.getVisitUv(),oneDayAgoVo.getVisitUv(),oneWeekAgoVo.getVisitUv(),oneMonthAgoVo.getVisitUv());
        //新访问用户数
        YesterdayStatisticsVo visitUvNew = getYesterdayDetail("visitUvNew",yesterdayVo.getVisitUvNew(),oneDayAgoVo.getVisitUvNew(),oneWeekAgoVo.getVisitUvNew(),oneMonthAgoVo.getVisitUvNew());
        //分享次数
        YesterdayStatisticsVo sharePv = getYesterdayDetail("sharePv",yesterdayVo.getSharePv(),oneDayAgoVo.getSharePv(),oneWeekAgoVo.getSharePv(),oneMonthAgoVo.getSharePv());
        //分享人数
        YesterdayStatisticsVo shareUv = getYesterdayDetail("shareUv",yesterdayVo.getShareUv(),oneDayAgoVo.getShareUv(),oneWeekAgoVo.getShareUv(),oneMonthAgoVo.getShareUv());
        List<YesterdayStatisticsVo> result = new ArrayList<>();
        result.add(sessionCount);
        result.add(visitPv);
        result.add(visitUv);
        result.add(visitUvNew);
        result.add(sharePv);
        result.add(shareUv);
        return result;
	}
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
    /**
     * 计算变化率
     * @param oldData 旧数据
     * @param newData 新数据
     * @return 变化率 四舍五入保留两位小数
     */
    private Double getChangeRate(Integer oldData,Integer newData){
        //数据为空，返回null
        if (oldData == null || newData == null){
            return null;
        }else if (oldData.equals(NumberUtils.INTEGER_ZERO)){
            return null;
        }else {
            //四舍五入并保留两位小数
            double doubleChangeRate = ((double) newData-(double)oldData)/(double)oldData*(double)100;
            BigDecimal changeRate = new BigDecimal(doubleChangeRate).setScale(2, RoundingMode.HALF_UP);
            return  changeRate.doubleValue();
        }
    }

    /**
     * 得到昨日概况详情
     * @param name 内容名称
     * @param yesterdayData 昨日数据
     * @param oneDayAgoData 前一天数据
     * @param oneWeekAgoData 前一周数据
     * @param oneMonthAgoData 前一月数据
     * @return 概况详情(基础信息+变化率)
     */
	private YesterdayStatisticsVo getYesterdayDetail(String name, Integer yesterdayData, Integer oneDayAgoData, Integer oneWeekAgoData, Integer oneMonthAgoData){
        YesterdayStatisticsVo details = new YesterdayStatisticsVo(){{
            setDataName(name);
            setDataNumber(yesterdayData);
            setDayRate(getChangeRate(oneDayAgoData,yesterdayData));
            setWeekRate(getChangeRate(oneWeekAgoData,yesterdayData));
            setMonthRate(getChangeRate(oneMonthAgoData,yesterdayData));
        }};
        return details;
    }
	/**
	 *折线图综合概况统计
	 *@param param 时间、数据类型、日期类型
	 *@return 起止时间和每日数据
	 */
	public VisitTrendVo getVisitTrend(VisitTrendParam param) {
		//得到时间
        if (!param.getType().equals(CUSTOM_DAYS)){
            param.setStartTime(getDate(param.getType()));
            param.setEndTime(getDate(NumberUtils.INTEGER_ONE));
        }
        //设置返回时间
        VisitTrendVo result = new VisitTrendVo();
        result.setStartTime(param.getStartTime());
        result.setEndTime(param.getEndTime());
        //设置查找内容
        Map<Integer, Field<?>> action = new HashMap<Integer,Field<?>>(9){{
            put(VISIT_TOTAL,MP_SUMMARY_TREND.VISIT_TOTAL);
            put(SESSION_CNT,MP_DAILY_VISIT.SESSION_CNT);
            put(VISIT_PV,MP_DAILY_VISIT.VISIT_PV);
            put(VISIT_UV,MP_DAILY_VISIT.VISIT_UV);
            put(VISIT_UV_NEW,MP_DAILY_VISIT.VISIT_UV_NEW);
            put(SHARE_PV,MP_SUMMARY_TREND.SHARE_PV);
            put(SHARE_UV,MP_SUMMARY_TREND.SHARE_UV);
            put(STAY_TIME_UV,MP_DAILY_VISIT.STAY_TIME_UV);
            put(STAY_TIME_SESSION,MP_DAILY_VISIT.STAY_TIME_SESSION);
        }};
        //定义每日数据
        List<VisitTrendDaily> dailyData;
        //不同数据查找不同的数据库
        if (param.getAction().equals(VISIT_TOTAL)||param.getAction().equals(SHARE_PV)||param.getAction().equals(SHARE_UV)){
            dailyData = getLessDailyData(action.get(param.getAction()),param.getStartTime(),param.getEndTime());
        }else {
            dailyData = getMoreDailyData(action.get(param.getAction()),param.getStartTime(),param.getEndTime());
        }
        //设置数据
        result.setDailyData(dailyData);
        return result;
	}

    /**
     * 得到活动类型为1,6,7的数据
     * @param field 字段名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 对应数据
     */
	private List<VisitTrendDaily> getLessDailyData(Field<?> field,String startTime,String endTime){
        List<VisitTrendDaily> dailyData = db().select(MP_SUMMARY_TREND.REF_DATE.as("date"),field.as("number"))
            .from(MP_SUMMARY_TREND)
            .where(MP_SUMMARY_TREND.REF_DATE.between(startTime,endTime))
            .fetchInto(VisitTrendDaily.class);
        return dailyData;
    }
    /**
     * 得到活动类型为2,3,4,5,8,9的数据
     * @param field 字段名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 对应数据
     */
    private List<VisitTrendDaily> getMoreDailyData(Field<?> field,String startTime,String endTime){
        List<VisitTrendDaily> dailyData = db().select(MP_DAILY_VISIT.REF_DATE.as("date"),field.as("number"))
            .from(MP_DAILY_VISIT)
            .where(MP_DAILY_VISIT.REF_DATE.between(startTime,endTime))
            .fetchInto(VisitTrendDaily.class);
        return dailyData;
    }


    /**
	 *页面访问次数统计
	 *@param param
	 *@return
	 */
	public PageStatisticsVo getPageInfo(PageStatisticsParam param) {
        //得到时间
        if (!param.getType().equals(CUSTOM_DAYS)){
            param.setStartTime(getDate(param.getType()));
            param.setEndTime(getDate(NumberUtils.INTEGER_ONE));
        }
        //设置返回时间
        PageStatisticsVo result = new PageStatisticsVo();
        result.setStartTime(param.getStartTime());
        result.setEndTime(param.getEndTime());
        //获取单个页面访问数据
		List<PageListVo> pageListVos = db().select(MP_VISIT_PAGE.PAGE_PATH, DSL.sum(MP_VISIT_PAGE.PAGE_VISIT_PV).as("pageVisitPv"))
				.from(MP_VISIT_PAGE)
				.where(MP_VISIT_PAGE.REF_DATE.between(param.getStartTime(), param.getEndTime()))
				.groupBy(MP_VISIT_PAGE.PAGE_PATH)
                .orderBy(DSL.sum(MP_VISIT_PAGE.PAGE_VISIT_PV).as("pageVisitPv").desc())
				.fetchInto(PageListVo.class);
		//计算访问总数
		Integer total = db().select(DSL.sum(MP_VISIT_PAGE.PAGE_VISIT_PV))
				.from(MP_VISIT_PAGE)
				.where(MP_VISIT_PAGE.REF_DATE.between(param.getStartTime(), param.getEndTime()))
				.fetchOneInto(Integer.class);
		for(PageListVo pageListVo:pageListVos) {
            pageListVo.setPageName(pageNameOf(pageListVo.getPagePath()));
            pageListVo.setRate(getRate(pageListVo.getPageVisitPv(),total));
		}
		result.setList(pageListVos);
		return result;
	}
	 /**
     * 获取路径对应的页面名称
     */
    private String pageNameOf(String pagePath) {
        return Optional.ofNullable(pageMap().get(pagePath)).orElse(PAGE_OTHER);
    }
    /**
     * 路径和页面名称对应关系
     */
    private Map<String, String> pageMap() {
        return PropertiesUtil.toMap("visit/pages.properties");
    }
    /**
     * 计算占比
     * @param singleData 单个数据
     * @param totalData 总数据
     * @return 占比 四舍五入保留两位小数
     */
    private Double getRate(Integer singleData,Integer totalData){
        //除数为空，返回null
        if (totalData.equals(NumberUtils.INTEGER_ZERO)){
            return null;
        }
        //四舍五入并保留两位小数
        double doubleRate = ((double)singleData)/(double)totalData*(double)100;
        BigDecimal rate = new BigDecimal(doubleRate).setScale(2, RoundingMode.HALF_UP);
        return  rate.doubleValue();
    }
}

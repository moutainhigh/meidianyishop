package com.meidianyi.shop.service.shop.task.wechat;

import static com.meidianyi.shop.db.shop.tables.MpDailyRetain.MP_DAILY_RETAIN;
import static com.meidianyi.shop.db.shop.tables.MpDailyVisit.MP_DAILY_VISIT;
import static com.meidianyi.shop.db.shop.tables.MpDistributionVisit.MP_DISTRIBUTION_VISIT;
import static com.meidianyi.shop.db.shop.tables.MpMonthlyRetain.MP_MONTHLY_RETAIN;
import static com.meidianyi.shop.db.shop.tables.MpMonthlyVisit.MP_MONTHLY_VISIT;
import static com.meidianyi.shop.db.shop.tables.MpSummaryTrend.MP_SUMMARY_TREND;
import static com.meidianyi.shop.db.shop.tables.MpUserPortrait.MP_USER_PORTRAIT;
import static com.meidianyi.shop.db.shop.tables.MpVisitPage.MP_VISIT_PAGE;
import static com.meidianyi.shop.db.shop.tables.MpWeeklyRetain.MP_WEEKLY_RETAIN;
import static com.meidianyi.shop.db.shop.tables.MpWeeklyVisit.MP_WEEKLY_VISIT;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.meidianyi.shop.service.saas.shop.MpAuthShopService;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.MpDailyRetainRecord;
import com.meidianyi.shop.db.shop.tables.records.MpDailyVisitRecord;
import com.meidianyi.shop.db.shop.tables.records.MpDistributionVisitRecord;
import com.meidianyi.shop.db.shop.tables.records.MpMonthlyRetainRecord;
import com.meidianyi.shop.db.shop.tables.records.MpMonthlyVisitRecord;
import com.meidianyi.shop.db.shop.tables.records.MpSummaryTrendRecord;
import com.meidianyi.shop.db.shop.tables.records.MpUserPortraitRecord;
import com.meidianyi.shop.db.shop.tables.records.MpVisitPageRecord;
import com.meidianyi.shop.db.shop.tables.records.MpWeeklyRetainRecord;
import com.meidianyi.shop.db.shop.tables.records.MpWeeklyVisitRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.MaPortraitResult;
import com.meidianyi.shop.service.wechat.api.WxGetWeAnalysService;

import cn.binarywang.wx.miniapp.api.WxMaAnalysisService;
import cn.binarywang.wx.miniapp.bean.analysis.WxMaRetainInfo;
import cn.binarywang.wx.miniapp.bean.analysis.WxMaSummaryTrend;
import cn.binarywang.wx.miniapp.bean.analysis.WxMaVisitDistribution;
import cn.binarywang.wx.miniapp.bean.analysis.WxMaVisitPage;
import cn.binarywang.wx.miniapp.bean.analysis.WxMaVisitTrend;
import me.chanjar.weixin.common.error.WxErrorException;


/**
 * 获取微信统计数据
 * @author: 卢光耀
 * @date: 2019-07-23 14:38
 *
*/
@Service
public class WechatTaskService extends ShopBaseService {

    @Autowired
    private MpAuthShopService mpAuthShopService;

    Logger logger= LoggerFactory.getLogger(WechatTaskService.class);
	private static final byte ZERO = 0;
	private static final byte ONE = 1;
	private static final byte TWO = 2;
    private static final String CONTENT = "wechat-context";
    private static final ThreadLocal<String> LOCAL = ThreadLocal.withInitial(() -> {
        LocalDate date = LocalDate.now().minusDays(1);
        DateTimeFormatter faDateTimeFormatter = DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_SHORT);
        return date.format(faDateTimeFormatter);
    });

    private WxMaAnalysisService getServiceByShopId(Integer shopId) {
        return saas().shop.mp.getMaServiceByShopId(shopId).getAnalysisService();
    }

    private String getAppId(Integer shopId) {
    	return saas().shop.mp.getAppIdByShopId(shopId);
    }

    public void beginDailyTask(){
        Date date = DateUtils.convert(LocalDate.now().minusDays(1));
        WxMaAnalysisService service = mpAuthShopService.getMaServiceByShopId(getShopId()).getAnalysisService();
        WxGetWeAnalysService maService=open().getMaExtService();

        this.getDailyRetainInfo(service,date);

        this.getDailyVisitTrend(service,date);

        this.getVisitDistribution(service,date);

        this.getDailySummaryTrend(service,date);

        this.getVisitPage(service,date);

        this.getUserPortrait(maService);
    }

    public void beginWeeklyTask(){
        Date date = java.sql.Date.valueOf(LocalDate.now());
        WxMaAnalysisService service = getServiceByShopId(getShopId());

        this.getWeeklyRetainInfo(service,date);

        this.getWeeklyVisitTrend(service,date);

    }

    public void beginMonthlyTask(){
        Date date = java.sql.Date.valueOf(LocalDate.now());
        WxMaAnalysisService service = getServiceByShopId(getShopId());

        this.getMonthlyRetainInfo(service,date);

        this.getMonthlyVisitTrend(service,date);

    }
    /**
     * 查取用户访问情况
     * @param service
     * @param date
     */
    private void getVisitDistribution(WxMaAnalysisService service,Date date){
        try {
            WxMaVisitDistribution result = service.getVisitDistribution(date,date);
            if(validationData(result.getRefDate(), MP_DISTRIBUTION_VISIT)){
                return ;
            }
            MpDistributionVisitRecord record = db().newRecord(MP_DISTRIBUTION_VISIT);
            record.setRefDate(result.getRefDate());
            record.setList(Util.toJson(result.getList()));
            record.insert();
        } catch (WxErrorException e) {
            logger.error(CONTENT,e);
        }
    }
    /**
     * 查取用户访问情况
     * @param service
     * @param date
     */
    private void getDailySummaryTrend(WxMaAnalysisService service,Date date){
        try {
            List<WxMaSummaryTrend> result = service.getDailySummaryTrend(date,date);
            if(validationData(result, MP_SUMMARY_TREND)){
                return ;
            }
            List<MpSummaryTrendRecord> list = new ArrayList<>(result.size());
            result.forEach(v->list.add(db().newRecord(MP_SUMMARY_TREND,v)));
            db().batchInsert(list).execute();
        } catch (WxErrorException e) {
            logger.error(CONTENT,e);
        }
    }
    /**
     * 查取访问页面
     * @param service
     * @param date
     */
    private void getVisitPage(WxMaAnalysisService service,Date date){
        try {
            List<WxMaVisitPage> result = service.getVisitPage(date,date);
            if(validationData(result, MP_VISIT_PAGE)){
                return ;
            }
            List<MpVisitPageRecord> list = new ArrayList<>(result.size());
            result.forEach(v->{
                MpVisitPageRecord record = db().newRecord(MP_VISIT_PAGE,v);
                record.setEntrypagePv(v.getEntryPagePv().intValue());
                record.setExitpagePv(v.getExitPagePv().intValue());
                record.setPageSharePv(v.getPageSharePv().intValue());
                record.setPageShareUv(v.getPageShareUv().intValue());
                record.setPageStaytimePv(v.getPageStayTimePv().doubleValue());
                record.setRefDate(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT, date));
                list.add(record);
            });
            db().batchInsert(list).execute();
        } catch (WxErrorException e) {
            logger.error(CONTENT,e);
        }
    }

    /**
     * 查取用户画像
     * @param service
     */
    private void getUserPortrait(WxGetWeAnalysService service){
    	//昨天日期
    	Date endDate = extractedDate(-1);
    	//昨天
    	logger().info("昨天");
    	Date beginDate = extractedDate(-1);
    	recordManage(service, beginDate, endDate,ZERO);
    	//7天前
    	logger().info("7天");
    	beginDate = extractedDate(-7);
    	recordManage(service, beginDate, endDate,ONE);
    	//30天前
    	logger().info("30天");
    	beginDate = extractedDate(-30);
    	recordManage(service, beginDate, endDate,TWO);
    }

	private void recordManage(WxGetWeAnalysService service, Date beginDate, Date endDate,Byte type) {
		try {
            MaPortraitResult info = service.getUserPortrait(getAppId(getShopId()),beginDate,endDate);
            MpUserPortraitRecord record = db().selectFrom(MP_USER_PORTRAIT).where(MP_USER_PORTRAIT.REF_DATE.eq(info.getRefDate())).fetchAny();

            if(record!=null) {
            	logger().info("更新:{}",record.getId());
            	record = assignment(type, info, record);
            	record.setId(record.getId());
            	int execute = db().update(MP_USER_PORTRAIT).set(record).where(MP_USER_PORTRAIT.ID.eq(record.getId())).execute();
            	//int update = db().executeUpdate(record);
            	logger().info("统计更新：{}，结果：{}",info.getRefDate(),execute);
            }else {
            	logger().info("插入");
            	record = db().newRecord(MP_USER_PORTRAIT);
            	record = assignment(type, info, record);
            	int insert = record.insert();
            	logger().info("统计插入：{}，结果：{}",info.getRefDate(),insert);
            }
        } catch (WxErrorException e) {
            logger.error(CONTENT,e);
        }
	}
	/**
	 * 赋值
	 * @param type
	 * @param info
	 * @param record
	 * @return
	 */
	private MpUserPortraitRecord assignment(Byte type, MaPortraitResult info, MpUserPortraitRecord record) {
		record.setRefDate(info.getRefDate());
		record.setVisitUvNew(Util.toJson(info.getVisitUvNew()));
		record.setVisitUv(Util.toJson(info.getVisitUv()));
		record.setType(type);
		String refDate = info.getRefDate();
		String date = refDate.substring(0,8);
		Timestamp startTime = extracted(date);
		record.setStartTime(startTime);
		return  record;
	}

	/**
	 * startTime日期处理
	 * @param date
	 * @return
	 */
	private Timestamp extracted(String date) {
		LocalDate ld=LocalDate.now();
		DateTimeFormatter  dtf2=DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate date2= LocalDate.parse(date,dtf2);
		LocalDateTime localDateTime=LocalDateTime.of(date2, java.time.LocalTime.MIN);
		Timestamp startTime = Timestamp.valueOf(localDateTime);
		return startTime;
	}

    /**
     * 查取日访问趋势
     * @param service
     * @param date
     */
    private void getDailyVisitTrend(WxMaAnalysisService service, Date date) {
        try {
            List<WxMaVisitTrend> result = service.getDailyVisitTrend(date,date);
            if(validationData(result, MP_DAILY_VISIT)){
                return;
            }
            List<MpDailyVisitRecord> list = new ArrayList<>(result.size());
            result.forEach(v->{
                MpDailyVisitRecord record = db().newRecord(MP_DAILY_VISIT,v);
                list.add(record);
            });
            db().batchInsert(list).execute();
        } catch (WxErrorException e) {
            logger.error(CONTENT,e);
        }
    }
    /**
     * 查取周访问趋势
     * @param service
     * @param date
     */
    private void getWeeklyVisitTrend(WxMaAnalysisService service, Date date) {
        try {
            LocalDate startDate = LocalDate.parse(LOCAL.get(),
                    DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_SHORT)).minusDays(6);
            List<WxMaVisitTrend> result = service.getWeeklyVisitTrend(
                    Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),date);
            if(validationData(result, MP_WEEKLY_VISIT)){
                return;
            }
            List<MpWeeklyVisitRecord> list = new ArrayList<>(result.size());
            result.forEach(v->{
                MpWeeklyVisitRecord record = db().newRecord(MP_WEEKLY_VISIT,v);
                list.add(record);
            });
            db().batchInsert(list).execute();
        } catch (WxErrorException e) {
            logger.error(CONTENT,e);
        }
    }
    /**
     * 查取月访问趋势
     * @param service
     * @param date
     */
    private void getMonthlyVisitTrend(WxMaAnalysisService service, Date date) {
        try {
            LocalDate startDate = LocalDate.parse(LOCAL.get(),
                    DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_SHORT)).withDayOfMonth(1);
            List<WxMaVisitTrend> result = service.getMonthlyVisitTrend(
                    Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),date);
            if(validationData(result, MP_MONTHLY_VISIT)){
                return;
            }
            List<MpMonthlyVisitRecord> list = new ArrayList<>(result.size());
            result.forEach(v->{
                MpMonthlyVisitRecord record = db().newRecord(MP_MONTHLY_VISIT,v);
                list.add(record);
            });
            db().batchInsert(list).execute();
        } catch (WxErrorException e) {
            logger.error(CONTENT,e);
        }
    }

    /**
     * 查取日留存
     * @param service
     * @param date
     */
    private void getDailyRetainInfo(WxMaAnalysisService service,Date date){
        try {
            LocalDate localDate = LocalDate.now();
            for( int i = 1; i<16;i++ ){
                Date dateParam = DateUtils.convert(localDate.minusDays(i));
                WxMaRetainInfo info = service.getDailyRetainInfo(dateParam,dateParam);
                MpDailyRetainRecord record = db().newRecord(MP_DAILY_RETAIN);
                record.setRefDate(info.getRefDate());
                record.setVisitUvNew(Util.toJson(info.getVisitUvNew()));
                record.setVisitUv(Util.toJson(info.getVisitUv()));
                if(validationData(info.getRefDate(), MP_DAILY_RETAIN)){
                    db().update(MP_DAILY_RETAIN).set(record).where(MP_DAILY_RETAIN.REF_DATE.eq(info.getRefDate())).execute();
                }else{
                    record.insert();
                }
            }

        } catch (WxErrorException e) {
            logger.error(CONTENT,e);
        }
    }
    /**
     * 查取周留存
     * @param service
     * @param date
     */
    private void getWeeklyRetainInfo(WxMaAnalysisService service,Date date){
            List<WxMaRetainInfo> infos = Lists.newArrayList();
            for (int i = 1; i < 6; i++) {
                Date startDate = DateUtils.convert(LocalDate.now().minusWeeks(i).with(DayOfWeek.MONDAY));
                Date endDate = DateUtils.convert(LocalDate.now().minusWeeks(i).with(DayOfWeek.SUNDAY));
                WxMaRetainInfo info = getWeeklyRetain(service,startDate,endDate);
                if( info!= null ){
                    infos.add(info);
                }
            }
            //如果查询结束后数据为空，直接返回终止操作
            if( infos.isEmpty() ){
                return ;
            }
            //由于没有主键因此暂时先不使用batch操作
            for( WxMaRetainInfo info :infos ){
                MpWeeklyRetainRecord record = db().newRecord(MP_WEEKLY_RETAIN);
                record.setRefDate(info.getRefDate());
                record.setVisitUvNew(Util.toJson(info.getVisitUvNew()));
                record.setVisitUv(Util.toJson(info.getVisitUv()));
                if(validationData(info.getRefDate(), MP_WEEKLY_RETAIN)){
                    db().update(MP_WEEKLY_RETAIN).set(record).where(MP_WEEKLY_RETAIN.REF_DATE.eq(info.getRefDate())).execute();
                }else{
                    record.insert();
                }
            }
    }
    public WxMaRetainInfo getWeeklyRetain(WxMaAnalysisService service,Date star,Date end){
        try {
            return service.getWeeklyRetainInfo(star,end);
        } catch (WxErrorException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    public WxMaRetainInfo getMonthlyRetain(WxMaAnalysisService service,Date star,Date end){
        try {
            return service.getMonthlyRetainInfo(star,end);
        } catch (WxErrorException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    /**
     * 查取月留存
     * @param service
     * @param date
     */
    private void getMonthlyRetainInfo(WxMaAnalysisService service,Date date){
            List<WxMaRetainInfo> infos = Lists.newArrayList();
            for( int i = 1; i < 3;i++){
                LocalDate local = LocalDate.now();
                LocalDate month = local.minusMonths(i);
                Date startDate = DateUtils.convert(month.withDayOfMonth(1));
                Date endDate = DateUtils.convert(month.withDayOfMonth(month.lengthOfMonth()));
                WxMaRetainInfo info = getMonthlyRetain(service,startDate,endDate);
                if( info!= null ){
                    infos.add(info);
                }
            }
            //如果查询结束后数据为空，直接返回终止操作
            if( infos.isEmpty() ){
                return ;
            }
            //由于没有主键因此暂时先不使用batch操作
            for( WxMaRetainInfo info :infos ){
                MpMonthlyRetainRecord record = db().newRecord(MP_MONTHLY_RETAIN);
                record.setRefDate(info.getRefDate());
                record.setVisitUvNew(Util.toJson(info.getVisitUvNew()));
                record.setVisitUv(Util.toJson(info.getVisitUv()));
                if( validationData(info.getRefDate(), MP_MONTHLY_RETAIN)){
                    db().update(MP_MONTHLY_RETAIN).set(record).where(MP_MONTHLY_RETAIN.REF_DATE.eq(info.getRefDate())).execute();
                }else{
                    record.insert();
                }
            }

    }
    private boolean validationData(String refDate,Table<?> table){
        if( refDate == null ){
            return false;
        }
        Field<String> data = DSL.val(refDate);
        return isHavingData(table, data);
    }
    private boolean validationData(List<?> o,Table<?> table){
        if( o == null ){
            return false;
        }
        Field<String> data = DSL.val(LOCAL.get());
        return isHavingData(table, data);
    }
    private boolean isHavingData(Table<?> table,Field<String> date){
        int count =db().selectCount().from(table).where(table.field("ref_date",String.class).eq(date)).fetchOneInto(Integer.class);
        return count > 0;
    }

    /**
     * 获取对应的日期
     * @param num
     * @return
     */
	private Date extractedDate(Integer num) {
		LocalDateTime localDateTime =LocalDateTime.now().plus(num,ChronoUnit.DAYS);
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		Date from = Date.from(instant);
		return from;
	}
	public void test(){
        Date date = java.sql.Date.valueOf(LocalDate.now().minusDays(1));
        WxMaAnalysisService service = getServiceByShopId(getShopId());
        WxGetWeAnalysService maService=open().getMaExtService();
        this.getDailyVisitTrend(service,date);
    }

}

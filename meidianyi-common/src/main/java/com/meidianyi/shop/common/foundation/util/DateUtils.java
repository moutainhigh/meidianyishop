package com.meidianyi.shop.common.foundation.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Date工具
 *
 * @author: 卢光耀
 * @date: 2019-07-26 10:51
 *
 */
public final class DateUtils {
	public static enum IntervalType{
		DAY,WEEK,MONTH,SEASON,YEAR
	}
	protected static ThreadLocal<Map<String, SimpleDateFormat>> threadLocal = ThreadLocal
			.withInitial(() -> new HashMap<String, SimpleDateFormat>());

	public static final String DATE_FORMAT_SHORT = "yyyyMMdd";
    public static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd";
	public static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_DOT ="yyyy.MM.dd";
    public static final String DATE_FORMATE_MONTH = "MM-dd HH:mm:ss";

	public static final String DATE_FORMAT_FULL_NO_UNDERLINE = "yyyyMMddHHmmss";

	public static final String DATE_MYSQL_SIMPLE="%Y-%m-%d";

	public static final String DATE_MYSQL_DAY="%Y-%m-%d %H:%i";

	private static final Integer MILLI_SECOND = 1000;

	public static final String DATE_FORMAT_FULL_BEGIN="yyyy-MM-dd 00:00:00";
	public static final String DATE_FORMAT_FULL_END="yyyy-MM-dd 23:59:59";
    public static final String DATE_FORMAT_API_EXTERNAL = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_DEFAULT = "1970-01-13 00:00:00";

    /** 时分秒最小 */
    public static final LocalTime MIN_TIME = LocalTime.of(0, 0, 0);
    /** 时分秒最大 */
    public static final LocalTime MAX_TIME = LocalTime.of(23, 59, 59);



    private static final  ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

	/**
	 * 转换日期格式输出
	 * @param format
	 * @param date
	 * @return
	 */
	public static String dateFormat(String format, Date date) {
		LocalDateTime localDateTime = convertLocalDate(date);
		return  localDateTime.format(DateTimeFormatter.ofPattern(format));
	}
    /**
     * 转换日期格式输出
     * @param format
     * @param date
     * @return
     */
    public static String dateFormat(String format, Timestamp date) {
        LocalDateTime localDateTime = convertLocalDate(date);
        return  localDateTime.format(DateTimeFormatter.ofPattern(format));
    }
    /**
     * 转换日期格式输出
     * @param format
     * @param date
     * @return
     */
    public static String dateFormat(String format, LocalDate date) {
        return  date.format(DateTimeFormatter.ofPattern(format));
    }
    /**
     * 日期类型转换
     * @param source
     * @return localDate
     */
    public static LocalDate convert(Date source) {
        Instant instant = source.toInstant();
        return  instant.atZone(DEFAULT_ZONE_ID).toLocalDate();
    }
    /**
     * 日期类型转换
     * @param source
     * @return localDate
     */
    public static Date convert(LocalDate source) {
        return  Date.from(source.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

	/**
	 * Date 转为 LocalDateTime
	 * @param date
	 * @return
	 */
	public static LocalDateTime convertLocalDate(Date date) {
		Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
		return  LocalDateTime.ofInstant(instant, zone);
	}

    public static Timestamp convertToTimestamp(LocalDate date, LocalTime time) {
       return Timestamp.valueOf(LocalDateTime.of(date, time));
    }
	/**
	 * 转为LocalDate类型
	 * @param format 日期格式
	 * @param date 日期
	 * @return
	 */
	public static LocalDate localDate(String format,String date) {
		return LocalDate.parse(date,DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 获取本地日期
	 * @return
	 */
	public static LocalDate getLocalDate() {
		return LocalDate.now();
	}

    /**
     * 获取本地日期
     * @return
     */
    public static String getLocalDateFormat() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_FULL));
    }

    /**
     * 获取本地时间紧凑型格式：20200201115502
     * @return
     */
    public static String getLocalDateFullTightFormat(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_API_EXTERNAL));
    }

	/**
	 * 转为LocalDateTime类型
	 * @param format 日期时间格式
	 * @param dateTime 时间
	 * @return
	 */
	public static LocalDateTime localDateTime(String format,String dateTime) {
		return LocalDateTime.parse(dateTime,DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 当前时间文本输出
	 * @param format
	 * @return
	 */
	public static String dateFormat(String format) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
	}


	/**
	 * 获取本地的时间
	 */
	public static Timestamp getLocalDateTime() {
		return Timestamp.valueOf(dateFormat(DATE_FORMAT_FULL));
	}


    /**
     * 获取当前延后（秒）的时间
     */
    public static Timestamp getDalyedDateTime(Integer second) {
        return new Timestamp(getLocalDateTime().getTime()+second*MILLI_SECOND);
    }

	/**
	 * 	获取当前时间戳（可以直接用此值放入数据库）
	 */
	public static Timestamp getSqlTimestamp() {
		return Timestamp.from(Instant.now());
	}

	/**
	 * 获取当前/指定月份第一天
	 */
	public static Timestamp currentMonthFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date != null ? date : new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return new Timestamp(calendar.getTime().getTime());
	}
	/**
	 * 获取当前月份第一天
	 */
	public static Timestamp currentMonthFirstDay() {
		return currentMonthFirstDay(null);
	}
	/**
	 * 获取当前月份最后一天
	 */
	public static Timestamp currentMonthLastDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return new Timestamp(calendar.getTime().getTime());
	}
	/**
	 * 获取指定月份的下一个月的第一天.
	 */
	public static Timestamp nextMonthFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * 转换为时间戳
	 */
	public static Timestamp convertToTimestamp(String dateTime) {
		return dateTime !=null ? Timestamp.valueOf(dateTime):null;
	}

    /**
     * 获取两个日期之间的所有日期
     * @param startDate 开始
     * @param endDate  截止
     * @return LocalDate集合
     */
	public static List<LocalDate> getAllDatesBetweenTwoDates(LocalDate startDate,LocalDate endDate){
        long numberOfDaysBetween = ChronoUnit.DAYS.between(startDate,endDate);
        return IntStream.iterate(0,i->i+1)
            .limit(numberOfDaysBetween)
            .mapToObj(startDate::plusDays)
            .collect(Collectors.toList());
    }


	/**
	 * 获取本地的时间
	 */
	public static Timestamp getLocalTimeDate() {
		return Timestamp.valueOf(dateFormat(DATE_FORMAT_SIMPLE));
	}


	/**
	 * 获取本地的时间
	 */
	public static Timestamp getLocalTimeDateBySelf(String format) {
		return Timestamp.valueOf(dateFormat(format));
	}

	/**
	 * 时间戳是否为今日
	 * @param timestamp
	 * @return
	 */
	public static Boolean timestampIsNowDay(Timestamp timestamp) {
	    if(timestamp == null){
	        return false;
        }
		DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_FORMAT_SIMPLE);
		LocalDateTime localDateTime=timestamp.toLocalDateTime();
		String formate1 = df.format(localDateTime);
		LocalDateTime localDateTime2=LocalDateTime.now();
		String formate2 = df.format(localDateTime2);
		return formate1.equals(formate2);
	}

    /**
     * 两个时间戳是否为同一天
     * @param timestamp1
     * @param timestamp2
     * @return
     */
    public static Boolean timestampIsSameDay(Timestamp timestamp1, Timestamp timestamp2) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_FORMAT_SIMPLE);
        LocalDateTime localDateTime=timestamp1.toLocalDateTime();
        String formate1 = df.format(localDateTime);
        LocalDateTime localDateTime2=timestamp2.toLocalDateTime();
        String formate2 = df.format(localDateTime2);
        return formate1.equals(formate2);
    }

	/**
	 * 获取addNum(unit)后的时间时间
	 * @param addNum 该单位添加到结果的数量，可能是负数
	 * @param unit  使用ChronoUnit类 单位  SECONDS秒 ,MINUTES分钟, HOURS小时, DAYS天, WEEKS星期, MONTHS月, YEARS年
	 * @return
	 */
	public static Timestamp getTimeStampPlus(int addNum, ChronoUnit unit) {
		return  Timestamp.valueOf(LocalDateTime.now().plus(addNum,unit));
	}

    /**
     * 获取addNum(unit)后的时间时间
     * @param time 指定时间
     * @param addNum 该单位添加到结果的数量，可能是负数
     * @param unit  使用ChronoUnit类 单位  SECONDS秒 ,MINUTES分钟, HOURS小时, DAYS天, WEEKS星期, MONTHS月, YEARS年
     * @return
     */
    public static Timestamp getTimeStampPlus(Timestamp time,int addNum, ChronoUnit unit) {
        return  Timestamp.valueOf(time.toLocalDateTime().plus(addNum,unit));
    }

	public static LocalDate getBeforeLocalFor(int day){
	    return LocalDate.now().minusDays(day);
    }

    public static final DateTimeFormatter YYYY_MM_DD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Yyyy mm dd date java . sql . date.获取yyyy-mm-dd格式的date
     *
     * @param date the date
     * @return the java . sql . date
     */
    public static java.sql.Date yyyyMmDdDate(LocalDate date) {
        return java.sql.Date.valueOf(date.format(YYYY_MM_DD_FORMATTER));
    }

    /**
     * 时间字符串转化为时间戳格式
     * @param dateFormat 时间格式
     * @param date 时间
     * @return Timestamp
     */
    public static Timestamp dateFormatToTimeStamp(String dateFormat, String date){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        LocalDateTime parse = LocalDateTime.parse(date, dtf);
        return Timestamp.valueOf(parse);
    }

    /**
     * The constant DATE_1970.
     */
    public static final Date DATE_1970 = new Date(0);

    /**
     * Gets 1970 time stamp.
     *
     * @return the 1970 time stamp
     */
    public static Timestamp get1970TimeStamp() {
        return Timestamp.from(DATE_1970.toInstant());
    }

    /**
     * 获取30天前的时间
     */
    public static Timestamp getBefore30Day() {
        return Timestamp.from(Instant.now().plus(-30 , ChronoUnit.DAYS));
    }

	/**
	 * 时间差 时间1-时间2
	 * @param formatTime1 时间1
	 * @param formatTime2 时间2
	 * @return 天 时 分 秒
	 */
	public static Integer[] getTimeDifference(Timestamp formatTime1, Timestamp formatTime2) {
		long t1 = formatTime1.getTime();
		long t2 = formatTime2.getTime();
		Integer days =(int)((t1-t2)/(1000*60*60*24));
		Integer hours=(int) (t1 - t2)/(1000*60*60)-days*(24);
		Integer minutes=(int) (t1 - t2)/(1000*60)-days*(60*24)-hours*(60);
		Integer second=(int) ((t1 - t2)/1000-days*(60*60*24)-hours*(60*60)-minutes*60);
		return new Integer[]{days,hours,minutes,second};
	}

    /**
     * 获取小时差，
     * @param formatTime1 时间1
     * @param formatTime2 时间2
     * @return 时
     */
	public static int getTimeHourDifference(Timestamp formatTime1, Timestamp formatTime2){
        long t1 = formatTime1.getTime();
        long t2 = formatTime2.getTime();
        return (int) (t1 - t2)/(1000*60*60);
    }

    /**
     *  获取当天日期的开始时间
     * @return {@link Date}
     */
    public static Date getDateForStartTime() {

        return getStartCalendar(0).getTime();
    }
    private static Calendar getStartCalendar(Integer day){
        return getCalendar(day,0,0,0,0);
    }
    private static Calendar getEndCalendar(Integer day){
        return getCalendar(day,23,59,59,999);
    }
    private static Calendar getCalendar(Integer dayOfMonth,Integer hourOfDay,Integer minute,Integer second,Integer milliSecond){
        Calendar todayStart = Calendar.getInstance();

        if( hourOfDay != null){
            todayStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
        }
        if( minute != null){
            todayStart.set(Calendar.MINUTE, minute);
        }
        if( second != null){
            todayStart.set(Calendar.SECOND, second);
        }
        if( milliSecond != null){
            todayStart.set(Calendar.MILLISECOND, milliSecond);
        }
        if( dayOfMonth != null ){
            todayStart.add(Calendar.DAY_OF_MONTH,dayOfMonth);
        }
        return todayStart;
    }
    /**
     *  获取当天日期的截止时间
     * @return {@link Date}
     */
    public static Date getEndTime() {
        return getEndCalendar(0).getTime();
    }
    /**
     *  获取当天日期的开始时间
     * @return {@link Timestamp}
     */
    public static Timestamp getTimestampForTodayStartTime(){
        return Timestamp.from(getStartCalendar(0).toInstant());
    }
    /**
     *  获取当天日期的截止时间
     * @return {@link Timestamp}
     */
    public static Timestamp getTimestampForTodayEndTime(){
        return Timestamp.from(getEndCalendar(0).toInstant());
    }
    /**
     *  获取距离今天n天的开始时间
     * @return {@link Timestamp}
     */
    public static Timestamp getTimestampForStartTime(Integer day){
        return Timestamp.from(getStartCalendar(day).toInstant());
    }
    /**
     *  获取距离今天n天的截止时间
     * @return {@link Timestamp}
     */
    public static Timestamp getTimestampForEndTime(Integer day){
        return Timestamp.from(getEndCalendar(day).toInstant());
    }




	/**
	 * 获取两个时间点中的日期的列表
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getBetweenTime(Timestamp startTime, Timestamp endTime) {
		String format = DATE_FORMAT_SIMPLE;
		String startDate = DateUtils.dateFormat(format, startTime);
		String endDate = DateUtils.dateFormat(format, endTime);
		List<String> list = new ArrayList<String>();
		long add = 24 * 60 * 60 * 1000L;
		list.add(startDate);
		while (!startDate.equals(endDate)) {
			long time = startTime.getTime();
			time = time + add;
			startTime = new Timestamp(time);
			startDate = DateUtils.dateFormat(format, startTime);
			list.add(startDate);
		}
		return list;
	}

	/**
	 * 	获取当前时间周期（日，周，月，季，年）的起止时间
	 * 	@return Timestamp[2] = {start, end}
	 */
	public static Timestamp[] getInterval(IntervalType type) {
        LocalDate startDate = null, endDate = null;
        LocalDate currentDate = LocalDate.now();
        //	初始化当前年月日
        int year = currentDate.getYear(), month = currentDate.getMonthValue(), day = currentDate.getDayOfMonth();
        if(IntervalType.YEAR.equals(type)) {
            //	年
            startDate = LocalDate.of(year, 1 ,1);
            endDate = LocalDate.of(year, 12, 1).with(TemporalAdjusters.lastDayOfMonth());
        }else if(IntervalType.SEASON.equals(type)) {
            //	季
            startDate = LocalDate.of(year, (((month -1) / 3 + 1)* 3 -2),1);
            endDate = LocalDate.of(year, (((month -1) / 3 + 1)* 3) ,1).with(TemporalAdjusters.lastDayOfMonth());
        }else if(IntervalType.MONTH.equals(type)) {
            //	月
            startDate = LocalDate.of(year, month, 1);
            endDate = LocalDate.of(year, month ,1).with(TemporalAdjusters.lastDayOfMonth());
        }else if(IntervalType.WEEK.equals(type)) {
            //	周(周一开始)
            //	the day-of-week, from 1 (Monday) to 7 (Sunday)
            int dayOfWeek = currentDate.getDayOfWeek().getValue();
            startDate = currentDate.plusDays(-dayOfWeek + 1);
            endDate = currentDate.plusDays(7 -dayOfWeek);
        }else if(IntervalType.DAY.equals(type)) {
            //	天
            startDate = currentDate;
            endDate = currentDate;
        }else {
            return null;
        }
        Timestamp[] startAndEnd =  new Timestamp[2];
        startAndEnd[0] = Timestamp.valueOf(LocalDateTime.of(startDate, DateUtils.MIN_TIME));
        startAndEnd[1] = Timestamp.valueOf(LocalDateTime.of(endDate, DateUtils.MAX_TIME));
        return startAndEnd;
    }

    /**
     * 根据出生日期获得年龄
     * @param date
     * @return
     */
    public static int getAgeByBirthDay(Date date){
        if(date==null){
            return 0;
        }
        Calendar calendar1=Calendar.getInstance();
        calendar1.setTime(date);
        Date dateNow=new Date();
        Calendar calendar2=Calendar.getInstance();
        calendar2.setTime(dateNow);
        int year1=calendar1.get(Calendar.YEAR);
        int year2=calendar2.get(Calendar.YEAR);
        int month1=calendar1.get(Calendar.MONTH);
        int month2=calendar2.get(Calendar.MONTH);
        int day1=calendar1.get(Calendar.DAY_OF_MONTH);
        int day2=calendar2.get(Calendar.DAY_OF_MONTH);
        //整岁数
        int age=year2-year1;
        //判断月份
        if(month2>=month1){
            if(month2==month1){
                //判断具体日
                if(day2<day1){
                    age--;
                }
            }
        }else {
            age--;
        }
        return age;

    }

    /**
     *得到之前的某一天(字符串类型)
     *@param days N天前
     *@return preDay(String)
     */
    public static String getDate(Integer days) {
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        Calendar c = Calendar.getInstance();
        //计算指定日期
        c.add(Calendar.DATE, - days);
        Date time = c.getTime();
        //返回格式化后的String日期
        return sdf.format(time);
    }
}

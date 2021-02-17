package com.meidianyi.shop.date;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author 孔德成
 * @date 2020/8/19 16:02
 */
public class DateTest {

    @Test
    public void test1(){
//        DateTime date = DateUtil.lastWeek();
        String dateStr = "2020-08-15";
        Date date = DateUtil.parse(dateStr);
        String dateStr1 = "2020-08-19";
        Date date1 = DateUtil.parse(dateStr1);

        Timestamp endDate = DateUtil.beginOfWeek(date).toTimestamp();
        Timestamp startDate =DateUtil.beginOfWeek(date1).toTimestamp();
        System.out.println("start"+endDate.toString());
        System.out.println("end"+startDate.toString());
        System.out.println(DateUtil.between(endDate,startDate, DateUnit.DAY));
        System.out.println(DateUtil.between(endDate,startDate, DateUnit.WEEK,true));
        System.out.println(DateUtil.betweenMonth(endDate,startDate,true));
        System.out.println(DateUtil.betweenYear(endDate,startDate,true));

    }

    @Test
    public void test2(){
        String dateStr = "2019-08-15";
        Date date = DateUtil.parse(dateStr);
        String dateStr1 = "2020-08-16";
        Timestamp date1 = DateUtil.parse(dateStr1).toTimestamp();

        Timestamp endDate;
        Timestamp startDate;
        Timestamp startDate2;
        Timestamp endDate2;


        startDate2 = DateUtil.beginOfWeek(date).toTimestamp();
        endDate2 = DateUtil.beginOfWeek(date1).toTimestamp();

        startDate2 = DateUtils.getTimeStampPlus(startDate2, ((2 - 1) * 20), ChronoUnit.WEEKS);
        startDate2 = DateUtil.beginOfWeek(startDate2).toTimestamp();

        endDate2 = DateUtils.getTimeStampPlus(startDate2,  (20-1), ChronoUnit.WEEKS);
        endDate2 = DateUtil.endOfWeek(endDate2).toTimestamp();
        if (endDate2.after(date1)) {
            endDate2 = date1;
        }
        System.out.println("start"+startDate2.toString());
        System.out.println("end"+endDate2.toString());
        System.out.println(DateUtil.between(startDate2,endDate2, DateUnit.DAY));
        System.out.println(DateUtil.between(startDate2,endDate2, DateUnit.WEEK,true)+1);
        System.out.println(DateUtil.betweenMonth(startDate2,endDate2,true));
        System.out.println(DateUtil.betweenYear(startDate2,endDate2,true));
    }

    @Test
    public void test3(){

        String dateStr = "2020-02-3";

        Date date = DateUtil.parse(dateStr);

        DateTime offset = DateUtil.offset(date, DateField.MONTH, 1);
        System.out.println("start"+offset.toString());
        offset = DateUtil.endOfQuarter(offset);
        System.out.println("start"+offset.toString());
        offset = DateUtil.endOfWeek(offset);
        System.out.println("start"+offset.toString());
        offset = DateUtil.endOfYear(offset);
        System.out.println("start"+offset.toString());
        offset= DateUtil.offset(offset,  DateField.DAY_OF_YEAR,1);
        System.out.println("start"+offset.toString());
        offset = DateUtil.beginOfWeek(offset);
        System.out.println("start"+offset.toString());
        offset= DateUtil.offset(offset,  DateField.WEEK_OF_YEAR,1);
        System.out.println("start"+offset.toString());



    }
}

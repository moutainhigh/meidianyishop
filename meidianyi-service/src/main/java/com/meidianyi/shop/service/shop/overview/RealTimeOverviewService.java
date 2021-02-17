package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.OrderInfo;
import com.meidianyi.shop.db.shop.tables.Trades;
import com.meidianyi.shop.db.shop.tables.UserSummaryTrend;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.overview.Tuple2;
import com.meidianyi.shop.service.pojo.shop.overview.realtime.CoreIndicatorParam;
import com.meidianyi.shop.service.pojo.shop.overview.realtime.CoreIndicatorVo;
import com.meidianyi.shop.service.pojo.shop.overview.realtime.LineChartVo;
import com.meidianyi.shop.service.pojo.shop.overview.realtime.RealTimeVo;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.meidianyi.shop.db.shop.tables.UserLoginRecord.USER_LOGIN_RECORD;
import static com.meidianyi.shop.db.shop.tables.UserSummaryTrend.USER_SUMMARY_TREND;
import static org.jooq.impl.DSL.cast;

/**
 * @author liufei
 * @date 2019/7/19
 * @description 实时概况
 */
@Service

public class RealTimeOverviewService extends ShopBaseService {

    /**
     * 实时概况
     */
    public RealTimeVo realTime(){
        RealTimeVo realTimeVo = new RealTimeVo();
        /* 各个时间段的交易额累加和（当天和昨天） */
        List<Record2<Byte,BigDecimal>> todayTrades = db().select(Trades.TRADES.HOUR,Trades.TRADES.PAY_ORDER_MONEY)
                .from(Trades.TRADES)
                .where(Trades.TRADES.REF_DATE.eq(new java.sql.Date(Util.getEarlyDate(new Date(),0).getTime())))
                .orderBy(Trades.TRADES.HOUR.asc())
                .fetch();
        List<Record2<Byte,BigDecimal>> yesTrades = db().select(Trades.TRADES.HOUR,Trades.TRADES.PAY_ORDER_MONEY)
                .from(Trades.TRADES)
                .where(Trades.TRADES.REF_DATE.eq(new java.sql.Date(Util.getEarlyDate(new Date(),-1).getTime())))
                .orderBy(Trades.TRADES.HOUR.asc())
                .fetch();
        realTimeVo.setTodayPaidMoney(cumulativeSum(todayTrades));
        realTimeVo.setYesterdayPaidMoney(cumulativeSum(yesTrades));
        /* pay_user_num */
        List<Integer> yesPayUserNum = db().select(USER_SUMMARY_TREND.ORDER_USER_DATA)
                .from(USER_SUMMARY_TREND)
                .where(USER_SUMMARY_TREND.REF_DATE.eq(new java.sql.Date(Util.getEarlyDate(new Date(),0).getTime())))
                .and(USER_SUMMARY_TREND.TYPE.eq((byte)1))
                .fetchInto(Integer.class);
        List<Integer> todayPayUserNum = db().select(DSL.countDistinct(OrderInfo.ORDER_INFO.USER_ID))
                .from(OrderInfo.ORDER_INFO)
                .where(OrderInfo.ORDER_INFO.CREATE_TIME.greaterOrEqual(Util.getStartToday(new Date())))
                .and(OrderInfo.ORDER_INFO.ORDER_STATUS.greaterOrEqual((byte)3))
                .and(OrderInfo.ORDER_INFO.IS_COD.eq((byte)0)
                        .or(OrderInfo.ORDER_INFO.IS_COD.eq((byte)1)
                                .and(OrderInfo.ORDER_INFO.SHIPPING_TIME.isNotNull())))
                .fetchInto(Integer.class);
        realTimeVo.setPayUserNum(new Tuple2<>(Util.isEmpty(todayPayUserNum) ? 0 : todayPayUserNum.get(0),Util.isEmpty(yesPayUserNum) ? 0 : yesPayUserNum.get(0)));
        /* uv */
        List<Integer> uvToday = db().select(DSL.countDistinct(USER_LOGIN_RECORD.USER_ID))
                .from(USER_LOGIN_RECORD)
                .where(USER_LOGIN_RECORD.CREATE_TIME.greaterOrEqual(Util.getStartToday(new Date())))
                .fetchInto(Integer.class);
        List<Integer> uvYesterday = db().select(DSL.countDistinct(USER_LOGIN_RECORD.USER_ID))
                .from(USER_LOGIN_RECORD)
                .where(USER_LOGIN_RECORD.CREATE_TIME.greaterOrEqual(Util.getEarlyTimeStamp(new Date(),-1)))
                .and(USER_LOGIN_RECORD.CREATE_TIME.lessThan(Util.getStartToday(new Date())))
                .fetchInto(Integer.class);
        realTimeVo.setVisitUsers(new Tuple2<>(Util.isEmpty(uvToday) ? 0 : uvToday.get(0),Util.isEmpty(uvYesterday) ? 0 : uvYesterday.get(0)));
        /* pv  pay_order_num  第一行昨天，第二行今天，按时间排序
          * select sum(pv),SUM(pay_order_num) from b2c_trades GROUP BY ref_date HAVING ref_date = '2018-09-11' OR ref_date = '2018-09-12'
          */
        List<Record2<BigDecimal,BigDecimal>> record2 = db().select(DSL.sum(Trades.TRADES.PV),DSL.sum(Trades.TRADES.PAY_ORDER_NUM))
                .from(Trades.TRADES)
                .groupBy(Trades.TRADES.REF_DATE)
                .having(Trades.TRADES.REF_DATE.eq(new java.sql.Date(Util.getEarlyDate(new Date(),0).getTime()))
                        .or(Trades.TRADES.REF_DATE.eq(new java.sql.Date(Util.getEarlyDate(new Date(),-1).getTime()))))
                .fetch();
        if(!Util.isEmpty(record2)){
            realTimeVo.setPageViews(new Tuple2<>(record2.get(1).value1().longValue(),record2.get(0).value1().longValue()));
            realTimeVo.setPayOrderNum(new Tuple2<>(record2.get(1).value2().intValue(),record2.get(0).value2().intValue()));
        }
        return realTimeVo;
    }
    private List<Tuple2<Byte,Double>> cumulativeSum(List<Record2<Byte,BigDecimal>> trades){
        double sum = 0;
        byte hour = 0;
        List<Tuple2<Byte,Double>> result = new ArrayList<>(24);
        for(Record2<Byte,BigDecimal> record2 : trades){
            byte tempHour = Byte.valueOf(record2.get(0).toString());
            double tempMoney = Double.valueOf(record2.get(1).toString());
            if (tempHour == hour){
                sum += tempMoney;
                result.add(new Tuple2<>(hour,sum));
                hour++;
            }else{
                while (hour <= tempHour){
                    result.add(new Tuple2<>(hour,sum));
                    hour++;
                }
            }
        }
        return result;
    }

    /**
     * 核心指标
     */
    public CoreIndicatorVo coreIndicator(CoreIndicatorParam param){
        UserSummaryTrend us = UserSummaryTrend.USER_SUMMARY_TREND.as("us");

        Condition one = us.REF_DATE.eq(Util.getEarlySqlDate(new Date(),0)).and(us.TYPE.eq(param.getScreeningTime()));
        Condition preOne = us.REF_DATE.eq(Util.getEarlySqlDate(new Date(),-(param.getScreeningTime()))).and(us.TYPE.eq(param.getScreeningTime()));

        Optional<CoreIndicatorVo> optional = getConditionSelect().and(one).fetchOptionalInto(CoreIndicatorVo.class);
        CoreIndicatorVo indicatorVo = optional.orElse(new CoreIndicatorVo());
        Optional<LineChartVo> optionalTemp = getConditionSelect().and(preOne).fetchOptionalInto(LineChartVo.class);
        LineChartVo temp = optionalTemp.orElse(new LineChartVo());
        /* 计算较上一周期的增长率 */
        indicatorVo.setPvIncr(div(temp.getPv(),indicatorVo.getPv()-temp.getPv()));
        indicatorVo.setUvIncr(div(temp.getUv(),indicatorVo.getUv()-temp.getUv()));
        indicatorVo.setPayOrderNumIncr(div(temp.getPayOrderNum(),indicatorVo.getPayOrderNum()-temp.getPayOrderNum()));
        indicatorVo.setPayUserNumIncr(div(temp.getPayUserNum(),indicatorVo.getPayUserNum()-temp.getPayUserNum()));
        indicatorVo.setTotalPaidMoneyIncr(div(temp.getTotalPaidMoney(),indicatorVo.getTotalPaidMoney()-temp.getTotalPaidMoney()));
        indicatorVo.setUv2PaidIncr(div(temp.getUv2Paid(),indicatorVo.getUv2Paid()-temp.getUv2Paid()));
        indicatorVo.setPctIncr(div(temp.getPct(),indicatorVo.getPct()-temp.getPct()));
        /* 获取折线图数据 */
        indicatorVo.setLineChartVos(getConditionSelect().and(us.REF_DATE.le(Util.getEarlySqlDate(new Date(),0)))
                .and(us.REF_DATE.greaterThan(Util.getEarlySqlDate(new Date(),-param.getScreeningTime())))
                .and(us.TYPE.eq((byte)1)).fetchInto(LineChartVo.class));
        indicatorVo.getLineChartVos().forEach(l->{
            l.setDate(Util.getEarlyDate(l.getDate(),-1));
        });
        return indicatorVo;
    }

    private SelectConditionStep<? extends Record> getConditionSelect(){
        UserSummaryTrend us = UserSummaryTrend.USER_SUMMARY_TREND.as("us");
        return db().select(us.REF_DATE.as("date")
                ,us.LOGIN_DATA.as("uv")
                ,us.LOGIN_PV.as("pv")
                ,us.ORDER_USER_DATA.as("payUserNum")
                ,us.TOTAL_PAID_MONEY.as("totalPaidMoney")
                ,us.PAY_ORDER_NUM.as("payOrderNum")
                ,cast(us.ORDER_USER_DATA.div(us.LOGIN_DATA),Double.class).as("uv2Paid")
                ,cast(us.TOTAL_PAID_MONEY.div(us.ORDER_USER_DATA),Double.class).as("pct"))
                .from(us)
                .where();
    }

    public static double div(Object divisor,Object divided){
        if(Objects.isNull(divisor)||Objects.isNull(divided)){
            return 0.0;
        }
        if(Double.valueOf(divisor.toString())==0.0||Double.valueOf(divided.toString())==0.0){
            return 0.0;
        }
        double result = Double.valueOf(divided.toString())/Double.valueOf(divisor.toString());
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

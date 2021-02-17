package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.overview.useranalysis.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.meidianyi.shop.db.shop.Tables.USER_RFM_SUMMARY;
import static com.meidianyi.shop.db.shop.Tables.USER_SUMMARY_TREND;

/**
 * 用户统计模块
 *
 * @author liangchen
 * @date 2019年7月18日
 */
@Service
public class UserAnalysisService extends ShopBaseService {

  /**
   * 查询客户概况及趋势
   *
   * @param param 开始日期和结束日期
   * @return 单天数据/总数据/数据变化率
   */
  public TrendVo getTrend(DateParam param) {
    // 得到两个结束日期
    getPastTime(param);
    // 得到上一个时间段的数据
    TrendTotalVo beforeVo =
        db().select(
                USER_SUMMARY_TREND.USER_DATA,
                USER_SUMMARY_TREND.LOGIN_DATA,
                USER_SUMMARY_TREND.ORDER_USER_DATA)
            .from(USER_SUMMARY_TREND)
            .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getOldTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
            .fetchOptionalInto(TrendTotalVo.class)
            .orElse(new TrendTotalVo());
    // 得到下一个时间段的数据
    TrendTotalVo afterVo =
        db().select(
                USER_SUMMARY_TREND.USER_DATA,
                USER_SUMMARY_TREND.LOGIN_DATA,
                USER_SUMMARY_TREND.ORDER_USER_DATA)
            .from(USER_SUMMARY_TREND)
            .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getNewTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
            .fetchOptionalInto(TrendTotalVo.class)
            .orElse(new TrendTotalVo());
    // 得到每天的数据
    List<TrendDailyVo> trendDailyVo =
        db().select(
                USER_SUMMARY_TREND.REF_DATE,
                USER_SUMMARY_TREND.LOGIN_DATA,
                USER_SUMMARY_TREND.USER_DATA,
                USER_SUMMARY_TREND.ORDER_USER_DATA)
            .from(USER_SUMMARY_TREND)
            .where(
                USER_SUMMARY_TREND.REF_DATE.greaterThan(new Date(param.getOldTime().getTime())))
            .and(USER_SUMMARY_TREND.REF_DATE.lessOrEqual(new Date(param.getNewTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(NumberUtils.BYTE_ONE))
            .orderBy(USER_SUMMARY_TREND.REF_DATE.asc())
            .fetchInto(TrendDailyVo.class);
//    trendDailyVo.remove(0);
    // 得到变化率
    Double loginDataRate = getChangeRate(beforeVo.getLoginData(), afterVo.getLoginData());
    Double userDataRate = getChangeRate(beforeVo.getUserData(), afterVo.getUserData());
    Double orderUserDataRate =
        getChangeRate(beforeVo.getOrderUserData(), afterVo.getOrderUserData());
    // 返回出参接收数据
    return new TrendVo() {
      {
        setTrendDailyVo(trendDailyVo);
        // 设置总数
        setLoginDataTotal(afterVo.getLoginData());
        setUserDataTotal(afterVo.getUserData());
        setOrderUserDataTotal(afterVo.getOrderUserData());
        // 设置变化率
        setLoginDataRate(loginDataRate);
        setUserDataRate(userDataRate);
        setOrderUserDataRate(orderUserDataRate);
        setStartTime(showStartTime(param.getType()));
        setEndTime(showEndTime());
      }
    };
  }
  /**
   * 通用方法-得到开始日期
   *
   * @param type 最近N天
   * @return 对应日期(String型)
   */
  private String showStartTime(Byte type) {
    // 设置日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c = Calendar.getInstance();
    // Calendar.DATE=5 代表对日期操作，减去N，得到N天前
    c.add(Calendar.DATE, -type);
    // 转换成Date型
    java.util.Date time = c.getTime();
    // 转换并返回指定时间格式的String型时间
    return sdf.format(time);
  }
  /**
   * 通用方法-得到结束日期
   *
   * @return 对应日期(String型)
   */
  private String showEndTime() {
    // 设置日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c = Calendar.getInstance();
    // 转换成Date型
    java.util.Date time = c.getTime();
    // 转换并返回指定时间格式的String型时间
    return sdf.format(time);
  }

  /**
   * 通用方法-得到N天前的日期
   *
   * @param type N天前
   * @return 对应日期(String型)
   */
  private String getNewDate(Byte type) {
    // 设置日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Calendar c = Calendar.getInstance();
    // Calendar.DATE=5 代表对日期操作，减去N，得到N天前
    c.add(Calendar.DATE, 0);
    // 转换成Date型
    java.util.Date time = c.getTime();
    // 转换并返回指定时间格式的String型时间
    return sdf.format(time);
  }

  /**
   * 通用方法-得到上一个N天前的日期
   *
   * @param type N天前
   * @return 对应日期(String型)
   */
  private String getOldDate(Byte type) {
    // 设置日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Calendar c = Calendar.getInstance();
    // Calendar.DATE=5 代表对日期操作，减去N，得到N天前
    c.add(Calendar.DATE, -(type));
    // 转换成Date型
    java.util.Date time = c.getTime();
    // 转换并返回指定时间格式的String型时间
    return sdf.format(time);
  }
  /**
   * 通用方法-得到两段时间的结束时间
   *
   * @param param 段时间
   */
  private void getPastTime(DateParam param) {
    try {
      String oldTimeString = getOldDate(param.getType());
      String newTimeString = getNewDate(param.getType());
      // 将String日期转为yyyyMMdd格式的Date型日期
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      // 将设置好的Date型日期放入param，进行db操作
      param.setOldTime(sdf.parse(oldTimeString));
      param.setNewTime(sdf.parse(newTimeString));
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * 计算数据变化率
   *
   * @param a 旧数据
   * @param b 新数据
   * @return 变化率
   */
  @SuppressWarnings("deprecation")
  private Double getChangeRate(Integer a, Integer b) {
    if (a == null || b == null) {
      // 数据为空，结果为null
      return null;
    } else {
      // 除数为0，令结果为null
      if (NumberUtils.INTEGER_ZERO.equals(a)) {
        return null;
      } else {
        // 公式计算结果
        return (double) Math.round((double) (b - a) / (double) a * 10000) / 10000;
      }
    }
  }
  /**
   * Double型计算数据变化率
   *
   * @param a 旧数据
   * @param b 新数据
   * @return 变化率
   */
  @SuppressWarnings("deprecation")
  private Double getChangeRate(Double a, Double b) {
    if (a == null || b == null) {
      // 数据为空，结果为null
      return null;
    } else {
      // 除数为0，令结果为null
      if (NumberUtils.DOUBLE_ZERO.equals(a)) {
        return null;
      } else {
        // 公式计算结果
        return (double) Math.round((b - a) / a * 10000) / 10000;
      }
    }
  }
  /**
   * bigdecimal型计算数据变化率
   *
   * @param a 旧数据
   * @param b 新数据
   * @return 变化率
   */
  @SuppressWarnings("deprecation")
  private Double getChangeRate(BigDecimal a, BigDecimal b) {
    if (a == null || b == null) {
      // 数据为空，结果为null
      return null;
    } else {
      // 除数为0，令结果为null
      if (BigDecimal.ZERO.compareTo(a) == 0) {
        return null;
      } else {
        // 公式计算结果
        Double c = (b.subtract(a)).divide(a, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return c;
      }
    }
  }

  /**
   * 用户活跃
   *
   * @param param 查看最近N天的数据(默认N=1) 对应时间段的开始日期和结束日期
   * @return 各类型会员数量及占比
   */
  public ActiveTotalVo getActive(DateParam param) {
    // 得到两个结束日期
    getPastTime(param);
    // 累加得到不同类型会员总数
    ActiveTotalVo totalVo =
        db().select(
                USER_SUMMARY_TREND.LOGIN_DATA,
                USER_SUMMARY_TREND.COUPON_DATA,
                USER_SUMMARY_TREND.CART_DATA,
                USER_SUMMARY_TREND.ORDER_USER_DATA,
                USER_SUMMARY_TREND.USER_DATA)
            .from(USER_SUMMARY_TREND)
            .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getNewTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
            .fetchOneInto(ActiveTotalVo.class);
    // 得到每日数据
    List<ActiveDailyVo> activeDailyVo =
        db().select(
                USER_SUMMARY_TREND.REF_DATE,
                USER_SUMMARY_TREND.LOGIN_DATA,
                USER_SUMMARY_TREND.COUPON_DATA,
                USER_SUMMARY_TREND.CART_DATA,
                USER_SUMMARY_TREND.ORDER_USER_DATA)
            .from(USER_SUMMARY_TREND)
            .where(
                USER_SUMMARY_TREND.REF_DATE.between(
                    new Date(param.getOldTime().getTime()), new Date(param.getNewTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(NumberUtils.BYTE_ONE))
            .fetchInto(ActiveDailyVo.class);
    activeDailyVo.remove(0);
    totalVo.setActiveDailyVo(activeDailyVo);
    // 计算占比
    // 会员数占比
    Double loginDataRate = getRate(totalVo.getLoginData(), totalVo.getUserData());
    totalVo.setLoginDataRate(loginDataRate);
    // 领券会员数占比
    Double couponDataRate = getRate(totalVo.getCouponData(), totalVo.getLoginData());
    totalVo.setCouponDataRate(couponDataRate);
    // 加购会员数占比
    Double cartDataRate = getRate(totalVo.getCartData(), totalVo.getLoginData());
    totalVo.setCartDataRate(cartDataRate);
    // 成交会员数占比
    Double orderUserDataRate = getRate(totalVo.getOrderUserData(), totalVo.getLoginData());
    totalVo.setOrderUserDataRate(orderUserDataRate);
    // 设置时间
    totalVo.setStartTime(showStartTime(param.getType()));
    totalVo.setEndTime(showEndTime());
    // 返回结果
    return totalVo;
  }

  /**
   * 计算数据占比
   *
   * @param a 被除数
   * @param b 除数
   * @return 占比（4位小数形式）
   */
  @SuppressWarnings("deprecation")
  private Double getRate(Integer a, Integer b) {
    // 数据为空时令返回结果为0
    if (a == null || b == null) {
      return null;
    }
    // 除数为0时令返回结果为null
    else if (b.equals(NumberUtils.INTEGER_ZERO)) {
      return null;
    } else {
      // 公式计算结果
      return (double) Math.round(((double) a / (double) b * 10000)) / 10000;
    }
  }
  /**
   * 会员统计
   *
   * @param param 起止时间
   * @return 数据和变化率
   */
  public VipVo getVip(DateParam param) {
    // 得到两个结束日期
    getPastTime(param);
    // 得到上一段时间的数据
    VipVo beforeVo =
        db().select(
                USER_SUMMARY_TREND.USER_DATA,
                USER_SUMMARY_TREND.REG_USER_DATA,
                USER_SUMMARY_TREND.UPGRADE_USER_DATA,
                USER_SUMMARY_TREND.CHARGE_USER_DATA)
            .from(USER_SUMMARY_TREND)
            .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getOldTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
            .fetchOneInto(VipVo.class);
    // 得到后一段时间的数据
    VipVo afterVo =
        db().select(
                USER_SUMMARY_TREND.USER_DATA,
                USER_SUMMARY_TREND.REG_USER_DATA,
                USER_SUMMARY_TREND.UPGRADE_USER_DATA,
                USER_SUMMARY_TREND.CHARGE_USER_DATA)
            .from(USER_SUMMARY_TREND)
            .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getNewTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
            .fetchOneInto(VipVo.class);
    // 计算变化率
    // 累积会员数变化率
    Double userDataRate = getChangeRate(beforeVo.getUserData(), afterVo.getUserData());
    afterVo.setUserDataRate(userDataRate);
    // 新增会员数变化率
    Double regUserDataRate = getChangeRate(beforeVo.getRegUserData(), afterVo.getRegUserData());
    afterVo.setRegUserDataRate(regUserDataRate);
    // 升级会员数变化率
    Double upgradeUserDataRate =
        getChangeRate(beforeVo.getUpgradeUserData(), afterVo.getUpgradeUserData());
    afterVo.setUpgradeUserDataRate(upgradeUserDataRate);
    // 储值会员数变化率
    Double chargeUserDataRate =
        getChangeRate(beforeVo.getChargeUserData(), afterVo.getChargeUserData());
    afterVo.setChargeUserDataRate(chargeUserDataRate);
    // 设置时间
    afterVo.setStartTime(showStartTime(param.getType()));
    afterVo.setEndTime(showEndTime());
    // 返回结果
    return afterVo;
  }

  /**
   * 成交用户分析
   *
   * @param param 起止时间
   * @return List<OverviewUserAnalysisOrderVo>
   */
  public OrderVo getOrder(DateParam param) {

    // 得到两个结束日期
    getPastTime(param);
    // 计算上一段时间数据
    OrderTotalVo beforeVo =
        db().select(
                USER_SUMMARY_TREND.USER_DATA,
                USER_SUMMARY_TREND.LOGIN_DATA,
                USER_SUMMARY_TREND.ORDER_USER_DATA,
                USER_SUMMARY_TREND.OLD_ORDER_USER_DATA,
                USER_SUMMARY_TREND.NEW_ORDER_USER_DATA,
                USER_SUMMARY_TREND.TOTAL_PAID_MONEY,
                USER_SUMMARY_TREND.OLD_PAID_MONEY,
                USER_SUMMARY_TREND.NEW_PAID_MONEY)
            .from(USER_SUMMARY_TREND)
            .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getOldTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
            .fetchOneInto(OrderTotalVo.class);
    // 计算客户数占比
    // 全部成交客户占比
    beforeVo.setOrderUserDataRate(getRate(beforeVo.getOrderUserData(), beforeVo.getUserData()));
    // 新成交占比
    beforeVo.setNewOrderUserDataRate(
        getRate(beforeVo.getNewOrderUserData(), beforeVo.getOrderUserData()));
    // 老成交占比
    beforeVo.setOldOrderUserDataRate(
        getRate(beforeVo.getOldOrderUserData(), beforeVo.getOrderUserData()));
    // 计算客单价
    // 全部客户单价
    beforeVo.setUnitPrice(getUnitPrice(beforeVo.getTotalPaidMoney(), beforeVo.getOrderUserData()));
    // 新客户单价
    beforeVo.setNewUnitPrice(
        getUnitPrice(beforeVo.getNewPaidMoney(), beforeVo.getNewOrderUserData()));
    // 老客户单价
    beforeVo.setOldUnitPrice(
        getUnitPrice(beforeVo.getOldPaidMoney(), beforeVo.getOldOrderUserData()));
    // 计算访问付款转化率
    // 全部成交客户转化率
    beforeVo.setTransRate(getRate(beforeVo.getOrderUserData(), beforeVo.getLoginData()));
    // 新成交转化率
    beforeVo.setNewTransRate(getRate(beforeVo.getNewOrderUserData(), beforeVo.getLoginData()));
    // 老成交转化率
    beforeVo.setOldTransRate(getRate(beforeVo.getOldOrderUserData(), beforeVo.getLoginData()));

    // 计算下一段时间数据
    OrderTotalVo afterVo =
        db().select(
                USER_SUMMARY_TREND.USER_DATA,
                USER_SUMMARY_TREND.LOGIN_DATA,
                USER_SUMMARY_TREND.ORDER_USER_DATA,
                USER_SUMMARY_TREND.OLD_ORDER_USER_DATA,
                USER_SUMMARY_TREND.NEW_ORDER_USER_DATA,
                USER_SUMMARY_TREND.TOTAL_PAID_MONEY,
                USER_SUMMARY_TREND.OLD_PAID_MONEY,
                USER_SUMMARY_TREND.NEW_PAID_MONEY)
            .from(USER_SUMMARY_TREND)
            .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getNewTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
            .fetchOneInto(OrderTotalVo.class);
    // 计算客户数占比
    // 全部成交客户占比
    afterVo.setOrderUserDataRate(getRate(afterVo.getOrderUserData(), afterVo.getUserData()));
    // 新成交占比
    afterVo.setNewOrderUserDataRate(
        getRate(afterVo.getNewOrderUserData(), afterVo.getOrderUserData()));
    // 老成交占比
    afterVo.setOldOrderUserDataRate(
        getRate(afterVo.getOldOrderUserData(), afterVo.getOrderUserData()));
    // 计算客单价
    // 全部客户单价
    afterVo.setUnitPrice(getUnitPrice(afterVo.getTotalPaidMoney(), afterVo.getOrderUserData()));
    // 新客户单价
    afterVo.setNewUnitPrice(getUnitPrice(afterVo.getNewPaidMoney(), afterVo.getNewOrderUserData()));
    // 老客户单价
    afterVo.setOldUnitPrice(getUnitPrice(afterVo.getOldPaidMoney(), afterVo.getOldOrderUserData()));
    // 计算访问付款转化率
    // 全部成交客户转化率
    afterVo.setTransRate(getRate(afterVo.getOrderUserData(), afterVo.getLoginData()));
    // 新成交转化率
    afterVo.setNewTransRate(getRate(afterVo.getNewOrderUserData(), afterVo.getLoginData()));
    // 老成交转化率
    afterVo.setOldTransRate(getRate(afterVo.getOldOrderUserData(), afterVo.getLoginData()));

    // 计算变化率
    OrderChangeRateVo changeRateVo =
        new OrderChangeRateVo() {
          {
            // 客户数
            setOrderUserDataTrend(
                getChangeRate(beforeVo.getOrderUserData(), afterVo.getOrderUserData()));
            setNewOrderUserDataTrend(
                getChangeRate(beforeVo.getNewOrderUserData(), afterVo.getNewOrderUserData()));
            setOldOrderUserDataTrend(
                getChangeRate(beforeVo.getOldOrderUserData(), afterVo.getOldOrderUserData()));
            // 客户数占比
            setOrderUserDataRateTrend(
                getChangeRate(beforeVo.getOrderUserDataRate(), afterVo.getOrderUserDataRate()));
            setNewOrderUserDataRateTrend(
                getChangeRate(
                    beforeVo.getNewOrderUserDataRate(), afterVo.getNewOrderUserDataRate()));
            setOldOrderUserDataRateTrend(
                getChangeRate(
                    beforeVo.getOldOrderUserDataRate(), afterVo.getOldOrderUserDataRate()));
            // 客单价
            setUnitPriceTrend(getChangeRate(beforeVo.getUnitPrice(), afterVo.getUnitPrice()));
            setNewUnitPriceTrend(
                getChangeRate(beforeVo.getNewUnitPrice(), afterVo.getNewUnitPrice()));
            setOldUnitPriceTrend(
                getChangeRate(beforeVo.getOldUnitPrice(), afterVo.getOldUnitPrice()));
            // 付款金额
            setTotalPaidMoneyTrend(
                getChangeRate(beforeVo.getTotalPaidMoney(), afterVo.getTotalPaidMoney()));
            setNewPaidMoneyTrend(
                getChangeRate(beforeVo.getNewPaidMoney(), afterVo.getNewPaidMoney()));
            setOldPaidMoneyTrend(
                getChangeRate(beforeVo.getOldPaidMoney(), afterVo.getOldPaidMoney()));
            // 转化率
            setTransRateTrend(getChangeRate(beforeVo.getTransRate(), afterVo.getTransRate()));
            setNewTransRateTrend(
                getChangeRate(beforeVo.getNewTransRate(), afterVo.getNewTransRate()));
            setOldTransRateTrend(
                getChangeRate(beforeVo.getOldTransRate(), afterVo.getOldTransRate()));
          }
        };
    // 新成交客户数每日数据
    List<OrderDailyVo> newVo =
        db().select(
                USER_SUMMARY_TREND.REF_DATE,
                USER_SUMMARY_TREND.NEW_ORDER_USER_DATA.as("order_user_data"),
                USER_SUMMARY_TREND.NEW_PAID_MONEY.as("paid_money"),
                USER_SUMMARY_TREND.LOGIN_DATA)
            .from(USER_SUMMARY_TREND)
            .where(
                USER_SUMMARY_TREND.REF_DATE.between(
                    new Date(param.getOldTime().getTime()), new Date(param.getNewTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(NumberUtils.BYTE_ONE))
            .fetchInto(OrderDailyVo.class);
    newVo.remove(0);
    Iterator<OrderDailyVo> tempNewVo = newVo.iterator();
    while (tempNewVo.hasNext()) {
      OrderDailyVo newDailyVo = tempNewVo.next();
      // 计算客单价
      BigDecimal unitPrice = getUnitPrice(newDailyVo.getPaidMoney(), newDailyVo.getOrderUserData());
      newDailyVo.setUnitPrice(unitPrice);
      // 计算转化率
      Double transRate = getRate(newDailyVo.getOrderUserData(), newDailyVo.getLoginData());
      newDailyVo.setTransRate(transRate);
    }
    // 老成交客户数每日数据
    List<OrderDailyVo> oldVo =
        db().select(
                USER_SUMMARY_TREND.REF_DATE,
                USER_SUMMARY_TREND.OLD_ORDER_USER_DATA.as("order_user_data"),
                USER_SUMMARY_TREND.OLD_PAID_MONEY.as("paid_money"),
                USER_SUMMARY_TREND.LOGIN_DATA)
            .from(USER_SUMMARY_TREND)
            .where(
                USER_SUMMARY_TREND.REF_DATE.between(
                    new Date(param.getOldTime().getTime()), new Date(param.getNewTime().getTime())))
            .and(USER_SUMMARY_TREND.TYPE.eq(NumberUtils.BYTE_ONE))
            .fetchInto(OrderDailyVo.class);
    oldVo.remove(0);
    Iterator<OrderDailyVo> tempOldVo = oldVo.iterator();
    while (tempOldVo.hasNext()) {
      OrderDailyVo oldDailyVo = tempOldVo.next();
      // 计算客单价
      BigDecimal unitPrice = getUnitPrice(oldDailyVo.getPaidMoney(), oldDailyVo.getOrderUserData());
      oldDailyVo.setUnitPrice(unitPrice);
      // 计算转化率
      Double transRate = getRate(oldDailyVo.getOrderUserData(), oldDailyVo.getLoginData());
      oldDailyVo.setTransRate(transRate);
    }
    // 最终出参
    OrderVo orderVo =
        new OrderVo() {
          {
            setDailyOldVo(oldVo);
            setDailyNewVo(newVo);
            setDataVo(afterVo);
            setChangeRateVo(changeRateVo);
            setStartTime(showStartTime(param.getType()));
            setEndTime(showEndTime());
          }
        };
    return orderVo;
  }

  /**
   * 计算单价
   *
   * @param a 付款金额
   * @param b 客户人数
   * @return 单价（2位小数形式）
   */
  @SuppressWarnings("deprecation")
  private BigDecimal getUnitPrice(BigDecimal a, Integer b) {
    // 数据为空时令返回结果为null
    if (a == null || b == null) {
      return null;
    }
    // 除数为0时令返回结果为null
    else if (b.equals(NumberUtils.INTEGER_ZERO)) {
      return null;
    } else {
      return a.divide(BigDecimal.valueOf(b), 2, BigDecimal.ROUND_HALF_UP);
    }
  }

  /**
   * 客户复购趋势
   *
   * @param param 周数和周日时间
   * @return 四周复购率
   */
  public RebuyVo getRebuyTrend(RebuyParam param) {
    try {
        if(null==param.getWeekNum()||StringUtils.isBlank(param.getSunday())){
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            param.setWeekNum(weekOfYear);
            int weekYear = calendar.get(Calendar.YEAR);
            calendar.setWeekDate(weekYear,weekOfYear,1);
            long endTime = calendar.getTime().getTime();
            java.util.Date date = new java.util.Date(endTime);
            SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd");
            String sundayDate = df.format(date);
            param.setSunday(sundayDate);
        }
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      // 周日转date型
      java.util.Date sunday = dateFormat.parse(param.getSunday());
      param.setFourthDate(sunday);

      // 得到前三周时间（date型）
      param.setThirdDate(
          new java.util.Date(param.getFourthDate().getTime() - (1000 * 60 * 60 * 24 * 7)));
      param.setSecondDate(
          new java.util.Date(param.getThirdDate().getTime() - (1000 * 60 * 60 * 24 * 7)));
      param.setFirstDate(
          new java.util.Date(param.getSecondDate().getTime() - (1000 * 60 * 60 * 24 * 7)));

      RebuyWeekVo fourthVo = new RebuyWeekVo();
      RebuyWeekVo thirdVo = new RebuyWeekVo();
      RebuyWeekVo secondVo = new RebuyWeekVo();
      RebuyWeekVo firstVo = new RebuyWeekVo();
      // 得到第四周的数据
      fourthVo =
          db().select(USER_SUMMARY_TREND.OLD_ORDER_USER_DATA, USER_SUMMARY_TREND.ORDER_USER_DATA)
              .from(USER_SUMMARY_TREND)
              .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getFourthDate().getTime())))
              .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
              .fetchOptionalInto(RebuyWeekVo.class)
              .orElse(fourthVo);
      // 得到当前周
      fourthVo.setWeekNum(param.getWeekNum());
      // 得到当前复购率
      fourthVo.setRebuyRate(getRate(fourthVo.getOldOrderUserData(), fourthVo.getOrderUserData()));
      // 得到时间
      fourthVo.setStartTime(
          dateFormat.format(param.getFourthDate().getTime() - (1000 * 60 * 60 * 24 * 6)));
      fourthVo.setEndTime(param.getSunday());
      // 得到第三周的数据
      thirdVo =
          db().select(USER_SUMMARY_TREND.OLD_ORDER_USER_DATA, USER_SUMMARY_TREND.ORDER_USER_DATA)
              .from(USER_SUMMARY_TREND)
              .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getThirdDate().getTime())))
              .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
              .fetchOptionalInto(RebuyWeekVo.class)
              .orElse(thirdVo);
      // 得到当前周
      thirdVo.setWeekNum(param.getWeekNum() - NumberUtils.INTEGER_ONE);
      // 得到当前复购率
      thirdVo.setRebuyRate(getRate(thirdVo.getOldOrderUserData(), thirdVo.getOrderUserData()));
      // 得到时间
      thirdVo.setStartTime(
          dateFormat.format(param.getThirdDate().getTime() - (1000 * 60 * 60 * 24 * 6)));
      thirdVo.setEndTime(dateFormat.format(param.getThirdDate()));
      // 得到第二周的数据
      secondVo =
          db().select(USER_SUMMARY_TREND.OLD_ORDER_USER_DATA, USER_SUMMARY_TREND.ORDER_USER_DATA)
              .from(USER_SUMMARY_TREND)
              .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getSecondDate().getTime())))
              .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
              .fetchOptionalInto(RebuyWeekVo.class)
              .orElse(secondVo);
      // 得到当前周
      secondVo.setWeekNum(thirdVo.getWeekNum() - NumberUtils.INTEGER_ONE);
      // 得到当前复购率
      secondVo.setRebuyRate(getRate(secondVo.getOldOrderUserData(), secondVo.getOrderUserData()));
      // 得到时间
      secondVo.setStartTime(
          dateFormat.format(param.getSecondDate().getTime() - (1000 * 60 * 60 * 24 * 6)));
      secondVo.setEndTime(dateFormat.format(param.getSecondDate()));
      // 得到第一周的数据
      firstVo =
          db().select(USER_SUMMARY_TREND.OLD_ORDER_USER_DATA, USER_SUMMARY_TREND.ORDER_USER_DATA)
              .from(USER_SUMMARY_TREND)
              .where(USER_SUMMARY_TREND.REF_DATE.eq(new Date(param.getFirstDate().getTime())))
              .and(USER_SUMMARY_TREND.TYPE.eq(param.getType()))
              .fetchOptionalInto(RebuyWeekVo.class)
              .orElse(firstVo);
      // 得到当前周
      firstVo.setWeekNum(secondVo.getWeekNum() - NumberUtils.INTEGER_ONE);
      // 得到当前复购率
      firstVo.setRebuyRate(getRate(firstVo.getOldOrderUserData(), firstVo.getOrderUserData()));
      // 得到时间
      firstVo.setStartTime(
          dateFormat.format(param.getFirstDate().getTime() - (1000 * 60 * 60 * 24 * 6)));
      firstVo.setEndTime(dateFormat.format(param.getFirstDate()));

      // 整合在一个集合里
      List<RebuyWeekVo> rebuyWeekVo = new ArrayList<>();
      rebuyWeekVo.add(firstVo);
      rebuyWeekVo.add(secondVo);
      rebuyWeekVo.add(thirdVo);
      rebuyWeekVo.add(fourthVo);
      rebuyWeekVo.forEach(r->{
          r.setXAxis("第"+r.getWeekNum()+"周"+"("+r.getStartTime().substring(5,10)+"~"+r.getEndTime().substring(5,10)+")");
      });
      // 返回集合
      RebuyVo rebuyVo =
          new RebuyVo() {
            {
              setRebuyWeekVo(rebuyWeekVo);
            }
          };
      return rebuyVo;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
  /**
   * RFM数据判断
   *
   * @param param 时间
   * @return 是否有数据 true or false
   */
  public Boolean getRFMData(RfmParam param) {
    try {
      // 格式化日期格式
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      if (StringUtils.isBlank(param.getRefDate())) {
        Calendar calendar = Calendar.getInstance();
        param.setRefDate(sdf.format(calendar.getTime()));
      }
      java.util.Date refDate = sdf.parse(param.getRefDate());
      // 判断当前日期是否有数据
      Integer num =
          db().select(DSL.count(USER_RFM_SUMMARY.ID))
              .from(USER_RFM_SUMMARY)
              .where(USER_RFM_SUMMARY.REF_DATE.eq(new Date(refDate.getTime())))
              .fetchOneInto(Integer.class);
      if (num.equals(NumberUtils.INTEGER_ZERO)) {
        return false;
      } else {
        return true;
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
  /** RFM-消费时间类型 */
  public static final Byte RECENCY_TYPE_SEVEN = 7;
  /** RFM-第六列 */
  public static final Byte COLUMN_TYPE = 6;
  /** RFM-第八行 */
  public static final Byte ROW_TYPE = 8;
  /** RFM-列 */
  public static final int COLUMN_MAX = 5;
  /** RFM-行 */
  public static final int ROW_MAX = 6;
  /**
   * RFM模型分析
   *
   * @param param 时间
   * @return 数据
   */
  public List<RfmVo> getRFMAnalysis(RfmParam param) {
    try {
      // 格式化日期格式
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      if (StringUtils.isBlank(param.getRefDate())) {
        Calendar calendar = Calendar.getInstance();
        param.setRefDate(sdf.format(calendar.getTime()));
      }
      java.util.Date refDate = sdf.parse(param.getRefDate());
      // 出参
      List<RfmVo> result = new ArrayList<>();
      // 先得到用户总数
      Integer userData =
          db().select(DSL.sum(USER_RFM_SUMMARY.PAY_USER_NUM))
              .from(USER_RFM_SUMMARY)
              .where(USER_RFM_SUMMARY.REF_DATE.eq(new Date(refDate.getTime())))
              .fetchOptionalInto(Integer.class)
              .orElse(null);
      // 循环遍历不同消费时间
      for (Byte i = NumberUtils.BYTE_ONE; i <= RECENCY_TYPE_SEVEN; i++) {
        // 查库
        List<RfmTableVo> tableVo =
            db().select(
                    USER_RFM_SUMMARY.FREQUENCY_TYPE,
                    USER_RFM_SUMMARY.TOTAL_PAID_MONEY,
                    USER_RFM_SUMMARY.PAY_USER_NUM,
                    USER_RFM_SUMMARY.ORDER_NUM)
                .from(USER_RFM_SUMMARY)
                .where(USER_RFM_SUMMARY.REF_DATE.eq(new Date(refDate.getTime())))
                .and(USER_RFM_SUMMARY.RECENCY_TYPE.eq(i))
                .fetchInto(RfmTableVo.class);
        // 数据处理
        List<RfmRowVo> rfmRowVo = new ArrayList<>();
        for (RfmTableVo tempTableVo : tableVo) {
          RfmRowVo tempRfmRowVo =
              new RfmRowVo() {
                {
                  setFrequencyType(tempTableVo.getFrequencyType());
                  setPayUserNum(tempTableVo.getPayUserNum());
                  setUserRate(getRate(tempTableVo.getPayUserNum(), userData));
                  setTotalPaidMoney(tempTableVo.getTotalPaidMoney());
                  setPrice(
                      getUnitPrice(tempTableVo.getTotalPaidMoney(), tempTableVo.getOrderNum()));
                }
              };
          rfmRowVo.add(tempRfmRowVo);
        }
        // 行总和
        Integer rowUserNum = 0;
        Double rowUserRate = 0.0000;
        BigDecimal rowMoney = BigDecimal.valueOf(0.00);
        BigDecimal rowPrice = BigDecimal.valueOf(0.00);
        for (RfmRowVo rowTotalVo : rfmRowVo) {
          rowUserNum += rowTotalVo.getPayUserNum();
          rowUserRate += rowTotalVo.getUserRate();
          rowMoney = rowMoney.add(rowTotalVo.getTotalPaidMoney());
          if (rowTotalVo.getPrice() != null) {
            rowPrice = rowPrice.add(rowTotalVo.getPrice());
          }
        }
        RfmRowVo rowTotalVo = new RfmRowVo();
        rowTotalVo.setFrequencyType(COLUMN_TYPE);
        rowTotalVo.setPayUserNum(rowUserNum);
        rowTotalVo.setUserRate(rowUserRate);
        rowTotalVo.setTotalPaidMoney(rowMoney);
        rowTotalVo.setPrice(rowPrice);
        rfmRowVo.add(rowTotalVo);
        // 当前行作为一个对象
        Byte finalI = i;
        RfmVo rfmVo =
            new RfmVo() {
              {
                setRecencyType(finalI);
                setRfmRowVo(rfmRowVo);
              }
            };
        result.add(rfmVo);
      }
      // 列总和
      RfmVo columnRfmVo = new RfmVo();

      List<RfmRowVo> columnRfmRowVo = new ArrayList<>();
      for (int j = NumberUtils.INTEGER_ZERO; j <= COLUMN_MAX; j++) {
        Integer columnUserNum = 0;
        Double columnUserRate = 0.0000;
        BigDecimal columnMoney = BigDecimal.valueOf(0.00);
        BigDecimal columnPrice = BigDecimal.valueOf(0.00);
        for (int k = NumberUtils.INTEGER_ZERO; k <= ROW_MAX; k++) {
          columnUserNum += result.get(k).getRfmRowVo().get(j).getPayUserNum();
          columnUserRate += result.get(k).getRfmRowVo().get(j).getUserRate();
          columnMoney = columnMoney.add(result.get(k).getRfmRowVo().get(j).getTotalPaidMoney());
          if (result.get(k).getRfmRowVo().get(j).getPrice() != null) {
            columnPrice = columnPrice.add(result.get(k).getRfmRowVo().get(j).getPrice());
          }
        }
        RfmRowVo columnTotalVo = new RfmRowVo();
        columnTotalVo.setFrequencyType((byte) (j + NumberUtils.INTEGER_ONE));
        columnTotalVo.setPayUserNum(columnUserNum);
        columnTotalVo.setUserRate(columnUserRate);
        columnTotalVo.setTotalPaidMoney(columnMoney);
        columnTotalVo.setPrice(columnPrice);
        columnRfmRowVo.add(columnTotalVo);
      }
      columnRfmVo.setRecencyType(ROW_TYPE);
      columnRfmVo.setRfmRowVo(columnRfmRowVo);

      result.add(columnRfmVo);
      return result;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
}

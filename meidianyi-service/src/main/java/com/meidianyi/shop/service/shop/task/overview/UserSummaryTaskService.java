package com.meidianyi.shop.service.shop.task.overview;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.*;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.db.shop.tables.Tag.TAG;
import static com.meidianyi.shop.db.shop.tables.UserTag.USER_TAG;
import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static com.meidianyi.shop.service.shop.task.overview.GoodsStatisticTaskService.*;
import static org.apache.commons.lang3.math.NumberUtils.*;

/**
 * The type User summary task service.
 *
 * @author liufei
 * @date 12 /11/19 用户相关统计信息
 */
@Service
public class UserSummaryTaskService extends ShopBaseService {
    /**
     * The constant LOGIN.
     */
    public static final UserLoginRecord LOGIN = UserLoginRecord.USER_LOGIN_RECORD.as("LOGIN");
    /**
     * The constant COUPONS.
     */
    public static final CustomerAvailCoupons COUPONS = CustomerAvailCoupons.CUSTOMER_AVAIL_COUPONS.as("COUPONS");
    /**
     * The constant UPGRADE.
     */
    public static final CardUpgrade UPGRADE = CardUpgrade.CARD_UPGRADE.as("UPGRADE");
    /**
     * The constant CHARGE.
     */
    public static final ChargeMoney CHARGE = ChargeMoney.CHARGE_MONEY.as("CHARGE");

    /**
     * Gets uv.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the uv
     */
    public Integer getUv(Timestamp startTime, Timestamp endTime) {
        Condition timeCondition = LOGIN.CREATE_TIME.ge(startTime).and(LOGIN.CREATE_TIME.lessThan(endTime));
        return db().select(DSL.countDistinct(LOGIN.USER_ID)).from(LOGIN).where(timeCondition).fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }

    /**
     * Gets pv.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the pv
     */
    public Integer getPv(Timestamp startTime, Timestamp endTime) {
        Condition timeCondition = LOGIN.CREATE_TIME.ge(startTime).and(LOGIN.CREATE_TIME.lessThan(endTime));
        return db().select(DSL.sum(LOGIN.COUNT)).from(LOGIN).where(timeCondition).fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }

    /**
     * Gets user num.新增用户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the user num
     */
    public Integer getUserNum(Timestamp startTime, Timestamp endTime) {
        Condition timeCondition = User.USER.CREATE_TIME.ge(startTime).and(User.USER.CREATE_TIME.lessThan(endTime));
        return db().fetchCount(User.USER, timeCondition);
    }

    /**
     * Gets user total.累积用户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the user total
     */
    public Integer getUserTotal(Timestamp startTime, Timestamp endTime) {
        Condition timeCondition = User.USER.CREATE_TIME.lessThan(endTime);
        return db().fetchCount(User.USER, timeCondition);
    }

    /**
     * Receive coupon user num integer.领券会员数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the integer
     */
    public Integer receiveCouponUserNum(Timestamp startTime, Timestamp endTime) {
        Condition timeCondition = COUPONS.CREATE_TIME.ge(startTime).and(COUPONS.CREATE_TIME.lessThan(endTime));
        return db().select(DSL.countDistinct(COUPONS.USER_ID)).from(COUPONS).where(timeCondition).fetchOneInto(Integer.class);
    }

    /**
     * Upgrade user sum integer.升级会员数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the integer
     */
    public Integer upgradeUserSum(Timestamp startTime, Timestamp endTime) {
        Condition timeCondition = UPGRADE.CREATE_TIME.ge(startTime).and(UPGRADE.CREATE_TIME.lessThan(endTime));
        return db().select(DSL.countDistinct(UPGRADE.USER_ID)).from(UPGRADE).where(timeCondition).fetchOneInto(Integer.class);
    }

    /**
     * Charge user sum integer.储值会员数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the integer
     */
    public Integer chargeUserSum(Timestamp startTime, Timestamp endTime) {
        Condition timeCondition = CHARGE.CREATE_TIME.ge(startTime).and(CHARGE.CREATE_TIME.lessThan(endTime))
            .and(CHARGE.TYPE.eq(BYTE_ZERO))
            .and(CHARGE.CHARGE.greaterThan(BIGDECIMAL_ZERO));
        return db().select(DSL.countDistinct(CHARGE.USER_ID)).from(CHARGE).where(timeCondition).fetchOneInto(Integer.class);
    }

    /**
     * Pay new user money big decimal.新用户成交金额(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the big decimal
     */
    public BigDecimal payNewUserMoney(Timestamp startTime, Timestamp endTime) {
        BigDecimal totalMoney = BIGDECIMAL_ZERO;
        Map<Integer, BigDecimal> userMoney = payUserMoney(startTime, endTime);
        // 拿到历史成交用户集合
        Set<Integer> users = payUserCollection(DateUtils.get1970TimeStamp(), startTime);
        for (Map.Entry<Integer, BigDecimal> entry : userMoney.entrySet()) {
            if (!users.contains(entry.getKey())) {
                totalMoney = totalMoney.add(entry.getValue());
            }
        }
        // 虚拟订单成交金额
        return totalMoney.add(virtualOrderMoney(startTime, endTime, true));
    }

    /**
     * Pay new user money big decimal.老用户成交金额(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the big decimal
     */
    public BigDecimal payOldUserMoney(Timestamp startTime, Timestamp endTime) {
        BigDecimal totalMoney = BIGDECIMAL_ZERO;
        Map<Integer, BigDecimal> userMoney = payUserMoney(startTime, endTime);
        // 拿到历史成交用户集合
        Set<Integer> users = payUserCollection(DateUtils.get1970TimeStamp(), startTime);
        for (Map.Entry<Integer, BigDecimal> entry : userMoney.entrySet()) {
            if (users.contains(entry.getKey())) {
                totalMoney = totalMoney.add(entry.getValue());
            }
        }
        // 虚拟订单会员卡订单成交金额
        return totalMoney.add(virtualOrderMoney(startTime, endTime, false));
    }

    /**
     * Function.虚拟订单成交金额
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @param flag      the flag（null总成交额，true新用户成交额，false旧用户成交额）
     */
    public BigDecimal virtualOrderMoney(Timestamp startTime, Timestamp endTime, Boolean flag) {
        BigDecimal totalMoney = BIGDECIMAL_ZERO;
        Map<Integer, BigDecimal> userMoney = virtualUserMoney(startTime, endTime);
        if (Objects.isNull(flag)) {
            // 总成交额
            return userMoney.values().stream().reduce(BIGDECIMAL_ZERO, BigDecimal::add);
        }
        // 拿到历史成交用户集合
        Set<Integer> users = virtualUserCollection(DateUtils.get1970TimeStamp(), startTime);
        Predicate<Integer> newOrOld = users::contains;
        if (flag) {
            newOrOld = newOrOld.negate();
        }
        for (Map.Entry<Integer, BigDecimal> entry : userMoney.entrySet()) {
            if (newOrOld.test(entry.getKey())) {
                totalMoney = totalMoney.add(entry.getValue());
            }
        }
        return totalMoney;
    }

    /**
     * Virtual user money map.指定时间内每一位用户(虚拟订单)成交金额
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, BigDecimal> virtualUserMoney(Timestamp startTime, Timestamp endTime) {
        Field<BigDecimal> field = DSL.sum(VIRTUAL_ORDER.MONEY_PAID).add(DSL.sum(VIRTUAL_ORDER.USE_ACCOUNT)).add(DSL.sum(VIRTUAL_ORDER.MEMBER_CARD_BALANCE));
        return db().select(VIRTUAL_ORDER.USER_ID, field)
            .from(VIRTUAL_ORDER).where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
            .and(VIRTUAL_ORDER.CREATE_TIME.ge(startTime)).and(VIRTUAL_ORDER.CREATE_TIME.lessThan(endTime))
            .groupBy(VIRTUAL_ORDER.USER_ID)
            .fetchMap(VIRTUAL_ORDER.USER_ID, field);
    }

    /**
     * Pay user money map.指定时间内每一位用户成交金额(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, BigDecimal> payUserMoney(Timestamp startTime, Timestamp endTime) {
        Field<BigDecimal> field = DSL.sum(ORDER_I.MONEY_PAID).add(DSL.sum(ORDER_I.USE_ACCOUNT)).add(DSL.sum(ORDER_I.MEMBER_CARD_BALANCE));
        return db().select(ORDER_I.USER_ID, field)
            .from(ORDER_I).where(STATUS_CONDITION)
            .and(ORDER_I.ORDER_SN.eq(ORDER_I.MAIN_ORDER_SN).or(ORDER_I.MAIN_ORDER_SN.eq(StringUtils.EMPTY)))
            .and(ORDER_I.CREATE_TIME.ge(startTime)).and(ORDER_I.CREATE_TIME.lessThan(endTime))
            .groupBy(ORDER_I.USER_ID)
            .fetchMap(ORDER_I.USER_ID, field);
    }

    /**
     * Pay user money map.指定时间内每一位用户成交件数(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Integer> payUserGoodsNum(Timestamp startTime, Timestamp endTime) {
        return db().select(ORDER_I.USER_ID, DSL.cast(DSL.sum(ORDER_G.GOODS_NUMBER), Integer.class))
            .from(ORDER_I).leftJoin(ORDER_G).on(ORDER_I.ORDER_SN.eq(ORDER_G.ORDER_SN))
            .where(STATUS_CONDITION)
            .and(ORDER_I.ORDER_SN.eq(ORDER_I.MAIN_ORDER_SN).or(ORDER_I.MAIN_ORDER_SN.eq(StringUtils.EMPTY)))
            .and(ORDER_I.CREATE_TIME.ge(startTime)).and(ORDER_I.CREATE_TIME.lessThan(endTime))
            .groupBy(ORDER_I.USER_ID)
            .fetchMap(ORDER_I.USER_ID, DSL.cast(DSL.sum(ORDER_G.GOODS_NUMBER), Integer.class));
    }

    /**
     * Pay new user money big decimal.新用户成交件数(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the big decimal
     */
    public int payNewUserGoodsNum(Timestamp startTime, Timestamp endTime) {
        int totalNum = INTEGER_ZERO;
        Map<Integer, Integer> userMoney = payUserGoodsNum(startTime, endTime);
        // 拿到历史成交用户集合
        Set<Integer> users = payUserCollection(DateUtils.get1970TimeStamp(), startTime);
        for (Map.Entry<Integer, Integer> entry : userMoney.entrySet()) {
            if (!users.contains(entry.getKey())) {
                totalNum += entry.getValue();
            }
        }
        return totalNum;
    }

    /**
     * Pay new user money big decimal.旧用户成交件数(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the big decimal
     */
    public int payOldUserGoodsNum(Timestamp startTime, Timestamp endTime) {
        int totalNum = INTEGER_ZERO;
        Map<Integer, Integer> userMoney = payUserGoodsNum(startTime, endTime);
        // 拿到历史成交用户集合
        Set<Integer> users = payUserCollection(DateUtils.get1970TimeStamp(), startTime);
        for (Map.Entry<Integer, Integer> entry : userMoney.entrySet()) {
            if (users.contains(entry.getKey())) {
                totalNum += entry.getValue();
            }
        }
        return totalNum;
    }

    /**
     * Pay user collection set.指定时间内成交用户集合(排除货到付款非发货)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the set
     */
    public Set<Integer> payUserCollection(Timestamp startTime, Timestamp endTime) {
        return db().select(ORDER_I.USER_ID)
            .from(ORDER_I).where(STATUS_CONDITION)
            .and(ORDER_I.ORDER_SN.eq(ORDER_I.MAIN_ORDER_SN).or(ORDER_I.MAIN_ORDER_SN.eq(StringUtils.EMPTY)))
            .and(ORDER_I.CREATE_TIME.ge(startTime)).and(ORDER_I.CREATE_TIME.lessThan(endTime))
            .orderBy(ORDER_I.CREATE_TIME)
            .fetchSet(ORDER_I.USER_ID);
    }

    /**
     * Virtual user collection set.指定时间内虚拟订单成交用户集合
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the set
     */
    public Set<Integer> virtualUserCollection(Timestamp startTime, Timestamp endTime) {
        return db().select(VIRTUAL_ORDER.USER_ID)
            .from(VIRTUAL_ORDER).where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
            .and(VIRTUAL_ORDER.CREATE_TIME.ge(startTime)).and(VIRTUAL_ORDER.CREATE_TIME.lessThan(endTime))
            .orderBy(VIRTUAL_ORDER.CREATE_TIME)
            .fetchSet(VIRTUAL_ORDER.USER_ID);
    }

    /**
     * Order num int.付款订单数 (待付尾款的也算)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the int
     */
    public int orderNum(Timestamp startTime, Timestamp endTime) {
        return db().fetchCount(ORDER_I, ORDER_SN_CONDITION.and(STATUS_CONDITION)
            .and(ORDER_I.CREATE_TIME.ge(startTime)).and(ORDER_I.CREATE_TIME.lessThan(endTime)));
    }

    /**
     * Order time con condition.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the condition
     */
    public static final Condition orderTimeCon(Timestamp startTime, Timestamp endTime) {
        return ORDER_I.CREATE_TIME.ge(startTime).and(ORDER_I.CREATE_TIME.lessThan(endTime));
    }

    /**
     * Generate order num int.下单笔数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the int
     */
    public int generateOrderNum(Timestamp startTime, Timestamp endTime) {
        return db().fetchCount(ORDER_I, orderTimeCon(startTime, endTime));
    }

    /**
     * Generate order user num int.下单用户数(生成订单就算)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the int
     */
    public int generateOrderUserNum(Timestamp startTime, Timestamp endTime) {
        return db().select(DSL.countDistinct(ORDER_I.USER_ID)).from(ORDER_I).where(orderTimeCon(startTime, endTime))
            .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }

    /**
     * Generate order user num int.下单人数(生成订单就算)
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the int
     */
    public int generatePeopleUserNum(Timestamp startTime, Timestamp endTime) {
        return db().select(DSL.count(ORDER_I.USER_ID)).from(ORDER_I).where(orderTimeCon(startTime, endTime))
            .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }

    /**
     * Has mobile num int.有手机号用户数
     *
     * @return the int
     */
    public int hasMobileNum() {
        return db().fetchCount(User.USER, User.USER.MOBILE.isNotNull());
    }

    /**
     * Gets tag user.获得标签用户
     */
    public void getTagUser() {
    }

    /**
     * Gets recency type.
     *
     * @param time the time
     * @return the recency type
     */
    public Map<Integer, Tuple2<Timestamp, Timestamp>> getRecencyType(LocalDateTime time) {
        return new HashMap<Integer, Tuple2<Timestamp, Timestamp>>(7) {{
            put(1, new Tuple2<>(Timestamp.valueOf(time.minusDays(5)), Timestamp.valueOf(time)));
            put(2, new Tuple2<>(Timestamp.valueOf(time.minusDays(10)), Timestamp.valueOf(time.minusDays(5))));
            put(3, new Tuple2<>(Timestamp.valueOf(time.minusDays(30)), Timestamp.valueOf(time.minusDays(10))));
            put(4, new Tuple2<>(Timestamp.valueOf(time.minusDays(90)), Timestamp.valueOf(time.minusDays(30))));
            put(5, new Tuple2<>(Timestamp.valueOf(time.minusDays(180)), Timestamp.valueOf(time.minusDays(90))));
            put(6, new Tuple2<>(Timestamp.valueOf(time.minusDays(365)), Timestamp.valueOf(time.minusDays(180))));
            put(7, new Tuple2<>(DateUtils.get1970TimeStamp(), Timestamp.valueOf(time.minusDays(365))));
        }};
    }

    /**
     * Gets rfm data.获取每个用户的下单数量和成交金额
     *
     * @param start the start
     * @param end   the end
     * @return the rfm data
     */
    public Map<Integer, Record3<Integer, Integer, BigDecimal>> getRrmData(Timestamp start, Timestamp end) {
        return db().select(ORDER_I.USER_ID, DSL.count(ORDER_I.USER_ID), DSL.sum(ORDER_I.MONEY_PAID).add(DSL.sum(ORDER_I.USE_ACCOUNT)).add(DSL.sum(ORDER_I.MEMBER_CARD_BALANCE)))
            .from(ORDER_I)
            .where(STATUS_CONDITION).and(ORDER_SN_CONDITION)
            .and(ORDER_I.CREATE_TIME.ge(start)).and(ORDER_I.CREATE_TIME.lessThan(end))
            .groupBy(ORDER_I.USER_ID)
            .fetchMap(ORDER_I.USER_ID);
    }

    /**
     * Reduce rfm data tuple 3.
     *
     * @param rfmData the rfm data
     * @param action  the action
     * @return the tuple 3
     */
    @SuppressWarnings({"unchecked"})
    public Tuple3<Integer, Integer, BigDecimal> reduceRfmData(Map<Integer, Record3<Integer, Integer, BigDecimal>> rfmData, Predicate<Integer> action) {
        List<Record3<Integer, Integer, BigDecimal>> temp = rfmData.values().stream().filter((r) -> action.test(r.value2())).collect(Collectors.toList());
        int payUserNum = temp.stream().mapToInt(Record3::value1).sum();
        int orderNum = temp.stream().mapToInt(Record3::value2).sum();
        BigDecimal totalPaidMoney = temp.stream().map(Record3::value3).reduce(BIGDECIMAL_ZERO, BigDecimal::add);
        return new Tuple3(payUserNum, orderNum, totalPaidMoney);
    }

    /**
     * Gets tag data.每个标签下所有用户 付款人数，付款订单数，付款金额，付款商品件数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the tag data
     */
    public Map<Integer, Record6<Integer, String, Integer, Integer, BigDecimal, Integer>> getTagData(Timestamp startTime, Timestamp endTime) {
        return db().select(USER_TAG.TAG_ID
            , TAG.TAG_NAME
            , DSL.countDistinct(ORDER_I.USER_ID)
            , DSL.count(ORDER_I.USER_ID)
            , DSL.sum(ORDER_I.MONEY_PAID).add(DSL.sum(ORDER_I.USE_ACCOUNT)).add(DSL.sum(ORDER_I.MEMBER_CARD_BALANCE))
            , DSL.count(ORDER_G.GOODS_NUMBER))
            .from(ORDER_I).leftJoin(USER_TAG).on(ORDER_I.USER_ID.eq(USER_TAG.USER_ID))
            .leftJoin(TAG).on(USER_TAG.TAG_ID.eq(TAG.TAG_ID))
            .leftJoin(ORDER_G).on(ORDER_I.ORDER_SN.eq(ORDER_G.ORDER_SN))
            .where(ORDER_SN_CONDITION).and(STATUS_CONDITION)
            .and(ORDER_I.CREATE_TIME.ge(startTime)).and(ORDER_I.CREATE_TIME.lessThan(endTime))
            .groupBy(USER_TAG.TAG_ID)
            .having(USER_TAG.TAG_ID.greaterThan(INTEGER_ZERO))
            .fetchMap(USER_TAG.TAG_ID);
    }

    /**
     * Tag user has mobile num map.标签用户付款且有手机号的用户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Record3<Integer, String, Integer>> tagUserHasMobileNum(Timestamp startTime, Timestamp endTime) {
        return db().select(USER_TAG.TAG_ID
            , TAG.TAG_NAME
            , DSL.countDistinct(ORDER_I.USER_ID))
            .from(ORDER_I).leftJoin(USER_TAG).on(ORDER_I.USER_ID.eq(USER_TAG.USER_ID))
            .leftJoin(TAG).on(USER_TAG.TAG_ID.eq(TAG.TAG_ID))
            .leftJoin(User.USER).on(User.USER.USER_ID.eq(ORDER_I.USER_ID))
            .where(ORDER_SN_CONDITION).and(STATUS_CONDITION)
            .and(ORDER_I.CREATE_TIME.ge(startTime)).and(ORDER_I.CREATE_TIME.lessThan(endTime))
            .and(User.USER.MOBILE.isNotNull())
            .groupBy(USER_TAG.TAG_ID)
            .having(USER_TAG.TAG_ID.greaterThan(INTEGER_ZERO))
            .fetchMap(USER_TAG.TAG_ID);
    }

    /**
     * Tag user num map.标签用户数
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the map
     */
    public Map<Integer, Record3<Integer, String, Integer>> tagUserNum(Timestamp startTime, Timestamp endTime) {
        return db().select(USER_TAG.TAG_ID, TAG.TAG_NAME, DSL.countDistinct(USER_TAG.USER_ID)).from(USER_TAG)
            .leftJoin(TAG).on(USER_TAG.TAG_ID.eq(TAG.TAG_ID))
            .where(USER_TAG.CREATE_TIME.ge(startTime)).and(USER_TAG.CREATE_TIME.lessThan(endTime))
            .groupBy(USER_TAG.TAG_ID)
            .fetchMap(USER_TAG.TAG_ID);
    }
}

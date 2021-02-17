package com.meidianyi.shop.service.saas.index;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.saas.index.cache.ThreadLocalCache;
import com.meidianyi.shop.service.saas.index.param.ShopViewParam;
import com.meidianyi.shop.service.saas.index.vo.*;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.main.tables.MpAuthShop.MP_AUTH_SHOP;
import static com.meidianyi.shop.db.main.tables.OrderInfoBak.ORDER_INFO_BAK;
import static com.meidianyi.shop.db.main.tables.Shop.SHOP;
import static com.meidianyi.shop.db.main.tables.ShopAccount.SHOP_ACCOUNT;
import static com.meidianyi.shop.db.main.tables.User.USER;

/**
 * @author luguangyao
 */
@Service
public class ShopViewService extends MainBaseService {


    public ShopViewVo getShopViewData(ShopViewParam param){
        ThreadLocalCache.timestampThreadLocal.set(LocalDate.now());
        ShopViewVo vo = new ShopViewVo();
        vo.setAccountStatisticsInfo(getAccountStatisticsInfo());
        vo.setOrderStatisticsInfo(getOrderStatisticsInfo(param));
        vo.setUserStatisticsInfo(getUserStatisticsInfo(param));
        ThreadLocalCache.timestampThreadLocal.remove();
        return vo;
    }

    /**
     * 获取订单统计相关数据
     * @param param {@link ShopViewParam}
     * @return {@link UserStatisticsInfo}
     */
    public OrderStatisticsInfo getOrderStatisticsInfo(ShopViewParam param){
        OrderStatisticsInfo info = new OrderStatisticsInfo();
        info.setAllOrderNum(getAllOrderNum());
        OrderMoneyInfo moneyInfo = getOrderMoneyStatistics();

        info.setOrderNumInfos(getOrderNumByDay(param));
        info.setOrderMoneyInfos(getOrderMoneyByDay(param));
        if( moneyInfo != null ){
            info.setWxPayed(moneyInfo.getWxPayed().toString());
            info.setBalancePayed(moneyInfo.getBalancePayed().toString());
            info.setCardBalancePayed(moneyInfo.getCardBalancePayed().toString());
            info.setIntegralPayed(moneyInfo.getIntegralPayed().toString());
        }

        return info;
    }

    /**
     * 获取用户统计信息
     * @param param {@link ShopViewParam}
     * @return {@link UserStatisticsInfo}
     */
    public UserStatisticsInfo getUserStatisticsInfo(ShopViewParam param){
        UserStatisticsInfo info = new UserStatisticsInfo();
        info.setAllNum(getAllUserNum());
        info.setUserNumsInfo(getUserNumByDay(param));

        return info;
    }

    /**
     * 获取账户统计相关数据
     * @return {@link AccountStatisticsInfo}
     */
    private AccountStatisticsInfo getAccountStatisticsInfo(){
        AccountStatisticsInfo info = new AccountStatisticsInfo();
        info.setAllAccountNum(getAllAccountNum());
        info.setAllShopNum(getAllShopNum());
        info.setEffectiveShopNum(getEffectiveShopNum());
        info.setUsedShopNum(getUsedShopNum());
        info.setToExpireShopNum(getToExpireShopNum());
        info.setExpiredShopNum(getExpireShopNum());
        info.setDisabledShopNum(getDisabledShopNum());
        info.setAuthorizedShopNum(getAuthorizeShopNum((byte)1));
        info.setUnauthorizedShopNum(getAuthorizeShopNum((byte)0));
        info.setOpenedPaymentShopNum(getOpenPaymentShopNum((byte)1));
        info.setNotOpenPaymentShopNum(getOpenPaymentShopNum((byte)0));
        return info;

    }

    /**
     * 获取所有统计金额
     * @return 支付代号，微信支付金额，账户支付金额，会员卡支付，积分支付金额
     */
    private OrderMoneyInfo  getOrderMoneyStatistics(){
        return db().select(ORDER_INFO_BAK.PAY_CODE,DSL.sum(ORDER_INFO_BAK.MONEY_PAID).as("wxPayed"),
                DSL.sum(ORDER_INFO_BAK.USE_ACCOUNT).as("balancePayed"),
                DSL.sum(ORDER_INFO_BAK.MEMBER_CARD_REDUCE).as("cardBalancePayed"),
                DSL.sum(ORDER_INFO_BAK.SCORE_DISCOUNT).as("integralPayed")).
            from(ORDER_INFO_BAK).where(ORDER_INFO_BAK.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY)).
            groupBy(ORDER_INFO_BAK.PAY_CODE).
            fetchAnyInto(OrderMoneyInfo.class);

    }

    /**
     * 统计所有订单数量
     * @return 统计数量
     */
    private Integer getAllOrderNum(){
        return db().selectCount().from(ORDER_INFO_BAK).
            where(ORDER_INFO_BAK.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY)).
            fetchOne(0,Integer.class);
    }

    /**
     * 统计是否开通支付店铺数量（关联表查询）
     * @param openPay 0:没有开通 1：已开通
     * @return 统计数量
     */
    private Integer getOpenPaymentShopNum(Byte openPay) {
        return db().selectCount().from(SHOP).
            join(SHOP_ACCOUNT).on(SHOP.SYS_ID.eq(SHOP_ACCOUNT.SYS_ID)).
            leftJoin(MP_AUTH_SHOP).on(SHOP.SHOP_ID.eq(MP_AUTH_SHOP.SHOP_ID)).
            where(MP_AUTH_SHOP.OPEN_PAY.eq(openPay)).execute();
    }

    /**
     * 统计授权店铺数量（关联表查询）
     * @param isAuthOk 0:没有授权 1：已授权
     * @return 统计数量
     */
    private Integer getAuthorizeShopNum(Byte isAuthOk) {
        return db().selectCount().from(SHOP).
            join(SHOP_ACCOUNT).on(SHOP.SYS_ID.eq(SHOP_ACCOUNT.SYS_ID)).
            leftJoin(MP_AUTH_SHOP).on(SHOP.SHOP_ID.eq(MP_AUTH_SHOP.SHOP_ID)).
            where(MP_AUTH_SHOP.IS_AUTH_OK.eq(isAuthOk)).execute();
    }


    /**
     * 统计已禁用店铺数量（is_enabled == 0）
     * @return 统计数量
     */
    private Integer getDisabledShopNum(){
        Condition condition = SHOP.IS_ENABLED.eq((byte)0);
        return getShopNumByCondition(condition);
    }

    /**
     * 统计已过期店铺数量（时间小于当前时间或者过期时间是null的）
     * @return 统计数量
     */
    private Integer getExpireShopNum(){
        Timestamp time = Timestamp.valueOf(ThreadLocalCache.timestampThreadLocal.get().atStartOfDay());
        Condition condition = SHOP.EXPIRE_TIME.le(time).or(SHOP.EXPIRE_TIME.isNull());
        return getShopNumByCondition(condition);
    }
    /**
     * 统计快到期店铺数量（时间距离到期时间30天内的）
     * @return 统计数量
     */
    private Integer getToExpireShopNum(){
        Timestamp time = Timestamp.valueOf(ThreadLocalCache.timestampThreadLocal.get().plusDays(30).atStartOfDay());
        Condition condition = SHOP.EXPIRE_TIME.greaterOrEqual(time);
        return getShopNumByCondition(condition);
    }

    /**
     * 统计使用中的店铺数量（只判断到期时间是否达到）
     * @return 统计数量
     */
    private Integer getUsedShopNum(){
        Timestamp time = Timestamp.valueOf(ThreadLocalCache.timestampThreadLocal.get().atStartOfDay());
        Condition condition = SHOP.EXPIRE_TIME.greaterOrEqual(time);
        return getShopNumByCondition(condition);
    }

    /**
     * 统计有效店铺数量（判断是可用状态且还没有到期）
     * @return 统计数量
     */
    private Integer getEffectiveShopNum(){
        Timestamp time = Timestamp.valueOf(ThreadLocalCache.timestampThreadLocal.get().atStartOfDay());
        Condition condition = SHOP.IS_ENABLED.eq((byte)0).and(SHOP.EXPIRE_TIME.greaterOrEqual(time));
        return getShopNumByCondition(condition);
    }

    /**
     * 统计所有账户的数量
     * @return 统计数量
     */
    private Integer getAllAccountNum(){
        return getAccountNumByCondition(DSL.noCondition());
    }

    /**
     * 统计所有店铺的数量
     * @return 统计数量
     */
    private Integer getAllShopNum(){
        return getShopNumByCondition(DSL.noCondition());
    }
    private Integer getShopNumByCondition(Condition condition){
        return db().selectCount().from(SHOP).where(condition).fetchOne(0,Integer.class);
    }

    private Integer getAccountNumByCondition(Condition condition){
        return db().selectCount().from(SHOP_ACCOUNT).where(condition).fetchOne(0,Integer.class);
    }

    /**
     * 获取所有用户数量
     * @return 统计数量
     */
    private Integer getAllUserNum(){
        return db().selectCount().from(USER).fetchOne(0,Integer.class);
    }

    /**
     * 根据日期筛选每日订单交易金额
     * @param param 筛选日期
     * @return {@link BaseInfo}
     */
    private List<BaseMoneyInfo> getOrderMoneyByDay(ShopViewParam param){
        List<LocalDate> localDates = DateUtils.getAllDatesBetweenTwoDates(
            param.getStartTime().toLocalDateTime().toLocalDate(),param.getEndTime().toLocalDateTime().toLocalDate());
        Map<String,OrderMoneyInfo> dateMap = localDates.stream().
            map(x-> DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE,Timestamp.valueOf(x.atStartOfDay()))).
            collect(Collectors.toMap(x->x,y->new OrderMoneyInfo(),(oldValue,newValue)->oldValue,LinkedHashMap::new));
        Field<String> time = dateFormat(ORDER_INFO_BAK.PAY_TIME, DateUtils.DATE_MYSQL_SIMPLE).as("time");
        List<OrderMoneyInfo> moneyInfos = db().select(time,ORDER_INFO_BAK.PAY_CODE,DSL.sum(ORDER_INFO_BAK.MONEY_PAID).as("wxPayed"),
            DSL.sum(ORDER_INFO_BAK.USE_ACCOUNT).as("balancePayed"),
            DSL.sum(ORDER_INFO_BAK.MEMBER_CARD_REDUCE).as("cardBalancePayed"),
            DSL.sum(ORDER_INFO_BAK.SCORE_DISCOUNT).as("integralPayed")).
            from(ORDER_INFO_BAK).
            where(ORDER_INFO_BAK.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY)).
                and(ORDER_INFO_BAK.IS_COD.eq((byte)0)).and(ORDER_INFO_BAK.PAY_TIME.greaterOrEqual(param.getStartTime())).
            and(ORDER_INFO_BAK.PAY_TIME.lessOrEqual(param.getEndTime())).
            groupBy(time,ORDER_INFO_BAK.PAY_CODE).fetchInto(OrderMoneyInfo.class);


        return getBaseInfoList(moneyInfos,dateMap);
    }
    /**
     * 根据日期筛选每日新增订单数量
     * @param param 筛选日期
     * @return {@link BaseInfo}
     */
    private List<BaseInfo> getOrderNumByDay(ShopViewParam param){
        List<LocalDate> localDates = DateUtils.getAllDatesBetweenTwoDates(
            param.getStartTime().toLocalDateTime().toLocalDate(),param.getEndTime().toLocalDateTime().toLocalDate());
        Map<String,Integer> dateMap = localDates.stream().
            map(x-> DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE,Timestamp.valueOf(x.atStartOfDay()))).
            collect(Collectors.toMap(x->x,y->0,(oldValue,newValue)->oldValue,LinkedHashMap::new));
        Field<String> time = dateFormat(ORDER_INFO_BAK.PAY_TIME, DateUtils.DATE_MYSQL_SIMPLE).as("time");
        Result<Record2<String,Integer>> result =db().
            select(time,DSL.count(ORDER_INFO_BAK.ORDER_ID)).
            from(ORDER_INFO_BAK).
            where(
                ORDER_INFO_BAK.PAY_TIME.greaterOrEqual(param.getStartTime())).
            and(ORDER_INFO_BAK.PAY_TIME.lessOrEqual(param.getEndTime())
            ).groupBy(time).fetch();
        return getBaseInfoList(result,dateMap);
    }
    /**
     * 根据日期筛选每日新增用户数量
     * @param param 筛选日期
     * @return {@link BaseInfo}
     */
    private List<BaseInfo> getUserNumByDay(ShopViewParam param){
        List<LocalDate> localDates = DateUtils.getAllDatesBetweenTwoDates(
            param.getStartTime().toLocalDateTime().toLocalDate(),param.getEndTime().toLocalDateTime().toLocalDate());
        Map<String,Integer> dateMap = localDates.stream()
            .map(x-> DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE,Timestamp.valueOf(x.atStartOfDay())))
            .collect(Collectors.toMap(x->x,y->0,(oldValue,newValue)->oldValue,LinkedHashMap::new));
        Result<Record2<String,Integer>> result =db().
            select(dateFormat(USER.CREATE_TIME, DateUtils.DATE_MYSQL_SIMPLE),DSL.count(USER.ID))
            .from(USER)
            .where(USER.CREATE_TIME.greaterOrEqual(param.getStartTime())).
                and(USER.CREATE_TIME.lessOrEqual(param.getEndTime()))
            .groupBy(dateFormat(USER.CREATE_TIME, DateUtils.DATE_MYSQL_SIMPLE))
            .orderBy(dateFormat(USER.CREATE_TIME, DateUtils.DATE_MYSQL_SIMPLE).asc())
            .fetch();
        return getBaseInfoList(result,dateMap);
    }

    private List<BaseInfo> buildNumInit(Map<String,Integer> dateMap){
        List<BaseInfo> list = Lists.newArrayList();
        for( Map.Entry<String,Integer> entry:dateMap.entrySet() ){
            BaseInfo info =  new BaseInfo();
            info.setDate(entry.getKey());
            info.setNum(entry.getValue());
            list.add(info);
        }
        return list;
    }
    private List<BaseMoneyInfo> buildMoneyInit(Map<String,OrderMoneyInfo> dateMap){
        List<BaseMoneyInfo> list = Lists.newArrayList();
        for( Map.Entry<String,OrderMoneyInfo> entry:dateMap.entrySet() ){
            BaseMoneyInfo info =  new BaseMoneyInfo();
            info.setDate(entry.getKey());
            info.setMoney(entry.getValue());
            list.add(info);
        }
        return list;
    }

    private List<BaseInfo> getBaseInfoList(Result<Record2<String,Integer>> result,Map<String,Integer> dateMap){
        for( Record2<String,Integer> record: result ){
            dateMap.put(String.valueOf(record.get(0)),Integer.parseInt(String.valueOf(record.get(1))));
        }
        return buildNumInit(dateMap);
    }
    private List<BaseMoneyInfo> getBaseInfoList(List<OrderMoneyInfo> result,Map<String,OrderMoneyInfo> dateMap){
        for( OrderMoneyInfo info: result ){
            dateMap.put(info.getTime(),info);
        }
        return buildMoneyInit(dateMap);
    }


}

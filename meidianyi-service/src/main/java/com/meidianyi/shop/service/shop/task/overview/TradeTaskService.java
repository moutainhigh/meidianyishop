package com.meidianyi.shop.service.shop.task.overview;

import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.overview.realtime.RealTimeBo;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.TradesRecord.TRADES_RECORD;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.StoreOrder.STORE_ORDER;
import static com.meidianyi.shop.db.shop.tables.ServiceOrder.SERVICE_ORDER;
import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.db.shop.tables.CardRenew.CARD_RENEW;
import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static org.apache.commons.lang3.math.NumberUtils.*;

/**
 * The type Trade task service.
 *
 * @author liufei
 * @date 12 /16/19
 */
@Service
public class TradeTaskService extends ShopBaseService {

    /**
     * Gets total income money.总现金资产收入
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the total income money
     */
    public BigDecimal getTotalIncomeMoney(Timestamp startTime, Timestamp endTime) {
        return commonOperator(startTime, endTime, BYTE_ZERO, BYTE_ZERO);
    }

    /**
     * Gets total expenses money.总现金资产支出
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the total expenses money
     */
    public BigDecimal getTotalExpensesMoney(Timestamp startTime, Timestamp endTime) {
        return commonOperator(startTime, endTime, BYTE_ZERO, BYTE_ONE);
    }

    /**
     * Gets total income score.总积分资产收入
     *
     * @param startTime the start time
     * @param endTime   the end time
     */
    public BigDecimal getTotalIncomeScore(Timestamp startTime, Timestamp endTime) {
        return commonOperator(startTime, endTime, BYTE_ONE, BYTE_ZERO);
    }

    /**
     * Gets total expenses score.总积分资产支出
     *
     * @param startTime the start time
     * @param endTime   the end time
     */
    public BigDecimal getTotalExpensesScore(Timestamp startTime, Timestamp endTime) {
        return commonOperator(startTime, endTime, BYTE_ONE, BYTE_ONE);
    }

    private BigDecimal commonOperator(Timestamp startTime, Timestamp endTime, Byte content, Byte flow) {
        return db().select(DSL.sum(TRADES_RECORD.TRADE_NUM)).from(TRADES_RECORD)
            .where(TRADES_RECORD.CREATE_TIME.ge(startTime).and(TRADES_RECORD.CREATE_TIME.lessThan(endTime)))
            .and(TRADES_RECORD.TRADE_CONTENT.eq(content))
            .and(TRADES_RECORD.TRADE_FLOW.eq(flow))
            .fetchOptionalInto(BigDecimal.class).orElse(BIGDECIMAL_ZERO);
    }

    /**
     * 成交用户数
     * @param start 开始时间
     * @param end   结束时间
     */
    public List<Integer> orderUserNum(Timestamp start,Timestamp end){
        List<Integer> orderUser = new ArrayList<>();
        //实物商品订单
        List<Integer> orderUserId = consumeOrderUser(start, end);
        orderUser.addAll(orderUserId);
        //门店买单订单
        List<Integer> storeOrderUserId = storeConsumeOrderUser(start, end);
        orderUser.addAll(storeOrderUserId);
        //门店服务订单
        List<Integer> serviceOrderUserId = serviceConsumeOrderUser(start, end);
        orderUser.addAll(serviceOrderUserId);
        //会员卡续费订单
        List<Integer> cardRenewUserId = cardRenewOrderUser(start, end);
        orderUser.addAll(cardRenewUserId);
        //虚拟订单
        List<Integer> virtualOrderUserId = virtualOrderUser(start, end);
        orderUser.addAll(virtualOrderUserId);
        orderUser = orderUser.stream().distinct().collect(Collectors.toList());
        return orderUser;
    }
    /**
     * 历史成交用户数
     * @param start 开始时间
     */
    public List<Integer> HistoryOrderUserNum(Timestamp start){
        List<Integer> orderUser = new ArrayList<>();
        //实物商品订单
        List<Integer> orderUserId = consumeOrderUser(start);
        orderUser.addAll(orderUserId);
        //门店买单订单
        List<Integer> storeOrderUserId = storeConsumeOrderUser(start);
        orderUser.addAll(storeOrderUserId);
        //门店服务订单
        List<Integer> serviceOrderUserId = serviceConsumeOrderUser(start);
        orderUser.addAll(serviceOrderUserId);
        //会员卡续费订单
        List<Integer> cardRenewUserId = cardRenewOrderUser(start);
        orderUser.addAll(cardRenewUserId);
        //虚拟订单
        List<Integer> virtualOrderUserId = virtualOrderUser(start);
        orderUser.addAll(virtualOrderUserId);
        orderUser = orderUser.stream().distinct().collect(Collectors.toList());
        return orderUser;
    }
    /**
     * 老成交用户数
     * @param start 开始时间
     * @param end   结束时间
     */
    public List<Integer> oldOrderUserNum(Timestamp start,Timestamp end){
        //全部用户
        List<Integer> orderUserList = orderUserNum(start, end);
        //历史订单
        List<Integer> HistoryOrderUserList = HistoryOrderUserNum(start);
        orderUserList.retainAll(HistoryOrderUserList);
        return orderUserList;
    }
    /**
     * 新成交用户数
     * @param start 开始时间
     * @param end   结束时间
     */
    public List<Integer> newOrderUserNum(Timestamp start,Timestamp end){
        //全部用户
        List<Integer> orderUserList = orderUserNum(start, end);
        //历史订单
        List<Integer> HistoryOrderUserList = HistoryOrderUserNum(start);
        orderUserList.removeAll(HistoryOrderUserList);
        return orderUserList;
    }

    /**
     * 实物商品订单-已付订单统计
     * @param start 开始时间
     * @return      订单用户
     */
    private List<Integer> consumeOrderUser(Timestamp start){
        List<Integer> userIds;
        userIds = db().selectDistinct(ORDER_INFO.USER_ID)
            .from(ORDER_INFO)
            .where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY))
            .and(ORDER_INFO.CREATE_TIME.lessThan(start))
            .and(ORDER_INFO.IS_COD.eq(BYTE_ZERO).or(ORDER_INFO.IS_COD.eq(BYTE_ONE).and(ORDER_INFO.SHIPPING_TIME.isNotNull())))
            .and(ORDER_INFO.ORDER_SN.eq(ORDER_INFO.MAIN_ORDER_SN).or(ORDER_INFO.MAIN_ORDER_SN.eq("")))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 门店买单订单-已付订单统计
     * @param start 开始时间
     * @return      订单用户
     */
    private List<Integer> storeConsumeOrderUser(Timestamp start) {
        List<Integer> userIds;
        userIds = db().selectDistinct(STORE_ORDER.USER_ID)
            .from(STORE_ORDER)
            .where(STORE_ORDER.ORDER_STATUS.greaterOrEqual(BYTE_ONE))
            .and(STORE_ORDER.CREATE_TIME.lessThan(start))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 门店服务订单-已付订单统计
     * @param start 开始时间
     * @return      订单用户
     */
    private List<Integer> serviceConsumeOrderUser(Timestamp start) {
        List<Integer> userIds;
        List<Byte> orderStatus = new ArrayList<Byte>(){{
            add((byte)0);
            add((byte)2);
        }};
        userIds = db().selectDistinct(SERVICE_ORDER.USER_ID)
            .from(SERVICE_ORDER)
            .where(SERVICE_ORDER.ORDER_STATUS.in(orderStatus))
            .and(SERVICE_ORDER.CREATE_TIME.lessThan(start))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 会员卡续费订单-已付订单统计
     * @param start 开始时间
     * @return      订单用户
     */
    private List<Integer> cardRenewOrderUser(Timestamp start) {
        List<Integer> userIds;
        userIds = db().selectDistinct(CARD_RENEW.USER_ID)
            .from(CARD_RENEW)
            .where(CARD_RENEW.ORDER_STATUS.eq(BYTE_ONE))
            .and(CARD_RENEW.ADD_TIME.lessThan(start))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 会员卡和优惠券礼包购买订单-已付订单统计
     * @param start 开始时间
     * @return      订单用户
     */
    private List<Integer> virtualOrderUser(Timestamp start) {
        List<Integer> userIds;
        userIds = db().selectDistinct(VIRTUAL_ORDER.USER_ID)
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
            .and(VIRTUAL_ORDER.CREATE_TIME.lessThan(start))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 实物商品订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private List<Integer> consumeOrderUser(Timestamp start,Timestamp end){
        List<Integer> userIds;
        userIds = db().selectDistinct(ORDER_INFO.USER_ID)
            .from(ORDER_INFO)
            .where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY))
            .and(ORDER_INFO.CREATE_TIME.greaterOrEqual(start))
            .and(ORDER_INFO.CREATE_TIME.lessThan(end))
            .and(ORDER_INFO.IS_COD.eq(BYTE_ZERO).or(ORDER_INFO.IS_COD.eq(BYTE_ONE).and(ORDER_INFO.SHIPPING_TIME.isNotNull())))
            .and(ORDER_INFO.ORDER_SN.eq(ORDER_INFO.MAIN_ORDER_SN).or(ORDER_INFO.MAIN_ORDER_SN.eq("")))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 门店买单订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private List<Integer> storeConsumeOrderUser(Timestamp start,Timestamp end) {
        List<Integer> userIds;
        userIds = db().selectDistinct(STORE_ORDER.USER_ID)
            .from(STORE_ORDER)
            .where(STORE_ORDER.ORDER_STATUS.greaterOrEqual(BYTE_ONE))
            .and(STORE_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(STORE_ORDER.CREATE_TIME.lessThan(end))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 门店服务订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private List<Integer> serviceConsumeOrderUser(Timestamp start,Timestamp end) {
        List<Integer> userIds;
        List<Byte> orderStatus = new ArrayList<Byte>(){{
            add((byte)0);
            add((byte)2);
        }};
        userIds = db().selectDistinct(SERVICE_ORDER.USER_ID)
            .from(SERVICE_ORDER)
            .where(SERVICE_ORDER.ORDER_STATUS.in(orderStatus))
            .and(SERVICE_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(SERVICE_ORDER.CREATE_TIME.lessThan(end))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 会员卡续费订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private List<Integer> cardRenewOrderUser(Timestamp start,Timestamp end) {
        List<Integer> userIds;
        userIds = db().selectDistinct(CARD_RENEW.USER_ID)
            .from(CARD_RENEW)
            .where(CARD_RENEW.ORDER_STATUS.eq(BYTE_ONE))
            .and(CARD_RENEW.ADD_TIME.greaterOrEqual(start))
            .and(CARD_RENEW.ADD_TIME.lessThan(end))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * 会员卡和优惠券礼包购买订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private List<Integer> virtualOrderUser(Timestamp start,Timestamp end) {
        List<Integer> userIds;
        userIds = db().selectDistinct(VIRTUAL_ORDER.USER_ID)
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
            .and(VIRTUAL_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(VIRTUAL_ORDER.CREATE_TIME.lessThan(end))
            .fetchInto(Integer.class);
        return userIds;
    }
    /**
     * trade表订单数和订单金额
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单数和订单金额
     */
    public RealTimeBo orderUserMoney(Timestamp start,Timestamp end,List<Integer> userIds){
        //实物商品订单
        RealTimeBo order = consumeOrder(start, end,userIds);
        order = processNullObject(order);
        //门店买单订单
        RealTimeBo storeOrder = storeConsumeOrder(start, end,userIds);
        storeOrder = processNullObject(storeOrder);
        //门店服务订单
        RealTimeBo serviceOrder = serviceConsumeOrder(start, end,userIds);
        serviceOrder = processNullObject(serviceOrder);
        //会员卡续费订单
        RealTimeBo cardRenew = cardRenewOrder(start, end,userIds);
        cardRenew = processNullObject(cardRenew);
        //门店服务订单
        RealTimeBo virtualOrder = virtualOrder(start, end,userIds);
        virtualOrder = processNullObject(virtualOrder);
        RealTimeBo allOrder = new RealTimeBo();
        allOrder.setOrderNum(order.getOrderNum()+storeOrder.getOrderNum()+serviceOrder.getOrderNum()+cardRenew.getOrderNum()+virtualOrder.getOrderNum());
        allOrder.setTotalMoneyPaid(order.getTotalMoneyPaid().add(storeOrder.getTotalMoneyPaid()).add(serviceOrder.getTotalMoneyPaid()).add(cardRenew.getTotalMoneyPaid()).add(virtualOrder.getTotalMoneyPaid()));
        return allOrder;
    }

    /**
     * 新trade表订单数和订单金额
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单数和订单金额
     */
    public RealTimeBo newOrderUserMoney(Timestamp start,Timestamp end){
        //新成交用户
        List<Integer> userIds = newOrderUserNum(start, end);
        RealTimeBo realTimeBo = orderUserMoney(start,end,userIds);
        return realTimeBo;
    }
    /**
     * 旧trade表订单数和订单金额
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单数和订单金额
     */
    public RealTimeBo oldOrderUserMoney(Timestamp start,Timestamp end){
        //新成交用户
        List<Integer> userIds = oldOrderUserNum(start, end);
        RealTimeBo realTimeBo = orderUserMoney(start,end,userIds);
        return realTimeBo;
    }
    /**
     * 处理对象潜在空指针问题
     * @param bo bo对象
     * @return 处理过的对象
     */
    private RealTimeBo processNullObject(RealTimeBo bo){
        if (null==bo){
            bo = new RealTimeBo(){{
                setOrderNum(0);
                setTotalMoneyPaid(BIGDECIMAL_ZERO);
            }};
        }else if (null==bo.getOrderNum()){
            bo.setOrderNum(0);
        }else if (null==bo.getTotalMoneyPaid()){
            bo.setTotalMoneyPaid(BIGDECIMAL_ZERO);
        }
        return bo;
    }
    /**
     * 实物商品订单-订单数&&订单金额
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单记录
     */
    private RealTimeBo consumeOrder(Timestamp start,Timestamp end,List<Integer> userIds){
        RealTimeBo realTimeBo;
        if (null!=userIds&&userIds.size()>0){
            realTimeBo = db().select(DSL.count(ORDER_INFO.ORDER_ID).as("orderNum"),
                DSL.sum(ORDER_INFO.MONEY_PAID.add(ORDER_INFO.USE_ACCOUNT).add(ORDER_INFO.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY))
                .and(ORDER_INFO.CREATE_TIME.greaterOrEqual(start))
                .and(ORDER_INFO.CREATE_TIME.lessThan(end))
                .and(ORDER_INFO.IS_COD.eq(BYTE_ZERO).or(ORDER_INFO.IS_COD.eq(BYTE_ONE).and(ORDER_INFO.SHIPPING_TIME.isNotNull())))
                .and(ORDER_INFO.ORDER_SN.eq(ORDER_INFO.MAIN_ORDER_SN).or(ORDER_INFO.MAIN_ORDER_SN.eq("")))
                .and(ORDER_INFO.USER_ID.in(userIds))
                .fetchAnyInto(RealTimeBo.class);
        }else {
            realTimeBo = db().select(DSL.count(ORDER_INFO.ORDER_ID).as("orderNum"),
                DSL.sum(ORDER_INFO.MONEY_PAID.add(ORDER_INFO.USE_ACCOUNT).add(ORDER_INFO.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY))
                .and(ORDER_INFO.CREATE_TIME.greaterOrEqual(start))
                .and(ORDER_INFO.CREATE_TIME.lessThan(end))
                .and(ORDER_INFO.IS_COD.eq(BYTE_ZERO).or(ORDER_INFO.IS_COD.eq(BYTE_ONE).and(ORDER_INFO.SHIPPING_TIME.isNotNull())))
                .and(ORDER_INFO.ORDER_SN.eq(ORDER_INFO.MAIN_ORDER_SN).or(ORDER_INFO.MAIN_ORDER_SN.eq("")))
                .fetchAnyInto(RealTimeBo.class);
        }
        return realTimeBo;
    }
    /**
     * 门店买单订单-订单数&&订单金额
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单记录
     */
    private RealTimeBo storeConsumeOrder(Timestamp start,Timestamp end,List<Integer> userIds) {
        RealTimeBo realTimeBo;
        if (null!=userIds&&userIds.size()>0){
            realTimeBo = db().select(DSL.count(STORE_ORDER.ORDER_ID).as("orderNum"),
                DSL.sum(STORE_ORDER.MONEY_PAID.add(STORE_ORDER.USE_ACCOUNT).add(STORE_ORDER.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(STORE_ORDER)
                .where(STORE_ORDER.ORDER_STATUS.greaterOrEqual(BYTE_ONE))
                .and(STORE_ORDER.CREATE_TIME.greaterOrEqual(start))
                .and(STORE_ORDER.CREATE_TIME.lessThan(end))
                .and(STORE_ORDER.USER_ID.in(userIds))
                .fetchAnyInto(RealTimeBo.class);
        }else {
            realTimeBo = db().select(DSL.count(STORE_ORDER.ORDER_ID).as("orderNum"),
                DSL.sum(STORE_ORDER.MONEY_PAID.add(STORE_ORDER.USE_ACCOUNT).add(STORE_ORDER.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(STORE_ORDER)
                .where(STORE_ORDER.ORDER_STATUS.greaterOrEqual(BYTE_ONE))
                .and(STORE_ORDER.CREATE_TIME.greaterOrEqual(start))
                .and(STORE_ORDER.CREATE_TIME.lessThan(end))
                .fetchAnyInto(RealTimeBo.class);
        }
        return realTimeBo;
    }
    /**
     * 门店服务订单-订单数&&订单金额
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单记录
     */
    private RealTimeBo serviceConsumeOrder(Timestamp start,Timestamp end,List<Integer> userIds) {
        List<Byte> orderStatus = new ArrayList<Byte>(){{
            add((byte)0);
            add((byte)2);
        }};
        RealTimeBo realTimeBo;
        if (null!=userIds&&userIds.size()>0){
            realTimeBo = db().select(DSL.count(SERVICE_ORDER.ORDER_ID).as("orderNum"),
                DSL.sum(SERVICE_ORDER.MONEY_PAID.add(SERVICE_ORDER.USE_ACCOUNT).add(SERVICE_ORDER.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(SERVICE_ORDER)
                .where(SERVICE_ORDER.ORDER_STATUS.in(orderStatus))
                .and(SERVICE_ORDER.CREATE_TIME.greaterOrEqual(start))
                .and(SERVICE_ORDER.CREATE_TIME.lessThan(end))
                .and(SERVICE_ORDER.USER_ID.in(userIds))
                .fetchAnyInto(RealTimeBo.class);
        }else {
            realTimeBo = db().select(DSL.count(SERVICE_ORDER.ORDER_ID).as("orderNum"),
                DSL.sum(SERVICE_ORDER.MONEY_PAID.add(SERVICE_ORDER.USE_ACCOUNT).add(SERVICE_ORDER.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(SERVICE_ORDER)
                .where(SERVICE_ORDER.ORDER_STATUS.in(orderStatus))
                .and(SERVICE_ORDER.CREATE_TIME.greaterOrEqual(start))
                .and(SERVICE_ORDER.CREATE_TIME.lessThan(end))
                .fetchAnyInto(RealTimeBo.class);
        }
        return realTimeBo;
    }
    /**
     * 会员卡续费订单-订单数&&订单金额
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单记录
     */
    private RealTimeBo cardRenewOrder(Timestamp start,Timestamp end,List<Integer> userIds) {
        RealTimeBo realTimeBo;
        if (null!=userIds&&userIds.size()>0){
            realTimeBo = db().select(DSL.count(CARD_RENEW.ID).as("orderNum"),
                DSL.sum(CARD_RENEW.MONEY_PAID.add(CARD_RENEW.USE_ACCOUNT).add(CARD_RENEW.MEMBER_CARD_REDUNCE)).as("totalMoneyPaid"))
                .from(CARD_RENEW)
                .where(CARD_RENEW.ORDER_STATUS.eq(BYTE_ONE))
                .and(CARD_RENEW.ADD_TIME.greaterOrEqual(start))
                .and(CARD_RENEW.ADD_TIME.lessThan(end))
                .and(CARD_RENEW.USER_ID.in(userIds))
                .fetchAnyInto(RealTimeBo.class);
        }else {
            realTimeBo = db().select(DSL.count(CARD_RENEW.ID).as("orderNum"),
                DSL.sum(CARD_RENEW.MONEY_PAID.add(CARD_RENEW.USE_ACCOUNT).add(CARD_RENEW.MEMBER_CARD_REDUNCE)).as("totalMoneyPaid"))
                .from(CARD_RENEW)
                .where(CARD_RENEW.ORDER_STATUS.eq(BYTE_ONE))
                .and(CARD_RENEW.ADD_TIME.greaterOrEqual(start))
                .and(CARD_RENEW.ADD_TIME.lessThan(end))
                .fetchAnyInto(RealTimeBo.class);
        }
        return realTimeBo;
    }
    /**
     * 会员卡和优惠券礼包购买订单-订单数&&订单金额
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单记录
     */
    private RealTimeBo virtualOrder(Timestamp start,Timestamp end,List<Integer> userIds) {
        RealTimeBo realTimeBo;
        if (null!=userIds&&userIds.size()>0){
            realTimeBo = db().select(DSL.count(VIRTUAL_ORDER.ORDER_ID).as("orderNum"),
                DSL.sum(VIRTUAL_ORDER.MONEY_PAID.add(VIRTUAL_ORDER.USE_ACCOUNT).add(VIRTUAL_ORDER.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(VIRTUAL_ORDER)
                .where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
                .and(VIRTUAL_ORDER.CREATE_TIME.greaterOrEqual(start))
                .and(VIRTUAL_ORDER.CREATE_TIME.lessThan(end))
                .and(VIRTUAL_ORDER.USER_ID.in(userIds))
                .fetchAnyInto(RealTimeBo.class);
        }else {
            realTimeBo = db().select(DSL.count(VIRTUAL_ORDER.ORDER_ID).as("orderNum"),
                DSL.sum(VIRTUAL_ORDER.MONEY_PAID.add(VIRTUAL_ORDER.USE_ACCOUNT).add(VIRTUAL_ORDER.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
                .from(VIRTUAL_ORDER)
                .where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
                .and(VIRTUAL_ORDER.CREATE_TIME.greaterOrEqual(start))
                .and(VIRTUAL_ORDER.CREATE_TIME.lessThan(end))
                .fetchAnyInto(RealTimeBo.class);
        }
        return realTimeBo;
    }

    /**
     *  下单笔数(生成订单就算)
     * @param start 开始时间
     * @param end   结束时间
     * @return 下单笔数(生成订单就算)
     */
    public Integer generateOrderNum(Timestamp start,Timestamp end){
        Integer count1 =db().selectCount()
            .from(ORDER_INFO)
            .where(ORDER_INFO.CREATE_TIME.greaterOrEqual(start))
            .and(ORDER_INFO.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer count2 =db().selectCount()
            .from(STORE_ORDER)
            .where(STORE_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(STORE_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer count3 =db().selectCount()
            .from(SERVICE_ORDER)
            .where(SERVICE_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(SERVICE_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer count4 =db().selectCount()
            .from(CARD_RENEW)
            .where(CARD_RENEW.ADD_TIME.greaterOrEqual(start))
            .and(CARD_RENEW.ADD_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer count5 =db().selectCount()
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(VIRTUAL_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer totalNum = count1+count2+count3+count4+count5;
        return totalNum;
    }
    /**
     *  下单人数(生成订单就算)
     * @param start 开始时间
     * @param end   结束时间
     * @return 下单笔数(生成订单就算)
     */
    public Integer generateOrderUserNum(Timestamp start,Timestamp end){
        Integer count1 =db().select(DSL.countDistinct(ORDER_INFO.USER_ID))
            .from(ORDER_INFO)
            .where(ORDER_INFO.CREATE_TIME.greaterOrEqual(start))
            .and(ORDER_INFO.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer count2 =db().select(DSL.countDistinct(STORE_ORDER.USER_ID))
            .from(STORE_ORDER)
            .where(STORE_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(STORE_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer count3 =db().select(DSL.countDistinct(SERVICE_ORDER.USER_ID))
            .from(SERVICE_ORDER)
            .where(SERVICE_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(SERVICE_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer count4 =db().select(DSL.countDistinct(CARD_RENEW.USER_ID))
            .from(CARD_RENEW)
            .where(CARD_RENEW.ADD_TIME.greaterOrEqual(start))
            .and(CARD_RENEW.ADD_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer count5 =db().select(DSL.countDistinct(VIRTUAL_ORDER.USER_ID))
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(VIRTUAL_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class)
            .orElse(INTEGER_ZERO);
        Integer totalNum = count1+count2+count3+count4+count5;
        return totalNum;
    }
    /**
     * 付款人数
     * @param start 开始时间
     * @param end   结束时间
     */
    public Integer orderPeopleNum(Timestamp start,Timestamp end){
        //实物商品订单
        Integer count1 = consumeOrderPeolple(start, end);
        //门店买单订单
        Integer count2 = storeConsumeOrderPeolple(start, end);
        //门店服务订单
        Integer count3 = serviceConsumeOrderPeolple(start, end);
        //会员卡续费订单
        Integer count4 = cardRenewOrderPeolple(start, end);
        //虚拟订单
        Integer count5 = virtualOrderPeolple(start, end);
        Integer totalNum = count1+count2+count3+count4+count5;
        return totalNum;
    }
    /**
     * 实物商品订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private Integer consumeOrderPeolple(Timestamp start,Timestamp end){
        return db().select(DSL.count(ORDER_INFO.USER_ID))
            .from(ORDER_INFO)
            .where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY))
            .and(ORDER_INFO.CREATE_TIME.greaterOrEqual(start))
            .and(ORDER_INFO.CREATE_TIME.lessThan(end))
            .and(ORDER_INFO.IS_COD.eq(BYTE_ZERO).or(ORDER_INFO.IS_COD.eq(BYTE_ONE).and(ORDER_INFO.SHIPPING_TIME.isNotNull())))
            .and(ORDER_INFO.ORDER_SN.eq(ORDER_INFO.MAIN_ORDER_SN).or(ORDER_INFO.MAIN_ORDER_SN.eq("")))
            .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }
    /**
     * 门店买单订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private Integer storeConsumeOrderPeolple(Timestamp start,Timestamp end) {
        return  db().select(DSL.count(STORE_ORDER.USER_ID))
            .from(STORE_ORDER)
            .where(STORE_ORDER.ORDER_STATUS.greaterOrEqual(BYTE_ONE))
            .and(STORE_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(STORE_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }
    /**
     * 门店服务订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private Integer serviceConsumeOrderPeolple(Timestamp start,Timestamp end) {
        List<Byte> orderStatus = new ArrayList<Byte>(){{
            add((byte)0);
            add((byte)2);
        }};
        return db().select(DSL.count(SERVICE_ORDER.USER_ID))
            .from(SERVICE_ORDER)
            .where(SERVICE_ORDER.ORDER_STATUS.in(orderStatus))
            .and(SERVICE_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(SERVICE_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }
    /**
     * 会员卡续费订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private Integer cardRenewOrderPeolple(Timestamp start,Timestamp end) {
        return db().select(DSL.count(CARD_RENEW.USER_ID))
            .from(CARD_RENEW)
            .where(CARD_RENEW.ORDER_STATUS.eq(BYTE_ONE))
            .and(CARD_RENEW.ADD_TIME.greaterOrEqual(start))
            .and(CARD_RENEW.ADD_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }
    /**
     * 会员卡和优惠券礼包购买订单-已付订单统计
     * @param start 开始时间
     * @param end   结束时间
     * @return      订单用户
     */
    private Integer virtualOrderPeolple(Timestamp start,Timestamp end) {
        return db().select(DSL.count(VIRTUAL_ORDER.USER_ID))
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.ORDER_STATUS.eq(BYTE_ONE))
            .and(VIRTUAL_ORDER.CREATE_TIME.greaterOrEqual(start))
            .and(VIRTUAL_ORDER.CREATE_TIME.lessThan(end))
            .fetchOptionalInto(Integer.class).orElse(INTEGER_ZERO);
    }
}

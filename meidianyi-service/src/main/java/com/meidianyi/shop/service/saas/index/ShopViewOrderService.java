package com.meidianyi.shop.service.saas.index;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.util.MathUtil;
import com.meidianyi.shop.db.main.tables.records.UserSummaryTrendRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.saas.index.cache.ThreadLocalCache;
import com.meidianyi.shop.service.saas.index.vo.*;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static com.meidianyi.shop.db.main.tables.OrderInfoBak.ORDER_INFO_BAK;
import static com.meidianyi.shop.db.main.tables.UserSummaryTrend.USER_SUMMARY_TREND;

/**
 * 数据统计-概览
 * @author luguangyao
 */
@Service
public class ShopViewOrderService extends MainBaseService {


    public AllOrderDataViewVo getAllOrderDataViewVo(){
        ThreadLocalCache.timestampThreadLocal.set(LocalDate.now());
        LocalDate date = ThreadLocalCache.timestampThreadLocal.get().minusDays(7);
        ThreadLocalCache.sevenDayTimestamp.set(Timestamp.valueOf(date.atTime(0,0,0)));
        AllOrderDataViewVo vo = new AllOrderDataViewVo();
        vo.setOrderPayViewInfo(getOrderPayViewInfo());
        vo.setOrderShipViewInfo(getOrderShipViewInfo());
        vo.setOrderStatusViewInfo(getOrderStatusViewInfo());
        vo.setDisplayInfoList(getDataDisplayInfos());
        ThreadLocalCache.sevenDayTimestamp.remove();
        ThreadLocalCache.timestampThreadLocal.remove();
        return null;
    }

    private OrderStatusViewInfo getOrderStatusViewInfo(){
        OrderStatusViewInfo info = new OrderStatusViewInfo();
        info.setTotalNum(getTotalNum());
        info.setFinishedNum(getFinishedNum());
        info.setSevenCancelledNum(getSevenCancelledNum());
        info.setSevenClosedNum(getSevenClosedNum());
        info.setSevenFinishedNum(getSevenFinishedNum());
        info.setSevenNoPayNum(getSevenNoPayNum());
        info.setSevenRefundNum(getSevenRefundNum());
        return info;
    }

    private OrderShipViewInfo getOrderShipViewInfo(){
        OrderShipViewInfo info = new OrderShipViewInfo();
        info.setTotalBeShippedNum(getTotalBeShippedNum());
        info.setTotalPickedUpNum(getTotalPickedUpNum());
        info.setTotalPickUpNum(getTotalPickUpNum());
        info.setTotalShippedNum(getTotalShippedNum());
        info.setSevenBeShippedNum(getSevenBeShippedNum());
        info.setSevenPickedUpNum(getSevenPickedUpNum());
        info.setSevenPickUpNum(getSevenPickUpNum());
        info.setSevenShippedNum(getSevenShippedNum());
        return  info;
    }

    private OrderPayViewInfo getOrderPayViewInfo(){
        OrderPayViewInfo info = new OrderPayViewInfo();
        info.setTotalRefundNum(getTotalRefundNum());
        info.setNotRefundedNum(getNotRefundNum());
        info.setRefundedNum(getRefundedNum());
        info.setRefuseRefundNum(getRefuseRefundNum());
        info.setSevenNotRefundedNum(getSevenNotRefundedNum());
        info.setSevenRefundedNum(getSevenRefundedNum());
        info.setSevenRefuseRefundNum(getSevenRefuseRefundNum());
        return info;
    }


    private List<DataDisplayInfo> getDataDisplayInfos(){
        List<DataDisplayInfo> list = Lists.newArrayList();

        Date oneDay = Date.valueOf(ThreadLocalCache.timestampThreadLocal.get().minusDays(1));
        Date weekly = Date.valueOf(ThreadLocalCache.timestampThreadLocal.get().minusDays(8));
        Date month = Date.valueOf(ThreadLocalCache.timestampThreadLocal.get().minusDays(31));
        Date threeMonth = Date.valueOf(ThreadLocalCache.timestampThreadLocal.get().minusDays(91));
        list.add(build(getDataRecord((byte)1,oneDay)));
        list.add(build(getDataRecord((byte)7,weekly)));
        list.add(build(getDataRecord((byte)30,month)));
        list.add(build(getDataRecord((byte)90,threeMonth)));
        return list;
    }


    public DataDisplayInfo build(UserSummaryTrendRecord record){
        DataDisplayInfo info = new DataDisplayInfo();
        info.setOrderMoney(record.getTotalPaidMoney().toString());
        info.setOrderNum(record.getOrderNum());
        info.setOrderUserNum(record.getOrderUserData());
        info.setPayedOrderNum(record.getPayOrderNum());
        info.setPayedOrderUserNum(record.getOrderUserData());
        info.setVisitUserNum(record.getLoginData());
        info.setVisitToPayedData(
            Double.valueOf(MathUtil.deciMal(record.getOrderUserData(),record.getLoginData())).toString());
        info.setVisitToOrderData(
            Double.valueOf(MathUtil.deciMal(record.getOrderNum(),record.getLoginData())).toString());
        info.setOrderToPayedData(
            Double.valueOf(MathUtil.deciMal(record.getOrderUserData(),record.getOrderNum())).toString());
        return info;
    }
    /**
     * 查询数据
     * @param type 1,7,30,90
     * @param date 查询日期
     * @return {@link UserSummaryTrendRecord}或者 null
     */
    private UserSummaryTrendRecord getDataRecord(Byte type, Date date){
        return db().selectFrom(USER_SUMMARY_TREND).
            where(USER_SUMMARY_TREND.REF_DATE.eq(date).and(USER_SUMMARY_TREND.TYPE.eq(type))).fetchOne();
    }
    /**
     * 获取7天内拒绝退款订单数量
     * @return 订单数量
     */
    private Integer getSevenRefuseRefundNum() {
        Condition condition = ORDER_INFO_BAK.REFUND_STATUS.in(OrderConstant.REFUND_STATUS_AUDIT_NOT_PASS,
            OrderConstant.REFUND_STATUS_REFUSE);
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }

    /**
     * 7天内已退款订单数量
     * @return 订单数量
     */
    private Integer getSevenRefundedNum() {
        Condition condition = ORDER_INFO_BAK.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_FINISH);
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }
    /**
     * 7天内退款订单数量
     * @return 订单数量
     */
    private Integer getSevenNotRefundedNum() {
        Condition condition = ORDER_INFO_BAK.REFUND_STATUS.
            in(OrderConstant.REFUND_STATUS_AUDITING,
                OrderConstant.REFUND_STATUS_AUDIT_PASS,
                OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }
    /**
     * 获取拒绝退款订单数量
     * @return 订单数量
     */
    private Integer getRefuseRefundNum() {
        Condition condition = ORDER_INFO_BAK.REFUND_STATUS.in(OrderConstant.REFUND_STATUS_AUDIT_NOT_PASS,
            OrderConstant.REFUND_STATUS_REFUSE);
        return getOrderNumByCondition(condition);
    }

    /**
     * 已退款订单数量
     * @return 订单数量
     */
    private Integer getRefundedNum() {
        Condition condition = ORDER_INFO_BAK.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_FINISH);
        return getOrderNumByCondition(condition);
    }
    /**
     * 待退款订单数量
     * @return 订单数量
     */
    private Integer getNotRefundNum() {
        Condition condition = ORDER_INFO_BAK.REFUND_STATUS.
            in(OrderConstant.REFUND_STATUS_AUDITING,
                OrderConstant.REFUND_STATUS_AUDIT_PASS,
                OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
        return getOrderNumByCondition(condition);
    }
    /**
     * 总退款订单数量
     * @return 订单数量
     */
    private Integer getTotalRefundNum() {
        Condition condition = ORDER_INFO_BAK.REFUND_STATUS.gt(OrderConstant.REFUND_DEFAULT_STATUS);
        return getOrderNumByCondition(condition);
    }

    /**
     * 获取七天内待发货的数量
     * @return 订单数量
     */
    private Integer getSevenBeShippedNum() {
        Condition condition = ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_DELIVERY).
            and(ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_COURIER));
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }
    /**
     * 获取七天内发货订单总数量
     * @return 订单数量
     */
    private Integer getSevenShippedNum() {
        Condition condition = ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_COURIER).
            and(ORDER_INFO_BAK.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY)).
            and(ORDER_INFO_BAK.SHIPPING_ID.ne((byte)0));
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }

    /**
     * 获取七天内待自提订单总数量
     * @return 订单数量
     */
    private Integer getSevenPickUpNum() {
        Condition condition = ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_SELF).
            and(ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_DELIVERY));
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }



    /**
     * 获取七天内自提订单的数量
     * @return 订单数量
     */
    private Integer getSevenPickedUpNum() {
        Condition condition = ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_SELF);
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }
    /**
     * 获取发货订单总数量
     * @return 订单数量
     */
    private Integer getTotalShippedNum() {
        Condition condition = ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_COURIER).
            and(ORDER_INFO_BAK.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_WAIT_DELIVERY)).
            and(ORDER_INFO_BAK.SHIPPING_ID.ne((byte)0));
        return getOrderNumByCondition(condition);
    }

    /**
     * 获取待自提订单总数量
     * @return 订单数量
     */
    private Integer getTotalPickUpNum() {
        Condition condition = ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_SELF).
            and(ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_DELIVERY));
        return getOrderNumByCondition(condition);
    }



    /**
     * 获取自提订单的数量
     * @return 订单数量
     */
    private Integer getTotalPickedUpNum() {
        Condition condition = ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_SELF);
        return getOrderNumByCondition(condition);
    }

    /**
     * 获取待发货订单的总数量
     * @return 订单数量
     */
    private Integer getTotalBeShippedNum() {
        Condition condition = ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_DELIVERY).
            and(ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_COURIER));
        return getOrderNumByCondition(condition);
    }

    /**
     * 获取7天内申请退款订单数量
     * @return 订单数量
     */
    private Integer getSevenRefundNum() {
        Condition condition = ORDER_INFO_BAK.REFUND_STATUS.gt(OrderConstant.REFUND_DEFAULT_STATUS);
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }

    /**
     * 获取7天内未支付的订单
     * @return 订单数量
     */
    private Integer getSevenNoPayNum() {
        Condition condition = ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_PAY).
            and(ORDER_INFO_BAK.DELIVER_TYPE.eq(OrderConstant.DELIVER_TYPE_SELF));
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }

    /**
     * 获取7天内关闭的订单数量
     * @return 订单数量
     */
    private Integer getSevenClosedNum() {
        Condition condition = ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_CLOSED);
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }

    /**
     * 获取7天内取消的订单数量
     * @return 订单数量
     */
    private Integer getSevenCancelledNum(){
        Condition condition = ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_CANCELLED);
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }


    /**
     * 获取订单总数
     * @return 订单总数
     */
    private Integer getTotalNum(){
        Condition condition = DSL.noCondition();
        return getOrderNumByCondition(condition);
    }
    /**
     * 获取7天内已完成订单数量
     * @return 订单数量
     */
    private Integer getSevenFinishedNum(){
        Condition condition = ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_FINISHED);
        return getOrderNumByCondition(buildSevenDayCondition(condition));
    }
    /**
     * 获取已完成订单数量
     * @return 订单数量
     */
    private Integer getFinishedNum(){
        Condition condition = ORDER_INFO_BAK.ORDER_STATUS.eq(OrderConstant.ORDER_FINISHED);
        return getOrderNumByCondition(condition);
    }

    /**
     * 根据条件获得订单的统计数量
     * @param condition 条件
     * @return 统计数量
     */
    private Integer getOrderNumByCondition(Condition condition){
        return db().selectCount().from(ORDER_INFO_BAK).where(condition).fetchOne(0,Integer.class);
    }

    private Condition buildSevenDayCondition(Condition condition){
        return condition.and(ORDER_INFO_BAK.CREATE_TIME.greaterOrEqual(ThreadLocalCache.sevenDayTimestamp.get()));
    }

}

package com.meidianyi.shop.dao.shop.order;

import com.meidianyi.shop.common.pojo.main.table.ReturnOrderBakDo;
import com.meidianyi.shop.common.pojo.shop.table.ReturnOrderDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveDiscountMoney;
import com.meidianyi.shop.service.pojo.shop.order.report.MedicalOrderReportVo;
import com.meidianyi.shop.service.pojo.wxapp.order.refund.ReturnOrderListMp;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.ORDER_INFO;
import static com.meidianyi.shop.db.shop.Tables.RETURN_ORDER;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_AUDITING;
import static org.jooq.impl.DSL.date;
import static org.jooq.impl.DSL.sum;

/**
 * 退款订单
 * @author 孔德成
 * @date 2020/7/31 17:34
 */
@Repository
public class ReturnOrderDao extends ShopBaseDao {


    /**
     * 	通过orderSn查询退货订单信息
     * @param orderSn
     * @return Result<?>
     */
    public List<ReturnOrderListMp> listByOrderSn(String orderSn) {
       return db().selectFrom(RETURN_ORDER).where(RETURN_ORDER.ORDER_SN.eq(orderSn)).fetchInto(ReturnOrderListMp.class);
    }


    /**
     * 获取时间内退款订单的报表
     * @param startTime
     * @param endTime
     * @return
     */
    public Map<Date, MedicalOrderReportVo> medicalOrderSalesReport(Timestamp startTime, Timestamp  endTime){
        return db().select(
                //日期
                date(RETURN_ORDER.CREATE_TIME).as(ActiveDiscountMoney.CREATE_TIME),
                //退款金额
                sum((RETURN_ORDER.MONEY).add(RETURN_ORDER.SHIPPING_FEE)).as(ActiveDiscountMoney.RETURN_AMOUNT),
                //退款单数
                DSL.count().as(ActiveDiscountMoney.RETURN_NUMBER))
                .from(RETURN_ORDER)
                .where(RETURN_ORDER.CREATE_TIME.between(startTime, endTime))
                .and(RETURN_ORDER.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_FINISH))
                .groupBy(date(RETURN_ORDER.CREATE_TIME))
                .orderBy(RETURN_ORDER.CREATE_TIME)
                .fetchMap(date(RETURN_ORDER.CREATE_TIME).as(ActiveDiscountMoney.CREATE_TIME), MedicalOrderReportVo.class);
    }



    public List<ReturnOrderBakDo> listCreateOrderByYesterday(Timestamp beginTime, Timestamp endTime) {
        return  db().selectFrom(RETURN_ORDER)
                .where(RETURN_ORDER.CREATE_TIME.ge(beginTime)).and(RETURN_ORDER.CREATE_TIME.le(endTime))
                .fetchInto(ReturnOrderBakDo.class);
    }

    public List<ReturnOrderBakDo> listUpdateOrderByYesterday(Timestamp beginTime, Timestamp endTime) {
        return  db().selectFrom(RETURN_ORDER)
                .where(RETURN_ORDER.UPDATE_TIME.ge(beginTime)).and(RETURN_ORDER.UPDATE_TIME.le(endTime))
                .and(RETURN_ORDER.CREATE_TIME.le(beginTime))
                .fetchInto(ReturnOrderBakDo.class);
    }

    /**
     * 获取门店售后订单数量
     * 订单待处理(审核中,审核通过,买家提交物流)
     * @param returnStatusList
     * @param storeId
     * @return
     */
    public Integer getCountByReturnStatus(List<Byte> returnStatusList,Integer storeId){
        return db().select(DSL.isnull(DSL.countDistinct(RETURN_ORDER.ORDER_SN),0)).from(RETURN_ORDER).
            leftJoin(ORDER_INFO).on(RETURN_ORDER.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
            .where(RETURN_ORDER.REFUND_STATUS.in(returnStatusList))
                .and(ORDER_INFO.STORE_ID.eq(storeId))
        .fetchAnyInto(Integer.class);
    }


    /**
     * 获取订单得退款状态
     * 订单待处理(审核中,审核通过,买家提交物流)
     * @param orderSnList
     * @return
     */
    public  Map<String, List<ReturnOrderDo>>listPendingReturnOrderDo(List<String> orderSnList){
        return db().select(RETURN_ORDER.asterisk())
                .from(RETURN_ORDER)
                .where(RETURN_ORDER.ORDER_SN.in(orderSnList))
                .and(RETURN_ORDER.REFUND_STATUS.in(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING,REFUND_STATUS_AUDITING))
                .fetchGroups(RETURN_ORDER.ORDER_SN, ReturnOrderDo.class);
    }
}

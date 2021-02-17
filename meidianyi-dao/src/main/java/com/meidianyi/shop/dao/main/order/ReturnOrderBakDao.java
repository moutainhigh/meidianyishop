package com.meidianyi.shop.dao.main.order;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveDiscountMoney;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnListVo;
import com.meidianyi.shop.service.pojo.shop.order.report.MedicalOrderReportVo;
import org.elasticsearch.common.Strings;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.main.Tables.ORDER_GOODS_BAK;
import static com.meidianyi.shop.db.main.Tables.RETURN_ORDER_BAK;
import static com.meidianyi.shop.db.main.Tables.RETURN_ORDER_GOODS_BAK;
import static com.meidianyi.shop.db.main.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.RETURN_ORDER;
import static org.jooq.impl.DSL.date;
import static org.jooq.impl.DSL.sum;

/**
 * @author 孔德成
 * @date 2020/8/20 17:11
 */
@Component
public class ReturnOrderBakDao extends MainBaseDao {

    /**
     * 获取时间内退款订单的报表
     * @param startTime
     * @param endTime
     * @param shopId
     * @return
     */
    public Map<Date, MedicalOrderReportVo> medicalOrderSalesReport(Timestamp startTime, Timestamp endTime, Integer shopId){
        SelectConditionStep<? extends Record> where = db().select(
                //日期
                date(RETURN_ORDER_BAK.CREATE_TIME).as(ActiveDiscountMoney.CREATE_TIME),
                //退款金额
                sum((RETURN_ORDER_BAK.MONEY).add(RETURN_ORDER_BAK.SHIPPING_FEE)).as(ActiveDiscountMoney.RETURN_AMOUNT),
                //退款单数
                DSL.count().as(ActiveDiscountMoney.RETURN_NUMBER))
                .from(RETURN_ORDER_BAK)
                .where(RETURN_ORDER_BAK.CREATE_TIME.between(startTime, endTime))
                .and(RETURN_ORDER.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_FINISH));
        if (shopId!=null&&shopId>0){
            where.and(RETURN_ORDER_BAK.SHOP_ID.eq(shopId));
        }

       return where.groupBy(date(RETURN_ORDER_BAK.CREATE_TIME))
                .orderBy(RETURN_ORDER_BAK.CREATE_TIME)
                .fetchMap(date(RETURN_ORDER_BAK.CREATE_TIME).as(ActiveDiscountMoney.CREATE_TIME), MedicalOrderReportVo.class);
    }

    /**
     * 	综合查询退订单
     * @param param
     * @return
     */
    public PageResult<OrderReturnListVo> getPageList(OrderPageListQueryParam param) {
        SelectJoinStep<Record> select = db().select(RETURN_ORDER_BAK.asterisk(),USER.USERNAME.as(OrderReturnListVo.ORDER_USERNAME),USER.MOBILE.as(OrderReturnListVo.ORDER_MOBILE))
                .from(RETURN_ORDER_BAK)
                .innerJoin(USER).on(RETURN_ORDER_BAK.USER_ID.eq(USER.USER_ID).and(RETURN_ORDER_BAK.SHOP_ID.eq(USER.SHOP_ID)));
        buildOptionsReturn(select, param);
        return getPageResult(select,param.getCurrentPage(),param.getPageRows(),OrderReturnListVo.class);
    }
    /**
     * 构造退货、款查询条件
     *
     * @param select
     * @param param
     * @return
     */
    private SelectWhereStep<?> buildOptionsReturn(SelectJoinStep<?> select, OrderPageListQueryParam param) {
        // 自增id排序
        select.orderBy(RETURN_ORDER_BAK.RET_ID.desc());

        if (!StringUtils.isEmpty(param.getOrderSn())) {
            select.where(RETURN_ORDER_BAK.ORDER_SN.like(likeValue(param.getOrderSn())));
        }
        if (!StringUtils.isEmpty(param.getReturnOrderSn())) {
            select.where(RETURN_ORDER_BAK.RETURN_ORDER_SN.like(likeValue(param.getReturnOrderSn())));
        }
        if (param.getShopId()!=null){
            select.where(RETURN_ORDER_BAK.SHOP_ID.eq(param.getShopId()));
        }
        if (param.getRefundStatus() != null) {
            switch (param.getRefundStatus()) {
                case OrderConstant.SEARCH_RETURNSTATUS_14:
                    select.where(RETURN_ORDER_BAK.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_AUDITING).or(RETURN_ORDER_BAK.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING).and(RETURN_ORDER_BAK.RETURN_TYPE.eq(OrderConstant.RT_ONLY_MONEY))));
                    break;
                case OrderConstant.SEARCH_RETURNSTATUS_41:
                    select.where(RETURN_ORDER_BAK.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING).and(RETURN_ORDER_BAK.RETURN_TYPE.eq(OrderConstant.RT_GOODS)));
                    break;
                case OrderConstant.SEARCH_RETURNSTATUS_60:
                    select.where(RETURN_ORDER_BAK.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_REFUSE).and(RETURN_ORDER_BAK.RETURN_TYPE.eq(OrderConstant.RT_ONLY_MONEY)));
                    break;
                case OrderConstant.SEARCH_RETURNSTATUS_61:
                    select.where(RETURN_ORDER_BAK.REFUND_STATUS.eq(OrderConstant.REFUND_STATUS_REFUSE).and(RETURN_ORDER_BAK.RETURN_TYPE.eq(OrderConstant.RT_GOODS)));
                    break;
                default:
                    select.where(RETURN_ORDER_BAK.REFUND_STATUS.eq(param.getRefundStatus()));
            }
        }
        if (param.getReturnType() != null && param.getReturnType().length != 0) {
            select.where(RETURN_ORDER_BAK.RETURN_TYPE.in(param.getReturnType()));
        }
        if (param.getReturnStart() != null) {
            select.where(RETURN_ORDER_BAK.CREATE_TIME.ge(param.getReturnStart()));
        }
        if (param.getReturnEnd() != null) {
            select.where(RETURN_ORDER_BAK.CREATE_TIME.le(param.getReturnEnd()));
        }
        if (param.getRetIds() != null && param.getRetIds().length != 0) {
            select.where(RETURN_ORDER_BAK.RET_ID.in(param.getRetIds()));
        }
        if(OrderConstant.SHOP_HELPER_OVERDUE_RETURN_APPLY.equals(param.getShopHelperAction())) {
            select.where(RETURN_ORDER_BAK.REFUND_STATUS.in(OrderConstant.REFUND_STATUS_AUDITING, OrderConstant.REFUND_STATUS_AUDIT_PASS, OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)
                    .and(RETURN_ORDER_BAK.CREATE_TIME.add(param.getShopHelperActionDays()).lessThan(Timestamp.valueOf(LocalDateTime.now()))));
        }
        if (param.getReturnSource() != null && param.getReturnSource().length != 0) {
            select.where(RETURN_ORDER_BAK.RETURN_SOURCE.in(param.getReturnSource()));
        }
        if (!Strings.isNullOrEmpty(param.getOrderUserNameOrMobile())){
            select.where(USER.USERNAME.like(likeValue(param.getOrderUserNameOrMobile())).or(USER.MOBILE.like(likeValue(param.getOrderUserNameOrMobile()))));
        }
        return select;
    }


    /**
     * 获取退款订单特定状态
     * @param orderIds
     * @param status
     * @return
     */
    public Map<Integer, Integer> getOrderCount(Integer[] orderIds , Byte... status) {
        return db().select(DSL.count(RETURN_ORDER_BAK.ORDER_ID).as("count") , RETURN_ORDER_BAK.ORDER_ID).
                from(RETURN_ORDER_BAK).
                where(RETURN_ORDER_BAK.ORDER_ID.in(orderIds).and(RETURN_ORDER_BAK.REFUND_STATUS.in(status))).
                groupBy(RETURN_ORDER_BAK.ORDER_ID).
                fetch().intoMap(RETURN_ORDER_BAK.ORDER_ID , DSL.count(RETURN_ORDER_BAK.ORDER_ID).as("count"));
    }

    /**
     * 获取该订单下可发货商品列表
     */
    public List<OrderGoodsVo> canBeShipped(String orderSn) {
        // TODO 修改select*
        //该单是否支持发货

        //TODO Short.valueOf("0")正常商品行
        List<OrderGoodsVo> orderGoods = db().select(ORDER_GOODS_BAK.asterisk()).from(ORDER_GOODS_BAK)
                .where(ORDER_GOODS_BAK.ORDER_SN.eq(orderSn).and(ORDER_GOODS_BAK.SEND_NUMBER.eq(0))).fetchInto(OrderGoodsVo.class);
        // 查询退货中信息
        Map<Integer, List<OrderReturnGoodsVo>> returnOrderGoods = db().select(RETURN_ORDER_GOODS_BAK.asterisk()).select()
                .from(RETURN_ORDER_GOODS_BAK)
                .where(RETURN_ORDER_GOODS_BAK.ORDER_SN.eq(orderSn),
                        RETURN_ORDER_GOODS_BAK.SUCCESS.eq(OrderConstant.SUCCESS_RETURNING))
                .fetchGroups(RETURN_ORDER_GOODS_BAK.REC_ID, OrderReturnGoodsVo.class);
        Iterator<OrderGoodsVo> iterator = orderGoods.iterator();
        while (iterator.hasNext()) {
            OrderGoodsVo vo = (OrderGoodsVo) iterator.next();
            // 可发货数量=总数-退货(退货完成)-发货-退货(退货中)
            int numTemp;
            List<OrderReturnGoodsVo> orgTemp = returnOrderGoods.get(vo.getRecId());
            int sum = orgTemp == null ? 0 : orgTemp.stream().mapToInt(OrderReturnGoodsVo::getGoodsNumber).sum();
            if ((numTemp = vo.getGoodsNumber() - vo.getReturnNumber() - vo.getSendNumber() - sum) > 0) {
                vo.setGoodsNumber(numTemp);
            } else {
                iterator.remove();
            }
        }
        return orderGoods;
    }
}

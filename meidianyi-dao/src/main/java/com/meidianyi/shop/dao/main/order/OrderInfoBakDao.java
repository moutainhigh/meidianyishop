package com.meidianyi.shop.dao.main.order;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderQueryVo;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveDiscountMoney;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.report.MedicalOrderReportVo;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.main.Tables.ORDER_GOODS_BAK;
import static com.meidianyi.shop.db.main.Tables.ORDER_INFO_BAK;
import static com.meidianyi.shop.db.main.Tables.RETURN_ORDER_BAK;
import static com.meidianyi.shop.db.main.Tables.SHOP;
import static com.meidianyi.shop.db.main.Tables.USER;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.ORDER_REFUNDING;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.ORDER_REFUND_FINISHED;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.ORDER_RETURNING;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.ORDER_RETURN_FINISHED;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.ORDER_WAIT_DELIVERY;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_DEFAULT_STATUS;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_AUDITING;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.REFUND_STATUS_AUDIT_PASS;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.RT_GOODS;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.RT_ONLY_MONEY;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.SHOP_HELPER_OVERDUE_DELIVERY;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.SHOP_HELPER_REMIND_DELIVERY;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.date;
import static org.jooq.impl.DSL.sum;

/**
 * @author 孔德成
 * @date 2020/8/18 16:08
 */
@Component
public class OrderInfoBakDao extends MainBaseDao {


    /**
     * 药品销售报表
     * @return
     */
    public Map<Date, MedicalOrderReportVo> orderSalesReport(Timestamp startTime, Timestamp endTime, Integer shopId){
        SelectConditionStep<? extends Record> where = db().select(
                //日期
                date(ORDER_INFO_BAK.CREATE_TIME).as(ActiveDiscountMoney.CREATE_TIME),
                //销售金额  微信+余额+运费
                sum((ORDER_INFO_BAK.MONEY_PAID.add(ORDER_INFO_BAK.USE_ACCOUNT))).as(ActiveDiscountMoney.ORDER_AMOUNT),
                //微信
                sum(ORDER_INFO_BAK.MONEY_PAID).as(ActiveDiscountMoney.MONEY_PAID),
                //余额
                sum(ORDER_INFO_BAK.USE_ACCOUNT).as(ActiveDiscountMoney.USE_ACCOUNT),
                //运费
                sum(ORDER_INFO_BAK.SHIPPING_FEE).as(ActiveDiscountMoney.SHIPPING_FEE),
                //销售单数
                count(ORDER_INFO_BAK.ORDER_ID).as(ActiveDiscountMoney.ORDER_NUMBER),
                //处方药订单数量
                sum(ORDER_INFO_BAK.ORDER_MEDICAL_TYPE).as(ActiveDiscountMoney.PRESCRIPTION_ORDER_NUMBER),
                //处方药金额
                sum(DSL.iif(ORDER_INFO_BAK.ORDER_MEDICAL_TYPE.eq(BaseConstant.YES),ORDER_INFO_BAK.MONEY_PAID.add(ORDER_INFO_BAK.USE_ACCOUNT),BigDecimal.ZERO)).as(ActiveDiscountMoney.PRESCRIPTION_ORDER_AMOUNT)
        )
                .from(ORDER_INFO_BAK)
                .where(ORDER_INFO_BAK.CREATE_TIME.between(startTime, endTime))
                .and(ORDER_INFO_BAK.ORDER_STATUS.notIn(
                        OrderConstant.ORDER_WAIT_PAY,
                        OrderConstant.ORDER_CANCELLED,
                        OrderConstant.ORDER_CLOSED
                ));
        if (shopId!=null&&shopId>0){
            where.and(ORDER_INFO_BAK.SHOP_ID.eq(shopId));
        }
       return where.groupBy(date(ORDER_INFO_BAK.CREATE_TIME))
                .orderBy(ORDER_INFO_BAK.CANCELLED_TIME)
                .fetchMap(date(ORDER_INFO_BAK.CREATE_TIME).as(ActiveDiscountMoney.CREATE_TIME), MedicalOrderReportVo.class);
    }


    /**
     * 按照主订单分组，正常订单的key为orderSn
     *
     * @param orderSn
     * @return
     */
    public Map<String, List<OrderListInfoVo>> getOrders(List<String> orderSn) {
        List<OrderListInfoVo> orders = db().select(ORDER_INFO_BAK.asterisk(), USER.USERNAME, USER.MOBILE.as("userMobile")
        ,SHOP.SHOP_NAME)
                .from(ORDER_INFO_BAK)
                .leftJoin(USER).on(ORDER_INFO_BAK.USER_ID.eq(USER.USER_ID).and(ORDER_INFO_BAK.SHOP_ID.eq(USER.SHOP_ID)))
                .leftJoin(SHOP).on(SHOP.SHOP_ID.eq(ORDER_INFO_BAK.SHOP_ID))
                .where(ORDER_INFO_BAK.MAIN_ORDER_SN.in(orderSn).or(ORDER_INFO_BAK.ORDER_SN.in(orderSn)))
                .orderBy(ORDER_INFO_BAK.ORDER_ID.desc())
                .fetchInto(OrderListInfoVo.class);
        orders.forEach(order -> {
            if (StringUtils.isBlank(order.getMainOrderSn())) {
                order.setLogicMainOrderSn(order.getOrderSn());
            } else {
                order.setLogicMainOrderSn(order.getMainOrderSn());
            }
        });
        Map<String, List<OrderListInfoVo>> result = new HashMap<String, List<OrderListInfoVo>>();
        for (OrderListInfoVo order : orders) {
            if (result.get(order.getLogicMainOrderSn()) != null) {
                result.get(order.getLogicMainOrderSn()).add(order);
            } else {
                ArrayList<OrderListInfoVo> orderList = new ArrayList<OrderListInfoVo>();
                orderList.add(order);
                result.put(order.getLogicMainOrderSn(), orderList);
            }
        }
        return result;
    }


    /**
     * 订单综合查询:通过条件获得订单号
     *
     * @param param
     * @return
     */
    public PageResult<String> getOrderSns(OrderPageListQueryParam param, OrderQueryVo result) {
        // 数量查询
        calculateOrderCount(param, result);
        SelectJoinStep<Record1<String>> mainOrder = db().selectDistinct(ORDER_INFO_BAK.ORDER_SN).from(ORDER_INFO_BAK);
        // 存在子单但是显示不易子单为主所以查询需过滤子单
        mainOrder.where(ORDER_INFO_BAK.ORDER_SN.eq(ORDER_INFO_BAK.MAIN_ORDER_SN).or(ORDER_INFO_BAK.MAIN_ORDER_SN.eq("")));
        buildOptions(mainOrder, param);
        buildOrderBy(mainOrder, param);
        // 得到订单号
        return getPageResult(mainOrder, param.getCurrentPage(), param.getPageRows(), String.class);
    }

    /**
     * 构造综合查询条件
     *
     * @param select
     * @param param
     * @return
     */
    public SelectWhereStep<?> buildOptions(SelectJoinStep<?> select, OrderPageListQueryParam param) {
        return buildOptions(select, param, false);
    }

    /**
     * 构造综合查询条件的排序
     *
     * @param mainOrder
     * @param param
     * @return
     */
    private void buildOrderBy(SelectJoinStep<Record1<String>> mainOrder, OrderPageListQueryParam param) {
        if (param.getSortRule() != null) {
            switch (param.getSortRule()) {
                case OrderConstant.OQSR_APPLY_RETURN: {
                    mainOrder.orderBy(RETURN_ORDER_BAK.RET_ID.desc());
                    break;
                }
                default:
            }
        } else {
            mainOrder.orderBy(ORDER_INFO_BAK.ORDER_ID.desc());
        }

    }



    /**
     * 构造综合查询条件
     *
     * @param select
     * @param param
     * @param joined select 已经连接过order_goods和user
     * @return
     */
    public SelectWhereStep<?> buildOptions(SelectJoinStep<?> select, OrderPageListQueryParam param, boolean joined) {
        // 输入商品名称需要join order_goods表
        if (!StringUtils.isBlank(param.goodsName) || !StringUtils.isBlank(param.productSn)) {
            if (!joined) {
                select.innerJoin(ORDER_GOODS_BAK).on(ORDER_INFO_BAK.ORDER_ID.eq(ORDER_GOODS_BAK.ORDER_ID));
            }
            if (!StringUtils.isBlank(param.goodsName)) {
                select.where(ORDER_GOODS_BAK.GOODS_NAME.like(likeValue(param.goodsName)));
            }
            if (!StringUtils.isBlank(param.productSn)) {
                select.where(ORDER_GOODS_BAK.PRODUCT_SN.like(likeValue(param.productSn)));
            }
        }
        if (!StringUtils.isBlank(param.orderSn)) {
            select.where(ORDER_INFO_BAK.ORDER_SN.contains(param.orderSn));
        }

        if (param.getOrderIds() != null && param.getOrderIds().length != 0) {
            select.where(ORDER_INFO_BAK.ORDER_ID.in(param.getOrderIds()));
        }
        if (param.getShopId()!=null&&param.getShopId()>0){
            select.where(ORDER_INFO_BAK.SHOP_ID.eq(param.getShopId()));
        }

        //店铺助手操作
        if (param.getShopHelperAction() != null) {
            if (param.getShopHelperAction().equals(SHOP_HELPER_OVERDUE_DELIVERY)) {
                select.where(ORDER_INFO_BAK.ORDER_STATUS.eq(ORDER_WAIT_DELIVERY)
                        .and(ORDER_INFO_BAK.CREATE_TIME.add(param.getShopHelperActionDays()).lessThan(Timestamp.valueOf(LocalDateTime.now()))));
            } else if (param.getShopHelperAction().equals(SHOP_HELPER_REMIND_DELIVERY)) {
                select.where(ORDER_INFO_BAK.ORDER_STATUS.eq(ORDER_WAIT_DELIVERY).and(ORDER_INFO_BAK.ORDER_REMIND.greaterThan(BYTE_ZERO)));
            }
        }

        processOrderStatusOption(select, param);

        processOtherOption(select, param, joined);

        processPayWayOption(select, param);

        // 构造营销活动查询条件
        activeBuildOptions(select, param);
        return select;
    }
    private void processOtherOption(SelectJoinStep<?> select, OrderPageListQueryParam param, boolean joined) {
        if (param.goodsType != null && param.goodsType.length != 0) {
            select.where(ORDER_INFO_BAK.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(param.getGoodsType())));
        }
        if (param.deliverType != null) {
            select.where(ORDER_INFO_BAK.DELIVER_TYPE.eq(param.deliverType));
        }
        // 昵称、会员标签tag需要连表查询
        boolean hasNameOrTagId = !StringUtils.isBlank(param.userName) || (param.tagIds != null && param.tagIds.length != 0);
        if (hasNameOrTagId) {
            if (!joined) {
                select.innerJoin(USER).on(ORDER_INFO_BAK.USER_ID.eq(USER.USER_ID));
            }
            if (!StringUtils.isBlank(param.userName)) {
                select.where(USER.USERNAME.like(likeValue(param.userName)));
            }
        }
        if (param.getUserId() != null) {
            select.where(ORDER_INFO_BAK.USER_ID.eq(param.getUserId()));
        }
        if (!StringUtils.isBlank(param.source)) {
            select.where(ORDER_INFO_BAK.SOURCE.eq(param.source));
        }
        if (param.storeId != null) {
            select.where(ORDER_INFO_BAK.STORE_ID.eq(param.storeId));
        }
        if (!StringUtils.isBlank(param.verifyCode)) {
            select.where(ORDER_INFO_BAK.VERIFY_CODE.eq(param.verifyCode));
        }
        if (!StringUtils.isBlank(param.consignee)) {
            select.where(ORDER_INFO_BAK.CONSIGNEE.contains(param.consignee));
        }
        if (!StringUtils.isBlank(param.getMobile())) {
            select.where(ORDER_INFO_BAK.MOBILE.contains(param.getMobile()));
        }
        if (param.countryCode != null) {
            select.where(ORDER_INFO_BAK.COUNTRY_CODE.eq(param.countryCode));
        }
        if (param.provinceCode != null) {
            select.where(ORDER_INFO_BAK.PROVINCE_CODE.eq(param.provinceCode));
        }
        if (param.cityCode != null) {
            select.where(ORDER_INFO_BAK.CITY_CODE.eq(param.cityCode));
        }
        if (param.districtCode != null) {
            select.where(ORDER_INFO_BAK.DISTRICT_CODE.eq(param.districtCode));
        }
        if (param.createTimeStart != null) {
            select.where(ORDER_INFO_BAK.CREATE_TIME.ge(param.createTimeStart));
        }
        if (param.createTimeEnd != null) {
            select.where(ORDER_INFO_BAK.CREATE_TIME.le(param.createTimeEnd));
        }
        if (param.finishedTimeStart != null) {
            select.where(ORDER_INFO_BAK.FINISHED_TIME.ge(param.finishedTimeStart));
        }
        if (param.finishedTimeEnd != null) {
            select.where(ORDER_INFO_BAK.FINISHED_TIME.le(param.finishedTimeEnd));
        }
        if (param.getIsStar() != null) {
            select.where(ORDER_INFO_BAK.STAR_FLAG.eq(param.getIsStar()));
        }
    }

    private void processPayWayOption(SelectJoinStep<?> select, OrderPageListQueryParam param) {
        // 支付方式
        if (param.payWay != null) {
            switch (param.payWay) {
                case OrderConstant.SEARCH_PAY_WAY_USE_ACCOUNT:
                    select.where(ORDER_INFO_BAK.USE_ACCOUNT.greaterThan(BigDecimal.ZERO));
                    break;
                case OrderConstant.SEARCH_PAY_WAY_SCORE_DISCOUNT:
                    select.where(ORDER_INFO_BAK.SCORE_DISCOUNT.greaterThan(BigDecimal.ZERO));
                    break;
                case OrderConstant.SEARCH_PAY_WAY_SCORE_EXCHANGE:
                    select.where(ORDER_INFO_BAK.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[]{BaseConstant.ACTIVITY_TYPE_INTEGRAL})));
                    break;
                case OrderConstant.SEARCH_PAY_WAY_COD:
                    select.where(ORDER_INFO_BAK.PAY_CODE.eq(OrderConstant.PAY_CODE_COD));
                    break;
                case OrderConstant.SEARCH_PAY_WAY_EVENT_PRIZE:
                    select.where(ORDER_INFO_BAK.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[]{BaseConstant.ACTIVITY_TYPE_MY_PRIZE})));
                    break;
                case OrderConstant.SEARCH_PAY_WAY_WXPAY:
                    select.where(ORDER_INFO_BAK.PAY_CODE.eq(OrderConstant.PAY_CODE_WX_PAY));
                    break;
                default:
                    break;
            }
        }
    }

    private void processOrderStatusOption(SelectJoinStep<?> select, OrderPageListQueryParam param) {
        if (param.orderStatus != null && param.orderStatus.length != 0) {
            List<Byte> status = Lists.newArrayList(param.orderStatus);
            Condition condition = DSL.noCondition();
            if (status.contains(ORDER_RETURNING) || status.contains(ORDER_REFUNDING) || status.contains(ORDER_RETURN_FINISHED) || status.contains(ORDER_REFUND_FINISHED)) {
                select.leftJoin(RETURN_ORDER_BAK).on(ORDER_INFO_BAK.ORDER_ID.eq(RETURN_ORDER_BAK.ORDER_ID));
                if (status.contains(ORDER_REFUNDING)) {
                    condition = condition.or(RETURN_ORDER_BAK.RETURN_TYPE.eq(RT_ONLY_MONEY).and(RETURN_ORDER_BAK.REFUND_STATUS.eq(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)));
                    status.remove(Byte.valueOf(ORDER_REFUNDING));
                }
                if (status.contains(ORDER_RETURNING)) {
                    condition = condition.or(RETURN_ORDER_BAK.RETURN_TYPE.eq(RT_GOODS).and(RETURN_ORDER_BAK.REFUND_STATUS.in(REFUND_DEFAULT_STATUS, REFUND_STATUS_AUDITING, REFUND_STATUS_AUDIT_PASS, REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)));
                    status.remove(Byte.valueOf(ORDER_RETURNING));
                }
            }
            if (CollectionUtils.isNotEmpty(status)) {
                condition = condition.or(ORDER_INFO_BAK.ORDER_STATUS.in(status));
            }
            select.where().and(condition);
        }
    }
    /**
     * 构造营销订查询条件
     *
     * @param select
     * @param param
     * @return
     */
    public SelectWhereStep<?> activeBuildOptions(SelectJoinStep<?> select, OrderPageListQueryParam param) {
        if (param.activityId != null) {
            if (param.goodsType != null && param.goodsType.length != 0 && Arrays.asList(param.getGoodsType()).contains(BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL)) {
                //首单特惠的活动ID记录在订单商品行内
                select.where(ORDER_GOODS_BAK.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL)).and(ORDER_GOODS_BAK.ACTIVITY_ID.eq(param.getActivityId()));
            } else if (param.goodsType != null && param.goodsType.length != 0 && Arrays.asList(param.getGoodsType()).contains(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE)) {
                //限时降价的活动ID记录在订单商品行内
                select.where(ORDER_GOODS_BAK.ACTIVITY_TYPE.eq(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE)).and(ORDER_GOODS_BAK.ACTIVITY_ID.eq(param.getActivityId()));
            } else {
                select.where(ORDER_INFO_BAK.ACTIVITY_ID.eq(param.activityId));
            }
        }
        if (param.getRoomId() != null) {
            select.where(ORDER_INFO_BAK.ROOM_ID.eq(param.getRoomId()));
        }
        return select;
    }
    /**
     * 计算部分订单数量
     *
     * @param result
     */
    private void calculateOrderCount(OrderPageListQueryParam param, OrderQueryVo result) {
        Map<Byte, Integer> count = new HashMap<>(OrderConstant.SEARCH_RETURN_COMPLETED);
        for (int i = 1; i <= OrderConstant.SEARCH_RETURN_COMPLETED; i++) {
            count.put(Integer.valueOf(i).byteValue(), calculateOrderCount(param, Integer.valueOf(i).byteValue()));
        }
        result.setCount(count);
    }
    private Integer calculateOrderCount(OrderPageListQueryParam param, byte type) {
        SelectJoinStep<Record1<String>> select = db().select(ORDER_INFO_BAK.ORDER_SN).from(ORDER_INFO_BAK);
        // 存在子单但是显示不易子单为主所以查询需过滤子单
        select.where(ORDER_INFO_BAK.ORDER_SN.eq(ORDER_INFO_BAK.MAIN_ORDER_SN).or(ORDER_INFO_BAK.MAIN_ORDER_SN.eq("")));
        buildOptions(select, param);
        switch (type) {
            case OrderConstant.SEARCH_WAIT_DELIVERY:
                // 待发货
                return db().fetchCount(select.where(ORDER_INFO_BAK.ORDER_STATUS.eq(ORDER_WAIT_DELIVERY).and(
                        ORDER_INFO_BAK.DELIVER_TYPE.in(OrderConstant.DELIVER_TYPE_COURIER, OrderConstant.CITY_EXPRESS_SERVICE))));
            case OrderConstant.SEARCH_WAIT_TAKEDELIVER:
                // 待核销
                return db().fetchCount(select.where(ORDER_INFO_BAK.ORDER_STATUS.eq(ORDER_WAIT_DELIVERY)
                        .and(ORDER_INFO_BAK.DELIVER_TYPE.in(OrderConstant.DELIVER_TYPE_SELF))));
            case OrderConstant.SEARCH_RETURNING:
                // 退款中
                return db().fetchCount(select.where(ORDER_INFO_BAK.REFUND_STATUS.in(OrderConstant.REFUND_STATUS_AUDITING,
                        OrderConstant.REFUND_STATUS_AUDIT_PASS, OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)));
            case OrderConstant.SEARCH_RETURN_COMPLETED:
                // 退完成
                return db().fetchCount(select.where(ORDER_INFO_BAK.REFUND_STATUS.in(OrderConstant.REFUND_STATUS_FINISH)));
            default:
                return 0;
        }
    }

    /**
     * 订单goodsType查询构造
     *
     * @param goodsType
     */
    public static String getGoodsTypeToSearch(Byte[] goodsType) {
        StringBuilder sbr = new StringBuilder();
        for (Byte one : goodsType) {
            // Prefix
            sbr.append("\\[");
            // 查找条件
            sbr.append(one);
            // suffix
            sbr.append("\\]");
            // 与条件
            sbr.append("|");
        }
        return sbr.deleteCharAt(sbr.length() - 1).toString();
    }


    /**
     * 设置订单支付方式数组
     *
     * @param order
     */
    public void setPayCodeList(OrderListInfoVo order) {
        ArrayList<Byte> payCodes = new ArrayList<Byte>(OrderConstant.SEARCH_PAY_WAY_WXPAY);
        //订单类型
        List<Byte> orderType = Arrays.asList(orderTypeToByte(order.getGoodsType()));
        if (BigDecimalUtil.compareTo(order.getUseAccount(), null) > 0
                || BigDecimalUtil.compareTo(order.getMemberCardBalance(), null) > 0) {
            /** 余额 */
            payCodes.add(OrderConstant.SEARCH_PAY_WAY_USE_ACCOUNT);
        }
        if (BigDecimalUtil.compareTo(order.getScoreDiscount(), null) > 0) {
            /** 积分支付 */
            payCodes.add(OrderConstant.SEARCH_PAY_WAY_SCORE_DISCOUNT);
        }
        if (orderType.contains(BaseConstant.ACTIVITY_TYPE_INTEGRAL)) {
            /**积分兑换*/
            payCodes.add(OrderConstant.SEARCH_PAY_WAY_SCORE_EXCHANGE);
        }
        if (OrderConstant.PAY_CODE_COD.equals(order.getPayCode())) {
            /** 货到付款 */
            payCodes.add(OrderConstant.SEARCH_PAY_WAY_COD);
        }
        if (orderType.contains(BaseConstant.ACTIVITY_TYPE_MY_PRIZE)) {
            /** 活动奖品 */
            payCodes.add(OrderConstant.SEARCH_PAY_WAY_EVENT_PRIZE);
        }
        if (OrderConstant.PAY_CODE_WX_PAY.equals(order.getPayCode())) {
            /** 微信支付 */
            payCodes.add(OrderConstant.SEARCH_PAY_WAY_WXPAY);
        }
        if (CollectionUtils.isEmpty(payCodes)) {
            payCodes.add(OrderConstant.SEARCH_PAY_WAY_USE_ACCOUNT);
        }
        order.setPayCodeList(payCodes);
    }
    /**
     * 转化订单类型
     *
     * @param orderType
     * @return
     */
    public static Byte[] orderTypeToByte(String orderType) {
        String[] split = orderTypeToArray(orderType);
        Byte[] bytes = new Byte[split.length];
        int i = 0;
        for (String str : split) {
            bytes[i++] = Byte.valueOf(str);
        }
        return bytes;
    }
    /**
     * 转化订单类型
     *
     * @param orderType
     * @return
     */
    public static String[] orderTypeToArray(String orderType) {
        return orderType.substring(1, orderType.length() - 1).split("\\]\\[");
    }


    /**
     * 过滤子订单数量>0时,过滤主订单下已被拆除的商品行（通过减小数量为0则不展示）
     *
     * @param order 主订单（已经在主订单下添加了子订单及其商品）, goods主订单商品行
     * @param goods 商品
     * @return
     */
    public OrderListInfoVo filterMainOrderGoods(OrderListInfoVo order, List<OrderGoodsVo> goods) {
        List<? extends OrderListInfoVo> cOrders = order.getChildOrders();
        // 构造Map<Integer(子订单规格号), Integer(数量)>
        Map<Integer, Integer> childGoodsCount = new HashMap<>(goods.size());
        for (OrderListInfoVo oneOrder : cOrders) {
            for (OrderGoodsVo tempGoods : oneOrder.getGoods()) {
                Integer tempCount = childGoodsCount.get(tempGoods.getMainRecId());
                if (tempCount == null) {
                    // 第一次goodsNumber
                    childGoodsCount.put(tempGoods.getMainRecId(), tempGoods.getGoodsNumber());
                } else {
                    // 后续加goodsNumber
                    childGoodsCount.put(tempGoods.getMainRecId(), tempCount + tempGoods.getGoodsNumber());
                }
            }
        }
        // 主订单减去子订单相应商品(数量为0不展示)
        Iterator<OrderGoodsVo> iter = goods.iterator();
        while (iter.hasNext()) {
            OrderGoodsVo orderOoods = iter.next();
            Integer count = childGoodsCount.get(orderOoods.getRecId());
            if (count != null) {
                int finalCount = orderOoods.getGoodsNumber() - count;
                if (finalCount > 0) {
                    orderOoods.setGoodsNumber(finalCount);
                } else {
                    iter.remove();
                }
            }
        }
        order.setGoods(goods);
        return order;
    }

}

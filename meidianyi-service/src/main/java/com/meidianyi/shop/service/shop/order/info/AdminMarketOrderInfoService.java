package com.meidianyi.shop.service.shop.order.info;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.service.pojo.shop.market.MarketAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderGoodsListVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListVo;
import com.meidianyi.shop.service.pojo.shop.market.givegift.record.GiveGiftRecordListParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveDiscountMoney;
import com.meidianyi.shop.service.pojo.shop.order.analysis.ActiveOrderList;
import com.meidianyi.shop.service.pojo.shop.order.analysis.OrderActivityUserNum;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import jodd.util.StringUtil;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.GIVE_GIFT_CART;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static org.jooq.impl.DSL.*;

/**
 * @author: 王兵兵
 * @create: 2019-10-16 15:51
 **/
@Service
public class AdminMarketOrderInfoService extends OrderInfoService {

    @Autowired
    private OrderGoodsService orderGoods;
    @Autowired
    private DomainConfig domainConfig;

    /**
     * 单一营销
     * 活动新用户订单
     *
     * @param goodType
     * @param activityId
     * @param startTime
     * @param endTime
     * @return
     */
    public ActiveOrderList getActiveOrderList(Byte goodType, Integer activityId, Timestamp startTime, Timestamp  endTime) {
        //查询该活动下过单的用户——所有用户
        List<Integer> userIdList = db().select(ORDER_INFO.USER_ID)
            .from(ORDER_INFO)
            .where(ORDER_INFO.ACTIVITY_ID.eq(activityId))
            .and(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.CREATE_TIME.between(startTime, endTime))
            .and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {goodType})))
            .groupBy(ORDER_INFO.USER_ID)
            .fetch(ORDER_INFO.USER_ID);
        //查新用户活动前下过订单——老用户
        SelectHavingStep<Record2<Integer, Timestamp>> record2s = db().select(ORDER_INFO.USER_ID, min(ORDER_INFO.CREATE_TIME).as("time"))
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
                .and(ORDER_INFO.USER_ID.in(userIdList))
                .and(ORDER_INFO.ACTIVITY_ID.eq(activityId))
                .groupBy(ORDER_INFO.USER_ID);
        List<Integer> oldUserIdList  = db().selectDistinct(ORDER_INFO.USER_ID).from(ORDER_INFO)
                .leftJoin(record2s).on(record2s.field(ORDER_INFO.USER_ID).eq(ORDER_INFO.USER_ID))
                .where(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
                .and(ORDER_INFO.CREATE_TIME.lt(record2s.field("time",Timestamp.class))).fetchInto(Integer.class);
        // 老用户订单数据
        List<OrderActivityUserNum> oldList= db().select(DslPlus.dateFormatDay(ORDER_INFO.CREATE_TIME).as("date"),count(ORDER_INFO.CREATE_TIME).as("num"))
            .from(ORDER_INFO)
            .where(ORDER_INFO.ACTIVITY_ID.eq(activityId))
            .and(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.CREATE_TIME.between(startTime, endTime))
            .and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {goodType})))
            .and(ORDER_INFO.USER_ID.in(oldUserIdList))
            .groupBy(DslPlus.dateFormatDay(ORDER_INFO.CREATE_TIME))
            .fetchInto(OrderActivityUserNum.class);
        //新用户订单数据
        userIdList.removeAll(oldUserIdList);
        List<OrderActivityUserNum> newList = db().select(DslPlus.dateFormatDay(ORDER_INFO.CREATE_TIME).as("date"), count(ORDER_INFO.CREATE_TIME).as("num"))
            .from(ORDER_INFO)
            .where(ORDER_INFO.ACTIVITY_ID.eq(activityId))
            .and(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.CREATE_TIME.between(startTime, endTime))
            .and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {goodType})))
            .and(ORDER_INFO.USER_ID.in(userIdList))
            .groupBy(DslPlus.dateFormatDay(ORDER_INFO.CREATE_TIME))
            .fetchInto(OrderActivityUserNum.class);
        ActiveOrderList activeOrderList = new ActiveOrderList();
        activeOrderList.setNewUserNum(newList);
        activeOrderList.setOldUserNum(oldList);
        activeOrderList.setOldUser(oldUserIdList.size());
        activeOrderList.setNewUser(userIdList.size());
        return activeOrderList;
    }

    /**
     * 非单一营销
     * 活动新用户订单
     *
     * @param goodType
     * @param activityId
     * @param startTime
     * @param endTime
     * @return
     */
    public ActiveOrderList getActiveOrderList2(Byte goodType, Integer activityId, Timestamp startTime, Timestamp endTime) {
        //查询该活动下过单的用户——所有用户
        List<Integer> userIdList = db().select(ORDER_INFO.USER_ID)
            .from(ORDER_INFO)
            .leftJoin(ORDER_GOODS).on(ORDER_INFO.ORDER_ID.eq(ORDER_GOODS.ORDER_ID))
            .where(ORDER_GOODS.ACTIVITY_ID.eq(activityId))
            .and(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.CREATE_TIME.between(startTime, endTime))
            .and(ORDER_GOODS.ACTIVITY_TYPE.eq(goodType))
            .groupBy(ORDER_INFO.USER_ID)
            .fetch(ORDER_INFO.USER_ID);
        //查新用户活动前下过订单——老用户
        SelectHavingStep<Record2<Integer, Timestamp>> record2s = db().select(ORDER_INFO.USER_ID, min(ORDER_INFO.CREATE_TIME).as("time"))
            .from(ORDER_INFO)
            .leftJoin(ORDER_GOODS).on(ORDER_INFO.ORDER_ID.eq(ORDER_GOODS.ORDER_ID))
            .where(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.USER_ID.in(userIdList))
            .and(ORDER_GOODS.ACTIVITY_ID.eq(activityId))
            .and(ORDER_GOODS.ACTIVITY_TYPE.eq(goodType))
            .groupBy(ORDER_INFO.USER_ID);
        List<Integer> oldUserIdList = db().selectDistinct(ORDER_INFO.USER_ID).from(ORDER_INFO)
            .leftJoin(record2s).on(record2s.field(ORDER_INFO.USER_ID).eq(ORDER_INFO.USER_ID))
            .where(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.CREATE_TIME.lt(record2s.field("time", Timestamp.class))).fetchInto(Integer.class);
        // 老用户订单数据
        List<OrderActivityUserNum> oldList = db().select(DslPlus.dateFormatDay(ORDER_INFO.CREATE_TIME).as("date"), count(ORDER_INFO.CREATE_TIME).as("num"))
            .from(ORDER_INFO)
            .leftJoin(ORDER_GOODS).on(ORDER_INFO.ORDER_ID.eq(ORDER_GOODS.ORDER_ID))
            .where(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.CREATE_TIME.between(startTime, endTime))
            .and(ORDER_GOODS.ACTIVITY_ID.eq(activityId))
            .and(ORDER_GOODS.ACTIVITY_TYPE.eq(goodType))
            .and(ORDER_INFO.USER_ID.in(oldUserIdList))
            .groupBy(DslPlus.dateFormatDay(ORDER_INFO.CREATE_TIME))
            .fetchInto(OrderActivityUserNum.class);
        //新用户订单数据
        userIdList.removeAll(oldUserIdList);
        List<OrderActivityUserNum> newList = db().select(DslPlus.dateFormatDay(ORDER_INFO.CREATE_TIME).as("date"), count(ORDER_INFO.CREATE_TIME).as("num"))
            .from(ORDER_INFO)
            .leftJoin(ORDER_GOODS).on(ORDER_INFO.ORDER_ID.eq(ORDER_GOODS.ORDER_ID))
            .where(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.CREATE_TIME.between(startTime, endTime))
            .and(ORDER_GOODS.ACTIVITY_ID.eq(activityId))
            .and(ORDER_GOODS.ACTIVITY_TYPE.eq(goodType))
            .and(ORDER_INFO.USER_ID.in(userIdList))
            .groupBy(DslPlus.dateFormatDay(ORDER_INFO.CREATE_TIME))
            .fetchInto(OrderActivityUserNum.class);
        ActiveOrderList activeOrderList = new ActiveOrderList();
        activeOrderList.setNewUserNum(newList);
        activeOrderList.setOldUserNum(oldList);
        activeOrderList.setOldUser(oldUserIdList.size());
        activeOrderList.setNewUser(userIdList.size());
        return activeOrderList;
    }

    /**
     * 分裂营销活动的活动数据分析的订单部分数据
     *
     * @param param
     * @return
     */
    public Map<Date, Integer> getMarketOrderAnalysis(MarketAnalysisParam param) {
        Map<Date, Integer> map = db().select(date(ORDER_INFO.CREATE_TIME).as("date"), count().as("number")).from(ORDER_INFO).
            where(ORDER_INFO.ACTIVITY_ID.eq(param.getActId())).
            and(ORDER_INFO.CREATE_TIME.between(param.getStartTime(), param.getEndTime())).
            and(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED)).
            and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {BaseConstant.ACTIVITY_TYPE_BARGAIN}))).
            groupBy(date(ORDER_INFO.CREATE_TIME)).fetch().intoMap(date(ORDER_INFO.CREATE_TIME).as("date"),count().as("number"));
        return map;
    }


    /**
     *
     *	活动实付总金额 活动优惠总金额
     * @param goodType
     * @param activityId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<ActiveDiscountMoney> getActiveDiscountMoney(Byte goodType, Integer activityId, Timestamp startTime, Timestamp  endTime) {
        SelectConditionStep<? extends Record> select = db().select(
            date(ORDER_INFO.CREATE_TIME).as(ActiveDiscountMoney.CREATE_TIME),
            sum(when(ORDER_GOODS.RETURN_NUMBER.eq(0), ORDER_GOODS.DISCOUNTED_TOTAL_PRICE).when(ORDER_GOODS.RETURN_NUMBER.notEqual(0), ORDER_GOODS.DISCOUNTED_GOODS_PRICE.multiply(ORDER_GOODS.GOODS_NUMBER.sub(ORDER_GOODS.RETURN_NUMBER)))).as(ActiveDiscountMoney.PAYMENT_AMOUNT),
            sum((ORDER_GOODS.GOODS_PRICE.sub(ORDER_GOODS.DISCOUNTED_GOODS_PRICE)).multiply(ORDER_GOODS.GOODS_NUMBER.sub(ORDER_GOODS.RETURN_NUMBER))).as(ActiveDiscountMoney.DISCOUNT_AMOUNT),
            count(ORDER_INFO.ORDER_ID).as(ActiveDiscountMoney.PAID_ORDER_NUMBER),
            sum(ORDER_GOODS.GOODS_NUMBER).as(ActiveDiscountMoney.PAID_GOODS_NUMBER))
            .from(ORDER_INFO)
            .leftJoin(ORDER_GOODS).on(ORDER_GOODS.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
            .where(ORDER_INFO.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_GOODS.IS_GIFT.eq(OrderConstant.IS_GIFT_N))
            .and(ORDER_INFO.CREATE_TIME.between(startTime, endTime));
        if (BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE.equals(goodType)) {
            //限时降价信息在order_goods
            select.and(ORDER_GOODS.ACTIVITY_ID.eq(activityId)).and(ORDER_GOODS.ACTIVITY_TYPE.eq(goodType));
        } else {
            select.and(ORDER_INFO.ACTIVITY_ID.eq(activityId)).and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[]{goodType})));
        }

        return select.groupBy(date(ORDER_INFO.CREATE_TIME))
            .orderBy(date(ORDER_INFO.CREATE_TIME))
            .fetchInto(ActiveDiscountMoney.class);
    }

    /**
     * 查找一口价活动 已购商品数量
     * @param activityId
     * @return
     */
    public Integer getPackageSaleGoodsNum(Integer activityId) {
        Integer goodsNum = db().select(DSL.sum(ORDER_INFO.GOODS_AMOUNT)).from(ORDER_INFO)
            .where(ORDER_INFO.ACTIVITY_ID.eq(activityId))
            .and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE})))
            .fetchOneInto(Integer.class);
        return goodsNum;
    }
    /**
     * 查找一口价活动下单用户数量
     * @param activityId
     * @return
     */
    public Integer getPackageSaleUserNum(Integer activityId) {
        Integer userNum = db().select(DSL.countDistinct(ORDER_INFO.USER_ID)).from(ORDER_INFO)
            .where(ORDER_INFO.ACTIVITY_ID.eq(activityId))
            .and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE})))
            .fetchOneInto(Integer.class);
        return userNum;
    }
    /**
     * 查找一口价活动订单数量
     * @param activityId
     * @return
     */
    public Integer getPackageSaleOrderNum(Integer activityId) {
        Integer goodsNum = db().select(DSL.count()).from(ORDER_INFO)
            .where(ORDER_INFO.ACTIVITY_ID.eq(activityId))
            .and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE})))
            .fetchOneInto(Integer.class);
        return goodsNum;
    }

    /**
     *  我要送礼
     * @param activityId 活动id
     * @return  送礼的礼物车数量
     */
    public Integer getSendGiftOrderCt(Integer activityId){
        return db().selectCount()
            .from(TABLE)
            .leftJoin(GIVE_GIFT_CART).on(TABLE.ACTIVITY_ID.eq(GIVE_GIFT_CART.ID))
            .where(GIVE_GIFT_CART.GIVE_GIFT_ID.eq(activityId))
            .and(TABLE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .and(TABLE.ORDER_STATUS.gt((byte) 2))
            .and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {BaseConstant.ACTIVITY_TYPE_GIVE_GIFT})))
            .and(TABLE.ORDER_SN.eq(TABLE.MAIN_ORDER_SN))
            .fetchOne().component1();
    }

    /**
     * 收礼明细
     * @param param GiveGiftReceiveListParam
     * @return SelectConditionStep<? extends Record>
     */
    public SelectJoinStep<? extends Record> giveGiftRecordList(GiveGiftRecordListParam param) {
        SelectJoinStep<? extends Record> select = db()
            .select(TABLE.MAIN_ORDER_SN,
                TABLE.ORDER_AMOUNT,
                TABLE.CREATE_TIME,
                TABLE.PAY_TIME,
                TABLE.ORDER_STATUS,
                GIVE_GIFT_CART.GIFT_TYPE,
                USER.USER_ID,
                USER.USERNAME,
                USER.MOBILE
            )
            .from(TABLE)
            .leftJoin(GIVE_GIFT_CART).on(GIVE_GIFT_CART.ID.eq(TABLE.ACTIVITY_ID))
            .leftJoin(USER).on(USER.USER_ID.eq(GIVE_GIFT_CART.USER_ID));

        select.where(GIVE_GIFT_CART.GIVE_GIFT_ID.eq(param.getActivityId()))
            .and(TABLE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .and(TABLE.ORDER_STATUS.gt(OrderConstant.ORDER_CLOSED))
            .and(ORDER_INFO.GOODS_TYPE.likeRegex(getGoodsTypeToSearch(new Byte[] {BaseConstant.ACTIVITY_TYPE_GIVE_GIFT})))
            .and(TABLE.ORDER_SN.eq(TABLE.MAIN_ORDER_SN));

        if (!StringUtils.isBlank(param.getGoodsName())){
            select.where(USER.USER_ID.like(prefixLikeValue(param.getGoodsName())));
        }
        if (!StringUtils.isBlank(param.getMobile())){
            select.where(USER.MOBILE.like(prefixLikeValue(param.getMobile())));
        }
        if (!StringUtils.isBlank(param.getGoodsName())||!StringUtils.isBlank(param.getGoodsSn())) {
            select.leftJoin(ORDER_GOODS).on(ORDER_GOODS.ORDER_SN.eq(ORDER_INFO.ORDER_SN));
            if (!StringUtils.isBlank(param.getGoodsName())) {
                select.where(ORDER_GOODS.GOODS_NAME.like(likeValue(param.getGoodsName())));
            }
            if (!StringUtils.isBlank(param.getGoodsSn())) {
                select.where(ORDER_GOODS.ORDER_SN.like(likeValue(param.getGoodsSn())));
            }
        }
        if (param.getOrderStatus()!=null&&param.getOrderStatus()>=OrderConstant.ORDER_WAIT_PAY){
            if (param.getOrderStatus()==OrderConstant.ORDER_GIVE_GIFT_FINISHED){
                select.where(TABLE.ORDER_STATUS.eq(param.getOrderStatus()));
            }else {
                select.where(TABLE.ORDER_STATUS.lt(OrderConstant.ORDER_GIVE_GIFT_FINISHED));
            }
        }
        return select;
    }

    /**
     *     营销订单列表-构造select
     * @param param
     * @param goodsType
     */
    private SelectJoinStep<? extends Record> buildMarketOrderOptions(MarketOrderListParam param, byte goodsType){
        SelectJoinStep<? extends Record> select = db().select(ORDER_INFO.ORDER_SN,ORDER_INFO.ORDER_STATUS,ORDER_INFO.REFUND_STATUS,ORDER_INFO.CONSIGNEE,ORDER_INFO.MOBILE,ORDER_INFO.PAY_CODE,ORDER_INFO.DELIVER_TYPE,ORDER_INFO.CREATE_TIME,ORDER_INFO.SHIPPING_FEE,ORDER_INFO.MONEY_PAID,ORDER_INFO.SCORE_DISCOUNT,ORDER_INFO.USE_ACCOUNT,ORDER_INFO.MEMBER_CARD_BALANCE,ORDER_INFO.USER_ID,USER.USERNAME,USER.MOBILE.as("userMobile")).
            from(ORDER_INFO).leftJoin(ORDER_GOODS).on(ORDER_GOODS.ORDER_SN.eq(ORDER_INFO.ORDER_SN)).
            leftJoin(USER).on(ORDER_INFO.USER_ID.eq(USER.USER_ID));
        buildMarketOrderOptionsParam(select,param, goodsType);
        select.orderBy(ORDER_INFO.ORDER_ID);
        return select;
    }

    private void buildMarketOrderOptionsParam(SelectJoinStep<? extends Record> select,MarketOrderListParam param, byte goodsType){
        param.initOrderStatus();
        OrderPageListQueryParam orderParam = new OrderPageListQueryParam();
        orderParam.setCurrentPage(param.getCurrentPage());
        orderParam.setPageRows(param.getPageRows());
        orderParam.setActivityId(param.getActivityId());
        orderParam.setGoodsType(new Byte[] {goodsType});
        orderParam.setGoodsName(param.getGoodsName());
        orderParam.setOrderSn(param.getOrderSn());
        orderParam.setOrderStatus(param.getOrderStatus());
        orderParam.setMobile(param.getMobile());
        orderParam.setConsignee(param.getConsignee());
        orderParam.setCreateTimeStart(param.getCreateTimeStart());
        orderParam.setCreateTimeEnd(param.getCreateTimeEnd());
        orderParam.setCountryCode(param.getCountryCode());
        orderParam.setProvinceCode(param.getProvinceCode());
        orderParam.setCityCode(param.getCityCode());
        orderParam.setDistrictCode(param.getDistrictCode());

        buildOptions(select, orderParam,true);
        select.where(ORDER_INFO.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        select.groupBy(ORDER_INFO.ORDER_SN,ORDER_INFO.ORDER_STATUS,ORDER_INFO.REFUND_STATUS,ORDER_INFO.CONSIGNEE,ORDER_INFO.MOBILE,ORDER_INFO.PAY_CODE,ORDER_INFO.DELIVER_TYPE,ORDER_INFO.CREATE_TIME,ORDER_INFO.SHIPPING_FEE,ORDER_INFO.MONEY_PAID,ORDER_INFO.SCORE_DISCOUNT,ORDER_INFO.USE_ACCOUNT,ORDER_INFO.MEMBER_CARD_BALANCE,ORDER_INFO.USER_ID,USER.USERNAME,USER.MOBILE);
    }

    /**
     * 营销活动订单查询
     *
     * @param param
     * @return
     */
    public PageResult<MarketOrderListVo> getMarketOrderPageList(MarketOrderListParam param, byte goodsType) {
        SelectJoinStep<? extends Record> select = buildMarketOrderOptions(param,goodsType);

        PageResult<MarketOrderListVo> res = getPageResult(select,param.getCurrentPage(),param.getPageRows(),MarketOrderListVo.class);
        /** 填充商品行 */
        for(MarketOrderListVo order : res.dataList){
            List<MarketOrderGoodsListVo> goods = orderGoods.getMarketOrderGoodsByOrderSn(order.getOrderSn());
            goods.forEach(g->{
                if(StringUtil.isNotBlank(g.getGoodsImg())){
                    g.setGoodsImg(domainConfig.imageUrl(g.getGoodsImg()));
                }
            });
            order.setGoods(goods);
        }

        return res;
    }

    /**
     * 营销活动订单查询(不分页)
     *
     * @param param
     * @return
     */
    public List<MarketOrderListVo> getMarketOrderList(MarketOrderListParam param, byte goodsType) {
        SelectJoinStep<? extends Record> select = buildMarketOrderOptions(param,goodsType);

        List<MarketOrderListVo> res = select.fetchInto(MarketOrderListVo.class);

        /** 填充商品行 */
        for(MarketOrderListVo order : res){
            order.setGoods(orderGoods.getMarketOrderGoodsByOrderSn(order.getOrderSn()));
        }

        return res;
    }

    /**
     * 订单的总条数
     * @param param
     * @param goodsType
     * @return
     */
    public int getMarketOrderListSize(MarketOrderListParam param, byte goodsType){
        SelectJoinStep<? extends Record> select = selectOne().from(ORDER_INFO).leftJoin(ORDER_GOODS).on(ORDER_GOODS.ORDER_SN.eq(ORDER_INFO.ORDER_SN)).leftJoin(USER).on(ORDER_INFO.USER_ID.eq(USER.USER_ID));
        buildMarketOrderOptionsParam(select,param,goodsType);
        return db().selectCount().from(select).fetchOptionalInto(Integer.class).orElse(0);
    }


}

package com.meidianyi.shop.service.shop.task.market;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.Presale.PRESALE;
import static com.meidianyi.shop.db.shop.tables.PresaleProduct.PRESALE_PRODUCT;
import static java.util.stream.Collectors.toList;

/**
 * @author: 王兵兵
 * @create: 2019-12-10 11:03
 **/
@Service
public class PreSaleTaskService extends ShopBaseService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;

    /**
     * 监控goodsType
     */
    public void monitorGoodsType(){
        //目前Goods表里还是预售类型的商品
        List<Integer> pastPreSaleGoodsIdList = getPastPreSaleGoodsId();
        //在活动有效期内的预售记录的ID列表
        List<Integer> currentPreSaleGoodsIdList = getCurrentPreSaleGoodsIdList();

        //求差集
        List<Integer> changeToNormalGoodsIds = Util.diffList(pastPreSaleGoodsIdList,currentPreSaleGoodsIdList);
        List<Integer> changeToActGoodsIds = Util.diffList(currentPreSaleGoodsIdList,pastPreSaleGoodsIdList);

        if(changeToNormalGoodsIds != null && changeToNormalGoodsIds.size() > 0){
            //活动已失效，将goodsType改回去
            goodsService.changeToNormalType(changeToNormalGoodsIds);
            //异步更新ES
            esDataUpdateMqService.addEsGoodsIndex(changeToNormalGoodsIds,getShopId(), DBOperating.UPDATE);
            //TODO 记录变动
        }

        if(changeToActGoodsIds != null && changeToActGoodsIds.size() > 0){
            //有新的活动生效，商品goodsType标记活动类型
            changeToPreSaleType(changeToActGoodsIds);
            //异步更新ES
            esDataUpdateMqService.addEsGoodsIndex(changeToActGoodsIds,getShopId(), DBOperating.UPDATE);
            //TODO 记录变动
        }
    }

    /**
     * 监控并处理预售订单
     */
    public void monitorOrder(){
        List<OrderInfoRecord> orderList = getExpiredPreSaleOrders();
        if(orderList.size() > 0){
            orderList.forEach(order->{
                //在订单关闭操作中对预售订单特殊处理，根据活动设置对订单退款
                OrderOperateQueryParam param = new OrderOperateQueryParam();
                param.setAction((byte) OrderServiceCode.CLOSE.ordinal());
                param.setIsMp(OrderConstant.IS_MP_AUTO);
                param.setOrderSn(order.getOrderSn());
                param.setOrderId(order.getOrderId());

                logger().info("预售定时任务-订单关闭-" + order.getOrderSn());
                //关闭订单
                ExecuteResult executeResult = saas.getShopApp(getShopId()).orderActionFactory.orderOperate(param);
            });
        }
    }

    private List<Integer> getPastPreSaleGoodsId(){
        return db().select(GOODS.GOODS_ID).from(GOODS).where(GOODS.GOODS_TYPE.eq(BaseConstant.ACTIVITY_TYPE_PRE_SALE)).fetchInto(Integer.class);
    }

    private List<Integer> getCurrentPreSaleGoodsIdList(){
        return db().select(PRESALE_PRODUCT.GOODS_ID)
            .from(PRESALE_PRODUCT).innerJoin(PRESALE).on(PRESALE.ID.eq(PRESALE_PRODUCT.PRESALE_ID))
            .where(PRESALE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
            .and(PRESALE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and((PRESALE.PRE_START_TIME.lt(DateUtils.getLocalDateTime()).and(PRESALE.PRE_END_TIME.gt(DateUtils.getLocalDateTime()))).or(PRESALE.PRE_START_TIME_2.lt(DateUtils.getLocalDateTime()).and(PRESALE.PRE_END_TIME_2.gt(DateUtils.getLocalDateTime()))))
        ).fetchInto(Integer.class);
    }

    /**
     * 批量将活动商品改成预售商品
     * @param goodsIds
     */
    private void changeToPreSaleType(List<Integer> goodsIds){
        //比预售优先级高的活动，不覆盖goodsType是这些的商品（5）
        List<Byte> highPriorityAct = Stream.of(BaseConstant.ACTIVITY_TYPE_SEC_KILL).collect(toList());
        db().update(GOODS).set(GOODS.GOODS_TYPE, BaseConstant.ACTIVITY_TYPE_PRE_SALE).where(GOODS.GOODS_TYPE.notIn(highPriorityAct).and(GOODS.GOODS_ID.in(goodsIds))).execute();
    }

    /**
     * 取需要处理的过期预售订单
     * @return
     */
    private List<OrderInfoRecord> getExpiredPreSaleOrders(){
        return db().select(ORDER_INFO.fields()).from(ORDER_INFO.leftJoin(PRESALE).on(ORDER_INFO.ACTIVITY_ID.eq(PRESALE.ID))).where(
            ORDER_INFO.GOODS_TYPE.likeRegex(OrderInfoService.getGoodsTypeToSearch(new Byte[] {BaseConstant.ACTIVITY_TYPE_PRE_SALE}))
            .and(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_PAY))
            .and(ORDER_INFO.ORDER_PAY_WAY.eq(OrderConstant.PAY_WAY_DEPOSIT))
            .and(PRESALE.END_TIME.lt(DateUtils.getLocalDateTime()))
            .and(ORDER_INFO.BK_ORDER_PAID.gt(OrderConstant.BK_PAY_NO))
        ).fetchInto(OrderInfoRecord.class);
    }

    /**
     * 关闭订单后更新b2c_presale_product的库存和销售数量
     * @param order
     */
    private void updatePreSaleProductNumber(OrderInfoRecord order){
        Result<OrderGoodsRecord> orderGoodsList = orderGoodsService.getOrderGoods(order.getOrderSn());
        orderGoodsList.forEach(orderGoods->{
            db().update(PRESALE_PRODUCT)
                .set(PRESALE_PRODUCT.PRESALE_NUMBER,PRESALE_PRODUCT.PRESALE_NUMBER.add(orderGoods.getGoodsNumber()))
                .set(PRESALE_PRODUCT.SALE_NUMBER,PRESALE_PRODUCT.SALE_NUMBER.sub(orderGoods.getGoodsNumber()))
                .where(PRESALE_PRODUCT.PRODUCT_ID.eq(orderGoods.getProductId()))
                .and(PRESALE_PRODUCT.PRESALE_ID.eq(order.getActivityId()))
                .execute();
        });
    }
}

package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;

import java.util.List;

/**
 * 小程序-订单生成时-营销处理器接口
 * @author wangbingbing
 */
public interface CreateOrderProcessor extends Processor {

    /**
     *  初始化参数,活动校验
     * @param param 参数
     * @throws MpException 异常
     */
    void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException;

    /**
     * 保存信息,此时订单数据已计算完成（此时订单状态已经明确）
     * @param param 参数
     * @param order 订单
     * @throws MpException 异常
     */
    void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException;


    /**
     * 订单生效后（微信支付、其他支付、货到付款等）的营销后续处理（库存、活动状态相关）
     * 下单时接口调用说明：此方法与扣减商品库存（非活动库存）方法同时调用
     * 支付回调调用说明：订单状态转化未待发货时调用
     * @param param
     * @param order
     * @throws MpException
     */
    void processOrderEffective(OrderBeforeParam param,OrderInfoRecord order)throws MpException;

    /**
     * 更新活动库存
     * @param param
     * @param order
     * @throws MpException
     */
    void processUpdateStock(OrderBeforeParam param,OrderInfoRecord order) throws MpException;

    /**
     * 退货、取消、关闭时更新（returnOrderRecord == null为关闭或取消）
     * @param returnOrderRecord
     * @param activityId 活动id
     * @param returnGoods 退款商品
     * @throws MpException 异常
     */
    void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods)throws MpException;
}

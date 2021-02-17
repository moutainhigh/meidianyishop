package com.meidianyi.shop.service.shop.activity.factory;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeVo;
import com.meidianyi.shop.service.shop.activity.processor.CreateOrderProcessor;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成订单时的营销处理
 *
 * @author: 王兵兵
 * @create: 2019-11-18 15:51
 **/
@Service
public class OrderCreateMpProcessorFactory extends AbstractProcessorFactory<CreateOrderProcessor, OrderBeforeVo> {
    @Override
    public void doProcess(List<OrderBeforeVo> capsules, Integer userId) throws MpException {

    }

    /**
     * 一般营销  首单特惠 会员专享
     */
    private final static List<Byte> GENERAL_ACTIVITY = Arrays.asList(
//            BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL,
//            BaseConstant.ACTIVITY_TYPE_MEMBER_GRADE,
//            BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE,
            BaseConstant.ACTIVITY_TYPE_REBATE
//            BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE,
//            BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION,
//            BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE
    );
    /**
     * 全局的活动  支付有礼
     */
    private final static List<Byte> GLOBAL_ACTIVITY = Arrays.asList(
            BaseConstant.ACTIVITY_TYPE_PRESCRIPTION
//            BaseConstant.ACTIVITY_TYPE_PAY_AWARD,
//            BaseConstant.ACTIVITY_TYPE_GIFT
    );

    /**
     * 单一营销 排他性
     */
    public final static List<Byte> SINGLENESS_ACTIVITY = Arrays.asList(
//            BaseConstant.ACTIVITY_TYPE_GROUP_BUY,
//            BaseConstant.ACTIVITY_TYPE_SEC_KILL,
//            BaseConstant.ACTIVITY_TYPE_BARGAIN,
//            BaseConstant.ACTIVITY_TYPE_MY_PRIZE,
//            BaseConstant.ACTIVITY_TYPE_PRE_SALE,
//            BaseConstant.ACTIVITY_TYPE_GROUP_DRAW,
//            BaseConstant.ACTIVITY_TYPE_INTEGRAL,
//            BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE,
//            BaseConstant.ACTIVITY_TYPE_EXCHANG_ORDER
    );

    /**
     * 退款
     */
    public final static List<Byte> RETURN_ACTIVITY = Arrays.asList(
//            BaseConstant.ACTIVITY_TYPE_GIFT,
//            BaseConstant.ACTIVITY_TYPE_GROUP_BUY,
//            BaseConstant.ACTIVITY_TYPE_SEC_KILL,
//            BaseConstant.ACTIVITY_TYPE_BARGAIN,
//            BaseConstant.ACTIVITY_TYPE_LOTTERY_PRESENT,
//            BaseConstant.ACTIVITY_TYPE_PROMOTE_ORDER,
//            BaseConstant.ACTIVITY_TYPE_INTEGRAL,
//            BaseConstant.ACTIVITY_TYPE_ASSESS_ORDER,
            BaseConstant.ACTIVITY_TYPE_PRESCRIPTION
    );

    /**
     * 取消
     */
    public final static List<Byte> CANCEL_ACTIVITY = Arrays.asList(
//            BaseConstant.ACTIVITY_TYPE_GIFT,
//            BaseConstant.ACTIVITY_TYPE_GROUP_BUY,
//            BaseConstant.ACTIVITY_TYPE_SEC_KILL,
//            BaseConstant.ACTIVITY_TYPE_BARGAIN,
//            BaseConstant.ACTIVITY_TYPE_LOTTERY_PRESENT,
//            BaseConstant.ACTIVITY_TYPE_PROMOTE_ORDER,
//            BaseConstant.ACTIVITY_TYPE_ASSESS_ORDER,
//            BaseConstant.ACTIVITY_TYPE_PAY_AWARD,
            BaseConstant.ACTIVITY_TYPE_PRESCRIPTION
    );

    /**
     * 单一营销
     */
    private Map<Byte, CreateOrderProcessor> processorMap;

    /**
     * 普通营销活动
     */
    private List<CreateOrderProcessor> processorGeneralList;

    /**
     * 退货活动处理
     */
    private Map<Byte, CreateOrderProcessor> processorReturnMap;

    /**
     * 全局营销
     */
    private List<CreateOrderProcessor> processorGlobalList;

    @Override
    @PostConstruct
    protected void init() {
        super.init();
        processorMap = new HashMap<>(processors.size());
        processorGeneralList = new ArrayList<>(processors.size());
        processorGlobalList = new ArrayList<>(processors.size());
        processorReturnMap = new HashMap<>(RETURN_ACTIVITY.size());
        for (CreateOrderProcessor processor : processors) {
            if (SINGLENESS_ACTIVITY.contains(processor.getActivityType())) {
                processorMap.put(processor.getActivityType(), processor);
            }
            if (GENERAL_ACTIVITY.contains(processor.getActivityType())) {
                processorGeneralList.add(processor);
            }
            if (GLOBAL_ACTIVITY.contains(processor.getActivityType())) {
                processorGlobalList.add(processor);
            }
            if (RETURN_ACTIVITY.contains(processor.getActivityType())) {
                processorReturnMap.put(processor.getActivityType(), processor);
            }
        }
    }

    /**
     * 处理下单时营销活动下单param中的相关
     *
     * @param param
     * @throws MpException
     */
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        if (param.getActivityType() != null && !SINGLENESS_ACTIVITY.contains(param.getActivityType())) {
            //因为小程序商品详情页面会带营销type,但是订单模块只接受单一营销type,其他营销自动处理
            param.setActivityType(null);
            param.setActivityId(null);
        }
        if (param.getActivityId() != null && param.getActivityType() != null && param.getActivityId() > 0 && param.getActivityType() > 0) {
            //单一营销
            processorMap.get(param.getActivityType()).processInitCheckedOrderCreate(param);
        } else {
            //普通营销
            for (CreateOrderProcessor processor : processorGeneralList) {
                processor.processInitCheckedOrderCreate(param);
            }
        }
        for (CreateOrderProcessor processor : processorGlobalList) {
            //全局活动
            processor.processInitCheckedOrderCreate(param);
        }
    }

    /**
     * 保存数据（该方法不要在并发情况下出现临界资源）
     *
     * @param param
     * @param order
     */
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        if (param.getActivityId() != null && param.getActivityType() != null && param.getActivityId() > 0 && param.getActivityType() > 0) {
            //单一营销
            processorMap.get(param.getActivityType()).processSaveOrderInfo(param, order);
        } else {
            for (CreateOrderProcessor processor : processorGeneralList) {
                processor.processSaveOrderInfo(param, order);
            }
        }
        for (CreateOrderProcessor processor : processorGlobalList) {
            //全局活动
            processor.processSaveOrderInfo(param, order);
        }
    }

    /**
     * 更新活动库存（存在特例）
     *
     * @param param
     * @param order
     */
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        Byte[] types = OrderInfoService.orderTypeToByte(order.getGoodsType());
        //过滤活动
        for (Byte type : types) {
            if (AtomicOperation.filterAct.contains(type)) {
                return;
            }
        }
        if (param.getActivityId() != null && param.getActivityType() != null && param.getActivityId() > 0 && param.getActivityType() > 0) {
            //单一营销
            processorMap.get(param.getActivityType()).processUpdateStock(param, order);
        } else {
            for (CreateOrderProcessor processor : processorGeneralList) {
                processor.processUpdateStock(param, order);
            }
        }
        for (CreateOrderProcessor processor : processorGlobalList) {
            //全局活动
            processor.processUpdateStock(param, order);
        }
    }

    /**
     * 订单生效后（微信支付、其他支付、货到付款等）的营销后续处理（活动状态相关）
     *
     * @param param
     * @param order
     * @throws MpException
     */
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        if (param.getActivityId() != null && param.getActivityType() != null && param.getActivityId() > 0 && param.getActivityType() > 0) {
            //单一营销
            // 可能有：我要送礼、限次卡兑换、拼团、砍价、积分兑换、秒杀、拼团抽奖、打包一口价、预售、抽奖、支付有礼、测评、好友助力、满折满减购物车下单
            processorMap.get(param.getActivityType()).processOrderEffective(param, order);
        } else {
            for (CreateOrderProcessor processor : processorGeneralList) {
                processor.processOrderEffective(param, order);
            }
        }
        for (CreateOrderProcessor processor : processorGlobalList) {
            //全局活动
            processor.processOrderEffective(param, order);
        }
    }

    /**
     * 退款\取消、关闭成功调用活动库存销量修改、活动记录修改方法
     *
     * @param returnOrderRecord
     * @param activityType      活动类型
     * @param activityId        活动id（赠品活动id在）
     * @param returnOrderGoods  退款商品
     * @throws MpException
     */
    public void processReturnOrder(ReturnOrderRecord returnOrderRecord, Byte activityType, Integer activityId, List<OrderReturnGoodsVo> returnOrderGoods) throws MpException {
        CreateOrderProcessor process = processorReturnMap.get(activityType);
        if (process != null) {
            process.processReturn(returnOrderRecord, activityId, returnOrderGoods);
        }
    }
}


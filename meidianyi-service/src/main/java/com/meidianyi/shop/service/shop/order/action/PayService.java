package com.meidianyi.shop.service.shop.order.action;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.OrderGoods;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PaymentRecordRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupOrderVo;
import com.meidianyi.shop.service.pojo.shop.market.presale.PreSaleVo;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.PayParam;
import com.meidianyi.shop.service.pojo.wxapp.order.CreateOrderVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam.Goods;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.saas.shop.ThirdPartyMsgServices;
import com.meidianyi.shop.service.shop.activity.dao.GiftProcessorDao;
import com.meidianyi.shop.service.shop.activity.dao.PreSaleProcessorDao;
import com.meidianyi.shop.service.shop.activity.factory.OrderCreateMpProcessorFactory;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyListService;
import com.meidianyi.shop.service.shop.market.presale.PreSaleService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperationJudgment;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.trade.OrderPayService;
import com.meidianyi.shop.service.shop.order.trade.TradesRecordService;
import com.meidianyi.shop.service.shop.prescription.UploadPrescriptionService;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_GROUP_BUY;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.IS_GROUPER_N;

/**
 * 订单支付
 * @author 王帅
 */
@Component
public class PayService  extends ShopBaseService implements IorderOperate<OrderOperateQueryParam,PayParam> {

    @Autowired
    private TradesRecordService tradesRecord;

    @Autowired
    private AtomicOperation atomicOperation;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private OrderInfoService orderInfo;

    @Autowired
    private PreSaleService preSale;

    @Autowired
    private CreateService createOrder;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsSpecProductService goodsSpecProduct;

    @Autowired
    private OrderPayService orderPay;

    @Autowired
    private GroupBuyListService groupBuyListService;

    @Autowired
    private GiftProcessorDao giftProcessorDao;

    @Autowired
    private PreSaleProcessorDao preSaleProcessorDao;

    @Autowired
    private OrderOperateSendMessage sendMessage;

    @Autowired
    private ThirdPartyMsgServices thirdPartyMsgServices;
    @Autowired
    private UploadPrescriptionService uploadPrescriptionService;

    /**
     * 营销活动processorFactory
     */
    @Autowired
    private OrderCreateMpProcessorFactory marketProcessorFactory;
    @Autowired
    private CartService cart;

    @Override
    public OrderServiceCode getServiceCode() {
        return OrderServiceCode.PAY;
    }

    @Override
    public Object query(OrderOperateQueryParam param) {
        return null;
    }

    @Override
    public ExecuteResult execute(PayParam param) {
        OrderInfoRecord order = orderInfo.getRecord(param.getOrderId());
        if (order == null) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_NOT_EXIST, null);
        }
        if (order.getOrderStatus() != OrderConstant.ORDER_WAIT_PAY) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_TOPAY_STATUS_NOT_WAIT_PAY, null);
        }
        //时间校验
        ExecuteResult checkPayTimeResult = checkPayTime(order);
        if (checkPayTimeResult != null) {
            return checkPayTimeResult;
        }
        //订单商品
        Result<OrderGoodsRecord> orderGoodsRecord = orderGoodsService.getByOrderId(param.getOrderId());
        //商品校验
        ExecuteResult checkGoodsResult = checkGoods(param, orderGoodsRecord);
        if (checkGoodsResult != null) {
            return checkGoodsResult;
        }
        //活动校验
        ExecuteResult checkActivityResult = checkActivity(order);
        if (checkActivityResult != null) {
            return checkActivityResult;
        }
        CreateOrderVo result = new CreateOrderVo();
        result.setOrderSn(order.getOrderSn());
        try {
            String orderSn;
            BigDecimal money;
            if(order.getOrderPayWay().equals(OrderConstant.PAY_WAY_DEPOSIT) && order.getBkOrderPaid().equals(OrderConstant.BK_PAY_FRONT)) {
                orderSn = order.getOrderSn() + OrderConstant.BK_SN_SUFFIX;
                money = order.getBkOrderMoney();
            }else {
                orderSn = order.getOrderSn();
                money = order.getMoneyPaid();
            }
            result.setWebPayVo(orderPay.isContinuePay(order, orderSn, money, orderPay.getGoodsNameForPay(order, orderGoodsRecord.into(OrderGoodsBo.class)), param.getClientIp(), param.getWxUserInfo().getWxUser().getOpenId(), null));
            return ExecuteResult.create(result);
        } catch (MpException e) {
            return ExecuteResult.create(e);
        }
    }

    /**
     * 支付时间校验
     * @param order
     * @return
     */
    private ExecuteResult checkPayTime(OrderInfoRecord order) {
        //过期校验
        long currenTmilliseconds = Instant.now().toEpochMilli();
        if (order.getBkOrderPaid() == OrderConstant.BK_PAY_FRONT) {
            //定金订单支付尾款
            Record2<Timestamp, Timestamp> timeInterval = preSale.getTimeInterval(order.getActivityId());
            if (timeInterval.value1().getTime() > currenTmilliseconds) {
                return ExecuteResult.create(JsonResultCode.CODE_ORDER_TOPAY_BK_PAY_NOT_START, null);
            }
            if (currenTmilliseconds > timeInterval.value2().getTime()) {
                return ExecuteResult.create(JsonResultCode.CODE_ORDER_TOPAY_EXPIRED, null);
            }
        } else {
            //普通订单或定金订单支付定金
            if (order.getExpireTime().getTime() < currenTmilliseconds) {
                return ExecuteResult.create(JsonResultCode.CODE_ORDER_TOPAY_EXPIRED, null);
            }
        }
        return null;
    }

    /**
     * 商品校验
     * @param param
     * @return
     */
    private ExecuteResult checkGoods(PayParam param, Result<OrderGoodsRecord> orderGoodsRecord) {
        //商品
        Map<Integer, GoodsRecord> goodsRecords = goodsService.getGoodsToOrder(orderGoodsRecord.stream().map(OrderGoodsRecord::getGoodsId).distinct().collect(Collectors.toList()));
        //规格信息,key proId
        Map<Integer, GoodsSpecProductRecord> productInfo = goodsSpecProduct.selectSpecByProIds(orderGoodsRecord.stream().map(OrderGoodsRecord::getProductId).distinct().collect(Collectors.toList()));
        for (OrderGoodsRecord orderGoods : orderGoodsRecord) {
            Goods temp = orderGoods.into(Goods.class);
            temp.setProductInfo(productInfo.get(temp.getProductId()));
            temp.setGoodsInfo(goodsRecords.get(temp.getGoodsId()));
            if(OrderConstant.IS_GIFT_Y.equals(orderGoods.getIsGift())) {
                if(!giftProcessorDao.toPayCheck(orderGoods.getGiftId(), orderGoods.getProductId(), orderGoods.getGoodsNumber())) {
                    //赠品不满足删除
                    orderGoods.delete();
                }
            }
            try {
                createOrder.checkGoodsAndProduct(temp);
            } catch (MpException e) {
                return ExecuteResult.create(e);
            }

        }
        return null;
    }

    /**
     * 活动校验
     */
    private ExecuteResult checkActivity(OrderInfoRecord order) {
        //订单类型
        ArrayList<Byte> goodsType = Lists.newArrayList(OrderInfoService.orderTypeToByte(order.getGoodsType()));
        if (goodsType.contains(ACTIVITY_TYPE_GROUP_BUY)){
            GroupOrderVo groupBuyRecord = groupBuyListService.getByOrder(order.getOrderSn());
            Timestamp date = DateUtils.getLocalDateTime();
            // 是否可以参加拼团
            ResultMessage resultMessage = groupBuyListService.canCreatePinGroupOrder(groupBuyRecord.getUserId(), date, groupBuyRecord.getActivityId(), groupBuyRecord.getGroupId(), IS_GROUPER_N);
            if (!resultMessage.getFlag()) {
                String[] str2 = resultMessage.getMessages().toArray(new String[0]);
                return ExecuteResult.create(resultMessage.getJsonResultCode(), null, str2);
            }
        } else if (goodsType.contains(BaseConstant.ACTIVITY_TYPE_PRE_SALE)){
            //预售
            PreSaleVo info = preSaleProcessorDao.getDetail(order.getActivityId());
            if(info == null || info.getDelFlag().equals(DelFlag.DISABLE_VALUE)) {
                return ExecuteResult.create(JsonResultCode.CODE_ORDER_ACTIVITY_DISABLE, null);
            }
        }
        return null;
    }

    /**
     * 目前转化为待发货只有微信这一种情况
     * @param orderInfo
     * @param payRecord
     * @throws MpException
     */
    public void toWaitDeliver(OrderInfoRecord orderInfo, PaymentRecordRecord payRecord) throws MpException {
        ArrayList<String> goodsTypes = Lists.newArrayList(OrderInfoService.orderTypeToArray(orderInfo.getGoodsType()));

        if(!OrderOperationJudgment.canWaitDeliver(orderInfo.getOrderStatus())) {
            logger().error("订单不可以变为待发货(PayService.toWaitDeliver),sn:{}", orderInfo.getOrderSn());
            throw new MpException(JsonResultCode.CODE_ORDER_NOT_TO_WAIT_DELIVER, "订单不可以变为待发货");
        }

        //TODO 目前转化只有可能为微信回调
        if(!OrderConstant.PAY_CODE_WX_PAY.equals(orderInfo.getPayCode()) || payRecord == null) {
            logger().error("订单支付方式必须为微信支付且必须有payRecord(PayService.toWaitDeliver),sn:{}", orderInfo.getOrderSn());
            throw new MpException(JsonResultCode.CODE_ORDER_NOT_TO_WAIT_DELIVER, "订单支付方式必须为微信支付且必须有payRecord");
        }
        //微信支付记录(全部)
        tradesRecord.addRecord(orderInfo.getMoneyPaid(),orderInfo.getOrderSn(),orderInfo.getUserId(), TradesRecordService.TRADE_CONTENT_MONEY, RecordTradeEnum.TYPE_CRASH_WX_PAY.val(),RecordTradeEnum.TRADE_FLOW_IN.val(),TradesRecordService.TRADE_CONTENT_MONEY);
        //状态转化
        if(goodsTypes.contains(String.valueOf(ACTIVITY_TYPE_GROUP_BUY)) || goodsTypes.contains(String.valueOf(BaseConstant.ACTIVITY_TYPE_GROUP_DRAW))){
            //拼团类型
            orderInfo.setOrderStatus(OrderConstant.ORDER_PIN_PAYED_GROUPING);
        }else{
            //TODO 通知服务、上报广告信息
            if (orderInfo.getOrderAuditType().equals(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT)){
                //待审核
                orderInfo.setOrderStatus(OrderConstant.ORDER_TO_AUDIT);
            }else if (orderInfo.getOrderAuditType().equals(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE)){
                //待开方
                orderInfo.setOrderStatus(OrderConstant.ORDER_TO_AUDIT_OPEN);
            }else {
                //代发货
                orderInfo.setOrderStatus(OrderConstant.ORDER_WAIT_DELIVERY);
            }

        }
        orderInfo.setPayTime(DateUtils.getSqlTimestamp());
        orderInfo.setPaySn(payRecord == null ? StringUtils.EMPTY : payRecord.getPaySn());
        orderInfo.update();

        //订单商品
        Result<OrderGoodsRecord> goods = orderGoodsService.getByOrderId(orderInfo.getOrderId());
        if(orderInfo.getIsLock().equals(OrderConstant.NO)) {
            //库存销量
            atomicOperation.updateStockAndSalesByLock(orderInfo, goods.into(OrderGoodsBo.class), false);
        }

        //TODO 异常订单处理等等
        // 订单生效时营销活动后续处理
        processOrderEffective(orderInfo);
        //模板消息
        sendMessage.send(orderInfo, goods);
        thirdPartyMsgServices.thirdPartService(orderInfo);
    }

    /**
     * 将要过期的未支付订单，进行支付提醒通知。 定时每分钟执行，获取10分钟后过期的订单，通知用户支付
     */
    public void autoExpiringNoPayOrderNotify(){
        Result<OrderInfoRecord> orders = orderInfo.getExpiringNoPayOrderList();
        orders.forEach(order->{
            //模板消息
            sendMessage.send(order);
        });

    }

    /**
     *  支付活动
     * @param orderInfo
     * @throws MpException
     */
    public void processOrderEffective(OrderInfoRecord orderInfo) throws MpException {
        if (!orderInfo.getOrderStatus().equals(OrderConstant.ORDER_WAIT_DELIVERY)&&!orderInfo.getOrderStatus().equals(OrderConstant.ORDER_PIN_PAYED_GROUPING)){
            return;
        }
        OrderBeforeParam orderBeforeParam = createOrderBeforeParam(orderInfo);
        marketProcessorFactory.processOrderEffective(orderBeforeParam,orderInfo);
        if(orderInfo.getIsLock().equals(OrderConstant.NO)) {
            marketProcessorFactory.processUpdateStock(orderBeforeParam, orderInfo);
        }
    }

    public OrderBeforeParam createOrderBeforeParam(OrderInfoRecord orderInfo) {
        Byte[] orderType = OrderInfoService.orderTypeToByte(orderInfo.getGoodsType());
        List<Byte> activityTypeList = Lists.newArrayList(orderType);
        List<Byte> singlenessActivity = activityTypeList.stream().filter(OrderCreateMpProcessorFactory.SINGLENESS_ACTIVITY::contains).collect(Collectors.toList());
        Byte activityType = CollectionUtils.isEmpty(singlenessActivity) ? null : singlenessActivity.get(0);
        OrderBeforeParam orderBeforeParam =new OrderBeforeParam();
        orderBeforeParam.setActivityType(activityType);
        orderBeforeParam.setActivityId(orderInfo.getActivityId());
        orderBeforeParam.setDate(orderInfo.getCreateTime());
        orderBeforeParam.setGoods(new ArrayList<>());
        List<GoodsRecord> goodsList = orderGoodsService.getGoodsInfoRecordByOrderSn(orderInfo.getOrderSn());
        Map<Integer, Goods> orderGoodsMap = orderGoodsService.getOrderGoods(orderInfo.getOrderSn()).intoMap(OrderGoods.ORDER_GOODS.GOODS_ID, Goods.class);
        goodsList.forEach(goods->{
            Goods goodsParam = orderGoodsMap.get(goods.getGoodsId());
            goodsParam.setGoodsInfo(goods);
            orderBeforeParam.getGoods().add(goodsParam);
        });
        return orderBeforeParam;
    }


}

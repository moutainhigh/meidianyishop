package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConsumpData;
import com.meidianyi.shop.service.pojo.shop.member.card.dao.CardFullDetail;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.card.wxapp.WxCardExchangeService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 王帅
 * 限次卡兑换
 */
@Service
public class CardExchangeProcess extends WxCardExchangeService implements Processor,GoodsDetailProcessor,CreateOrderProcessor {
    @Autowired
    private OrderInfoService orderInfo;
    @Override
    public Byte getPriority() {
        return 1;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_EXCHANG_ORDER;
    }

    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        CardFullDetail cardDetail = userCard.memberCardService.getCardDetailByNo(param.getMemberCardNo());
        orderCheck(param,cardDetail);
        orderInit(param,cardDetail);
    }

    private void orderCheck(OrderBeforeParam param, CardFullDetail cardDetail) throws MpException {
        //构造校验参数
        Map<Integer, Integer> checkParam = param.getGoods().stream()
            .collect(Collectors.groupingBy(OrderBeforeParam.Goods::getGoodsId))
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().stream().mapToInt(OrderBeforeParam.Goods::getGoodsNumber).sum()));
        judgeExchangGoodsAvailable(checkParam, param.getMemberCardNo(), false);
    }

    private void orderInit(OrderBeforeParam param, CardFullDetail cardDetail) {
        //免运费
        if(CardUtil.isFreeShipping(cardDetail.getMemberCard().getExchangFreight())) {
            logger().info("限次卡兑换免运费设置");
            param.setIsFreeShippingAct(OrderConstant.YES);
        }
        //取消商品限购
        param.getGoods().forEach(x->x.setIsAlreadylimitNum(true));
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        //限次卡兑换不会存在待付款状态直接可以扣次数
        CardConsumpData cardConsumpData = new CardConsumpData();
        cardConsumpData.setUserId(order.getUserId());
        cardConsumpData.setCardNo(order.getCardNo());
        cardConsumpData.setReasonId(RemarkTemplate.ORDER_LIMIT_EXCHGE_GOODS.code);
        cardConsumpData.setReason(order.getOrderSn());
        cardConsumpData.setType(CardConstant.MCARD_TP_LIMIT);
        cardConsumpData.setOrderSn(order.getOrderSn());
        cardConsumpData.setExchangeCount((short)(-param.getBos().stream().filter(x-> x.getIsGift() != null && x.getIsGift() == OrderConstant.NO).map(OrderGoodsBo::getGoodsNumber).reduce(0, Integer::sum)));
        cardConsumpData.setPayment("");
        mCardSvc.updateMemberCardExchangeSurplus(cardConsumpData);
    }

    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        userCheckedGoodsSvc.removeGoodsAfterOrder(order.getCardNo());
    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {
        OrderInfoRecord order = orderInfo.getOrderByOrderSn(returnOrderRecord.getOrderSn());
        //限次卡兑换不会存在待付款状态直接可以扣次数
        CardConsumpData cardConsumpData = new CardConsumpData();
        cardConsumpData.setUserId(returnOrderRecord.getUserId());
        cardConsumpData.setCardNo(order.getCardNo());
        cardConsumpData.setReasonId(RemarkTemplate.ORDER_RETURN_LIMIT_CARD.code);
        cardConsumpData.setReason(returnOrderRecord.getOrderSn());
        cardConsumpData.setType(CardConstant.MCARD_TP_LIMIT);
        cardConsumpData.setOrderSn(returnOrderRecord.getOrderSn());
        cardConsumpData.setExchangeCount(returnGoods.stream().map(OrderReturnGoodsVo::getGoodsNumber).reduce(0, Integer::sum).shortValue());
        cardConsumpData.setPayment("");
        mCardSvc.updateMemberCardExchangeSurplus(cardConsumpData);

    }

    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {

    }
}

package com.meidianyi.shop.service.shop.activity.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.market.gift.GiftVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.gift.GoodsGiftMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.activity.dao.GiftProcessorDao;
import com.meidianyi.shop.service.shop.config.GiftConfigService;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.meidianyi.shop.service.shop.market.gift.GiftService.CONDITION_PRIORITY;

/**
 * 赠品
 * @author 王帅
 */
@Service
@Slf4j
public class GiftProcessor implements GoodsDetailProcessor,CreateOrderProcessor,ActivityCartListStrategy {

    @Autowired
    private GiftProcessorDao giftDao;
    @Autowired
    private OrderGoodsService orderGoods;
    @Autowired
    private GiftConfigService giftConfigService;
    @Override
    public Byte getPriority() {
        return 0;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_GIFT;
    }

    /*****************商品详情处理*******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        List<GoodsGiftMpVo> goodsGift = giftDao.getGoodsGiftInfoForDetail(capsule.getGoodsId(), DateUtils.getLocalDateTime());
        capsule.setGoodsGifts(goodsGift);
    }


    public void getGifts(Integer userId, List<OrderGoodsBo> goodsBo, List<Byte> orderType){
        giftDao.getGifts( userId, goodsBo, orderType);
    }

    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        //赠品不在这判断
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        //赠品不在这判断
    }

    @Override
    public void processOrderEffective(OrderBeforeParam param,OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        log.info("赠品下单更新库存start");
        Map<Integer, Map<Integer, Integer>> updateparam = Maps.newHashMap();
        param.getBos().stream()
            .filter(x -> OrderConstant.IS_GIFT_Y.equals(x.getIsGift())).collect(Collectors.groupingBy(OrderGoodsBo::getGiftId))
            .forEach((k, v)->
                updateparam.put(k, v.stream().collect(Collectors.toMap(OrderGoodsBo::getProductId, OrderGoodsBo::getGoodsNumber)))
            );
        if(updateparam.size() != 0){
            giftDao.updateStockAndSales(updateparam);
        }
        log.info("赠品下单更新库存end,date:{}",updateparam);
    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException{
        if(CollectionUtils.isEmpty(returnGoods)){
            return;
        }
        Map<Integer, Map<Integer, Integer>> updateparam = Maps.newHashMap();
        //退款商品recIds
        List<Integer> recIds = returnGoods.stream().map(OrderReturnGoodsVo::getRecId).collect(Collectors.toList());
        //退款退货商品对应的orderGoods商品
        Map<Integer, OrderGoodsRecord> orderGoodsRecord = orderGoods.getOrderGoods(returnGoods.get(0).getOrderSn(), recIds).intoMap(OrderGoodsRecord::getRecId);
        returnGoods.stream().collect(Collectors.groupingBy(x->orderGoodsRecord.get(x.getRecId()).getGiftId()))
            .forEach((k, v) ->{
                updateparam.put(k, v.stream().collect(Collectors.toMap(OrderReturnGoodsVo::getProductId, OrderReturnGoodsVo::getGoodsNumber)));
                //退款时将数量置为负数
                updateparam.get(k).forEach((k1, v1)-> updateparam.get(k).put(k1, -v1));
                }
            );
        if(updateparam.size() != 0){
            giftDao.updateStockAndSales(updateparam);
        }
    }

    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        log.info("购物车--赠品活动");
        Timestamp nowTime = DateUtils.getLocalDateTime();
        //0：赠送满足赠品条件的所有赠品;1：只赠送其中优先级最高的活动赠品
        Byte cfg = giftConfigService.getCfg();
        List<GiftVo> activeActivity = giftDao.getActiveActivity();
        //k->goodsId;v->数量
        Map<Integer, Integer> goodsMapCount = cartBo.getCartGoodsList().stream().collect(Collectors.toMap(WxAppCartGoods::getGoodsId, WxAppCartGoods::getCartNumber, (ov, nv) -> ov + nv));
        //商品未参与赠品记录
        Set<Integer> noJoinRecord = cartBo.getCartGoodsList().stream().map(WxAppCartGoods::getGoodsId).collect(Collectors.toSet());
        activeActivity.forEach(giftVo -> {
            cartBo.getCartGoodsList().forEach(goods->{
                if (giftVo.getGoodsId().contains(goods.getGoodsId().toString())){
                    CartActivityInfo cartActivityInfo =new CartActivityInfo();
                    cartActivityInfo.setActivityId(giftVo.getId());
                    cartActivityInfo.setActivityType(BaseConstant.ACTIVITY_TYPE_GIFT);
                    goods.getCartActivityInfos().add(cartActivityInfo);
                }
            });
        });
        activeActivity.forEach(giftVo->{
            log.info("赠品活动{},id:{},判断",giftVo.getName(),giftVo.getId());
            if(CONDITION_PRIORITY.equals(cfg) && noJoinRecord.size() == 0){
                //只送最高优先级：如果当前未参与商品为0则
                return;
            }
            giftDao.transformVo(giftVo);
            //参加活动商品数量
            final int[] number = {0};
            //参加活动商品金额
            final BigDecimal[] price = {BigDecimal.ZERO};
            //当前活动参与商品
            Set<Integer> joinRecord = Sets.newHashSet();
            cartBo.getCartGoodsList().forEach(goods->{
                //活动商品
                if((CollectionUtils.isEmpty(giftVo.getGoodsIds()) || giftVo.getGoodsIds().contains(goods.getGoodsId()))){
                    number[0] = number[0] + goods.getCartNumber();
                    price[0] = price[0].add(goods.getPrdPrice());
                    joinRecord.add(goods.getGoodsId());
                }
            });
            if(number[0] > 0){
                List<OrderGoodsBo> orderGoodsBos = giftDao.packageAndCheckGift(cartBo.getUserId(), giftVo, price[0], number[0], goodsMapCount,  Lists.newArrayList(), noJoinRecord);
                if(CollectionUtils.isNotEmpty(orderGoodsBos)){
                    log.info("赠品活动{},生效,适合的商品有",giftVo.getName());
                    orderGoodsBos.forEach(orderGoods ->{
                        WxAppCartGoods cartGoods = giftDao.getOrderGoodsToCartGoods(orderGoods, giftVo, cartBo.getUserId());
                        cartBo.getCartGoodsList().add(cartGoods);
                    });
                    noJoinRecord.removeAll(joinRecord);
                }
            }
        });

        log.info("购物车--赠品活动结束");
    }
}

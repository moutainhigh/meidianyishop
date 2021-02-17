package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PurchasePriceRuleRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.PurchasePriceCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.PurchasePricePromotion;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.activity.dao.PurchasePriceProcessorDao;
import com.meidianyi.shop.service.shop.market.increasepurchase.IncreasePurchaseService;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Record4;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.PURCHASE_PRICE_RULE;
import static com.meidianyi.shop.db.shop.tables.PurchasePriceDefine.PURCHASE_PRICE_DEFINE;

/**
 * 加价购处理器
 *
 * @author 李晓冰
 * @date 2020年01月14日
 */
@Service
@Slf4j
public class PurchasePriceProcessor implements Processor, GoodsDetailProcessor, ActivityCartListStrategy, CreateOrderProcessor {
    @Autowired
    PurchasePriceProcessorDao purchasePriceProcessorDao;

    @Autowired
    IncreasePurchaseService increasePurchase;
    @Autowired
    private CartService cartService;

    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_FREE_SHIP_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE;
    }

    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        List<PurchasePricePromotion> promotions = purchasePriceProcessorDao.getPurchasePriceInfoForDetail(capsule.getGoodsId(), DateUtils.getLocalDateTime());
        if (promotions == null) {
            return;
        }
        capsule.getPromotions().put(BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE, promotions);
    }

    //******************购物车-加价购********

    /**
     * @param cartBo 业务数据类
     */
    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        log.info("购物车--加价购");
        //缓存
        Map<Integer, Map<Integer, Result<Record4<Integer, Integer, BigDecimal, BigDecimal>>>> activityCacheMap = new HashMap<>();
        //活动记录
        Map<Integer, PurchasePriceCartBo> activityMap = new HashMap<>();
        //商品活动信息
        for (WxAppCartGoods goods : cartBo.getCartGoodsList()) {
            Map<Integer, Result<Record4<Integer, Integer, BigDecimal, BigDecimal>>> purchaseRulesMap;
            //获取商品的活动
            if (activityCacheMap.size() > 0 && activityCacheMap.get(goods.getGoodsId()) != null) {
                purchaseRulesMap = activityCacheMap.get(goods.getGoodsId());
            } else {
                //获取商品的活动规则
                purchaseRulesMap = purchasePriceProcessorDao.getPurchasePriceInfo(goods.getGoodsId(), cartBo.getDate());
                if (purchaseRulesMap!=null){
                    activityCacheMap.put(goods.getGoodsId(), purchaseRulesMap);
                }
            }
            //配置商品符合的活动信息
            if (purchaseRulesMap != null && purchaseRulesMap.size() != 0) {
                purchaseRulesMap.forEach((key, purchaseRules) -> {
                    log.info("加价购商品:{},活动id:{}", goods.getGoodsName(), key);
                    CartActivityInfo cartActivityInfo = new CartActivityInfo();
                    cartActivityInfo.setActivityType(BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE);
                    cartActivityInfo.setActivityId(key);
                    CartActivityInfo.PurchasePrice purchasePrice = new CartActivityInfo.PurchasePrice();
                    purchasePrice.setPurchasePriceRule(new ArrayList<>());
                    //加价购规则
                    for (Record4<Integer, Integer, BigDecimal, BigDecimal> rule : purchaseRules) {
                        CartActivityInfo.PurchasePriceRule cartPurchasePriceRule = new CartActivityInfo.PurchasePriceRule();
                        cartPurchasePriceRule.setFullPrice(rule.get(PURCHASE_PRICE_RULE.FULL_PRICE));
                        cartPurchasePriceRule.setPurchasePrice(rule.get(PURCHASE_PRICE_RULE.PURCHASE_PRICE));
                        cartPurchasePriceRule.setRuleId(rule.get(PURCHASE_PRICE_RULE.ID));
                        cartPurchasePriceRule.setActivityId(rule.get(PURCHASE_PRICE_DEFINE.ID));
                        purchasePrice.getPurchasePriceRule().add(cartPurchasePriceRule);
                        if (purchasePrice.getRule() == null) {
                            purchasePrice.setRule(cartPurchasePriceRule);
                        }
                    }
                    //设置
                    cartActivityInfo.setPurchasePrice(purchasePrice);
                    goods.getCartActivityInfos().add(cartActivityInfo);
                    //当前商品活动
                    if (goods.getType().equals(BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE) && goods.getExtendId().equals(key)) {
                        log.info("参与活动");
                        goods.setActivityType(BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE);
                        goods.setActivityId(cartActivityInfo.getActivityId());
                        //当前生效活动
                        purchasePriceProcessorDao.purchasePricePutMap(activityMap, goods, cartActivityInfo);
                    }
                });
            }
            if (BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE.equals(goods.getType())&&goods.getExtendId()!=null){
                List<CartActivityInfo> activityInfos = goods.getCartActivityInfos().stream().filter(activityInfo -> BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE.equals(activityInfo.getActivityType()) && activityInfo.getActivityId().equals(goods.getExtendId())).collect(Collectors.toList());
                if (activityInfos.size() == 0){
                    log.info("购物车-加价购-没有找到合适活动,取消活动选中");
                    cartService.switchActivityGoods(cartBo.getUserId(),goods.getCartId(),0,(byte)0);
                    goods.setActivityType(null);
                    goods.setActivityId(null);
                }
            }

        }
        //启用的活动配置
        purchasePriceProcessorDao.enabledActivity(activityMap);
        //国际化
        purchasePriceProcessorDao.activityToString(cartBo);
        //加价购商品校验
        purchasePriceProcessorDao.checkActivityGoods(cartBo, activityMap);
    }


    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        //加价购只有购物车可以下单
        if(!OrderConstant.CART_Y.equals(param.getIsCart())){
            return;
        }
        List<Integer> actRuleIds = param.getGoods().stream().filter(x -> (BaseConstant.ACTIVITY_TYPE_PURCHASE_GOODS.equals(x.getCartType()))).map(OrderBeforeParam.Goods::getCartExtendId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(actRuleIds)) {
            return;
        }
        Map<Integer, PurchasePriceRuleRecord> actRules = increasePurchase.getRules(actRuleIds).intoMap(PurchasePriceRuleRecord::getId);
        List<Integer> actIds = actRules.values().stream().map(PurchasePriceRuleRecord::getPurchasePriceId).distinct().collect(Collectors.toList());

        boolean statusFlag = purchasePriceProcessorDao.checkActStatus(actIds);
        if(!statusFlag) {
            throw new MpException(JsonResultCode.CODE_ORDER_ACTIVITY_END);
        }
        for (OrderBeforeParam.Goods goods : param.getGoods()) {
            if(BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE.equals(goods.getCartType())) {
                goods.setPurchasePriceId(goods.getCartExtendId());
            }
            else if(BaseConstant.ACTIVITY_TYPE_PURCHASE_GOODS.equals(goods.getCartType())) {
                PurchasePriceRuleRecord rule = actRules.get(goods.getCartExtendId());
                if(rule == null) {
                    throw new MpException(JsonResultCode.CODE_ORDER_ACTIVITY_END);
                }
                goods.setPurchasePriceId(rule.getPurchasePriceId());
                goods.setPurchasePriceRuleId(rule.getId());
                goods.setProductPrice(rule.getPurchasePrice());
            }
        }
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        List<OrderGoodsBo> bos = param.getBos();
        for(OrderGoodsBo bo:bos){
            if(bo.getPurchasePriceId() != null && bo.getPurchasePriceRuleId() != null){
                increasePurchase.addActivityTag(bo.getPurchasePriceId(),param.getWxUserInfo().getUserId());
            }
        }
    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {

    }
}

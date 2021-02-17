package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.tables.records.PurchasePriceDefineRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.PurchasePriceCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.PurchasePricePromotion;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import org.jooq.Record4;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.PURCHASE_PRICE_RULE;
import static com.meidianyi.shop.db.shop.tables.PurchasePriceDefine.PURCHASE_PRICE_DEFINE;

/**
 * 加价购processor dao
 * @author 李晓冰
 * @date 2020年01月14日
 */
@Service
public class PurchasePriceProcessorDao extends ShopBaseService {


    @Autowired
    private CartService cartService;
    /**
     * 加价购促销信息
     * @param goodsId 商品ID
     * @return 加价购促销信息
     */
    public List<PurchasePricePromotion> getPurchasePriceInfoForDetail(Integer goodsId, Timestamp now){

        Map<Integer, List<PurchasePricePromotion.PurchasePriceRule>> purchaseRulesMap = db().select(PURCHASE_PRICE_RULE.PURCHASE_PRICE_ID,PURCHASE_PRICE_RULE.FULL_PRICE,PURCHASE_PRICE_RULE.PURCHASE_PRICE)
            .from(PURCHASE_PRICE_DEFINE).innerJoin(PURCHASE_PRICE_RULE).on(PURCHASE_PRICE_DEFINE.ID.eq(PURCHASE_PRICE_RULE.PURCHASE_PRICE_ID))
            .where(PURCHASE_PRICE_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(PURCHASE_PRICE_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(PURCHASE_PRICE_RULE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(DslPlus.findInSet(goodsId,PURCHASE_PRICE_DEFINE.GOODS_ID)))
                .and(PURCHASE_PRICE_DEFINE.START_TIME.le(now)).and(PURCHASE_PRICE_DEFINE.END_TIME.ge(now))).orderBy(PURCHASE_PRICE_DEFINE.LEVEL.desc(), PURCHASE_PRICE_DEFINE.CREATE_TIME.desc())
            .fetchGroups(PURCHASE_PRICE_RULE.PURCHASE_PRICE_ID, PurchasePricePromotion.PurchasePriceRule.class);


        if (purchaseRulesMap == null || purchaseRulesMap.size() == 0) {
            return null;
        }

        List<PurchasePricePromotion> promotions =new ArrayList<>(purchaseRulesMap.size());
        for (Map.Entry<Integer, List<PurchasePricePromotion.PurchasePriceRule>> entry : purchaseRulesMap.entrySet()) {
            PurchasePricePromotion promotion = new PurchasePricePromotion();
            promotion.setPromotionId(entry.getKey());
            promotion.setPurchasePriceRules(entry.getValue());
            promotions.add(promotion);
        }

        return promotions;
    }

    /**
     *加价购
     * @param goodsId 商品id
     * @param date 日期
     * @return 加价购数据
     */
    public Map<Integer, Result<Record4<Integer, Integer, BigDecimal, BigDecimal>>> getPurchasePriceInfo(Integer goodsId, Timestamp date){
        return db().select(PURCHASE_PRICE_DEFINE.ID,PURCHASE_PRICE_RULE.ID, PURCHASE_PRICE_RULE.FULL_PRICE, PURCHASE_PRICE_RULE.PURCHASE_PRICE)
                .from(PURCHASE_PRICE_DEFINE)
                .innerJoin(PURCHASE_PRICE_RULE).on(PURCHASE_PRICE_DEFINE.ID.eq(PURCHASE_PRICE_RULE.PURCHASE_PRICE_ID))
                .where(PURCHASE_PRICE_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
                .and(PURCHASE_PRICE_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(PURCHASE_PRICE_RULE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(DslPlus.findInSet(goodsId, PURCHASE_PRICE_DEFINE.GOODS_ID))
                .and(PURCHASE_PRICE_DEFINE.START_TIME.le(date))
                .and(PURCHASE_PRICE_DEFINE.END_TIME.ge(date))
                .orderBy(PURCHASE_PRICE_DEFINE.LEVEL.desc(), PURCHASE_PRICE_DEFINE.CREATE_TIME.desc())
                .fetch().intoGroups(PURCHASE_PRICE_DEFINE.ID);
    }
    /**
     * 把活动记录下来
     *
     * @param activityMap      活动map
     * @param goods            商品
     * @param cartActivityInfo activityId
     */
    public void purchasePricePutMap(Map<Integer, PurchasePriceCartBo> activityMap, WxAppCartGoods goods, CartActivityInfo cartActivityInfo) {
        PurchasePriceCartBo priceCartBo = activityMap.get(goods.getExtendId()) != null ? activityMap.get(cartActivityInfo.getActivityId()) : new PurchasePriceCartBo();
        priceCartBo.getCartId().add(goods.getCartId());
        priceCartBo.getProductId().add(goods.getProductId());
        if (goods.getIsChecked().equals(CartConstant.CART_IS_CHECKED)){
            priceCartBo.getNum().add(goods.getCartNumber());
            priceCartBo.getMoney().add(goods.getGoodsPrice().multiply(BigDecimal.valueOf(goods.getCartNumber())));
        }
        priceCartBo.setPurchasePrice(cartActivityInfo.getPurchasePrice());
        activityMap.put(cartActivityInfo.getActivityId(), priceCartBo);
    }

    /**
     * 校验加价购商品
     * --超出设置范围的商品删除,商品价格设置为加价购价格
     * --删除多余的加价购的商品的记录
     * @param cartBo      购物车商品列表
     * @param activityMap 活动数据
     */
    public void checkActivityGoods(WxAppCartBo cartBo, Map<Integer, PurchasePriceCartBo> activityMap) {
        for (Map.Entry<Integer, PurchasePriceCartBo> entry : activityMap.entrySet()) {
            Integer k = entry.getKey();
            PurchasePriceCartBo v = entry.getValue();
            BigDecimal moneySums = v.getMoney().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            //规则id
            Map<Integer, CartActivityInfo.PurchasePriceRule> ruleMap = v.getPurchasePrice().getPurchasePriceRule().stream().collect(Collectors.toMap(CartActivityInfo.PurchasePriceRule::getRuleId, Function.identity()));
            PurchasePriceDefineRecord purchaseInfo = getPurchaseInfo(k);
            int goodsNum = 0;
            Iterator<WxAppCartGoods> iterator = cartBo.getCartGoodsList().iterator();
            while (iterator.hasNext()) {
                WxAppCartGoods goods = iterator.next();
                if (goods.getType() != null && goods.getType().equals(BaseConstant.ACTIVITY_TYPE_PURCHASE_GOODS) && ruleMap.containsKey(goods.getExtendId())) {
                    CartActivityInfo.PurchasePriceRule rule = ruleMap.get(goods.getExtendId());
                    logger().info("加价购-加价购商品:{},加价价格:{},数量:{}", goods.getGoodsName(), rule.getPurchasePrice().toString(),goods.getCartNumber());
                    goods.setActivityType(BaseConstant.ACTIVITY_TYPE_PURCHASE_GOODS);
                    goods.setActivityId(rule.getActivityId());
                    goods.setPrdPrice(ruleMap.get(goods.getExtendId()).getPurchasePrice());
                    goods.setCartActivityInfos(null);
                    logger().info("加价购-规则校验");
                    if (moneySums.compareTo(rule.getFullPrice())<0){
                        logger().info("该商品的规则不符合要求");
                        if (goods.getIsChecked().equals(CartConstant.CART_IS_CHECKED)){
                            cartService.switchCheckedProduct(cartBo.getUserId(),goods.getCartId(),CartConstant.CART_NO_CHECKED);
                        }
                        goods.setIsChecked(CartConstant.CART_NO_CHECKED);
                        continue;
                    }else {
                        if (goods.getIsChecked().equals(CartConstant.CART_NO_CHECKED)){
                            cartService.switchCheckedProduct(cartBo.getUserId(),goods.getCartId(),CartConstant.CART_IS_CHECKED);
                        }
                        goods.setIsChecked(CartConstant.CART_IS_CHECKED);
                    }
                    goodsNum+=goodsNum+goods.getCartNumber();
                    logger().info("加价购-加购商品数量限制");
                    if (purchaseInfo.getMaxChangePurchase()!=0&&purchaseInfo.getMaxChangePurchase()<goodsNum){
                        logger().info("加价商品数量大于规则数量:{}",purchaseInfo.getMaxChangePurchase());
                        for (int i=goodsNum-purchaseInfo.getMaxChangePurchase();i>0;i--){
                            if (goods.getCartNumber()>1){
                                logger().info("商品数量减少:{}",goods.getGoodsName());
                                cartService.changeGoodsNumber(cartBo.getUserId(),goods.getCartId(),goods.getProductId(),goods.getCartNumber()-1);
                                goods.setCartNumber(goods.getCartNumber()-1);
                            }else {
                                logger().info("删除加价购商品:{}",goods.getGoodsName());
                                cartService.removeCartProductById(cartBo.getUserId(),goods.getCartId());
                                iterator.remove();
                            }
                        }
                        goodsNum =purchaseInfo.getMaxChangePurchase().intValue();
                    }

                }
            }
        }
        //删除无用的数据--无主商品的
        Iterator<WxAppCartGoods> iterator = cartBo.getCartGoodsList().iterator();
        while (iterator.hasNext()){
            WxAppCartGoods goods = iterator.next();
            if (goods.getType().equals(BaseConstant.ACTIVITY_TYPE_PURCHASE_GOODS) && goods.getActivityType() == null) {
                cartService.removeCartProductById(cartBo.getUserId(), goods.getCartId());
                iterator.remove();
            }
        }
    }





    /**
     * 国际化
     *
     * @param cartBo
     */
    public void activityToString(WxAppCartBo cartBo) {
        for (WxAppCartGoods goods : cartBo.getCartGoodsList()) {
            goods.getCartActivityInfos().forEach(cartActivityInfo -> {
                if (cartActivityInfo.getActivityType().equals(BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE)) {
                    CartActivityInfo.PurchasePrice purchasePrice = cartActivityInfo.getPurchasePrice();
                    CartActivityInfo.PurchasePriceRule enabledRule = purchasePrice.getRule();
                    List<CartActivityInfo.PurchasePriceRule> purchasePriceRule = purchasePrice.getPurchasePriceRule();
                    if (enabledRule != null) {
                        enabledRule.setName("满 " + enabledRule.getFullPrice() + " 加价" + enabledRule.getPurchasePrice() + "换购");
                        purchasePrice.setCondition(enabledRule.getName());
                    }
                    purchasePriceRule.forEach(rule -> {
                        rule.setName("满 " + rule.getFullPrice() + " 加价" + rule.getPurchasePrice() + "换购");
                    });
                }
            });
        }
    }

    /**
     * 启用的活动配置
     * @param activityMap map
     */
    public void enabledActivity(Map<Integer, PurchasePriceCartBo> activityMap) {
        //筛选活动
        activityMap.forEach((k, purchasePriceCartBo) -> {
            logger().info("购物车中的加价购活动id{}", k);
            BigDecimal moneySums = purchasePriceCartBo.getMoney().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            CartActivityInfo.PurchasePrice purchasePrice = purchasePriceCartBo.getPurchasePrice();
            purchasePrice.getPurchasePriceRule().forEach(rule -> {
                if (moneySums.compareTo(rule.getFullPrice()) >= 0) {
                    if (purchasePrice.getRule() == null || purchasePrice.getRule().getFullPrice().compareTo(rule.getFullPrice()) < 0) {
                        purchasePrice.setRule(rule);
                    }
                }
            });
            //没有合适规则,取最小的值
            if (purchasePrice.getRule() == null) {
                purchasePrice.getPurchasePriceRule().forEach(rule -> {
                    if (purchasePrice.getRule() == null || rule.getFullPrice().compareTo(purchasePrice.getRule().getFullPrice()) < 0) {
                        purchasePrice.setRule(rule);
                    }
                });
            }
            logger().info("购物车中的加价购活动id{},启用适合的规则为:满{},加{},", k, purchasePrice.getRule().getFullPrice().toString(), purchasePrice.getRule().getPurchasePrice());
        });
    }



    /**
     * 加价购
     * @param activityId activityId
     * @return 加价购记录
     */
    public PurchasePriceDefineRecord  getPurchaseInfo(Integer activityId){
      return  db().selectFrom(PURCHASE_PRICE_DEFINE).where(PURCHASE_PRICE_DEFINE.ID.eq(activityId)).fetchAny();
    }

    /**
     * 校验活动是否进行中
     * @param actIds
     * @return
     */
    public boolean checkActStatus(List<Integer> actIds) {
        Integer count = db().selectCount().from(PURCHASE_PRICE_DEFINE).where(PURCHASE_PRICE_DEFINE.ID.in(actIds)
            .and(PURCHASE_PRICE_DEFINE.START_TIME.lessThan(Timestamp.valueOf(LocalDateTime.now())))
            .and(PURCHASE_PRICE_DEFINE.END_TIME.greaterThan(Timestamp.valueOf(LocalDateTime.now()))))
            .fetchOneInto(int.class);
        return actIds.size() == count;
    }
}

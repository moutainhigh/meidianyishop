package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.FirstSpecialProductBo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.GoodsActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.OrderCartProductBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.firstspecial.FirstSpecialMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.firstspecial.FirstSpecialPrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.FirstSpecialPromotion;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.shop.activity.dao.FirstSpecialProcessorDao;
import com.meidianyi.shop.service.shop.config.FirstSpecialConfigService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record3;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL;
import static com.meidianyi.shop.db.shop.tables.FirstSpecialProduct.FIRST_SPECIAL_PRODUCT;

/**
 * @author 李晓冰
 * @date 2019年11月01日
 */
@Service
@Slf4j
public class FirstSpecialProcessor implements Processor, ActivityGoodsListProcessor, ActivityCartListStrategy, CreateOrderProcessor, GoodsDetailProcessor {

    @Autowired
    FirstSpecialProcessorDao firstSpecialProcessorDao;

    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    private CartService cartService;
    @Autowired
    private FirstSpecialConfigService firstSpecialConfigService;

    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_FIRST_SPECIAL_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return ACTIVITY_TYPE_FIRST_SPECIAL;
    }

    /*****************商品列表处理*******************/
    @Override
    /**
     * 商品装修列表-首单特惠
     */
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {
        if (userId != null && !orderInfoService.isNewUser(userId, true)) {
            return;
        }
        List<GoodsListMpBo> availableCapsules = capsules.stream().filter(x -> !GoodsConstant.isGoodsTypeIn13510(x.getActivityType()) && !x.getProcessedTypes().contains(BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE))
            .collect(Collectors.toList());

        List<Integer> goodsIds = availableCapsules.stream().map(GoodsListMpBo::getGoodsId).collect(Collectors.toList());
        Map<Integer, Result<Record3<Integer, Integer, BigDecimal>>> firstSpecialPrds = firstSpecialProcessorDao.getGoodsFirstSpecialForListInfo(goodsIds, DateUtils.getLocalDateTime());

        availableCapsules.forEach(capsule -> {
            Integer goodsId = capsule.getGoodsId();
            Result<Record3<Integer, Integer, BigDecimal>> result = firstSpecialPrds.get(goodsId);
            if (result == null) {
                return;
            }
            capsule.setRealPrice(result.get(0).get(FIRST_SPECIAL_PRODUCT.PRD_PRICE));
            GoodsActivityBaseMp activity = new GoodsActivityBaseMp();
            activity.setActivityType(ACTIVITY_TYPE_FIRST_SPECIAL);
            activity.setActivityId(result.get(0).get(FIRST_SPECIAL_PRODUCT.FIRST_SPECIAL_ID));
            capsule.getGoodsActivities().add(activity);
            capsule.getProcessedTypes().add(ACTIVITY_TYPE_FIRST_SPECIAL);
        });
    }

    /*****************商品详情处理*******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {

        // 已经被其它活动处理则退出
        if (capsule.getActivity() != null) {
            return;
        }
        // 不是首单直接退出
        if (param.getUserId() != null && !orderInfoService.isNewUser(param.getUserId(), true)) {
            return;
        }

        FirstSpecialMpVo vo = firstSpecialProcessorDao.getFirstSpecialInfo(param.getGoodsId(), DateUtils.getLocalDateTime());
        // 该商品无有效首单特惠活动
        if (vo == null) {
            return;
        }
        // 原始规格对象映射
        Map<Integer, GoodsPrdMpVo> prdMap = capsule.getProducts().stream().collect(Collectors.toMap(GoodsPrdMpVo::getPrdId, Function.identity()));

        // 设置规格价格，并且设置有效规格
        int goodsNum = 0;
        List<FirstSpecialPrdMpVo> newPrdMp = new ArrayList<>(vo.getFirstSpecialPrdMpVos().size());
        for (FirstSpecialPrdMpVo fsPrd : vo.getFirstSpecialPrdMpVos()) {
            GoodsPrdMpVo goodsPrdMpVo = prdMap.get(fsPrd.getProductId());
            // 商品修改了规格，此特惠规格已不存在
            if (goodsPrdMpVo == null) {
                prdMap.remove(fsPrd.getProductId());
            } else {
                fsPrd.setPrdPrice(goodsPrdMpVo.getPrdRealPrice());
                goodsNum+=goodsPrdMpVo.getPrdNumber();
                newPrdMp.add(fsPrd);
            }
        }
        // 设置有效商品数量和对应的规格信息
        capsule.setGoodsNumber(goodsNum);
        capsule.setProducts(new ArrayList<>(prdMap.values()));
        // 商品规格信息修改
        if (prdMap.values().size() == 0) {
            log.debug("小程序-商品详情-首单特惠-商品规格信息和活动规格信息无交集");
            vo.setActState(BaseConstant.ACTIVITY_STATUS_NO_PRD_TO_USE);
        }
        // 设置新的首单特惠活动规格信息
        vo.setFirstSpecialPrdMpVos(newPrdMp);
        capsule.setActivity(vo);

        // 设置促销列表里的内容
        FirstSpecialPromotion promotion = new FirstSpecialPromotion();
        promotion.setPromotionId(vo.getActivityId());
        promotion.setPromotionType(vo.getActivityType());
        promotion.setIsLimit(vo.getIsLimit());
        promotion.setLimitAmount(vo.getLimitAmount());
        promotion.setLimitFlag(vo.getLimitFlag());

        capsule.getPromotions().put(promotion.getPromotionType(), Collections.singletonList(promotion));
    }


    //**********************购物车********************

    /**
     * 购物车首单特惠
     *新用户专享,按照优先级排序
     * 活动关联到商品,具体活动价格关联到规格
     * 全局数量限制--------购物车中规格数量限制--超过限制的不计算优惠
     * 活动中限购数量①----商品规格--超过限制后全部按照原价
     * 活动中限制数量②----不能超出规定数量
     * @param cartBo 业务数据类
     */
    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        log.info("购物车首单特惠");
        boolean isNewUser = orderInfoService.isNewUser(cartBo.getUserId());
        if (isNewUser) {
            List<Integer> productIds = cartBo.getProductIdList();
            List<FirstSpecialProductBo> specialPrdIdList = firstSpecialProcessorDao.getGoodsFirstSpecialPrdId(productIds, cartBo.getDate()).into(FirstSpecialProductBo.class);
            if (specialPrdIdList != null && specialPrdIdList.size() > 0) {
                log.info("新用户触发首单特惠活动FirstSpecialProductBo:" + Util.toJson(specialPrdIdList));
                // 活动商品数量
                AtomicReference<Integer> goodsNum = new AtomicReference<>(0);
                // 选中的商品数量
                AtomicReference<Integer> checkedGoodsNum = new AtomicReference<>(0);
                cartBo.getCartGoodsList().stream().filter(goods->goods.getBuyStatus().equals(BaseConstant.YES)).forEach(cartGoods -> {
                    specialPrdIdList.forEach(firstSpecial -> {
                        if (firstSpecial.getPrdId().equals(cartGoods.getProductId())&&cartGoods.getActivity(ACTIVITY_TYPE_FIRST_SPECIAL)==null) {
                            //首单特惠价格小于商品原价
                            if (cartGoods.getProductRecord().getPrdPrice().compareTo(firstSpecial.getPrdPrice())>0){
                                log.info("首单特惠商品[goodsName:{} ]",cartGoods.getGoodsName());
                                CartActivityInfo firstActivityInfo = new CartActivityInfo();
                                firstActivityInfo.setActivityType(ACTIVITY_TYPE_FIRST_SPECIAL);
                                firstActivityInfo.setProductPrice(firstSpecial.getPrdPrice());
                                //活动规格id
                                firstActivityInfo.setActivityId(firstSpecial.getId());
                                firstActivityInfo.setLimitMaxNum(firstSpecial.getLimitAmount());
                                firstActivityInfo.setLimitNumberType(firstSpecial.getLimitFlag());
                                if (firstSpecial.getLimitAmount() > 0 && cartGoods.getCartNumber() > firstSpecial.getLimitAmount()) {
                                    //超出限购数量后，买家可继续添加购买该商品
                                    if (!firstSpecial.getLimitFlag().equals(BaseConstant.LIMIT_FLAG_CONTINUE)) {
                                        log.info("商品数量超过活动数量限制,带查看商品是否符合活动其他要求");
                                        goodsNum.updateAndGet(v -> v + 1);
                                        cartGoods.getCartActivityInfos().add(firstActivityInfo);
                                    }
                                }else {
                                    goodsNum.updateAndGet(v -> v + 1);
                                    cartGoods.getCartActivityInfos().add(firstActivityInfo);
                                }
                            }
                        }
                    });
                });
                // 全局限制
                Integer limitGoodsNum = firstSpecialConfigService.getFirstLimitGoods();
                log.info("首单特惠全局限制商品种类[limitGoodsNum:{}}]",limitGoodsNum);
                if (limitGoodsNum!=0&&goodsNum.get() > limitGoodsNum) {
                    log.info("选中商品过多,触发首单特惠商品数(种类)限制[goodsNum:{}},limitGoodsNum:{}}]",goodsNum,limitGoodsNum);
                    cartBo.getCartGoodsList().forEach(cartGoods -> {
                        CartActivityInfo firstActivityInfo = cartGoods.getActivity(ACTIVITY_TYPE_FIRST_SPECIAL);
                        if (firstActivityInfo!=null&&Objects.equals(cartGoods.getIsChecked(), CartConstant.CART_IS_CHECKED)) {
                            checkedGoodsNum.updateAndGet(v -> v + 1);
                            if (checkedGoodsNum.get() > limitGoodsNum) {
                                log.info("超过限制的商品首单特惠不生效,商品价格为原价[product:{},checkedGoodsNum{}]",cartGoods.getProductId(),checkedGoodsNum);
                                firstActivityInfo.setStatus(CartConstant.ACTIVITY_STATUS_INVALID);
                                cartGoods.getCartActivityInfos().remove(firstActivityInfo);
                            }
                        }
                    });
                }
                //商品不能超出限购数量
                cartBo.getCartGoodsList().forEach(cartGoods -> {
                    CartActivityInfo firstSpecial = cartGoods.getActivity(ACTIVITY_TYPE_FIRST_SPECIAL);
                    if (firstSpecial!=null){
                        if (firstSpecial.getLimitMaxNum()!=null&&firstSpecial.getLimitMaxNum() > 0 && cartGoods.getCartNumber() > firstSpecial.getLimitMaxNum()) {
                            //超出限购数量后，买家可继续添加购买该商品
                            if (firstSpecial.getLimitNumberType().equals(BaseConstant.LIMIT_FLAG_CONFINE)) {
                                //不可继续添加
                                log.info("购物车-首单特惠商品{}-活动数量限制{}-取消选中",cartGoods.getGoodsName(),firstSpecial.getLimitMaxNum());
                                cartService.switchCheckedProduct(cartBo.getUserId(),cartGoods.getCartId(),CartConstant.CART_NO_CHECKED);
                                cartGoods.setIsChecked(CartConstant.CART_NO_CHECKED);
                                cartGoods.setBuyStatus(BaseConstant.NO);
                                //提示前端
                                cartBo.setNoticeStatus(CartConstant.CART_NOTICE_STATUS_WARNINGS);
                                cartBo.setNotice("活动限购" + firstSpecial.getLimitMaxNum() + "个");
                            }
                        }
                        if (firstSpecial.getStatus().equals(CartConstant.ACTIVITY_STATUS_VALID)){
                            log.info("首单特惠商品价格修改[goodsName:{},prize{}]",cartGoods.getGoodsName(),cartGoods.getPrdPrice());
                            cartGoods.setPrdPrice(firstSpecial.getProductPrice());
                            cartGoods.setPriceActivityType(ACTIVITY_TYPE_FIRST_SPECIAL);
                            cartGoods.setActivityLimitMaxNum(firstSpecial.getLimitMaxNum());
                            cartGoods.setLimitActivityType(ACTIVITY_TYPE_FIRST_SPECIAL);
                            cartGoods.setActivityLimitType(firstSpecial.getLimitNumberType());
                            //价格确定 -之后的活动不修改价格
                            cartGoods.setPriceStatus(BaseConstant.YES);
                        }
                    }
                });

            }
        }
    }

    /**
     * @param productBo
     */
    public void doOrderOperation(OrderCartProductBo productBo) throws MpException {
        log.info("下单首单特惠--开始");
        boolean isNewUser = orderInfoService.isNewUser(productBo.getUserId());
        if (isNewUser) {
            List<Integer> productIds = productBo.getAll().stream().map(OrderCartProductBo.OrderCartProduct::getProductId).collect(Collectors.toList());
            log.info("debug,{}",productBo);
            List<FirstSpecialProductBo> specialPrdIdList = firstSpecialProcessorDao.getGoodsFirstSpecialPrdId(productIds, productBo.getDate()).into(FirstSpecialProductBo.class);
            if (specialPrdIdList != null && specialPrdIdList.size() > 0) {
                log.info("新用户触发首单特惠活动FirstSpecialProductBo:" + Util.toJson(specialPrdIdList));
                // 活动商品数量
                AtomicReference<Integer> goodsNum = new AtomicReference<>(0);
                // 选中的商品数量
                AtomicReference<Integer> checkedGoodsNum = new AtomicReference<>(0);
                productBo.getAll().forEach(product -> {
                    specialPrdIdList.forEach(firstSpecial -> {
                        if (firstSpecial.getPrdId().equals(product.getProductId()) && !product.getActivityInfo().containsKey(ACTIVITY_TYPE_FIRST_SPECIAL)) {
                            //首单特惠价格小于商品原价
                            if (product.getProductRecord().getPrdPrice().compareTo(firstSpecial.getPrdPrice())>0){
                                log.info("首单特惠商品[getPrdId:" + firstSpecial.getPrdId() + "]");
                                GoodsActivityInfo firstActivityInfo = new GoodsActivityInfo();
                                firstActivityInfo.setActivityType(ACTIVITY_TYPE_FIRST_SPECIAL);
                                firstActivityInfo.setFirstSpecialPrice(firstSpecial.getPrdPrice());
                                firstActivityInfo.setActivityId(firstSpecial.getFirstSpecialId());
                                firstActivityInfo.setFirstSpecialNumber(firstSpecial.getLimitAmount());
                                firstActivityInfo.setFirstSpecialNumberType(firstSpecial.getLimitFlag());
                                if (firstSpecial.getLimitAmount() > 0 && product.getGoodsNumber() > firstSpecial.getLimitAmount()) {
                                    //超出限购数量后，买家可继续添加购买该商品
                                    if (!firstSpecial.getLimitFlag().equals(BaseConstant.LIMIT_FLAG_CONTINUE)) {
                                        log.info("商品数量超过活动数量限制,带查看商品是否符合活动其他要求");
                                        goodsNum.updateAndGet(v -> v + 1);
                                        product.getActivityInfo().put(ACTIVITY_TYPE_FIRST_SPECIAL, firstActivityInfo);
                                    }
                                }else {
                                    goodsNum.updateAndGet(v -> v + 1);
                                    product.getActivityInfo().put(ACTIVITY_TYPE_FIRST_SPECIAL, firstActivityInfo);
                                }
                            }
                        }
                    });
                });
                // 全局限制
                Integer limitGoodsNum = firstSpecialConfigService.getFirstLimitGoods();
                log.info("首单特惠全局限制商品种类[limitGoodsNum:" + limitGoodsNum + "]");
                if (goodsNum.get() >= limitGoodsNum) {
                    log.info("选中商品过多,触发首单特惠商品数(种类)限制[goodsNum:" + goodsNum + "checkedGoodsNum:" + checkedGoodsNum + ",limitGoodsNum:" + limitGoodsNum + "]");
                    productBo.getAll().forEach(product -> {
                        GoodsActivityInfo actInfo = product.getActivity(ACTIVITY_TYPE_FIRST_SPECIAL);
                        if (actInfo != null && Objects.equals(actInfo.getStatus(), CartConstant.ACTIVITY_STATUS_VALID)) {
                            if (Objects.equals(product.getIsChecked(), CartConstant.CART_IS_CHECKED)) {
                                checkedGoodsNum.updateAndGet(v -> v + 1);
                                if (checkedGoodsNum.get() > limitGoodsNum) {
                                    log.info("超过限制的商品首单特惠不生效,商品价格为原价[" + "product:" + product.getProductId() + "]");
                                    actInfo.setStatus(CartConstant.ACTIVITY_STATUS_INVALID);
                                }
                            } else {
                                actInfo.setStatus(CartConstant.ACTIVITY_STATUS_INVALID);
                            }
                        }
                    });
                }
                //商品不能超出限购数量
                for (OrderCartProductBo.OrderCartProduct product : productBo.getProductList()) {
                    GoodsActivityInfo firstSpecial = product.getActivity(ACTIVITY_TYPE_FIRST_SPECIAL);
                    if (firstSpecial != null && firstSpecial.getStatus().equals(CartConstant.ACTIVITY_STATUS_VALID)) {
                        if (firstSpecial.getFirstSpecialNumber() > 0 && product.getGoodsNumber() > firstSpecial.getFirstSpecialNumber()) {
                            //超出限购数量后，买家可继续添加购买该商品
                            if (firstSpecial.getFirstSpecialNumberType().equals(BaseConstant.LIMIT_FLAG_CONFINE)) {
                                //不可继续添加
                                log.info("商品数量超过活动数量限制,提出警告[getCartNumber:" + product.getGoodsNumber() + ",getLimitAmount:" + firstSpecial.getFirstSpecialNumber() + "]");
                                firstSpecial.setStatus(CartConstant.ACTIVITY_STATUS_INVALID);
                                throw new MpException(JsonResultCode.FIRST_SPECIAL_NUMBER_LIMIT, null, firstSpecial.getFirstSpecialNumber().toString());
                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        doOrderOperation(param.createOrderCartProductBo());
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) {

    }
}

package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.market.fullcut.MrkingStrategyPageListQueryVo;
import com.meidianyi.shop.service.pojo.shop.market.fullcut.MrkingStrategyVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.FullReductionGoodsCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.FullReductionPromotion;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.fullreduce.OrderFullReduce;
import com.meidianyi.shop.service.shop.activity.dao.FullReductionProcessorDao;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.order.action.base.Calculate;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 满折满减
 * @author 李晓冰
 * @date 2019年10月30日
 */
@Service
@Slf4j
public class FullReductionProcessor implements Processor, ActivityGoodsListProcessor, CreateOrderProcessor, GoodsDetailProcessor, ActivityCartListStrategy {

    @Autowired
    FullReductionProcessorDao fullReductionProcessorDao;
    @Autowired
    UserCardService userCard;
    @Autowired
    Calculate calculate;
    @Autowired
    private CartService cartService;

    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_FULL_REDUCTION_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION;
    }

    /*****************商品列表处理*******************/
    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {
        List<GoodsListMpBo> availableCapsules = capsules.stream().filter(x -> !GoodsConstant.isGoodsTypeIn13510(x.getActivityType())).collect(Collectors.toList());

        Timestamp now = DateUtils.getLocalDateTime();
        availableCapsules.forEach(capsule->{
            boolean has = fullReductionProcessorDao.getIsFullReductionListInfo(capsule.getGoodsId(), capsule.getCatId(), capsule.getSortId(), capsule.getBrandId(), now);
            if (has) {
                GoodsActivityBaseMp activity = new GoodsActivityBaseMp();
                activity.setActivityType(BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION);
                capsule.getGoodsActivities().add(activity);
            }
        });
    }

    /*****************商品详情处理******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        List<FullReductionPromotion> promotions = fullReductionProcessorDao.getFullReductionInfoForDetail(capsule.getGoodsId(), capsule.getCatId(), capsule.getBrandId(), capsule.getSortId(), DateUtils.getLocalDateTime());
        if (promotions != null && promotions.size() > 0) {
            capsule.getPromotions().put(BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION,promotions);
        }
    }

    /**订单处理start**/
    /**
     * 不可共存活动
     */
    private static List<Byte> NOT_PERMIT_ACTIVITY;
    static {
        NOT_PERMIT_ACTIVITY = Arrays.asList(
            BaseConstant.ACTIVITY_TYPE_GROUP_BUY,
            BaseConstant.ACTIVITY_TYPE_BARGAIN,
            BaseConstant.ACTIVITY_TYPE_INTEGRAL,
            BaseConstant.ACTIVITY_TYPE_GROUP_DRAW,
            BaseConstant.ACTIVITY_TYPE_SEC_KILL,
            BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE,
            BaseConstant.ACTIVITY_TYPE_PRE_SALE,
            BaseConstant.ACTIVITY_TYPE_EXCHANG_ORDER,
            BaseConstant.ACTIVITY_TYPE_PROMOTE_ORDER,
            BaseConstant.ACTIVITY_TYPE_ASSESS_ORDER
        );
    }

    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        //满折满减校验（只有购物车计算此时会存在straid>0）,校验把不通过straId=0
        if(!OrderConstant.CART_Y.equals(param.getIsCart())){
            return;
        }
        List<Integer> straIds = param.getGoods().stream().filter(x -> (BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION.equals(x.getCartType()))).map(OrderBeforeParam.Goods::getCartExtendId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(straIds)) {
            return;
        }
        //可用会员卡
        List<ValidUserCardBean> validCardList = userCard.userCardDao.getValidCardList(param.getWxUserInfo().getUserId(), new Byte[]{CardConstant.MCARD_TP_NORMAL, CardConstant.MCARD_TP_GRADE}, UserCardDaoService.CARD_ONLINE);
        Set<Integer> cardIds = validCardList.stream().map(ValidUserCardBean::getCardId).collect(Collectors.toSet());
        //活动
        Map<Integer, MrkingStrategyPageListQueryVo> processingActivity = fullReductionProcessorDao.getProcessingActivity(param.getDate(), straIds.toArray(new Integer[0])).stream().collect(Collectors.toMap(MrkingStrategyPageListQueryVo::getId, Function.identity()));

        for (OrderBeforeParam.Goods goods : param.getGoods()) {
            if(goods.getCartType() != null && BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION.equals(goods.getCartType()) && goods.getCartExtendId() != null && goods.getCartExtendId() > 0){
                MrkingStrategyPageListQueryVo activity = processingActivity.get(goods.getCartExtendId());
                if(activity == null){
                    goods.setStraId(0);
                    continue;
                }
                //活动详情
                MrkingStrategyVo activityInfo = fullReductionProcessorDao.getMrkingStrategyById(activity.getId());
                //check
                if(fullReductionProcessorDao.checkMemberCard(activityInfo, cardIds) && fullReductionProcessorDao.checkGoods(activityInfo, goods.getGoodsInfo().into(OrderGoodsBo.class))) {
                    goods.setStraId(goods.getCartExtendId());
                }else {
                    goods.setStraId(0);
                }
            }
        }
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        //无
    }

    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        //无
    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) {

    }

    /**
     * 处理满折满减活动（金额计算）
     * @param param
     * @param bos
     * @throws MpException
     * @return 折扣详情
     */
    public List<OrderFullReduce> calculate(OrderBeforeParam param, List<OrderGoodsBo> bos) {
        List<OrderFullReduce> result = new ArrayList<>();
        if(!NOT_PERMIT_ACTIVITY.contains(param.getActivityType())){
            List<MrkingStrategyPageListQueryVo> processingActivity = fullReductionProcessorDao.getProcessingActivity(param.getDate());
            if (CollectionUtils.isEmpty(processingActivity)) {
                return result;
            }
            HashMap<MrkingStrategyVo, List<OrderGoodsBo>> joinActivity = new HashMap<>();
            HashSet<Integer> used = new HashSet<>();
            //可用会员卡
            List<ValidUserCardBean> validCardList = userCard.userCardDao.getValidCardList(param.getWxUserInfo().getUserId(), new Byte[]{CardConstant.MCARD_TP_NORMAL, CardConstant.MCARD_TP_GRADE}, UserCardDaoService.CARD_ONLINE);
            Set<Integer> cardIds = validCardList.stream().map(ValidUserCardBean::getCardId).collect(Collectors.toSet());
            for(MrkingStrategyPageListQueryVo activity : processingActivity) {
                MrkingStrategyVo activityInfo = fullReductionProcessorDao.getMrkingStrategyById(activity.getId());
                if(!fullReductionProcessorDao.checkMemberCard(activityInfo, cardIds)) {
                    continue;
                }
                for (OrderGoodsBo goods : bos) {
                    if(used.contains(goods.getProductId()) || !fullReductionProcessorDao.checkGoods(activityInfo, goods)) {
                        //参与满折满减、活动不包含该商品
                        continue;
                    }
                    if(!OrderConstant.CART_Y.equals(param.getIsCart()) || param.getStoreId() != null || (activity.getId().equals(goods.getStraId()))) {
                        //购物车可选是否参加活动需要特殊处理
                        joinActivity.computeIfAbsent(activityInfo, k -> new ArrayList<>());
                        goods.setStraId(activity.getId());
                        joinActivity.get(activityInfo).add(goods);
                        used.add(goods.getProductId());
                    }
                }
            }
            for(Map.Entry<MrkingStrategyVo, List<OrderGoodsBo>> entry : joinActivity.entrySet()) {
                BigDecimal[] tolalNumberAndPrice = calculate.getTolalNumberAndPriceByType(entry.getValue(), OrderConstant.D_T_FULL_REDUCE, null);
                BigDecimal discount = fullReductionProcessorDao.calculateFullReduce(entry.getKey(), entry.getValue(), tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_NUMBER].intValue(), tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE]);
                OrderFullReduce orderFullReduce = new OrderFullReduce();
                if(BigDecimalUtil.compareTo(discount, BigDecimal.ZERO) < 1) {
                    entry.getValue().forEach(x->x.setStraId(0));
                    continue;
                }
                orderFullReduce.setInfo(entry.getKey());
                orderFullReduce.setTotalDiscount(discount);
                orderFullReduce.setTotalPrice(tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE]);
                orderFullReduce.setTotalGoodsNumber(tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_NUMBER]);
                orderFullReduce.setIdentity(entry.getKey().getId().toString());
                orderFullReduce.setBos(entry.getValue());
                orderFullReduce.initRatio();
                result.add(orderFullReduce);
                //记录满折满减活动折扣到每件商品的价格
                orderFullReduce.getBos().forEach(
                    x-> x.setPerDiscount(BigDecimalUtil.multiplyOrDivide(
                            BigDecimalUtil.BigDecimalPlus.create(x.getDiscountedTotalPrice(), BigDecimalUtil.Operator.divide),
                            BigDecimalUtil.BigDecimalPlus.create(tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE], BigDecimalUtil.Operator.multiply),
                            BigDecimalUtil.BigDecimalPlus.create(discount)
                        )));
                log.info("满折满减计算成功，详情：{}", orderFullReduce);
            }
        }
        return result;
    }

    /**
     * 订单处理end
     **/


    /**
     * 购物车--满折满减
     * @param cartBo 业务数据类
     */
    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        log.info("购物车-满折满减");
        //可用的会员
        List<ValidUserCardBean> validCardList = userCard.userCardDao.getValidCardList(cartBo.getUserId(), new Byte[]{CardConstant.MCARD_TP_NORMAL, CardConstant.MCARD_TP_GRADE}, UserCardDaoService.CARD_ONLINE);
        List<Integer> cardIds = validCardList.stream().map(ValidUserCardBean::getCardId).collect(Collectors.toList());
        //活动和商品信息 k 活动id v商品信息
        Map<Integer, List<FullReductionGoodsCartBo>> ruleGoodsMap = new HashMap<>();
        //k 活动id v活动信息
        Map<Integer,CartActivityInfo> activityMap =new HashMap<>();
        //获取商品可用的活动
        for (WxAppCartGoods goods : cartBo.getCartGoodsList()) {
            Map<Integer, CartActivityInfo> cartActivityInfoMap = fullReductionProcessorDao.getGoodsFullReductionActivityList(goods.getGoodsId(),
                    goods.getGoodsRecord().getCatId(),
                    goods.getGoodsRecord().getBrandId(),
                    goods.getGoodsRecord().getSortId(),
                    cardIds,
                    cartBo.getDate());
            if (cartActivityInfoMap != null && cartActivityInfoMap.size() > 0) {
                goods.getCartActivityInfos().addAll(cartActivityInfoMap.values());
                activityMap.putAll(cartActivityInfoMap);
                //商品是否已经选择活动 且商品选中状态
                if (goods.getType().equals(BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION)) {
                    Optional<CartActivityInfo> any = cartActivityInfoMap.values().stream().filter(cartActivityInfo -> cartActivityInfo.getActivityId().equals(goods.getExtendId())).findAny();
                    if (any.isPresent()) {
                        //商品活动
                        goods.setActivityType(BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION);
                        goods.setActivityId(goods.getExtendId());
                        //活动记录
                        fullReductionProcessorDao.getFullReductionGoodsBo(ruleGoodsMap, goods,any.get());
                    }else {
                        log.info("购物车满折满减-没有找到合适活动,取消活动选中");
                        cartService.switchActivityGoods(cartBo.getUserId(),goods.getCartId(),0,(byte)0);
                        goods.setActivityType(null);
                        goods.setActivityId(null);
                    }
                }
            }
        }
        //当前活动选择规则
        fullReductionProcessorDao.fullReductionRuleOption(ruleGoodsMap, activityMap);
        //折扣总金额
        BigDecimal totalReductionMoney = fullReductionProcessorDao.getFullReductionMoney(ruleGoodsMap,activityMap);
        cartBo.setFullReductionPrice(totalReductionMoney);
        //国际化
        fullReductionProcessorDao.internationalMessage(cartBo,activityMap);
        //给商品分配规则
        for (WxAppCartGoods goods : cartBo.getCartGoodsList()) {
            if (goods.getActivityType()!=null&&goods.getActivityType().equals(BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION)){
                CartActivityInfo fullActivityInfo = activityMap.get(goods.getActivityId());
                List<CartActivityInfo> cartActivityInfos = goods.getCartActivityInfos();
                Iterator<CartActivityInfo> iterator = cartActivityInfos.iterator();
                while (iterator.hasNext()) {
                    CartActivityInfo cartActivityInfo = iterator.next();
                    if (cartActivityInfo.getActivityType()!=null&&cartActivityInfo.getActivityType().equals(BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION)
                            && goods.getActivityId().equals(cartActivityInfo.getActivityId())){
                        if (cartActivityInfo.getActivityId().equals(goods.getExtendId())) {
                            iterator.remove();
                            continue;
                        }
                    }
                }
                cartActivityInfos.add(0,fullActivityInfo);
            }
        }
    }


}

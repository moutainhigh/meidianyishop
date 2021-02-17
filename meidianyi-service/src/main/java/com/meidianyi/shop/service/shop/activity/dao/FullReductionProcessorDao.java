package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.tables.MrkingStrategy;
import com.meidianyi.shop.service.pojo.shop.market.fullcut.MrkingStrategyCondition;
import com.meidianyi.shop.service.pojo.shop.market.fullcut.MrkingStrategyPageListQueryVo;
import com.meidianyi.shop.service.pojo.shop.market.fullcut.MrkingStrategyVo;
import com.meidianyi.shop.service.pojo.shop.member.card.SimpleMemberCardVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.FullReductionGoodsCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.FullReductionPromotion;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.market.fullcut.MrkingStrategyService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record7;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.MRKING_STRATEGY;
import static com.meidianyi.shop.db.shop.Tables.MRKING_STRATEGY_CONDITION;

/**
 * @author 李晓冰
 * @date 2019年10月30日
 */
@Service
public class FullReductionProcessorDao extends MrkingStrategyService {

    /**
     * 全部商品参与活动
     */
    private static final byte ACT_TYPE_ALL_GOODS = 0;
    /**
     * 指定条件参与活动
     */
    private static final byte ACT_TYPE_POINT_CONDITION = 1;
    @Autowired
    private UserCardService userCard;

    /**
     * 判断根据传入的条件是否存在对应的满折满减活动
     *
     * @param goodsId 商品id
     * @param catId   平台分类id
     * @param sortId  商家分类id
     * @param brandId 品牌分类id
     * @param date    时间
     * @return true 存在对应的活动，false 不存在对应的活动
     */
    public boolean getIsFullReductionListInfo(Integer goodsId, Integer catId, Integer sortId, Integer brandId, Timestamp date) {
        Condition condition = MRKING_STRATEGY.ACT_TYPE.eq(ACT_TYPE_ALL_GOODS)
            .or(MRKING_STRATEGY.ACT_TYPE.eq(ACT_TYPE_POINT_CONDITION).and(
                DslPlus.findInSet(goodsId, MRKING_STRATEGY.RECOMMEND_GOODS_ID)
                    .or(DslPlus.findInSet(catId, MRKING_STRATEGY.RECOMMEND_CAT_ID))
                    .or(DslPlus.findInSet(sortId, MRKING_STRATEGY.RECOMMEND_SORT_ID))
                    .or(DslPlus.findInSet(brandId, MRKING_STRATEGY.RECOMMEND_BRAND_ID))
            ));

        condition = MRKING_STRATEGY.DEL_FLAG.eq(DelFlag.NORMAL.getCode())
            .and(MRKING_STRATEGY.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(MRKING_STRATEGY.START_TIME.lt(date))
            .and(MRKING_STRATEGY.END_TIME.gt(date)).and(condition);

        int count = db().fetchCount(MRKING_STRATEGY, condition);
        return count > 0;
    }

    /**
     * 获取满折满减促销信息
     * @param goodsId 商品ID
     * @param catId 平台分类ID
     * @param brandId 品牌ID
     * @param sortId  商家分类ID
     * @param now 限制时间
     * @return 满折促销信息
     */
    public List<FullReductionPromotion> getFullReductionInfoForDetail(Integer goodsId, Integer catId, Integer brandId, Integer sortId, Timestamp now) {
        Condition condition = MRKING_STRATEGY.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(MRKING_STRATEGY.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(MRKING_STRATEGY.START_TIME.le(now)).and(MRKING_STRATEGY.END_TIME.ge(now));

        Condition idCondition = MRKING_STRATEGY.ACT_TYPE.eq((byte) 0).or(DslPlus.findInSet(goodsId, MRKING_STRATEGY.RECOMMEND_GOODS_ID)
            .or(DslPlus.findInSet(catId, MRKING_STRATEGY.RECOMMEND_CAT_ID)).or(DslPlus.findInSet(brandId, MRKING_STRATEGY.RECOMMEND_BRAND_ID))
            .or(DslPlus.findInSet(sortId, MRKING_STRATEGY.RECOMMEND_SORT_ID)));
        logger().debug("小程序-商品详情-获取满折满减促销");
        Map<Integer, List<Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal>>> groups =
            db().select(MRKING_STRATEGY.ID, MRKING_STRATEGY.CARD_ID, MRKING_STRATEGY.TYPE, MRKING_STRATEGY_CONDITION.FULL_MONEY, MRKING_STRATEGY_CONDITION.REDUCE_MONEY,
            MRKING_STRATEGY_CONDITION.AMOUNT, MRKING_STRATEGY_CONDITION.DISCOUNT)
            .from(MRKING_STRATEGY).innerJoin(MRKING_STRATEGY_CONDITION).on(MRKING_STRATEGY.ID.eq(MRKING_STRATEGY_CONDITION.STRATEGY_ID))
            .where(condition.and(idCondition)).orderBy(MRKING_STRATEGY.STRATEGY_PRIORITY.desc(), MRKING_STRATEGY.START_TIME.desc())
            .stream().collect(Collectors.groupingBy(x -> x.get(MRKING_STRATEGY.ID)));

        List<FullReductionPromotion> returnList = new ArrayList<>(groups.size());

        for (Map.Entry<Integer, List<Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal>>> entry : groups.entrySet()) {
            Integer key = entry.getKey();
            List<Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal>> values = entry.getValue();

            FullReductionPromotion promotion = new FullReductionPromotion();
            promotion.setPromotionId(key);
            promotion.setRules(new ArrayList<>(values.size()));

            Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal> record = values.get(0);
            promotion.setType(record.get(MRKING_STRATEGY.TYPE));
            if (StringUtils.isNotBlank(record.get(MRKING_STRATEGY.CARD_ID))) {
                promotion.setIsExclusive(true);
            } else {
                promotion.setIsExclusive(false);
            }
            for (Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal> value : values) {
                FullReductionPromotion.FullReductionRule rule =new FullReductionPromotion.FullReductionRule();
                rule.setAmount(value.get(MRKING_STRATEGY_CONDITION.AMOUNT));
                rule.setDiscount(value.get(MRKING_STRATEGY_CONDITION.DISCOUNT));
                rule.setFullMoney(value.get(MRKING_STRATEGY_CONDITION.FULL_MONEY));
                rule.setReduceMoney(value.get(MRKING_STRATEGY_CONDITION.REDUCE_MONEY));
                promotion.getRules().add(rule);
            }
            returnList.add(promotion);
        }
        return returnList;
    }

    /**
     * 获取当前进行中的活动
     *
     * @param date   下单时间
     * @param straId id
     */
    public List<MrkingStrategyPageListQueryVo> getProcessingActivity(Timestamp date, Integer... straId) {
        Condition condition = MRKING_STRATEGY.DEL_FLAG.eq(DelFlag.NORMAL.getCode())
            .and(MRKING_STRATEGY.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(MRKING_STRATEGY.START_TIME.lt(date))
            .and(MRKING_STRATEGY.END_TIME.gt(date));
        if (straId != null && straId.length > 0) {
            condition.and(MRKING_STRATEGY.ID.in(straId));
        }
        List<MrkingStrategyPageListQueryVo> result = db().select(MrkingStrategy.MRKING_STRATEGY.ID, MrkingStrategy.MRKING_STRATEGY.ACT_NAME, MrkingStrategy.MRKING_STRATEGY.TYPE, MrkingStrategy.MRKING_STRATEGY.START_TIME, MrkingStrategy.MRKING_STRATEGY.END_TIME, MrkingStrategy.MRKING_STRATEGY.STATUS)
            .from(MrkingStrategy.MRKING_STRATEGY)
            .where(condition)
            .orderBy(MRKING_STRATEGY.STRATEGY_PRIORITY.desc(), MRKING_STRATEGY.CREATE_TIME.desc())
            .fetchInto(MrkingStrategyPageListQueryVo.class);
        return result;
    }

    /**
     * 校验
     *
     * @param activityInfo 活动详情
     * @param cardIds      用户有效会员卡
     */
    public boolean checkMemberCard(MrkingStrategyVo activityInfo, Set<Integer> cardIds) {
        if (CollectionUtils.isEmpty(activityInfo.getMemberCards())) {
            return true;
        }
        Set<Integer> ids = activityInfo.getMemberCards().stream().map(SimpleMemberCardVo::getId).collect(Collectors.toSet());
        ids.retainAll(cardIds);
        if (CollectionUtils.isNotEmpty(ids)) {
            return true;
        }
        return false;
    }

    /**
     * 校验
     *
     * @param activityInfo 活动详情
     * @param goods        商品
     */
    public boolean checkGoods(MrkingStrategyVo activityInfo, OrderGoodsBo goods) {
        if (ACT_TYPE_ALL_GOODS == activityInfo.getActType()) {
            logger().info("满折满减：支持全部商品,活动id:{}", activityInfo.getId());
            return true;
        } else {
            if (CollectionUtils.isNotEmpty(activityInfo.getRecommendGoodsIds())) {
                if (activityInfo.getRecommendGoodsIds().contains(goods.getGoodsId())) {
                    logger().info("满折满减：指定商品满足,活动id:{}", activityInfo.getId());
                    return true;
                }
            }
            if (CollectionUtils.isNotEmpty(activityInfo.getRecommendCatIds())) {
                if (activityInfo.getRecommendCatIds().contains(goods.getCatId())) {
                    logger().info("满折满减：指定平台分类满足,活动id:{}", activityInfo.getId());
                    return true;
                }
            }
            if (CollectionUtils.isNotEmpty(activityInfo.getRecommendSortIds())) {
                if (activityInfo.getRecommendSortIds().contains(goods.getSortId())) {
                    logger().info("满折满减：指定商家分类满足,活动id:{}", activityInfo.getId());
                    return true;
                }
            }
            if (CollectionUtils.isNotEmpty(activityInfo.getRecommendBrandIds())) {
                if (activityInfo.getRecommendBrandIds().contains(goods.getBrandId())) {
                    logger().info("满折满减：指定品牌满足,活动id:{}", activityInfo.getId());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 得到当满折满减优惠金额，如果为0，则不符合条件
     *
     * @param activityInfo 活动信息
     * @param bos          商品
     * @param num          商品总数量
     * @param price        商品总价格
     */
    public BigDecimal calculateFullReduce(MrkingStrategyVo activityInfo, List<OrderGoodsBo> bos, int num, BigDecimal price) {
        // 倒序排列，阶梯满减满折，从最大值金额开始判断
        Collections.sort(activityInfo.getCondition());
        //1每满减 2满减 3满折 4仅第X件打折
        for (MrkingStrategyCondition condition : activityInfo.getCondition()) {
            BigDecimal result = null;
            switch (activityInfo.getType()) {
                case 1:
                    //每满减
                    if (BigDecimalUtil.compareTo(condition.getFullMoney(), null) > 0 && condition.getFullMoney().compareTo(price) < 1) {
                        result = BigDecimalUtil.multiply(
                            condition.getReduceMoney(),
                            new BigDecimal(BigDecimalUtil.divide(price, condition.getFullMoney(), RoundingMode.FLOOR).intValue()));
                    } else if (condition.getAmount() != null && condition.getAmount() > 0 && condition.getAmount() <= num) {
                        result = BigDecimalUtil.multiply(
                            condition.getReduceMoney(),
                            new BigDecimal(num / condition.getAmount()));
                    }
                    break;
                case 2:
                    //满减
                    if (BigDecimalUtil.compareTo(condition.getFullMoney(), null) > 0 && condition.getFullMoney().compareTo(price) < 1) {
                        result = condition.getReduceMoney();
                    } else if (condition.getAmount() != null && condition.getAmount() > 0 && condition.getAmount() <= num) {
                        result = condition.getReduceMoney();
                    }
                    break;
                case 3:
                    //满折
                    boolean isDiscountByFullMoney = (BigDecimalUtil.compareTo(condition.getFullMoney(), null) > 0 && condition.getFullMoney().compareTo(price) < 1) ||
                        (condition.getAmount() != null && condition.getAmount() > 0 && condition.getAmount() <= num);
                    if (isDiscountByFullMoney) {
                        result =
                            price.subtract(
                                BigDecimalUtil.multiplyOrDivide(
                                    BigDecimalUtil.BigDecimalPlus.create(price, BigDecimalUtil.Operator.multiply),
                                    BigDecimalUtil.BigDecimalPlus.create(condition.getDiscount(), BigDecimalUtil.Operator.divide),
                                    BigDecimalUtil.BigDecimalPlus.create(BigDecimal.TEN, null)
                                )
                            );
                    }
                    break;
                case 4:
                    BigDecimal temp = null;
                    for (OrderGoodsBo bo : bos) {
                        if (bo.getGoodsNumber() >= condition.getAmount()) {
                            temp = temp == null ? BigDecimal.ZERO : temp;
                            temp = temp.add(
                                bo.getDiscountedGoodsPrice().subtract(
                                    BigDecimalUtil.multiplyOrDivide(
                                        BigDecimalUtil.BigDecimalPlus.create(bo.getDiscountedGoodsPrice(), BigDecimalUtil.Operator.multiply),
                                        BigDecimalUtil.BigDecimalPlus.create(condition.getDiscount(), BigDecimalUtil.Operator.divide),
                                        BigDecimalUtil.BigDecimalPlus.create(BigDecimal.TEN, null)
                                    )
                                )
                            );
                        }
                    }
                    result = temp;
                    break;
                default:
            }
            if (result != null) {
                return result;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取商品的满折满减活动--购物车
     *  @param goodsId 商品id
     * @param catId 平台分类ID
     * @param brandId 品牌ID
     * @param sortId  商家分类ID
     * @param date 限制时间
     * @return k活动id v活动信息
     */
    public Map<Integer, CartActivityInfo> getGoodsFullReductionActivityList(Integer goodsId, Integer catId, Integer brandId, Integer sortId, List<Integer> cardIds, Timestamp date) {
        Condition condition = MRKING_STRATEGY.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(MRKING_STRATEGY.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
                .and(MRKING_STRATEGY.START_TIME.le(date)).and(MRKING_STRATEGY.END_TIME.ge(date));

        Condition idCondition = MRKING_STRATEGY.ACT_TYPE.eq((byte) 0).or(DslPlus.findInSet(goodsId, MRKING_STRATEGY.RECOMMEND_GOODS_ID)
                .or(DslPlus.findInSet(catId, MRKING_STRATEGY.RECOMMEND_CAT_ID)).or(DslPlus.findInSet(brandId, MRKING_STRATEGY.RECOMMEND_BRAND_ID))
                .or(DslPlus.findInSet(sortId, MRKING_STRATEGY.RECOMMEND_SORT_ID)));
        Map<Integer, List<Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal>>> fullReductionMap =
                db().select(MRKING_STRATEGY.ID, MRKING_STRATEGY.CARD_ID, MRKING_STRATEGY.TYPE,
                        MRKING_STRATEGY_CONDITION.FULL_MONEY, MRKING_STRATEGY_CONDITION.REDUCE_MONEY,
                        MRKING_STRATEGY_CONDITION.AMOUNT, MRKING_STRATEGY_CONDITION.DISCOUNT)
                        .from(MRKING_STRATEGY)
                        .innerJoin(MRKING_STRATEGY_CONDITION).on(MRKING_STRATEGY.ID.eq(MRKING_STRATEGY_CONDITION.STRATEGY_ID))
                        .where(condition.and(idCondition))
                        .orderBy(MRKING_STRATEGY.STRATEGY_PRIORITY.desc(), MRKING_STRATEGY.START_TIME.desc())
                        .stream().collect(Collectors.groupingBy(x -> x.get(MRKING_STRATEGY.ID)));

        List<CartActivityInfo> cartActivityInfos =new ArrayList<>(fullReductionMap.keySet().size());
        AA:
        for (Map.Entry<Integer, List<Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal>>> entry : fullReductionMap.entrySet()) {
            Integer key = entry.getKey();
            List<Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal>> values = entry.getValue();

            CartActivityInfo activityInfo = new CartActivityInfo();
            CartActivityInfo.FullReduction fullReduction =new CartActivityInfo.FullReduction();

            activityInfo.setActivityId(key);
            activityInfo.setActivityType(BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION);
            activityInfo.setFullReduction(fullReduction);

            Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal> record = values.get(0);
            fullReduction.setFullReductionType(record.get(MRKING_STRATEGY.TYPE));
            //会员专享
            if (StringUtils.isNotBlank(record.get(MRKING_STRATEGY.CARD_ID))) {
                fullReduction.setIsExclusive(true);
                for (Integer id : cardIds) {
                    if (!Util.splitValueToList(record.get(MRKING_STRATEGY.CARD_ID)).contains(id)) {
                        break AA;
                    }
                }
            }
            fullReduction.setRules(new ArrayList<>(values.size()));
            for (Record7<Integer, String, Byte, BigDecimal, BigDecimal, Integer, BigDecimal> value : values) {
                CartActivityInfo.FullReductionRule rule = new CartActivityInfo.FullReductionRule();
                rule.setAmount(value.get(MRKING_STRATEGY_CONDITION.AMOUNT));
                rule.setDiscount(value.get(MRKING_STRATEGY_CONDITION.DISCOUNT).setScale(2,RoundingMode.HALF_UP));
                rule.setFullMoney(value.get(MRKING_STRATEGY_CONDITION.FULL_MONEY).setScale(2,RoundingMode.HALF_UP));
                rule.setReduceMoney(value.get(MRKING_STRATEGY_CONDITION.REDUCE_MONEY).setScale(2,RoundingMode.HALF_UP));
                if (rule.getAmount()>0){
                    fullReduction.setRulesType((byte) 2);
                }else{
                    fullReduction.setRulesType((byte) 1);
                }
                fullReduction.getRules().add(rule);
            }
            cartActivityInfos.add(activityInfo);
        }
        Map<Integer, CartActivityInfo> activityInfoMap = cartActivityInfos.stream().collect(Collectors.toMap(CartActivityInfo::getActivityId, Function.identity()));
        return activityInfoMap;
    }

    /**
     * 国际化
     *
     * @param fullReduction 满折满减
     * @param rule 满折满减规则
     * @param reduceMoney 已折扣
     * @return 国际化
     */
    public String fullReductionRuleToString(CartActivityInfo.FullReduction fullReduction, CartActivityInfo.FullReductionRule rule, BigDecimal reduceMoney) {
        if (reduceMoney.compareTo(BigDecimal.ZERO)>0){
            /**活动类型 1每满减 2满减 3满折 4仅第X件打折*/
            byte typeDiscountNth = (byte) 4;
            if (!fullReduction.getFullReductionType().equals(typeDiscountNth)){
                if (fullReduction.getRulesType().equals((byte)1)){
                    logger().info("已满{}元,减{}元",rule.getFullMoney(),reduceMoney);
                    return "已购满"+rule.getFullMoney()+"元,下单立减"+reduceMoney+"元";
                }else {
                    logger().info("已满{}件,减{}元",rule.getAmount(),reduceMoney);
                    return "已购满"+rule.getAmount()+"件,下单立减"+reduceMoney+"元";
                }
            }else {
                logger().info("减{}元",reduceMoney);
                return "下单立减"+reduceMoney+"元";
            }
        }
        //满减
        switch (fullReduction.getFullReductionType()){
            case 1:
                //每满减
                if (rule.getFullMoney().compareTo(BigDecimal.ZERO)>0&&rule.getReduceMoney()!=null){
                    return "每满"+rule.getFullMoney()+"元,减"+rule.getReduceMoney()+"元";
                }
                if (rule.getAmount()>0&&rule.getReduceMoney()!=null){
                    return "每满"+rule.getAmount()+"件,减"+rule.getReduceMoney()+"元";
                }
                break;
            case 2:
                //满减
                if (rule.getFullMoney().compareTo(BigDecimal.ZERO)>0&&rule.getReduceMoney()!=null){
                    return "满"+rule.getFullMoney()+"元,减"+rule.getReduceMoney()+"元";
                }
                if (rule.getAmount()>0&&rule.getReduceMoney()!=null){
                    return "满"+rule.getAmount()+"件,减"+rule.getReduceMoney()+"元";
                }
                break;
            case 3:
                //3满折
                if (rule.getFullMoney().compareTo(BigDecimal.ZERO)>0&&rule.getReduceMoney()!=null){
                    return "满"+rule.getFullMoney()+"元,打"+rule.getDiscount()+"折";
                }
                if (rule.getAmount()>0&&rule.getReduceMoney()!=null){
                    return "满"+rule.getAmount()+"件,打"+rule.getDiscount()+"折";
                }
                break;
            case 4:
                //第几件=
                if (rule.getAmount()>0&&rule.getReduceMoney()!=null){
                    return "购买同一件商品第"+rule.getAmount()+"件,打"+rule.getDiscount()+"折";
                }
                break;
            default:
        }
        return "";
    }

    /**
     * 活动选择
     * @param ruleCartIdMap k 活动id  v 活动生效信息
     * @param cartActivityMap k 活动id v 活动信息
     */
    public void fullReductionRuleOption(Map<Integer, List<FullReductionGoodsCartBo>> ruleCartIdMap, Map<Integer, CartActivityInfo> cartActivityMap) {
        //判断商品参与活动信息
        for (Map.Entry<Integer, List<FullReductionGoodsCartBo>> entry : ruleCartIdMap.entrySet()) {
            Integer ruleId = entry.getKey();
            List<FullReductionGoodsCartBo> fullGoodsList = entry.getValue();
            CartActivityInfo cartActivityInfo = cartActivityMap.get(ruleId);
            //stream().filter(cartActivityInfo -> cartActivityInfo.getActivityId().equals(ruleId)).findFirst();
            if (cartActivityInfo!=null) {
                CartActivityInfo.FullReduction fullReduction = cartActivityInfo.getFullReduction();
                CartActivityInfo.FullReductionRule fullReductionRule = fullReduction.getRules().get(0);
                /**活动类型 1每满减 2满减 3满折 4仅第X件打折*/
                switch (fullReduction.getFullReductionType()) {
                    case 1:
                        break;
                    case 2:
                    case 3:
                        List<CartActivityInfo.FullReductionRule> ruleList = fullReduction.getRules();
                        Optional<CartActivityInfo.FullReductionRule> firstRule = ruleList.stream().findFirst();
                        if (fullReduction.getRulesType().equals((byte)2)) {
                            //满件数
                            int sum = fullGoodsList.stream().filter(fullGoods->fullGoods.getIsChecked().equals(CartConstant.CART_IS_CHECKED)).mapToInt(FullReductionGoodsCartBo::getNum).sum();
                            for (CartActivityInfo.FullReductionRule fullRule : ruleList) {
                                if (fullRule.getAmount() <= sum) {
                                    // fullRule 可用规则
                                    if (fullReductionRule.getAmount() < sum) {
                                        if (fullRule.getAmount() > fullReductionRule.getAmount()) {
                                            //最好规则
                                            fullReductionRule = fullRule;
                                        }
                                    } else {
                                        fullReductionRule = fullRule;
                                    }
                                }
                            }
                        } else {
                            //满金额
                            BigDecimal moneySums = fullGoodsList.stream().filter((fullGoods->fullGoods.getIsChecked().equals(CartConstant.CART_IS_CHECKED))).map(FullReductionGoodsCartBo::getMoney)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            for (CartActivityInfo.FullReductionRule fullRule : ruleList) {
                                if (moneySums.compareTo(fullRule.getFullMoney()) >= 0) {
                                    if (moneySums.compareTo(fullReductionRule.getFullMoney()) >= 0) {
                                        if (fullRule.getFullMoney().compareTo(fullReductionRule.getFullMoney()) > 0) {
                                            fullReductionRule = fullRule;
                                        }
                                    } else {
                                        fullReductionRule = fullRule;
                                    }
                                }
                            }
                        }
                        break;
                    case 4:
                        break;
                    default:
                }
                for (FullReductionGoodsCartBo goods : fullGoodsList) {
                    goods.setFullReductionRule(fullReductionRule);
                    cartActivityInfo.getFullReduction().setRule(fullReductionRule);
                }
            }
        }

    }

    public void getFullReductionGoodsBo(Map<Integer, List<FullReductionGoodsCartBo>> ruleGoodsMap, WxAppCartGoods goods,CartActivityInfo activityInfo) {
        List<FullReductionGoodsCartBo> fullGoods = ruleGoodsMap.get(goods.getExtendId()) != null ? ruleGoodsMap.get(goods.getExtendId()) : new ArrayList<>();
        FullReductionGoodsCartBo fullGoodsBo =new FullReductionGoodsCartBo();
        fullGoodsBo.setCartId(goods.getCartId());
        fullGoodsBo.setProductId(goods.getProductId());
        fullGoodsBo.setIsChecked(goods.getIsChecked());
        fullGoodsBo.setNum(goods.getCartNumber());
        fullGoodsBo.setMoney(goods.getGoodsPrice().multiply(BigDecimal.valueOf(goods.getCartNumber())));
        fullGoodsBo.setFullReduction(activityInfo);
        fullGoods.add(fullGoodsBo);
        ruleGoodsMap.put(goods.getExtendId(), fullGoods);
    }

    /**
     * 或计划
     * @param cartBo
     * @param activityMap 活动
     */
    public void internationalMessage(WxAppCartBo cartBo, Map<Integer, CartActivityInfo> activityMap) {
        for (WxAppCartGoods goods : cartBo.getCartGoodsList()) {
            goods.getCartActivityInfos().forEach(cartActivityInfo -> {
                if (cartActivityInfo.getActivityType().equals(BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION)) {
                    CartActivityInfo.FullReduction fullReduction = cartActivityInfo.getFullReduction();
                    if (fullReduction != null) {
                        CartActivityInfo.FullReductionRule fullReductionRule = fullReduction.getRule();
                        //当前选中的国际化
                        if (fullReductionRule != null) {
                            String message = fullReductionRuleToString(fullReduction, fullReductionRule,BigDecimal.ZERO);
                            fullReduction.setCondition(message);
                        }
                        fullReduction.getRules().forEach(rule -> {
                            rule.setName(fullReductionRuleToString(fullReduction, rule,BigDecimal.ZERO));
                        });
                    }
                }
            });
        }
        for (CartActivityInfo cartActivityInfo : activityMap.values()) {
            CartActivityInfo.FullReduction fullReduction = cartActivityInfo.getFullReduction();
            if (fullReduction!=null){
                CartActivityInfo.FullReductionRule rule = fullReduction.getRule();
                //当前选中的国际化
                if (rule!=null){
                    String message = fullReductionRuleToString(fullReduction,rule,rule.getReduceTotalMoney());
                    fullReduction.setCondition(message);
                }
                fullReduction.getRules().forEach(rule1->{
                    rule1.setName(fullReductionRuleToString(fullReduction,rule1,BigDecimal.ZERO));
                });
            }
        }
    }

    /**
     * 获取活动折扣总金额
     * @param ruleGoodsMap
     * @param activityMap
     * @return
     */
    public BigDecimal getFullReductionMoney(Map<Integer, List<FullReductionGoodsCartBo>> ruleGoodsMap, Map<Integer, CartActivityInfo> activityMap) {
        BigDecimal totalReductionMoney=BigDecimal.ZERO;
        //满减金额计算
        for (Map.Entry<Integer, List<FullReductionGoodsCartBo>> entry : ruleGoodsMap.entrySet()) {
            List<FullReductionGoodsCartBo> goodsList = entry.getValue();
            CartActivityInfo fullReduction = goodsList.get(0).getFullReduction();
            CartActivityInfo.FullReductionRule fullReductionRule = goodsList.get(0).getFullReductionRule();
            CartActivityInfo cartActivityInfo = activityMap.get(entry.getKey());
            //stream().filter(cartActivityInfo -> cartActivityInfo.getActivityId().equals(ruleId)).findFirst();
            //总数量
            int totalNum = goodsList.stream().filter(fullGoods->fullGoods.getIsChecked().equals(CartConstant.CART_IS_CHECKED)).mapToInt(FullReductionGoodsCartBo::getNum).sum();
            //总金额
            BigDecimal totalMoney = goodsList.stream().filter(fullGoods->fullGoods.getIsChecked().equals(CartConstant.CART_IS_CHECKED)).map(FullReductionGoodsCartBo::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reduceMoney=BigDecimal.ZERO;
            /**活动类型 1每满减 2满减 3满折 4仅第X件打折*/
            switch (fullReduction.getFullReduction().getFullReductionType()) {
                case 1:
                    if (fullReduction.getFullReduction().getRulesType().equals((byte)2)){
                        if (fullReductionRule.getAmount()<=totalNum){
                            int time = totalNum / fullReductionRule.getAmount();
                            reduceMoney = fullReductionRule.getReduceMoney().multiply(BigDecimal.valueOf(time));
                            logger().info("符合每满{}件,减{}元,{}件,减{}元",fullReductionRule.getAmount(),fullReductionRule.getReduceMoney(),totalNum,reduceMoney);
                        }
                    }else {
                        if (fullReductionRule.getFullMoney().compareTo(totalMoney)<=0){
                            BigDecimal time = totalMoney.divide(fullReductionRule.getFullMoney(), 0, BigDecimal.ROUND_DOWN);
                            reduceMoney = time.multiply(fullReductionRule.getReduceMoney());
                            logger().info("符合每满{}元,减{}元,共{}元,减{}元",fullReductionRule.getFullMoney(),fullReductionRule.getReduceMoney(),totalMoney,reduceMoney);
                        }
                    }
                    break;
                case 2:
                    //满减
                    if (fullReduction.getFullReduction().getRulesType().equals((byte)2)){
                        if (fullReductionRule.getAmount()<=totalNum){
                            reduceMoney =fullReductionRule.getReduceMoney();
                            logger().info("符合满{}件减{}元,件数{},减{}元",fullReductionRule.getAmount(),fullReductionRule.getReduceMoney(),totalNum,reduceMoney);

                        }
                    }else {
                        if (fullReductionRule.getFullMoney().compareTo(totalMoney)<=0){
                            reduceMoney =fullReductionRule.getReduceMoney();
                            logger().info("符合满{}元减{}元,共{}元,减{}",fullReductionRule.getFullMoney(),fullReductionRule.getReduceMoney(),totalMoney,reduceMoney);

                        }
                    }
                    break;
                case 3:
                    //3满折
                    if (fullReduction.getFullReduction().getRulesType().equals((byte)2)){
                        if (fullReductionRule.getAmount()<=totalNum){
                            reduceMoney = BigDecimal.ONE.subtract(fullReductionRule.getDiscount().multiply(new BigDecimal("0.1"))).multiply(totalMoney);
                            logger().info("符合满{}件打{}折,件数{},减{}元",fullReductionRule.getAmount(),fullReductionRule.getDiscount(),totalNum,reduceMoney);

                        }
                    }else {
                        if (fullReductionRule.getFullMoney().compareTo(totalMoney)<=0){
                            reduceMoney =BigDecimal.ONE.subtract(fullReductionRule.getDiscount().multiply(new BigDecimal("0.1"))).multiply(totalMoney);
                            logger().info("符合满{}元打{}折,共{}元,减{}",fullReductionRule.getFullMoney(),fullReductionRule.getReduceMoney(),totalMoney,reduceMoney);

                        }
                    }
                    break;
                case 4:
                    if (fullReductionRule.getAmount()<=totalNum){
                        List<FullReductionGoodsCartBo> goodsCartBoList = goodsList.stream()
                                .filter(fullGoods -> fullGoods.getIsChecked().equals(CartConstant.CART_IS_CHECKED) && fullGoods.getNum() >= fullReductionRule.getAmount())
                                .collect(Collectors.toList());
                        //符合的商品计算减价金额
                        for (FullReductionGoodsCartBo goods : goodsCartBoList) {
                            BigDecimal multiply = BigDecimal.ONE.subtract(fullReductionRule.getDiscount().multiply(new BigDecimal("0.1"))).multiply(goods.getMoney());
                            logger().info("符合第{}件打{}折,件数{},减{}元",fullReductionRule.getAmount(),fullReductionRule.getDiscount(),goods.getNum(),multiply);
                            reduceMoney = reduceMoney.add(multiply);
                        }
                    }
                    break;
                default:
                    break;
            }
            fullReductionRule.setReduceTotalMoney(reduceMoney.setScale(2,RoundingMode.HALF_UP));
            cartActivityInfo.getFullReduction().setRule(fullReductionRule);
            totalReductionMoney=totalReductionMoney.add(reduceMoney);
        }
        return totalReductionMoney;
    }

}

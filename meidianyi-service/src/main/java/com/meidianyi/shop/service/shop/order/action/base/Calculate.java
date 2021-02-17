package com.meidianyi.shop.service.shop.order.action.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRebateRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.UserAddressRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRebatePriceRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.config.trade.GoodsPackageParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionStrategyParam;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateRatioVo;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressInfo;
import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.base.CardMarketActivity;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.calculate.UniteMarkeingtRecalculateBo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.GoodsActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.OrderCartProductBo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.base.BaseMarketingBaseVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.coupon.OrderCouponVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.fullreduce.OrderFullReduce;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.member.OrderMemberVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.packsale.OrderPackageSale;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.presale.OrderPreSale;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.process.DefaultMarketingProcess;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.rebate.RebateRecord;
import com.meidianyi.shop.service.pojo.wxapp.order.must.OrderMustVo;
import com.meidianyi.shop.service.pojo.wxapp.order.term.OrderTerm;
import com.meidianyi.shop.service.shop.activity.processor.FullReductionProcessor;
import com.meidianyi.shop.service.shop.activity.processor.PackageSaleProcessor;
import com.meidianyi.shop.service.shop.activity.processor.PreSaleProcessor;
import com.meidianyi.shop.service.shop.config.ShopReturnConfigService;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.distribution.MpDistributionGoodsService;
import com.meidianyi.shop.service.shop.distribution.OrderGoodsRebateService;
import com.meidianyi.shop.service.shop.goods.GoodsDeliverTemplateService;
import com.meidianyi.shop.service.shop.market.increasepurchase.IncreasePurchaseService;
import com.meidianyi.shop.service.shop.member.AddressService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.user.user.UserLoginRecordService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Result;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_MEMBER_GRADE;

/**
 * 订单模块计算类
 * @author 王帅
 */
@Component
public class Calculate extends ShopBaseService {

    @Autowired
    private GoodsDeliverTemplateService shippingFeeTemplate;
    @Autowired
    private CouponService coupon;
    @Autowired
    private UserCardService userCard;
    @Autowired
    private TradeService trade;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private UserLoginRecordService userLoginRecordService;
    @Autowired
    private ShopReturnConfigService returnCfg;
    @Autowired
    private FullReductionProcessor fullReductionProcessor;
    @Autowired
    private PreSaleProcessor preSaleProcessor;
    @Autowired
    private UserService user;
    @Autowired
    private MpDistributionGoodsService distributionGoods;
    @Autowired
    private OrderGoodsRebateService orderGoodsRebate;
    @Autowired
    private OrderGoodsService orderGoods;
    @Autowired
    private PackageSaleProcessor packageSaleProcessor;
    @Autowired
    private IncreasePurchaseService increasePurchase;

    /**
     * 计算订单商品折扣金额
     *
     * @param mbv   营销活动
     * @param mType 活动类型
     * @return 折扣总额
     */
    public BigDecimal calculateOrderGoodsDiscount(BaseMarketingBaseVo mbv, Byte mType) {
        logger().info("计算订单商品折扣金额start,营销活动:{},活动类型（0会员卡，1优惠卷，2满折满减，3预售）：{}", mbv, mType);
        if (mbv == null || CollectionUtils.isEmpty(mbv.getBos()) || !mbv.checkRatio()) {
            return BigDecimal.ZERO;
        }
        // 累计折扣金额
        BigDecimal discountPrica = BigDecimal.ZERO;
        for (int i = 0, lenght = mbv.getBos().size(); i < lenght; i++) {
            //该商品行折扣金额(向下取整)
            BigDecimal tdPrica;
            if (i == lenght - 1) {
                //最后一个商品通过减法计算
                tdPrica = BigDecimalUtil.subtrac(mbv.getTotalDiscount(), discountPrica);
            } else {
                //该商品行折扣金额(向下取整)
                tdPrica = BigDecimalUtil.multiply(mbv.getBos().get(i).getDiscountedTotalPrice(), mbv.getRatio(), RoundingMode.FLOOR);
            }
            //保存记录
            saveRecord(mbv.getBos().get(i), mType);
            //设置折扣信息
            BigDecimal currentDiscount = setOrderGoodsDiscountInfo(tdPrica, mbv.getBos().get(i));
            // 累计折扣金额++
            discountPrica = BigDecimalUtil.add(discountPrica, currentDiscount);
        }
        logger().info("计算订单商品折扣金额end,折扣金额：{}", discountPrica);
        return discountPrica;
    }

    /**
     * 商品总数量
     */
    public static final int BY_TYPE_TOLAL_NUMBER = 0;
    /**
     * 商品总价格
     */
    public static final int BY_TYPE_TOLAL_PRICE = 1;

    /**
     * 获取订单商品总数量、总价格
     *
     * @param bos              bos
     * @param discountType     折扣类型
     * @param defaultMarketing 临时存储默认营销信息
     * @return BigDecimal[0->数量，1->价格] （上面有常量）
     */
    public BigDecimal[] getTolalNumberAndPriceByType(List<OrderGoodsBo> bos, Byte discountType, DefaultMarketingProcess defaultMarketing) {
        BigDecimal[] tolalNumberAndPrice = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        for (OrderGoodsBo bo : bos) {
            //分销赠品过滤
            if(OrderConstant.D_T_REBATE.equals(discountType) && bo.getIsGift() != null && OrderConstant.YES == bo.getIsGift()) {
                continue;
            }
            //会员卡 或 优惠卷-> one
            if (OrderConstant.D_T_MEMBER_CARD.equals(discountType) || OrderConstant.D_T_COUPON.equals(discountType)) {
                //加价购 或 满折满减 与 one 不共存
                boolean isPlusPriceBuyOrDiscountReduce = (bo.getPurchasePriceId() != null && bo.getPurchasePriceId() > 0) ||
                    (bo.getStraId() != null && bo.getStraId() > 0);
                if (isPlusPriceBuyOrDiscountReduce) {
                    continue;
                }

                if (OrderConstant.D_T_MEMBER_CARD.equals(discountType) &&
                    defaultMarketing != null &&
                    OrderConstant.D_T_MEMBER_CARD.equals(defaultMarketing.getType()) &&
                    !CardConstant.MCARD_TP_LIMIT.equals(defaultMarketing.getCard().getInfo().getCardType())) {
                    //会员卡 and
                    // 临时存储默认营销信息!=null and
                    // 临时存储默认营销信息==card and
                    // 非限次卡
                    if (!userCard.isContainsProduct(defaultMarketing.getCard().getInfo().getCardId(), bo)) {
                        //校验该卡是否可用该商品
                        continue;
                    }
                }
                //优惠券校验是否使用不可使用优惠券的会员卡
                if(OrderConstant.D_T_COUPON.equals(discountType) && Byte.valueOf(OrderConstant.YES).equals(bo.getNoUseCoupon())) {
                    continue;
                }
                //会员卡优惠券与活动优惠是否叠加
                if(ACTIVITY_TYPE_FIRST_SPECIAL.equals(bo.getGoodsPriceAction())) {
                    if(OrderConstant.D_T_MEMBER_CARD.equals(discountType) ? defaultMarketing.getCard().getInfo().getMarketActivities().contains(CardMarketActivity.FIRST_SPECIAL) : defaultMarketing.getCoupon().getInfo().getCouponOverlay().equals(OrderConstant.YES)) {
                        continue;
                    }
                }else if(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE.equals(bo.getGoodsPriceAction())) {
                    if(OrderConstant.D_T_MEMBER_CARD.equals(discountType) ? defaultMarketing.getCard().getInfo().getMarketActivities().contains(CardMarketActivity.REDUCE_PRICE) : defaultMarketing.getCoupon().getInfo().getCouponOverlay().equals(OrderConstant.YES)) {
                        continue;
                    }
                }else if(BaseConstant.ACTIVITY_TYPE_MEMBER_GRADE.equals(bo.getGoodsPriceAction())) {
                    if(OrderConstant.D_T_MEMBER_CARD.equals(discountType) ? defaultMarketing.getCard().getInfo().getMarketActivities().contains(CardMarketActivity.MEMBER_PRICE) : defaultMarketing.getCoupon().getInfo().getCouponOverlay().equals(OrderConstant.YES)) {
                        continue;
                    }
                }
            }
            if (OrderConstant.D_T_MEMBER_CARD.equals(discountType)) {
                //会员卡初始化支持商品
                if (CollectionUtils.isEmpty(defaultMarketing.getCard().getBos())) {
                    defaultMarketing.getCard().setBos(new ArrayList<>());
                }
                defaultMarketing.getCard().getBos().add(bo);
            }
            if (OrderConstant.D_T_FULL_REDUCE.equals(discountType) && bo.getPurchasePriceId() != null && bo.getPurchasePriceId() > 0) {
                continue;
            }
            //数量
            tolalNumberAndPrice[BY_TYPE_TOLAL_NUMBER] = BigDecimalUtil.add(tolalNumberAndPrice[BY_TYPE_TOLAL_NUMBER], BigDecimal.valueOf(bo.getGoodsNumber()));
            //价格
            tolalNumberAndPrice[BY_TYPE_TOLAL_PRICE] = BigDecimalUtil.add(tolalNumberAndPrice[BY_TYPE_TOLAL_PRICE], bo.getDiscountedTotalPrice());
        }
        return tolalNumberAndPrice;
    }

    /**
     * 计算优惠卷折扣,设置默认优惠卷
     *
     * @param param 参数
     * @param vo    vo
     */
    public void calculateCoupon(OrderBeforeParam param, OrderBeforeVo vo) {
        logger().info("获取可用优惠卷start");
        if (StringUtils.EMPTY.equals(param.getCouponSn())) {
            logger().info("不可使用优惠券，end");
            return;
        }
        if (vo.getDefaultMemberCard() == null || !CardConstant.MCARD_TP_LIMIT.equals(vo.getDefaultMemberCard().getInfo().getCardType())) {
            logger().info("该次下单可以用优惠卷，准备获取优惠卷");
            //可用优惠卷
            List<OrderCouponVo> coupons = coupon.getValidCoupons(param.getWxUserInfo().getUserId());
            if (CollectionUtils.isEmpty(coupons)) {
                return;
            }
            for (OrderCouponVo temp : coupons) {
                for (OrderGoodsBo bo : param.getBos()) {
                    if (coupon.isContainsProduct(temp, bo)) {
                        logger().info("该商品可用使用该优惠卷");
                        if (temp.getBos() == null) {
                            temp.setBos(new ArrayList<>());
                        }
                        //优惠卷设置商品
                        temp.getBos().add(bo);
                    }
                }
            }
            for (Iterator<OrderCouponVo> i = coupons.iterator(); i.hasNext(); ) {
                OrderCouponVo temp = i.next();
                if (temp.getBos() != null) {
                    //获取该优惠卷对应商品的总价、总数
                    BigDecimal[] tolalNumberAndPrice = getTolalNumberAndPriceByType(temp.getBos(), OrderConstant.D_T_COUPON, DefaultMarketingProcess.builder().coupon(temp).type(OrderConstant.D_T_COUPON).build());
                    if (BigDecimalUtil.compareTo(tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE], null) == 1) {
                        //获取折扣金额
                        BigDecimal discountAmount = coupon.getDiscountAmount(temp, tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE]);
                        logger().info("优惠卷折扣金额为{}", discountAmount);
                        //折扣金额 > 0
                        if (BigDecimalUtil.compareTo(discountAmount, null) > 0) {
                            temp.setTotalDiscount(discountAmount);
                            temp.setTotalGoodsNumber(tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_NUMBER]);
                            temp.setTotalPrice(tolalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE]);
                            temp.setIdentity(temp.getInfo().getCouponSn());
                            //折扣
                            BigDecimal ratio = temp.initRatio();
                            //
                            if (ratio.compareTo(BigDecimal.ZERO) < 0 || ratio.compareTo(BigDecimal.ONE) > 0) {
                                logger().error("订单结算优惠券计算ratio数据非法,信息为:{},{}", param.getWxUserInfo(), temp.getInfo().getCouponSn());
                                //数据异常不影响正常流程，不使用该优惠券
                                i.remove();
                            }
                        } else {
                            logger().info("优惠券折扣金额不大于0删除");
                            i.remove();
                        }
                    } else {
                        logger().info("优惠券折扣金额不大于0删除(getTolalNumberAndPriceByType校验)");
                        i.remove();
                    }
                } else {
                    logger().info("优惠券无匹配商品删除");
                    i.remove();
                }
            }
            //再次校验
            if (CollectionUtils.isEmpty(coupons)) {
                vo.setCouponSn(null);
                logger().info("无可用优惠券");
                return;
            }
            if (OrderConstant.DEFAULT_COUPON_OR_ORDER_SN.equals(param.getCouponSn())) {
                //默认选择
                vo.setCouponSn(coupons.get(0).getInfo().getCouponSn());
                vo.setDefaultCoupon(coupons.get(0));
            } else if (StringUtils.isBlank(param.getCouponSn())) {
                //不选择优惠券
            } else {
                //选择指定优惠券
                for (OrderCouponVo coupon : coupons) {
                    if (coupon.getInfo().getCouponSn().equals(param.getCouponSn())) {
                        vo.setCouponSn(param.getCouponSn());
                        vo.setDefaultCoupon(coupon);
                    }
                }
            }
            vo.setCoupons(coupons);
            logger().info("获取可以优惠卷列表end,列表：{}，此次选择：{}", vo.getCoupons(), vo.getDefaultCoupon());
        }
        logger().info("获取可以优惠卷列表end,列表：{}，此次选择：{}", vo.getCoupons(), vo.getDefaultCoupon());
    }

    /**
     * 获取会员卡列表
     *
     * @param param
     * @param vo
     */
    public void calculateCardInfo(OrderBeforeParam param, OrderBeforeVo vo) {
        logger().info("获取可用会员卡列表start");
        if (StringUtils.EMPTY.equals(param.getMemberCardNo())) {
            logger().info("不可使用会员卡，end");
            return;
        }
        //会员卡折扣
        if (param.getMemberCardNo() != null) {
            /**使用会员卡，其中cardNo==0为使用默认会员卡*/
            OrderMemberVo card = userCard.userCardDao.getValidByCardNo(param.getMemberCardNo());
            if (card != null && CardConstant.MCARD_TP_LIMIT.equals(card.getInfo().getCardType())) {
                //限次卡
                userCard.getValidCardList(param.getWxUserInfo().getUserId(), param.getBos(), param.getStoreId(), Lists.newArrayList(card));
                vo.setDefaultMemberCard(card);
            } else {
                //普通卡、等级卡（或者没有传入cardNo）
                OrderMemberVo defaultCard = card;
                if (OrderConstant.DEFAULT_COUPON_OR_ORDER_SN.equals(param.getMemberCardNo())) {
                    defaultCard = userCard.userCardDao.getOrderGradeCard(param.getWxUserInfo().getUserId());
                }
                List<OrderMemberVo> validCardList = userCard.getValidCardList(param.getWxUserInfo().getUserId(), param.getBos(), param.getStoreId(), defaultCard == null ? null : Lists.newArrayList(defaultCard));
                defaultCard = CollectionUtils.isEmpty(validCardList) ? null : validCardList.get(0);
                vo.setDefaultMemberCard(defaultCard);
                vo.setMemberCards(validCardList);
            }
        } else {
            //不使用会员卡
            List<OrderMemberVo> validCardList = userCard.getValidCardList(param.getWxUserInfo().getUserId(), param.getBos(), param.getStoreId(), null);
            vo.setMemberCards(validCardList);
        }
        if(vo.getDefaultMemberCard() != null && CollectionUtils.isNotEmpty(vo.getDefaultMemberCard().getBos())) {
            //标记该商品参与会员卡打折且该卡不可使用优惠券
            if(vo.getDefaultMemberCard().getInfo().getMarketActivities().contains(CardMarketActivity.COUPON)) {
                vo.getDefaultMemberCard().getBos().forEach(x->x.setNoUseCoupon(OrderConstant.YES));
            }
        }
        logger().info("获取可用会员卡列表end,列表：{}，此次选择：{}", vo.getMemberCards(), vo.getDefaultMemberCard());

    }

    /**
     * 设置商品行折扣信息前保存记录
     *
     * @param bo            业务数据
     * @param marketingType 营销类型
     */
    public void saveRecord(OrderGoodsBo bo, Byte marketingType) {
        bo.setDiscountDetail(
            new StringBuilder().append("marketingType:").append(marketingType).
                append(",old_discounted_goods_price:").append(bo.getDiscountedGoodsPrice()).
                append(",old_discounted_total_price:").append(bo.getDiscountedTotalPrice()).
                toString()
        );
    }

    /**
     * 设置商品行折扣信息
     *
     * @param totalDiscountPrica
     * @param bo                 业务对象
     * @return 此次折扣金额
     */
    public BigDecimal setOrderGoodsDiscountInfo(BigDecimal totalDiscountPrica, OrderGoodsBo bo) {
        logger().info("setOrderGoodsDiscountInfo设置商品行折扣信息:totalDiscountPrica->{},bo->{}", totalDiscountPrica, bo);
        //商品单价折扣金额
        BigDecimal perDiscount = BigDecimalUtil.divide(totalDiscountPrica, BigDecimal.valueOf(bo.getGoodsNumber()));
        //新折后单价
        BigDecimal newDiscountedGoodsPrice = BigDecimalUtil.subtrac(bo.getDiscountedGoodsPrice(), perDiscount);
        //折后单价小于0（eg:商品10.00,优惠卷减20）时,折扣金额为商品折后单价
        if (newDiscountedGoodsPrice.compareTo(BigDecimal.ZERO) < 0) {
            perDiscount = bo.getDiscountedGoodsPrice();
            newDiscountedGoodsPrice = BigDecimalUtil.BIGDECIMAL_ZERO;
            totalDiscountPrica = BigDecimalUtil.multiply(perDiscount, BigDecimal.valueOf(bo.getGoodsNumber()));
        }
        //商品最后一个Sku单价折扣金额(退款使用，有时退不完)
        BigDecimal lastSkuReduce = BigDecimalUtil.subtrac(
            totalDiscountPrica,
            BigDecimalUtil.multiply(BigDecimal.valueOf(bo.getGoodsNumber() - 1),
                perDiscount)
        );
        //数据库记录修改前信息
        //赋值
        bo.setDiscountedGoodsPrice(newDiscountedGoodsPrice);
        bo.setDiscountedTotalPrice(BigDecimalUtil.subtrac(bo.getDiscountedTotalPrice(), totalDiscountPrica));
        return totalDiscountPrica;
    }

    /**
     * 计算运费
     *
     * @param districtCode 区县编号
     * @param bos          bos
     * @param storeId      门店id
     * @return 运费
     */
    public BigDecimal calculateShippingFee(Integer districtCode, List<OrderGoodsBo> bos, Integer storeId) {
        logger().info("计算运费start");
        BigDecimal result = BigDecimal.ZERO;
        /**处理过程中局部内部类*/
        @Getter
        @Setter
        @ToString
        class Total {
            private List<OrderGoodsBo> bos = Lists.newArrayList();
            private Integer totalNumber = 0;
            private BigDecimal totalPrice = BigDecimal.ONE;
            private BigDecimal totalWeight = BigDecimal.ZERO.setScale(3);
        }
        Map<Integer, Total> totalMaps = Maps.newHashMap();
        for (OrderGoodsBo bo : bos) {
            //过滤不参与计算的商品
            if (bo.getFreeShip() != null && bo.getFreeShip() == OrderConstant.YES) {
                bo.setIsShipping(OrderConstant.YES);
                continue;
            }
            //检查加价购换购商品是否走运费计算
            if(bo.getPurchasePriceRuleId() != null && bo.getPurchasePriceRuleId() > 0 && increasePurchase.isFreeShip(bo.getPurchasePriceId())) {
                bo.setIsShipping(OrderConstant.YES);
                continue;
            }
            if (totalMaps.get(bo.getDeliverTemplateId()) == null) {
                totalMaps.put(bo.getDeliverTemplateId(), new Total());
            }
            Total total = totalMaps.get(bo.getDeliverTemplateId());
            total.getBos().add(bo);
            total.setTotalNumber(total.getTotalNumber() + bo.getGoodsNumber());
            total.setTotalPrice(total.getTotalPrice().add(bo.getDiscountedTotalPrice()));
            total.setTotalWeight(total.getTotalWeight().add(BigDecimalUtil.multiply(bo.getGoodsWeight(), new BigDecimal(bo.getGoodsNumber()))));
        }

        for (Map.Entry<Integer, Total> entry : totalMaps.entrySet()) {
            Integer templateId = entry.getKey();
            Total total = entry.getValue();
            logger().info("计算运费模板id:{},参数:{}", templateId, total);
            BigDecimal shippingFeeByTemplate;
            try {
                if (districtCode == null || districtCode.equals(0)) {
                    total.getBos().forEach(x -> {
                        x.setIsShipping(OrderConstant.NO);
                    });
                } else {
                    shippingFeeByTemplate = shippingFeeTemplate.getShippingFeeByTemplate(districtCode, templateId, total.getTotalNumber(), total.getTotalPrice(), total.getTotalWeight());
                    result = BigDecimalUtil.add(result, shippingFeeByTemplate);
                    total.getBos().forEach(x -> {
                        x.setIsShipping(OrderConstant.YES);
                    });
                }
            } catch (MpException e) {
                total.getBos().forEach(x -> {
                    x.setIsShipping(OrderConstant.NO);
                });
            }
        }
        logger().info("计算运费end");
        return result;
    }

    /**
     * 计算运费
     * 默认地址>最近使用地址>定位
     *
     * @param userId      用户id
     * @param lat         经度
     * @param lng         纬度
     * @param goodsId     商品id
     * @param templateId  模板
     * @param totalNumber 要购买的商品数量
     * @param goodsPrice  单个商品价格
     * @param goodWeight  单个商品重量
     * @return 运费
     * @author kdc
     */
    public BigDecimal calculateShippingFee(Integer userId, String lat, String lng, Integer goodsId, Integer templateId, Integer totalNumber, BigDecimal goodsPrice, BigDecimal goodWeight) {
        logger().debug("开始计算运费，输入 userId{},lat:{},lng:{},goodsId:{},templateId:{},totalNumber:{},goodsPrice:{},goodsWeight:{}", userId, lat, lng, goodsId, templateId, totalNumber, goodsPrice, goodWeight);
        UserAddressRecord defaultAddress = addressService.getDefaultAddress(userId);
        if (defaultAddress==null){
            logger().info("默认地址为空,获取最近使用地址");
            UserAddressVo lastOrderAddress = orderInfoService.getLastOrderAddress(userId);
            if (lastOrderAddress!=null){
                UserAddressRecord addressInfo = addressService.getAddressById(userId, lastOrderAddress.getAddressId());
                if (addressInfo!=null&&addressInfo.getDelFlag().equals(DelFlag.NORMAL_VALUE)){
                    defaultAddress = addressInfo;
                }
            }
        }
        Integer districtCode=null;
        if (defaultAddress==null){
            logger().info("最近地址不可用,获取定位地址");
            AddressInfo userAddress = addressService.getGeocoderAddressInfo(lat, lng);
            logger().debug("定位地址：{}", userAddress);
            districtCode = addressService.getUserAddressDistrictId(userAddress);
            logger().debug("获取用户地址区域code:{}", districtCode);
            if (districtCode==null){
                logger().debug("获取用户最近登录地址");
                Integer userLoginRecordDistrictCode = userLoginRecordService.getUserLoginRecordDistrictCode(userId);
                if (userLoginRecordDistrictCode != null) {
                    districtCode = userLoginRecordDistrictCode;
                }
            }
        }else {
            districtCode= defaultAddress.getDistrictCode();
        }
        logger().debug("用户地址code:{}", districtCode);
        BigDecimal shippingFeeByTemplate = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimalUtil.multiply(goodsPrice, BigDecimal.valueOf(totalNumber));
        BigDecimal totalWeight = BigDecimalUtil.multiply(goodWeight, BigDecimal.valueOf(totalNumber));
        try {
            shippingFeeByTemplate = shippingFeeTemplate.getShippingFeeByTemplate(districtCode, templateId, totalNumber, totalPrice, totalWeight);
        } catch (MpException | NullPointerException e) {
            logger().debug("获取商品运费信息失败");
            e.printStackTrace();
        }
        return shippingFeeByTemplate;
    }

    /**
     * 下单校验必填信息
     *
     * @param orderGoods goods
     * @return
     */
    public OrderMustVo getOrderMust(List<OrderGoodsBo> orderGoods) {
        OrderMustVo must = new OrderMustVo();
        //初始化赋值
        must.init(trade.getOrderRealName(), trade.getOrderCid(), trade.getConsigneeRealName(), trade.getConsigneeCid(), trade.getCustom(), trade.getCustomTitle());
        if (OrderConstant.NO == must.isCheck()) {
            return must;
        }
        //规则(This will never be null)
        GoodsPackageParam rule = trade.getOrderRequireGoodsPackage();

        if (rule.getAddGoods() != null) {
            //goodsId校验
            ArrayList<Integer> goodsIds = Lists.newArrayList(rule.getAddGoods());
            goodsIds.retainAll(orderGoods.stream().map(OrderGoodsBo::getGoodsId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(goodsIds)) {
                return must.show();
            }
        }

        if (rule.getAddCate() != null) {
            //cateId校验
            ArrayList<Integer> cateIds = Lists.newArrayList(rule.getAddCate());
            cateIds.retainAll(orderGoods.stream().map(OrderGoodsBo::getCatId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(cateIds)) {
                return must.show();
            }
        }

        if (rule.getAddSort() != null) {
            //sortId校验
            ArrayList<Integer> sortId = Lists.newArrayList(rule.getAddSort());
            sortId.retainAll(orderGoods.stream().map(OrderGoodsBo::getSortId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(sortId)) {
                return must.show();
            }
        }

        if (rule.getAddBrand() != null) {
            //brandId校验
            ArrayList<Integer> brandIds = Lists.newArrayList(rule.getAddBrand());
            brandIds.retainAll(orderGoods.stream().map(OrderGoodsBo::getBrandId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(brandIds)) {
                return must.show();
            }
        }
        return must.hide();
    }

    public OrderTerm getTermsofservice() {
        OrderTerm orderTerm = new OrderTerm();
        orderTerm.init(trade.getServiceTerms(), trade.getServiceName(), trade.getServiceChoose());
        return orderTerm;
    }

    /**
     * 获取商品退款退货配置
     *
     * @param orderGoods 商品
     * @return map<goodsid, no / ys>
     */
    public void getGoodsReturnCfg(List<OrderGoodsBo> orderGoods) {
        byte allGoods = 2;
        Byte returnChangeGoodsStatus = returnCfg.getReturnChangeGoodsStatus();
        if (allGoods == returnChangeGoodsStatus) {
            //全部支持
            orderGoods.forEach(goods -> {
                goods.setIsCanReturn(OrderConstant.IS_CAN_RETURN_Y);
            });
        } else {
            //规则
            GoodsPackageParam rule = returnCfg.getOrderReturnGoodsPackage();
            if (rule.getAddGoods() != null) {
                //goodsId校验
                ArrayList<Integer> goodsIds = Lists.newArrayList(rule.getAddGoods());
                goodsIds.retainAll(orderGoods.stream().map(OrderGoodsBo::getGoodsId).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(goodsIds)) {
                    goodsIds.forEach(goodsId -> {
                        orderGoods.forEach(goods -> {
                            if (goods.getGoodsId().equals(goodsId)) {
                                goods.setIsCanReturn(returnChangeGoodsStatus == 0 ? OrderConstant.IS_CAN_RETURN_Y : OrderConstant.IS_CAN_RETURN_N);
                            }
                        });
                    });
                }
            }

            if (rule.getAddCate() != null) {
                //cateId校验
                ArrayList<Integer> cateIds = Lists.newArrayList(rule.getAddCate());
                cateIds.retainAll(orderGoods.stream().map(OrderGoodsBo::getCatId).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(cateIds)) {
                    cateIds.forEach(cateId -> {
                        orderGoods.forEach(goods -> {
                            if (goods.getCatId().equals(cateId)) {
                                goods.setIsCanReturn(returnChangeGoodsStatus == 0 ? OrderConstant.IS_CAN_RETURN_Y : OrderConstant.IS_CAN_RETURN_N);
                            }
                        });
                    });
                }
            }

            if (rule.getAddSort() != null) {
                //sortId校验
                ArrayList<Integer> sortIds = Lists.newArrayList(rule.getAddSort());
                sortIds.retainAll(orderGoods.stream().map(OrderGoodsBo::getSortId).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(sortIds)) {
                    sortIds.forEach(sortId -> {
                        orderGoods.forEach(goods -> {
                            if (goods.getSortId().equals(sortId)) {
                                goods.setIsCanReturn(returnChangeGoodsStatus == 0 ? OrderConstant.IS_CAN_RETURN_Y : OrderConstant.IS_CAN_RETURN_N);
                            }
                        });
                    });
                }
            }

            if (rule.getAddBrand() != null) {
                //brandId校验
                ArrayList<Integer> brandIds = Lists.newArrayList(rule.getAddBrand());
                brandIds.retainAll(orderGoods.stream().map(OrderGoodsBo::getBrandId).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(brandIds)) {
                    brandIds.forEach(brandId -> {
                        orderGoods.forEach(goods -> {
                            if (goods.getBrandId().equals(brandId)) {
                                goods.setIsCanReturn(returnChangeGoodsStatus == 0 ? OrderConstant.IS_CAN_RETURN_Y : OrderConstant.IS_CAN_RETURN_N);
                            }
                        });
                    });
                }
            }

            //设置null值
            orderGoods.forEach(goods -> {
                if (goods.getIsCanReturn() == null) {
                    goods.setIsCanReturn(returnChangeGoodsStatus == 0 ? OrderConstant.IS_CAN_RETURN_N : OrderConstant.IS_CAN_RETURN_Y);
                }
            });
        }
    }

    /**
     * 设置商品退款退货配置
     *
     * @param orderGoods 商品
     * @param goodsType  商品类型
     * @param order      订单
     */
    public void setReturnCfg(List<OrderGoodsBo> orderGoods, List<Byte> goodsType, OrderInfoRecord order) {
        Byte isCanReturn = null;
        //售后开关
        Byte saleSwitch = returnCfg.getPostSaleStatus();
        if (saleSwitch.equals(OrderConstant.NO) || goodsType.contains(BaseConstant.ACTIVITY_TYPE_GIVE_GIFT)) {
            isCanReturn = OrderConstant.IS_CAN_RETURN_N;
        } else if (order.getPosFlag() != null && OrderConstant.YES == order.getPosFlag()) {
            isCanReturn = OrderConstant.IS_CAN_RETURN_Y;
        }
        if (isCanReturn == null) {
            getGoodsReturnCfg(orderGoods);
        } else {
            for (OrderGoodsBo bo : orderGoods) {
                bo.setIsCanReturn(isCanReturn);
            }
        }
        if (orderGoods.stream().map(OrderGoodsBo::getIsCanReturn).max(Byte::compareTo).orElse(OrderConstant.IS_CAN_RETURN_Y).equals(OrderConstant.IS_CAN_RETURN_N)) {
            //max不可能为null
            //所有商品都为不可退则订单可退状态也改为不可退
            order.setReturnTypeCfg(OrderConstant.CFG_RETURN_TYPE_N);
        }
    }

    /**
     * 计算实际规格价（统一营销（普通商品营销））
     *
     * @param goods
     * @param uniteMarkeingt
     * @return
     */
    public UniteMarkeingtRecalculateBo uniteMarkeingtRecalculate(OrderBeforeParam.Goods goods, OrderCartProductBo.OrderCartProduct uniteMarkeingt,int userId) throws MpException {
        logger().info("uniteMarkeingtRecalculate start,参数为:{}", uniteMarkeingt);
        //分销改价
        if (uniteMarkeingt != null) {
            GoodsActivityInfo rebate = uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REBATE);
            if (rebate != null && rebate.getDistributionPrice() != null) {
                return UniteMarkeingtRecalculateBo.create(rebate.getDistributionPrice(), BaseConstant.ACTIVITY_TYPE_REBATE, null);
            }
        }
        //首单特惠
        if (uniteMarkeingt != null) {
            GoodsActivityInfo firstSpecial = uniteMarkeingt.getActivity(ACTIVITY_TYPE_FIRST_SPECIAL);
            if (firstSpecial != null && firstSpecial.getStatus().equals(CartConstant.ACTIVITY_STATUS_VALID) && firstSpecial.getFirstSpecialPrice() != null) {
                goods.setFirstSpecialId(firstSpecial.getActivityId());
                if (firstSpecial.getFirstSpecialNumber()!=null&&!firstSpecial.getFirstSpecialNumber().equals(0)){
                    goods.setIsAlreadylimitNum(true);
                }
                return UniteMarkeingtRecalculateBo.create(firstSpecial.getFirstSpecialPrice(), ACTIVITY_TYPE_FIRST_SPECIAL, firstSpecial.getActivityId());
            }
        }

        BigDecimal memberGradePrice = null;
        BigDecimal reducePrice = null;

        //限时降价
        if(uniteMarkeingt != null && uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE) != null && uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE).getReducePricePrdPrice() != null){
            reducePrice = uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE).getReducePricePrdPrice();
        }

        //会员等级价
        if(uniteMarkeingt != null && uniteMarkeingt.getActivity(ACTIVITY_TYPE_MEMBER_GRADE) != null && uniteMarkeingt.getActivity(ACTIVITY_TYPE_MEMBER_GRADE).getMemberPrice() != null){
            memberGradePrice = uniteMarkeingt.getActivity(ACTIVITY_TYPE_MEMBER_GRADE).getMemberPrice();
        }

        //会员等级价 和 限时降价 二取一，取低价
        if(memberGradePrice != null && reducePrice != null){
            if( memberGradePrice.compareTo(reducePrice) > 0){
                return calculateReducePrice(goods,uniteMarkeingt,userId,reducePrice);
            }else {
                return UniteMarkeingtRecalculateBo.create(memberGradePrice, ACTIVITY_TYPE_MEMBER_GRADE, null);
            }
        }else if(memberGradePrice != null){
            return UniteMarkeingtRecalculateBo.create(memberGradePrice, ACTIVITY_TYPE_MEMBER_GRADE, null);
        }else if(reducePrice != null){
            return calculateReducePrice(goods,uniteMarkeingt,userId,reducePrice);
        }

        return UniteMarkeingtRecalculateBo.create(goods.getProductPrice(), BaseConstant.ACTIVITY_TYPE_GENERAL, null);
    }

    /**
     * 计算限时降价的限购
     * @param goods
     * @param uniteMarkeingt
     * @param userId
     * @param reducePrice
     * @return
     * @throws MpException
     */
    private UniteMarkeingtRecalculateBo calculateReducePrice(OrderBeforeParam.Goods goods, OrderCartProductBo.OrderCartProduct uniteMarkeingt,int userId,BigDecimal reducePrice) throws MpException {
        goods.setReducePriceId(uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE).getActivityId());
        goods.setIsAlreadylimitNum(true);

        Integer limitAmount = uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE).getLimitAmount();
        Byte limitFlag = uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE).getLimitFlag();
        //限时降价的限购
        if(limitAmount > 0){
            if(goods.getGoodsNumber() > limitAmount || (orderGoods.getBuyGoodsNumberByReducePriceId(userId,uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE).getActivityId(),goods.getProductId()) + goods.getGoodsNumber()) > limitAmount){
                if (BaseConstant.LIMIT_FLAG_CONFINE.equals(limitFlag)){
                    //已达到限时降价设置的限购数量，并且禁止继续购买
                    throw new MpException(JsonResultCode.CODE_ORDER_GOODS_LIMIT_MAX, "限时降价最大限购", goods.getGoodsInfo().getGoodsName(), limitAmount.toString());
                }else{
                    //不禁止继续下单时以原价购买
                    goods.setReducePriceId(0);
                    return UniteMarkeingtRecalculateBo.create(goods.getProductPrice(), BaseConstant.ACTIVITY_TYPE_GENERAL, null);
                }
            }
        }
        goods.setReducePriceId(uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE).getActivityId());
        return UniteMarkeingtRecalculateBo.create(reducePrice, BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE, uniteMarkeingt.getActivity(BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE).getActivityId());
    }


    /**
     * 计算满折满减活动
     *
     * @param param
     * @param bos
     * @return
     */
    public List<OrderFullReduce> calculateFullReduce(OrderBeforeParam param, List<OrderGoodsBo> bos) {
        return fullReductionProcessor.calculate(param, bos);
    }

    /**
     * 计算预售
     *
     * @param param
     * @param bos
     * @param tolalNumberAndPrice
     * @param vo
     * @return
     */
    public OrderPreSale calculatePreSale(OrderBeforeParam param, List<OrderGoodsBo> bos, BigDecimal[] tolalNumberAndPrice, OrderBeforeVo vo) {
        if (!BaseConstant.ACTIVITY_TYPE_PRE_SALE.equals(param.getActivityType())) {
            return null;
        }
        return preSaleProcessor.calculate(param, bos, tolalNumberAndPrice, vo);
    }

    /**
     * 下单计算分销价格
     *
     * @param param
     */
    public void calculatePrice(OrderBeforeParam param) {
        DistributionParam cfg = distributionGoods.distributionConf.getDistributionCfg();
        //开关
        if (cfg == null || cfg.getStatus() == null || cfg.getStatus() == OrderConstant.NO) {
            logger().info("开关关闭，结束");
            return;
        }
        Map<Integer, UserRebatePriceRecord> rebatePriceRecordMap = distributionGoods.userRebatePrice.getUserRebatePrice(param.getWxUserInfo().getUserId(),  param.getProductIds().toArray(new Integer[]{})).intoMap(UserRebatePriceRecord::getProductId);
        param.createOrderCartProductBo().getAll().forEach(goods -> {
            UserRebatePriceRecord rebatePriceRecord = rebatePriceRecordMap.get(goods.getProductId());
            if (rebatePriceRecord != null) {
                logger().info("规格：{},分销价 ：{}", rebatePriceRecord.getProductId(), rebatePriceRecord.getAdvicePrice());
                GoodsActivityInfo goodsActivityInfo = new GoodsActivityInfo();
                goodsActivityInfo.setActivityType(BaseConstant.ACTIVITY_TYPE_REBATE);
                goodsActivityInfo.setDistributionPrice(rebatePriceRecord.getAdvicePrice());
                goods.getActivityInfo().put(BaseConstant.ACTIVITY_TYPE_REBATE, goodsActivityInfo);
            }
        });
    }

    /**
     * 分销处理
     *
     * @param param
     * @param order
     */
    public void rebate(OrderBeforeParam param, OrderInfoRecord order) {
        //配置
        DistributionParam cfg = distributionGoods.distributionConf.getDistributionCfg();
        //开关
        if (cfg == null || cfg.getStatus() == null || cfg.getStatus() == OrderConstant.NO) {
            logger().info("开关关闭processSaveOrderInfo，结束");
            return;
        }
        //获取该订单参与分销计算商品数量（除赠品）
        BigDecimal[] tolal = getTolalNumberAndPriceByType(param.getBos(), OrderConstant.D_T_REBATE, null);
        //商品平均积分抵扣
        BigDecimal avgScoreDiscount = BigDecimalUtil.divide(order.getScoreDiscount(), tolal[BY_TYPE_TOLAL_NUMBER], RoundingMode.HALF_UP);
        //进行中的返利策略
        List<DistributionStrategyParam> goingStrategy = distributionGoods.goingStrategy();
        //用户信息
        UserRecord userInfo = user.getUserByUserId(param.getWxUserInfo().getUserId());
        //是否首单
        boolean isFs = orderInfoService.isNewUser(userInfo.getUserId());

        rebateAll(param, order, cfg, avgScoreDiscount, goingStrategy, userInfo, isFs);
    }

    private void rebateAll(OrderBeforeParam param, OrderInfoRecord order, DistributionParam cfg, BigDecimal avgScoreDiscount, List<DistributionStrategyParam> goingStrategy, UserRecord userInfo, boolean isFs) {
        //是否进行返利标识
        boolean flag = false;
        //总返利
        BigDecimal rebateMoney = BigDecimalUtil.BIGDECIMAL_ZERO;
        for (OrderGoodsBo bo : param.getBos()) {
            if(bo.getIsGift() != null && OrderConstant.YES == bo.getIsGift()) {
                //赠品不参与
                continue;
            }
            if(bo.getPurchasePriceRuleId() != null && bo.getPurchasePriceRuleId() > 0) {
                //换购商品不参与
                continue;
            }
            //折扣价-积分抵扣
            BigDecimal canRebateMoney = BigDecimalUtil.subtrac(bo.getDiscountedTotalPrice(), BigDecimalUtil.multiply(avgScoreDiscount, new BigDecimal(bo.getGoodsNumber())));
            //zero check
            canRebateMoney = BigDecimalUtil.compareTo(canRebateMoney, null) > 0 ? canRebateMoney : BigDecimalUtil.BIGDECIMAL_ZERO;
            if(BigDecimalUtil.compareTo(canRebateMoney, null) < 1) {
                logger().info("初步计算可返利金额为0（支付价格 - 积分平均抵扣；此时未进行佣金计算方式）");
                continue;
            }
            //成本价保护(最大返利金额)->as (实际利润)
            BigDecimal check =  BigDecimalUtil.subtrac(canRebateMoney, BigDecimalUtil.multiply(bo.getCostPrice(), new BigDecimal(bo.getGoodsNumber())));
            //商品返利计算
            List<RebateRecord> rebateRecords = calculateGoodsRebate(cfg, bo, goingStrategy, userInfo, isFs);
            if(CollectionUtils.isNotEmpty(rebateRecords)) {
                //同一个商品返利策略一样，取第一个即可
                DistributionStrategyParam strategy = rebateRecords.get(0).getStrategy();
                //佣金计算方式
                if(DistributionConstant.STRATEGY_TYPE_PROFIT.equals(strategy.getStrategyType())) {
                    //商品实际利润（实际支付金额-成本价）* 佣金比例'
                    canRebateMoney = check;
                    if(BigDecimalUtil.compareTo(canRebateMoney, null) < 1) {
                        logger().info("佣金计算方式为商品实际利润时可返利金额为0");
                        continue;
                    }
                } else if(strategy.getCostProtection() == OrderConstant.YES) {
                    if(BigDecimalUtil.compareTo(check, BigDecimalUtil.BIGDECIMAL_ZERO) < 1) {
                        logger().info("成本价保护策略生效");
                        bo.setFanliStrategy("成本价保护策略生效");
                        continue;
                    }
                }
                ArrayList<OrderGoodsRebateRecord> records = orderGoodsRebate.create(rebateRecords, bo, canRebateMoney, check, order.getOrderSn());
                //赋值
                bo.setFanliPercent(BigDecimalUtil.multiply(records.get(0).getRebatePercent(), BigDecimalUtil.BIGDECIMAL_100));
                bo.setFanliType(rebateRecords.get(rebateRecords.size()-1).getRebateLevel());
                bo.setTotalFanliMoney(records.stream().map(OrderGoodsRebateRecord::getTotalRebateMoney).reduce(BigDecimalUtil.BIGDECIMAL_ZERO, BigDecimalUtil::add));
                bo.setFanliStrategy(Util.toJson(strategy));
                //商品参与返利金额
                bo.setCanCalculateMoney(bo.getCanCalculateMoney() == null ? canRebateMoney : bo.getCanCalculateMoney());
                bo.setRebateList(records);
                rebateMoney = BigDecimalUtil.add(bo.getTotalFanliMoney(), rebateMoney);
                flag = true;
            }
        }
        if(flag) {
            order.setFanliType(DistributionConstant.REBATE_ORDER);
            order.setFanliUserId(userInfo.getInviteId());
            order.setFanliMoney(rebateMoney);
            order.setGoodsType(OrderInfoService.addGoodsTypeToInsert(order.getGoodsType(), Lists.newArrayList(BaseConstant.ACTIVITY_TYPE_REBATE)));
        }
    }

    /**
     * 商品返利计算
     * @param cfg 分销配置
     * @param bo 商品bo
     * @param goingStrategy 正在进行的返利策略
     * @param userInfo 当前用户
     * @param isFs 是否首单
     * @return
     */
    private List<RebateRecord> calculateGoodsRebate(DistributionParam cfg, OrderGoodsBo bo, List<DistributionStrategyParam> goingStrategy, UserRecord userInfo, boolean isFs) {
        //获取商品返利策略
        DistributionStrategyParam goodsStrategy = distributionGoods.getGoodsStrategy(bo.getGoodsId(), bo.getSortId(), goingStrategy);
        if (goodsStrategy == null) {
            logger().info("该商品无返利策略，goodsId:{}", bo.getGoodsId());
            return null;
        }
        Timestamp current = DateUtils.getSqlTimestamp();

        List<RebateRecord> rebateRecords = selfRebate(cfg, userInfo, isFs, goodsStrategy, current);
        if(CollectionUtils.isEmpty(rebateRecords)) {
            //正常返利
            rebateRecords = rebate(cfg, userInfo, isFs, goodsStrategy, current);
        }
        return rebateRecords;
    }

    /**
     * 自购返利(当自购返利开关开启，若下单人是分销员，则该下单人的间接邀请人不会获得返利，其直接邀请人可获得返利，返利比例为直接邀请人所在等级的间接邀请返利比例)
     * @param cfg
     * @param userInfo
     * @param isFs
     * @param goodsStrategy
     * @param current
     * @return
     */
    private ArrayList<RebateRecord> selfRebate(DistributionParam cfg, UserRecord userInfo, boolean isFs, DistributionStrategyParam goodsStrategy, Timestamp current) {
        logger().info("自购返利start");
        if (goodsStrategy.getSelfPurchase() == OrderConstant.YES) {
            ArrayList<RebateRecord> result = new ArrayList<>();
            //获取用户自购返利比例
            RebateRatioVo userRebateRatio = distributionGoods.getUserRebateRatio(userInfo, goodsStrategy, cfg);
            //下单用户的直接邀请人
            UserRecord userInfo2;
            if (userRebateRatio != null && userRebateRatio.getFanliRatio() != null) {
                logger().info("自购自己返利");
                //返利比例(直接返利比例)
                BigDecimal ratio = BigDecimalUtil.divide(new BigDecimal(userRebateRatio.getFanliRatio().toString()), BigDecimalUtil.BIGDECIMAL_100);
                result.add(new RebateRecord(goodsStrategy, userInfo.getUserId(), DistributionConstant.REBATE_TYPE_SELF, ratio));
                //邀请过期时间校验
                if(userInfo.getInviteExpiryDate() != null && userInfo.getInviteExpiryDate().compareTo(DistributionConstant.NO_INVITE_EXPIRY) > 0 && userInfo.getInviteExpiryDate().compareTo(current) > 0) {
                    logger().info("自购，邀请已过期,自己过期不返直接上级");
                }if((userInfo2 = user.getUserByUserId(userInfo.getInviteId())) == null) {
                    logger().info("自购，无直接上级");
                }else {
                    //自购二级返利（下单用户的直接邀请人，间接返利比例（或首单返利比例））
                    RebateRatioVo userRebateRatio2 = distributionGoods.getUserRebateRatio(userInfo2, goodsStrategy, cfg);
                    if(userRebateRatio2 != null) {
                        Double rebateRatio2 = (isFs && goodsStrategy.getFirstRebate() == OrderConstant.YES) ? userRebateRatio2.getFirstRatio() : userRebateRatio2.getRebateRatio();
                        if(rebateRatio2 != null) {
                            logger().info("自购直接上级返利");
                            BigDecimal ratio2 = BigDecimalUtil.divide(new BigDecimal(rebateRatio2.toString()), BigDecimalUtil.BIGDECIMAL_100);
                            result.add(new RebateRecord(goodsStrategy, userInfo2.getUserId(), DistributionConstant.REBATE_TYPE_DIRECT, ratio2));
                        }
                    }
                }
            }
            logger().info("自购返利end");
            return result;
        }
        logger().info("自购返利end");
        return null;
    }

    /**
     *正常返利
     * @param cfg
     * @param userInfo
     * @param isFs
     * @param goodsStrategy
     * @param current
     * @return
     */
    private ArrayList<RebateRecord> rebate(DistributionParam cfg, UserRecord userInfo, boolean isFs, DistributionStrategyParam goodsStrategy, Timestamp current) {
        logger().info("正常返利start");
        ArrayList<RebateRecord> result = new ArrayList<>();
        UserRecord userInfo1 = null;
        //一级返利
        if(userInfo.getInviteExpiryDate() != null && userInfo.getInviteExpiryDate().compareTo(DistributionConstant.NO_INVITE_EXPIRY) > 0 && userInfo.getInviteExpiryDate().compareTo(current) > 0) {
            logger().info("正常返利，邀请已过期,自己过期不返直接上级");
        }else if(userInfo.getInviteId() == null || userInfo.getInviteId() == 0) {
            logger().info("正常返利，无直接上级");
        }else {
            //直接上级用户信息
            userInfo1 = user.getUserByUserId(userInfo.getInviteId());
            RebateRatioVo userRebateRatio1 = distributionGoods.getUserRebateRatio(userInfo1, goodsStrategy, cfg);
            if(userRebateRatio1 != null) {
                Double rebateRatio1 = (isFs && goodsStrategy.getFirstRebate() == OrderConstant.YES) ? userRebateRatio1.getFirstRatio() : userRebateRatio1.getFanliRatio();
                if(rebateRatio1 != null) {
                    logger().info("正常返利直接上级返利");
                    BigDecimal ratio1 = BigDecimalUtil.divide(new BigDecimal(rebateRatio1.toString()), BigDecimalUtil.BIGDECIMAL_100);
                    result.add(new RebateRecord(goodsStrategy, userInfo.getInviteId(), DistributionConstant.REBATE_TYPE_DIRECT, ratio1));
                }
            }
        }
        //二级返利
        UserRecord userInfo2 = null;
        if(userInfo1 != null && userInfo1.getInviteId() != null && userInfo1.getInviteId() != 0) {
            //间接上级用户信息
            userInfo2 = user.getUserByUserId(userInfo1.getInviteId());
        }
        if(userInfo2 == null) {
            logger().info("正常返利，无间接上级");
        } else if(userInfo2.getInviteExpiryDate() != null && userInfo2.getInviteExpiryDate().compareTo(DistributionConstant.NO_INVITE_EXPIRY) > 0 && userInfo2.getInviteExpiryDate().compareTo(current) > 0) {
            logger().info("正常返利，邀请已过期,直接上级过期不返间接上级");
        }else {
            RebateRatioVo userRebateRatio2 = distributionGoods.getUserRebateRatio(userInfo2, goodsStrategy, cfg);
            if(userRebateRatio2 != null) {
                Double rebateRatio2 = userRebateRatio2.getRebateRatio();
                if(rebateRatio2 != null) {
                    logger().info("正常返利间接上级返利");
                    BigDecimal ratio1 = BigDecimalUtil.divide(new BigDecimal(rebateRatio2.toString()), BigDecimalUtil.BIGDECIMAL_100);
                    result.add(new RebateRecord(goodsStrategy, userInfo2.getUserId(), DistributionConstant.REBATE_TYPE_INDIRECT, ratio1));
                }
            }
        }
        logger().info("正常返利end");
        return result;
    }

    /**
     * 计算打包一口价
     *
     * @param param
     * @param bos
     * @param totalNumberAndPrice
     * @param vo
     * @return
     */
    public OrderPackageSale calculatePackageSale(OrderBeforeParam param, List<OrderGoodsBo> bos, BigDecimal[] totalNumberAndPrice, OrderBeforeVo vo) {
        if (!BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE.equals(param.getActivityType())) {
            return null;
        }
        return packageSaleProcessor.calculate(param, bos, totalNumberAndPrice, vo);
    }
    public void recalculationRebate(List<OrderReturnGoodsVo> returnGoods, Result<OrderGoodsRecord> orderGoods, String orderSn) {
        if(CollectionUtils.isEmpty(returnGoods)) {
            return;
        }
        Map<Integer, OrderGoodsRecord> goodsMap = orderGoods.stream().collect(Collectors.toMap(OrderGoodsRecord::getRecId, Function.identity()));
        //需要更新的记录
        ArrayList<OrderGoodsRebateRecord> updateRecords = new ArrayList<>();
        for (OrderReturnGoodsVo returnGoodsVo : returnGoods) {
            if(returnGoodsVo.getGoodsNumber() != null && returnGoodsVo.getGoodsNumber() > 0) {
                OrderGoodsRecord orderGoodsRecord = goodsMap.get(returnGoodsVo.getRecId());
                Result<OrderGoodsRebateRecord> rebateRecords = orderGoodsRebate.get(orderSn, returnGoodsVo.getRecId());
                for (OrderGoodsRebateRecord rebateRecord : rebateRecords) {
                    rebateRecord.setRealRebateMoney(BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.HALF_DOWN,
                        BigDecimalUtil.BigDecimalPlus.create(rebateRecord.getTotalRebateMoney(), BigDecimalUtil.Operator.multiply),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(orderGoodsRecord.getGoodsNumber() - orderGoodsRecord.getReturnNumber()), BigDecimalUtil.Operator.divide),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(orderGoodsRecord.getGoodsNumber())))
                    );
                    updateRecords.add(rebateRecord);
                }
            }
        }
        db().batchUpdate(updateRecords).execute();
    }
}

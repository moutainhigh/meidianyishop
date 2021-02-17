package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PackageSaleRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.market.packagesale.PackSaleConstant;
import com.meidianyi.shop.service.pojo.shop.market.packagesale.PackSaleParam;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.PackSalePromotion;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.packsale.OrderPackageSale;
import com.meidianyi.shop.service.shop.market.packagesale.PackSaleService;
import com.meidianyi.shop.service.shop.order.action.base.Calculate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 打包一口价
 *
 * @author: 王兵兵
 * @create: 2020-04-15 09:53
 **/
@Service
@Slf4j
public class PackageSaleProcessor implements CreateOrderProcessor, GoodsDetailProcessor {

    @Autowired
    private PackSaleService packSaleService;

    /**
     * 活动排序优先级
     *
     * @return
     */
    @Override
    public Byte getPriority() {
        return 1;
    }

    /**
     * 获取活动类型,方便查找
     *
     * @return 活动类型
     */
    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE;
    }

    /**
     * 商品详情
     **/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        List<PackSalePromotion> canUsePackSalePromotion = packSaleService.getCanUsePackSalePromotion(capsule.getGoodsId(), capsule.getSortId(), DateUtils.getLocalDateTime());
        if (canUsePackSalePromotion != null && canUsePackSalePromotion.size() > 0) {
            capsule.getPromotions().put(BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE, canUsePackSalePromotion);
        }
    }

    /**
     * 初始化参数,活动校验
     *
     * @param param 参数
     * @throws MpException 异常
     */
    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        //禁用优惠券
        param.setCouponSn("");

        //校验
        PackageSaleRecord packageSaleRecord = packSaleService.getRecord(param.getActivityId());
        List<PackSaleParam.GoodsGroup> groups = packSaleService.getPackageGroups(packageSaleRecord);
        Set<Integer> actGoodsIds = new HashSet<>();
        for (PackSaleParam.GoodsGroup group : groups) {
            int groupSelectNumber = packSaleService.packageGoodsCartService.getUserGroupGoodsNumber(param.getWxUserInfo().getUserId(), param.getActivityId(), group.getGroupId());
            if (groupSelectNumber != group.getGoodsNumber()) {
                throw new MpException(JsonResultCode.PACKAGE_SALE_RULE_CHANGED);
            }
            List<Integer> effectiveGoodsIds = packSaleService.getPackageSaleGroupGoodsIds(group);
            actGoodsIds.addAll(effectiveGoodsIds);
        }
        param.getGoods().forEach(goods->{
            goods.setIsAlreadylimitNum(true);
        });

        Set<Integer> orderGoodsIds = new HashSet<>(param.getGoodsIds());
        if (!actGoodsIds.containsAll(orderGoodsIds)) {
            throw new MpException(JsonResultCode.PACKAGE_SALE_RULE_CHANGED);
        }
    }

    /**
     * 保存信息,此时订单数据已计算完成（此时订单状态已经明确）
     *
     * @param param 参数
     * @param order 订单
     * @throws MpException 异常
     */
    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        packSaleService.packageGoodsCartService.deleteUserCartGoods(param.getActivityId(), param.getWxUserInfo().getUserId());
    }

    /**
     * 订单生效后（微信支付、其他支付、货到付款等）的营销后续处理（库存、活动状态相关）
     * 下单时接口调用说明：此方法与扣减商品库存（非活动库存）方法同时调用
     * 支付回调调用说明：订单状态转化未待发货时调用
     *
     * @param param
     * @param order
     * @throws MpException
     */
    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    /**
     * 更新活动库存
     *
     * @param param
     * @param order
     * @throws MpException
     */
    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    /**
     * 退货、取消、关闭时更新（returnOrderRecord == null为关闭或取消）
     *
     * @param returnOrderRecord
     * @param activityId        活动id
     * @param returnGoods       退款商品
     */
    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {

    }

    /**
     * 订单处理金额
     *
     * @param param
     * @param bos
     * @param totalNumberAndPrice
     * @param vo
     * @return
     */
    public OrderPackageSale calculate(OrderBeforeParam param, List<OrderGoodsBo> bos, BigDecimal[] totalNumberAndPrice, OrderBeforeVo vo) {
        log.info("一口价处理金额start");
        OrderPackageSale res = new OrderPackageSale();
        PackageSaleRecord packageSaleRecord = packSaleService.getRecord(param.getActivityId());
        BigDecimal ratio = null;

        //指定金额结算
        if (packageSaleRecord.getPackageType().equals(PackSaleConstant.PACKAGE_TYPE_MONEY)) {
            ratio = BigDecimalUtil.divide(packageSaleRecord.getTotalMoney(), totalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE]);
            res.setTotalDiscount(totalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE].subtract(packageSaleRecord.getTotalMoney()));
            res.setPackageTotalMoney(packageSaleRecord.getTotalMoney());
            res.setTotalPrice(packageSaleRecord.getTotalMoney());
            res.setRatio(ratio);
            //指定折扣结算
        } else if (packageSaleRecord.getPackageType().equals(PackSaleConstant.PACKAGE_TYPE_DISCOUNT)) {
            ratio = BigDecimalUtil.divide(packageSaleRecord.getTotalRatio(), new BigDecimal(10));
            res.setTotalDiscount(totalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE].subtract(totalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE].multiply(ratio)));
            res.setPackageTotalMoney(BigDecimalUtil.multiply(totalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE], ratio));
            res.setTotalPrice(BigDecimalUtil.multiply(totalNumberAndPrice[Calculate.BY_TYPE_TOLAL_PRICE], ratio));
            res.setRatio(ratio);
        }
        for (OrderGoodsBo orderGoodsBo : bos) {
            orderGoodsBo.setDiscountedGoodsPrice(BigDecimalUtil.multiply(orderGoodsBo.getGoodsPrice(), ratio));
            orderGoodsBo.setDiscountedTotalPrice(BigDecimalUtil.multiply(orderGoodsBo.getDiscountedGoodsPrice(), new BigDecimal(orderGoodsBo.getGoodsNumber())));
        }
        res.setBos(bos);
        res.setTotalGoodsNumber(totalNumberAndPrice[Calculate.BY_TYPE_TOLAL_NUMBER]);
        vo.setPackageDiscount(res.getTotalDiscount());
        log.info("一口价处理金额end");

        return res;
    }
}

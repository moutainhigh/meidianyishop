package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRebatePriceRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateRatioVo;
import com.meidianyi.shop.service.pojo.shop.distribution.UserDistributionVo;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.distribution.GoodsDistributionVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.shop.distribution.MpDistributionGoodsService;
import com.meidianyi.shop.service.shop.order.action.base.Calculate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分销
 * @author 王帅
 */
@Slf4j
@Service
public class RebateProcess implements Processor,ActivityGoodsListProcessor,GoodsDetailProcessor,CreateOrderProcessor,ActivityCartListStrategy{
    @Autowired
    MpDistributionGoodsService distributionGoods;
    @Autowired
    Calculate calculate;
    @Override
    public Byte getPriority() {
        return GoodsConstant.ACTIVITY_DISTRIBUTION_PRIORITY;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_REBATE;
    }

    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {

    }

    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        log.info("下单分销计算start");
        calculate.calculatePrice(param);
        log.info("下单分销计算end");
    }

    /**
     * @param param 参数
     * @param order 订单
     * @throws MpException
     */
    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        log.info("下单分销save start");
        calculate.rebate(param,order );
        log.info("下单分销save end");
    }

    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {

    }

    @Override
    public void processGoodsDetail(GoodsDetailMpBo goodsDetailMpBo, GoodsDetailCapsuleParam param) {
        //商品分销
            //判断商品是否存在营销活动
            GoodsActivityBaseMp activity = goodsDetailMpBo.getActivity();
            Byte[] intArray = new Byte[]{BaseConstant.ACTIVITY_STATUS_NORMAL,BaseConstant.ACTIVITY_TYPE_BARGAIN,BaseConstant.NAVBAR_TYPE_DISABLED,BaseConstant.ACTIVITY_TYPE_SEC_KILL,
                BaseConstant.ACTIVITY_TYPE_GROUP_DRAW,BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE,BaseConstant.ACTIVITY_TYPE_PRE_SALE,BaseConstant.ACTIVITY_TYPE_LOTTERY_PRESENT,BaseConstant.ACTIVITY_TYPE_EXCHANG_ORDER,
                BaseConstant.ACTIVITY_TYPE_PROMOTE_ORDER,BaseConstant.ACTIVITY_TYPE_PAY_AWARD,BaseConstant.ACTIVITY_TYPE_ASSESS_ORDER};
            ArrayList<Byte> goodsTypes = new ArrayList<>(Arrays.asList(intArray));
            if(activity == null || !(goodsTypes.contains(activity.getActivityType()))){
                GoodsDistributionVo goodsDistributionVo = new GoodsDistributionVo();
                //获取用户分销等级
                UserDistributionVo distributionLevel = distributionGoods.userDistributionLevel(param.getUserId());
                RebateRatioVo rebateRatioVo = distributionGoods.goodsRebateInfo(param.getGoodsId(), param.getCatId(), param.getSortId(), param.getUserId());
                goodsDistributionVo.setIsDistributor(distributionLevel.getIsDistributor());
                goodsDistributionVo.setCanRebate(goodsDetailMpBo.getCanRebate());
                goodsDistributionVo.setRebateRatio(rebateRatioVo);
                goodsDetailMpBo.setGoodsDistribution(goodsDistributionVo);
                goodsDistributionVo.setPromotionLanguage(distributionGoods.getGoodsPromotionLanguage(param.getUserId(),param.getGoodsId()));
        }
    }

    //*************************************

    /**
     * 购物车 分销商品
     * @param cartBo 业务数据类
     */
    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        log.info("购物车-分销-开始");
        DistributionParam cfg = distributionGoods.distributionConf.getDistributionCfg();
        //开关
        if (cfg.getStatus() == OrderConstant.NO) {
            log.info("购物车-分销-开关关闭processInitCheckedOrderCreate，结束");
            return;
        }
        List<Integer> product = cartBo.getCartGoodsList().stream().map(WxAppCartGoods::getProductId).distinct().collect(Collectors.toList());
        Map<Integer, UserRebatePriceRecord> rebatePriceRecordMap = distributionGoods.userRebatePrice.getUserRebatePrice(cartBo.getUserId(), product.toArray(new Integer[]{}))
            .intoMap(UserRebatePriceRecord::getProductId);
        cartBo.getCartGoodsList().forEach(goods -> {
            UserRebatePriceRecord rebatePriceRecord = rebatePriceRecordMap.get(goods.getProductId());
            if (rebatePriceRecord != null) {
                log.info("购物车-分销-规格：{},分销价 ：{}", rebatePriceRecord.getProductId(), rebatePriceRecord.getAdvicePrice());
                CartActivityInfo activityInfo = new CartActivityInfo();
                activityInfo.setActivityType(BaseConstant.ACTIVITY_TYPE_REBATE);
                activityInfo.setActivityId(rebatePriceRecord.getId());
                activityInfo.setProductPrice(rebatePriceRecord.getAdvicePrice());
                goods.getCartActivityInfos().add(activityInfo);
                goods.setPriceStatus(BaseConstant.YES);
                goods.setPrdPrice(rebatePriceRecord.getAdvicePrice());
            }
        });
        log.info("购物车-分销-结算");
    }
}

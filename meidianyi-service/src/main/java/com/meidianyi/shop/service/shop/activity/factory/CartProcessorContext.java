package com.meidianyi.shop.service.shop.activity.factory;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.shop.activity.processor.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/11/7 18:32
 */
@Component
@Slf4j
public class CartProcessorContext {



    @Autowired(required = false)
    private List<ActivityCartListStrategy> sortProcessors;

    @Autowired
    private GoodsBeginProcessor goodsBegin;
    @Autowired
    private GoodsTailProcessor goodsTail;
    @Autowired
    private GradeCardProcessor gradeCard;
    @Autowired
    private ExclusiveProcessor exclusive;
    @Autowired
    private FirstSpecialProcessor firstSpecial;
    @Autowired
    private SecKillProcessor seckill;
    @Autowired
    private PreSaleProcessor preSale;
    @Autowired
    private ReducePriceProcessor reducePrice;
    @Autowired
    private FullReductionProcessor fullReduction;
    @Autowired
    private PurchasePriceProcessor purchasePrice;
    @Autowired
    private CouponPackageProcessor couponPackage;
    @Autowired
    private RebateProcess rebateProcess;
    /**
     * 购物车一般活动
     */
    public final static List<Byte> GENERAL_ACTIVITY = Arrays.asList(
            BaseConstant.ACTIVITY_TYPE_MEMBER_GRADE,
            BaseConstant.ACTIVITY_TYPE_MEMBER_EXCLUSIVE,
            BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL,
            BaseConstant.ACTIVITY_TYPE_SEC_KILL,
            BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE,
            BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION,
            BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE
    );


    /**
     *  购物车执行方法
     * @param cartBo
     */
    public void executeCart(WxAppCartBo cartBo){
        // 数据初始化
        executeStrategy(goodsBegin,cartBo);
        //预售-不可购买
//        executeStrategy(preSale,cartBo);
        //会员专享-不可购买
//        executeStrategy(exclusive,cartBo);
        //秒杀-提示
//        executeStrategy(seckill,cartBo);
        //分销
//        executeStrategy(rebateProcess,cartBo);
        //首单特惠
//        executeStrategy(firstSpecial,cartBo);
        //限时降价
//        executeStrategy(reducePrice,cartBo);
        //等级价格
//        executeStrategy(gradeCard,cartBo);
        //满折满减
//        executeStrategy(fullReduction,cartBo);
        //加价购
//        executeStrategy(purchasePrice,cartBo);
        //优惠券礼包
//        executeStrategy(couponPackage, cartBo);
        //活动冲突处理
        executeStrategy(goodsTail,cartBo);
    }


    private void executeStrategy(ActivityCartListStrategy strategy, WxAppCartBo cartBo){
        try {
            strategy.doCartOperation(cartBo);
        }catch (Exception e){
            log.error("商品规格策略失败"+strategy.getClass()+e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.coupon.pack.CouponPackCartVo;
import com.meidianyi.shop.service.shop.market.couponpack.CouponPackService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author: 王兵兵
 * @create: 2020-04-28 08:57
 **/
@Service
public class CouponPackageProcessor implements Processor, ActivityCartListStrategy {
    @Autowired
    private CouponPackService couponPackService;

    /**
     * 活动排序优先级
     *
     * @return
     */
    @Override
    public Byte getPriority() {
        return 0;
    }

    /**
     * 获取活动类型,方便查找
     *
     * @return 活动类型
     */
    @Override
    public Byte getActivityType() {
        return 0;
    }

    /**
     * 执行业务的处理方法
     *
     * @param cartBo 业务数据类
     */
    @Override
    public void doCartOperation(WxAppCartBo cartBo) {
        if (CollectionUtils.isNotEmpty(cartBo.getCartGoodsList())) {
            CouponPackCartVo couponPackCartVo = couponPackService.getCartCouponPack(
                cartBo.getUserId(),
                cartBo.getCartGoodsList().stream().filter(g -> g.getIsChecked().equals(BaseConstant.YES)).map(g -> g.getGoodsId()).distinct().collect(Collectors.toList()));
            cartBo.setCouponPackage(couponPackCartVo);
        }
    }
}

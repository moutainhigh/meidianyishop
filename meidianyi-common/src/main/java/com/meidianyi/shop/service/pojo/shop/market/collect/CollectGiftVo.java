package com.meidianyi.shop.service.pojo.shop.market.collect;

import java.util.List;

import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;

import lombok.Data;


/**
 * 收藏有礼-详情
 * @author liangchen
 * @date 2020.05.20
 */
@Data
public class CollectGiftVo extends CollectGiftParam {
    private List<CouponView> couponDetail;
}

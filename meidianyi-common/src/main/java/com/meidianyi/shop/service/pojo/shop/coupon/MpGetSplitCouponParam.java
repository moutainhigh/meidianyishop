package com.meidianyi.shop.service.pojo.shop.coupon;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/4/17
 */
@Getter
@Setter
public class MpGetSplitCouponParam {
    @NotNull
    private Integer couponId;
    private Integer userId;
    @NotNull
    private Integer shareUserId;
    /**
     * 活动来源
     */
    private Byte source;
    /**
     * 优惠券sn
     */
    private String couponSn;
}

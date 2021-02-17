package com.meidianyi.shop.service.pojo.shop.coupon;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/5/29 16:45
 */
@Getter
@Setter
@ToString
public class MpCouponSnParam {
    @NotNull
    private String couponSn;
    private Integer userId;
}

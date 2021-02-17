package com.meidianyi.shop.service.pojo.wxapp.coupon.pack;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2020-03-03 10:29
 **/
@Setter
@Getter
public class CouponPackOrderBeforeParam {
    @NotNull
    private Integer packId;

    /**
     * 选择的会员卡号
     */
    private String cardNo;
}

package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-08-20 17:44
 **/
@Data
public class SimpleCouponPackParam {
    @NotNull
    private Integer id;
}

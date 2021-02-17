package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 加价购商品
 * @author 孔德成
 * @date 2020/3/5
 */
@Getter
@Setter
public class CartPurchaseParam {

    /**
     * 加价购Id
     */
    @NotNull
    private Integer purchaseId;
    /**
     * 规格id
     */
    @NotNull
    private Integer productId;
    /**
     * 用户id
     */
    private Integer userId;
}

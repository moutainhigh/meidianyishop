package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 购物车商品数量
 * @author 孔德成
 * @date 2019/12/3 17:22
 */
@Getter
@Setter
public class CartGoodsNumParam {

    @NotNull
    private Integer goodsId;
}

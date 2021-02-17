package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 *  删除购物车商品参数
 * @author 孔德成
 * @date 2019/10/17 11:04
 */
@Getter
@Setter
public class WxAppRemoveCartProductParam {

    @NotNull
    private Integer cartId;
}

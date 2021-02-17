package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/11/25 18:33
 */
@Setter
@Getter
public class CartProductIdParam {
    @NotNull
    private Integer recId;
}

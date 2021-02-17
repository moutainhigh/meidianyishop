package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/12/3 17:40
 */
@Getter
@Setter
public class CartGoodsNumVo {

    private Integer goodsId;
    private Integer goodsNum;
}

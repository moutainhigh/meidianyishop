package com.meidianyi.shop.service.pojo.wxapp.market.increasepurchase;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2020-03-10 16:46
 **/
@Setter
@Getter
public class PurchaseChangeGoodsParam {
    @NotNull
    private Integer purchasePriceId;

    private Integer storeId;
}

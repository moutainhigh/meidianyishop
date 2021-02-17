package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2020-06-11 16:13
 **/
@Getter
@Setter
public class PackageSaleGoodsDeleteParam {
    @NotNull
    protected Integer packageId;
    @NotNull
    @Min(1)
    @Max(3)
    protected Byte groupId;
    @NotNull
    protected Integer goodsId;
    @NotNull
    protected Integer productId;
}

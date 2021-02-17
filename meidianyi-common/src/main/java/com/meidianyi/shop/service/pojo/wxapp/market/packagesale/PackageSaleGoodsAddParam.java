package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2020-04-13 13:56
 **/
@Setter
@Getter
public class PackageSaleGoodsAddParam {
    @NotNull
    private Integer packageId;
    @NotNull
    @Min(1)
    @Max(3)
    private Byte groupId;
    @NotNull
    private Integer goodsId;
    @NotNull
    private Integer productId;

    /**
     * 增量，可以是负数
     */
    @NotNull
    private Integer goodsNumber;
}

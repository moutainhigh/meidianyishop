package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2020-04-14 19:22
 **/
@Getter
@Setter
public class PackageSaleIdParam {
    @NotNull
    private Integer packageId;
}

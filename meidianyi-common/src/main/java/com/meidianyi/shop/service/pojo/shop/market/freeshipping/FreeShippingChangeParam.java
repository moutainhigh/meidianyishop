package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/7/31 15:05
 */
@Getter
@Setter
public class FreeShippingChangeParam {

    @NotNull
    private Integer id;
}

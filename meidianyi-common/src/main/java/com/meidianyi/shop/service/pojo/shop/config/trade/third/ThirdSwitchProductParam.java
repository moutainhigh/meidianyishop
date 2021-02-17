package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/5/19
 */
@Getter
@Setter
@ToString
public class ThirdSwitchProductParam {
    @NotNull
    @Min(1)
    @Max(3)
    private byte product;
    @NotNull
    private Integer id;
}

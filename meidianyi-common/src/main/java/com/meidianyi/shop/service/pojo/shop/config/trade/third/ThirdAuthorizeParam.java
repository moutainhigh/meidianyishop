package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/5/18
 */
@Getter
@Setter
@ToString
public class ThirdAuthorizeParam {

    @NotNull
    private Integer id;
    @NotNull
    @Max(3)
    @Min(1)
    private Byte action;
    /**
     * 1启用 0禁用
     */
    @NotNull
    @Max(1)
    @Min(0)
    private Byte status;


    private Integer shopId;
}

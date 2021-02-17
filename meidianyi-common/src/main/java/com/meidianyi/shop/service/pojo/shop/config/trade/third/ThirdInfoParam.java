package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 第三方信息
 * @author 孔德成
 * @date 2020/5/18
 */
@Getter
@Setter
@ToString
public class ThirdInfoParam {

    /**
     *  1rep 2pos 3 crm
     */
    @Max(3)
    @Min(1)
    @NotNull
    private Byte action;

}

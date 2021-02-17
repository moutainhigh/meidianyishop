package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 *
 * @author 孔德成
 * @date 2020/5/18
 */
@Getter
@Setter
@ToString
public class ThirdAppAuthIdParam {

    @NotNull
    private Integer id;

}

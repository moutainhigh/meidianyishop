package com.meidianyi.shop.service.pojo.shop.market.payaward;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/8/12 18:28
 */
@Getter
@Setter
public class PayAwardIdParam {
    @NotNull
    private Integer    id;
}

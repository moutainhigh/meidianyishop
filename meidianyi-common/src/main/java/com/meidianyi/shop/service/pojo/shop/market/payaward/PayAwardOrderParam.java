package com.meidianyi.shop.service.pojo.shop.market.payaward;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/12/19 15:39
 */
@Getter
@Setter
public class PayAwardOrderParam {
    @NotNull
    private String orderSn;
}

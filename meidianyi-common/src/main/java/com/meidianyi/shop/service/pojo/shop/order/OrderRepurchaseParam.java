package com.meidianyi.shop.service.pojo.shop.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 订单再次购买
 * @author 孔德成
 * @date 2020/4/26
 */
@Getter
@Setter
public class OrderRepurchaseParam {
    /**
     * 订单号
     */
    @NotNull
    private String orderSn;

    private Integer userId;
}

package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liufei
 * @date 2019/8/14
 * @description
 */
@Data
public class PurchaseStatusParam {
    /**
     * 加价购活动id
     */
    @NotNull
    private Integer id;
    /**
     * 加价购活动状态
     */
    @NotNull
    private Byte status;
}

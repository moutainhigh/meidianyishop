package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2019/7/30 14:46
 */
@Data
public class FreeShippingGoodsRuleVo {

    private Integer action;
    private Integer identityId;
    private Integer shippingId;
    private String name;
    private BigDecimal money;
    private Integer num;
    private Integer conType;



}

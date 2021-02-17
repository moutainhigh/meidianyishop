package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2019/7/31 13:40
 */
@Data
public class FreeShippingRuleVo {

    private Integer id;
    private Integer conType;
    private BigDecimal money;
    private Integer num;
    private String  area;
}

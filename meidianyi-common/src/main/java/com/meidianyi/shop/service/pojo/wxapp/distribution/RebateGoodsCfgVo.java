package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author 常乐
 * @Date 2020-04-06
 * 小程序分销改价页面信息
 */
@Data
public class RebateGoodsCfgVo {
    /**
     * 商品规格ID
     */
    private Integer prdId;
    /**
     * 商品规格价格
     */
    private BigDecimal prdPrice;
    /**
     * 商品规格描述
     */
    private String prdDesc;
    /**
     * 分销改价建议价格
     */
    private BigDecimal advisePrice;
    /**
     * 分销改价最低价格
     */
    private BigDecimal minPrice;
    /**
     * 分销改价最高价格
     */
    private BigDecimal maxPrice;

}

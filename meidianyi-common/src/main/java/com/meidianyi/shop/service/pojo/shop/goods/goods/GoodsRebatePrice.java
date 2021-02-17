package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分销改价的建议数据
 * @author 李晓冰
 * @date 2019年08月21日
 */
@Data
public class GoodsRebatePrice {
    private Integer id;
    private Integer goodsId;
    /**
     * 商品规格描述
     */
    private String prdDesc;
    /**
     * 商品规格id
     */
    private Integer productId;
    /**
     * 建议售价
     */
    private BigDecimal advisePrice;
    /**
     * 最低售价
     */
    private BigDecimal minPrice;
    /**
     * 最高售价
     */
    private BigDecimal maxPrice;
}

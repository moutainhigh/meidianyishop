package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.reduce;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 小程序-商品详情-限时降价规格
 * @author 李晓冰
 * @date 2020年01月07日
 */
@Data
public class ReducePricePrdMpVo {
    /**规格id*/
    private Integer productId;
    /**限时降价价格*/
    private BigDecimal reducePrice;
    /**规格原价*/
    private BigDecimal prdPrice;
}

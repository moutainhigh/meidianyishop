package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.presale;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 小程序-商品详情-预售活动规格信息
 * @author 李晓冰
 * @date 2020年01月09日
 */
@Data
public class PreSalePrdMpVo {
    /**规格id*/
    private Integer productId;
    /**规格库存*/
    private Integer stock;
    /**当前规格已售数量*/
    private Integer saleNumber;
    /**预售价格*/
    private BigDecimal preSalePrice;
    /**定金*/
    private BigDecimal depositPrice;
    /**抵扣价*/
    private BigDecimal discountPrice;
    /**规格原价*/
    private BigDecimal prdPrice;
}

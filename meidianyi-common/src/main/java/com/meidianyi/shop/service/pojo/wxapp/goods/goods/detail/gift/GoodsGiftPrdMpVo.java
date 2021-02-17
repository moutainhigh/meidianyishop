package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.gift;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2020年01月14日
 */
@Data
public class GoodsGiftPrdMpVo {
    /**赠品ID*/
    private Integer id;
    /**商品名称*/
    private String goodsName;
    /**规格名称*/
    private String prdDesc;
    /**赠品对应的规格原id*/
    private Integer productId;
    /**赠品图片*/
    private String prdImg;
    /**赠品价格*/
    private BigDecimal prdPrice;
}

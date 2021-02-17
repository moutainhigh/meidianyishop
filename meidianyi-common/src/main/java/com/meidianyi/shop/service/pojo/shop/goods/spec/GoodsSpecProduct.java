package com.meidianyi.shop.service.pojo.shop.goods.spec;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 货品SKU
 * 
 * @author 李晓冰
 * @date 2019年07月05日
 */
@Data
public class GoodsSpecProduct {
	private Integer prdId;

	private Integer goodsId;
    private BigDecimal prdPrice;
    private BigDecimal prdMarketPrice;
    private BigDecimal prdCostPrice;
    private Integer prdNumber;
    private BigDecimal prdWeight;

    /**
     * 	规格编码
     */
	private String prdSn;
	/**
     * 商品条码
     * */
	private String prdCodes;
	private String prdSpecs;
	private String prdDesc;

	private BigDecimal lowShopPrice;
	private String prdImg;
	private String prdImgUrl;
}

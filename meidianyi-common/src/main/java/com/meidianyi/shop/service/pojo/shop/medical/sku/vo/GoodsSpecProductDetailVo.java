package com.meidianyi.shop.service.pojo.shop.medical.sku.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2020年07月07日
 */
@Getter
@Setter
public class GoodsSpecProductDetailVo extends GoodsSpecProductBaseVo{
    private BigDecimal prdMarketPrice;
    private BigDecimal prdCostPrice;
    private String prdCodes;
    private String prdSpecs;
    private String prdDesc;
    private String prdImg;
    private BigDecimal prdWeight;
}

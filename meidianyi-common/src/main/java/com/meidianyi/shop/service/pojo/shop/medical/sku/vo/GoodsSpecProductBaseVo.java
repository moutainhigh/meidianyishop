package com.meidianyi.shop.service.pojo.shop.medical.sku.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2020年07月08日
 */
@Data
public class GoodsSpecProductBaseVo {
    private Integer prdId;
    private Integer goodsId;
    private String prdSn;
    private BigDecimal prdPrice;
    private Integer prdNumber;
}

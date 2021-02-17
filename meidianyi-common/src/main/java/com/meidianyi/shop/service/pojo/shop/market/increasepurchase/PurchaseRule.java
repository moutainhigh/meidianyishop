package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liufei
 * @date 2019/8/14
 * @description
 */
@Data
public class PurchaseRule {
    /** 活动信息规则，主商品购满 [] 元可加 [] 元换购 [{...,...}] 商品*/
    private Integer id;
    private BigDecimal fullPrice;
    private BigDecimal purchasePrice;
    private String productId;
}

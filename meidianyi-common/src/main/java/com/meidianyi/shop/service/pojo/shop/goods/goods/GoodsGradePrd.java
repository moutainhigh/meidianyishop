package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品特定规格对应的不同会员等级的价格
 *
 * @author 李晓冰
 * @date 2019年08月21日
 */
@Data
public class GoodsGradePrd {
    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 商品规格id
     */
    private Integer prdId;

    /**
     * 规格描述内容，通过这个值判断是哪一种规格
     */
    private String prdDesc;
    /**
     * 特定规格的会员价格
     */
    private BigDecimal gradePrice;
    /**
     * 会员等级
     */
    private String grade;
}

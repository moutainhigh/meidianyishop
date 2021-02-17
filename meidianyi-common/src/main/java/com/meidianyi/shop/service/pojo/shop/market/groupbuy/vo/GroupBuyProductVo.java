package com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2019/7/19 17:50
 */
@Data
public class GroupBuyProductVo {

    private Integer id;
    /**
     * 拼团定义Id
     */
    private Integer activityId;

    /**
     * 商品规格id
     */
    @JsonProperty(value = "prdId")
    private Integer productId;

    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 商品名称
     */
    private Integer goodsName;

    /**
     * 商品规格名称
     */
    private String prdDesc;

    /**
     * 原价价格
     */

    private BigDecimal prdPrice;
    /**
     * 原库存
     */
    private Short prdNumber;

    /**
     * 拼团价格
     */
    private BigDecimal groupPrice;

    /**
     * 当前库存
     */
    private Short stock;

    /**
     * 销量
     */
    private Short saleNum;

    /**
     * 团长价格
     */
    private BigDecimal grouperPrice;
}

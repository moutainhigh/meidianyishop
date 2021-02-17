package com.meidianyi.shop.service.pojo.shop.market.gift;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 赠品规格出参
 *
 * @author 郑保乐
 */
@Data
public class ProductVo {

    /**id **/
    private Integer id;
    /** 活动id **/
    private Integer giftId;
    /** 规格id **/
    private Integer productId;
    /** 赠品库存 **/
    private Integer productNumber;
    /** 规格图片 **/
    private String prdImg;
    /** 产品价格 **/
    private BigDecimal prdPrice;
    /** 产品库存 **/
    private Integer prdNumber;
    /** 已赠送数量 **/
    private Integer offerNumber;
    /** 规格名称 **/
    private String prdDesc;
    /** 商品名称 **/
    private String goodsName;
    /** 商品图片 **/
    private String goodsImg;
}

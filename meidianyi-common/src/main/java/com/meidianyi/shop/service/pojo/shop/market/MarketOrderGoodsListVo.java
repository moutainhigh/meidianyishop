package com.meidianyi.shop.service.pojo.shop.market;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 营销活动订单的行信息
 * @author: 王兵兵
 * @create: 2019-09-04 16:03
 **/
@Data
public class MarketOrderGoodsListVo {
    private Integer recId;
    private Integer mainRecId;
    private Integer orderId;
    private String orderSn;
    private Integer goodsId;
    /**货号*/
    private String goodsSn;
    private String goodsName;
    /**退货时为退货数量,发货时为可发货数量*/
    private Integer goodsNumber;
    /**单价*/
    private BigDecimal goodsPrice;
    /**属性（规格）*/
    private String goodsAttr;
    /**SKU*/
    private Integer productId;
    private String goodsImg;
    private Integer sendNumber;
    private String productSn;
    /**商品参与的促销活动id*/
    private Integer straId;
    /**促销折扣均摊到每件商品的折扣*/
    private BigDecimal perDiscount;
    /**商品积分*/
    private Integer goodsScore;
    private Integer isGift;
    private Integer giftId;
    private BigDecimal discountedGoodsPrice;
}

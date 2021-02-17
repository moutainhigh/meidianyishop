package com.meidianyi.shop.service.pojo.shop.market.presale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 活动商品规格出参
 *
 * @author 郑保乐
 */
@Data
public class ProductVo {

    /** 活动商品规格id **/
    private Integer id;
    /** 商品id **/
    private Integer goodsId;
    /** 产品id **/
    private Integer productId;
    /** 规格名 **/
    private String prdDesc;
    /**商品名称*/
    private String goodsName;
    /** 原价 **/
    private BigDecimal prdPrice;
    /** 库存 **/
    private Integer prdNumber;
    /** 活动价格 **/
    private Double presalePrice;
    /** 活动库存 **/
    private Integer presaleNumber;
    /** 定金 **/
    private Double presaleMoney;
    /** 1段定金可抵扣金额 **/
    private Double preDiscountMoney1;
    /** 2段定金可抵扣金额 **/
    private Double preDiscountMoney2;
    @JsonIgnore
    private Integer presaleId;
    @JsonIgnore
    private Byte status;
    @JsonIgnore
    private Byte delFlag;
    @JsonIgnore
    private Timestamp createTime;
    @JsonIgnore
    private Timestamp updateTime;
}

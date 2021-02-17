package com.meidianyi.shop.service.pojo.shop.order.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 王帅
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApiOrderGoodsListVo {
    @JsonIgnore
    private Integer recId;
    @JsonProperty("sku_id")
    private Integer productId;
    @JsonProperty("prd_sn")
    private String productSn;
    private Integer goodsId;
    private String goodsSn;
    private String goodsName;
    @JsonProperty("prd_desc")
    private String goodsAttr;
    private BigDecimal goodsPrice;
    private Integer goodsNumber;
    @JsonProperty("goods_refund_status")
    private Byte refundStatus;
    private String shippingNo;
    private String shippingName;
    private Timestamp shippingTime;
    @JsonIgnore
    private Integer sendNumber;
    @JsonIgnore
    private Integer goodsScore;
    @JsonIgnore
    private BigDecimal discountedGoodsPrice;

}

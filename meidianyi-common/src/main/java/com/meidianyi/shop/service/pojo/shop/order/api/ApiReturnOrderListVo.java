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
import java.util.List;

/**
 * @author 王帅
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApiReturnOrderListVo {
    @JsonIgnore
    private Integer retId;
    @JsonIgnore
    private Byte refundStatus;
    @JsonProperty("refund_status")
    private String erpRefundStatus;
    private String orderSn;
    private BigDecimal money;
    private Byte returnType;
    @JsonIgnore
    private Byte reasonType;
    private String reason;
    private String returnDesc;
    @JsonIgnore
    private String shippingType;
    @JsonProperty("shipping_type")
    private String shippingName;
    private String shippingNo;
    private String goodsImages;
    private String voucherImages;
    private String phone;
    private Timestamp applyNotPassReason;
    private Timestamp returnTime;
    private Timestamp returnFinishTime;
    private Timestamp refundTime;
    private Timestamp refundFinishTime;
    private Timestamp refuseTime;
    private Timestamp refuseRefundTime;
    private String refundRefuseReason;
    private String returnAddress;
    private String merchantTelephone;
    private String consignee;
    private String zipCode;

    /**直接查订单列表的出参start*/
    /**1：售前 2：售后*/
    private Byte afterSales;
    private String returnOrderSn;
    private Timestamp createTime;
    @JsonProperty("order_goods_list")
    private List<ApiReturnGoodsListVo> returnGoodsList;
    /**TODO 换货未实现*/
    /*private List<?> exchangeGoods;*/
    /**直接查订单列表的出参end*/

}

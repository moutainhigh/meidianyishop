package com.meidianyi.shop.service.pojo.shop.order.pos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * pos退款
 *
 * @author 王帅
 * @date 2020年04月29日
 */
@Data
public class PosReturnGoodsParam {
    @JsonProperty("order_sn")
    private String orderSn;
    /**退款类型（目前支支持0，1）*/
    @JsonProperty("return_type")
    private Byte returnType;
    /**退商品金额*/
    @JsonProperty("money")
    private BigDecimal money;
    @JsonProperty("shipping_fee")
    private BigDecimal shippingFee;
    private String reason;
    /**退商品*/
    private Map<String, Integer> goods;
    /**商品属性(3加价购0普通商品)*/
    private Map<String, Integer> gift;
}

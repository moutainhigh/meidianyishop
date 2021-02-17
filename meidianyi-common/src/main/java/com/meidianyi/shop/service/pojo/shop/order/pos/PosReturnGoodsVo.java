package com.meidianyi.shop.service.pojo.shop.order.pos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * pos退款
 *
 * @author 王帅
 * @date 2020年04月29日
 */
@Data
@AllArgsConstructor
public class PosReturnGoodsVo {
    private BigDecimal money;
    @JsonProperty("shipping_fee")
    private BigDecimal shippingFee;
}

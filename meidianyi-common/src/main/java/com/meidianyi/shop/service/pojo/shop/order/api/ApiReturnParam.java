package com.meidianyi.shop.service.pojo.shop.order.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 退货操作
 *
 * @author 王帅
 * @date 2020年04月29日
 */
@Data
@AllArgsConstructor
public class ApiReturnParam {
    @JsonProperty("order_sn")
    private String orderSn;
    @JsonProperty("return_order_sn")
    private String returnOrderSn;
    /**
     * 退款结果：同意agree 拒绝refuse
     */
    @JsonProperty("refund_type")
    private String refundType;
    /**
     * 拒绝原因，拒绝退款必传项
     */
    @JsonProperty("refuse_reason")
    private String refuseReason;
}

package com.meidianyi.shop.service.pojo.shop.order.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.util.api.ApiBasePageParam;

import lombok.Data;

/**
 *  对外接口-订单详情-入参
 * @author 王帅
 */
@Data
public class ApiOrderQueryParam extends ApiBasePageParam {
    @JsonProperty("order_sn")
    private String orderSn;
}

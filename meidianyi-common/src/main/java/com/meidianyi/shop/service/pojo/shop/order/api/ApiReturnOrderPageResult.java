package com.meidianyi.shop.service.pojo.shop.order.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.util.api.ApiPageResult;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 退款订单列表分页类-此类是由于php目前规定的对接文档字段不统一导致，
 * 后期可以直接在ApiPageResult类进行统一
 * @author 王帅
 */
@Getter
@Setter
public class ApiReturnOrderPageResult extends ApiPageResult {
    @JsonProperty("total_order_count")
    private Integer totalOrderCount;
    @JsonProperty("list")
    private List<ApiReturnOrderListVo> orderList;
}

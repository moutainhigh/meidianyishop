package com.meidianyi.shop.service.pojo.shop.order.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author 王帅
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApiReturnGoodsListVo {
    private String orderSn;
    private Integer goodsId;
    private String goodsName;
    private Integer productId;
    private Integer goodsNumber;
    private Integer sendNumber;
    @JsonProperty("refund_status")
    private Byte success;
}

package com.meidianyi.shop.service.pojo.wxapp.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/10/17 11:34
 */
@Getter
@Setter
public class WxAppChangeNumberParam {

    /**
     * 规格id
     */
    @NotNull
    private Integer productId;
    /**
     *购物车id
     */
    @NotNull
    private Integer cartId;
    @Min(value = 0)
    private Integer cartNumber;

    @JsonProperty("UserId")
    private Integer userId;
}

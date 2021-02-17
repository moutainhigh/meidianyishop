package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleCoupon extends ModuleBase {

    @JsonProperty("coupon_arr")
    private Coupon[] couponAyy;

    @Setter
    @Getter
    public static class Coupon{
        @JsonProperty("act_code")
        private String actCode;

        @JsonProperty("denomination")
        private BigDecimal denomination;

        @JsonProperty("consume_text")
        private String consumeText;

        @JsonProperty("receive_text")
        private String receiveText;

        @JsonProperty("coupon_id")
        private Integer couponId;

        @JsonProperty("use_score")
        private Byte useScore;

        @JsonProperty("score_number")
        private Integer scoreNumber;

        @JsonProperty("limit_surplus_flag")
        private Byte limitSurplusFlag;
    }
}

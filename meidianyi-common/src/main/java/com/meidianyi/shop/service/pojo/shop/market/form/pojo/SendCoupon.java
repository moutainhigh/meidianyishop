package com.meidianyi.shop.service.pojo.shop.market.form.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 孔德成
 * @date 2020/5/6
 */
@Getter
@Setter
@ToString
public class SendCoupon {
    @JsonProperty("act_code")
    private String actCode;

    private int denomination;

    @JsonProperty("consume_text")
    private String consumeText;

    @JsonProperty("receive_text")
    private String receiveText;

    @JsonProperty("coupon_id")
    private Integer couponId;

    @JsonProperty("use_score")
    private int useScore;

    @JsonProperty("score_number")
    private int scoreNumber;

    private int limitSurplusFlag;
}

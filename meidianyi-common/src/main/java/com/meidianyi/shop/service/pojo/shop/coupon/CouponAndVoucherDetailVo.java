package com.meidianyi.shop.service.pojo.shop.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * customer_avail_coupons 表和 mrking_voucher表信息
 * @author 孔德成
 * @date 2020/3/17
 */
@Data
@NoArgsConstructor
public class CouponAndVoucherDetailVo {
    private Integer id;
    private String couponSn;
    private Integer userId;
    private Integer actType;
    private Integer actId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte type;
    private BigDecimal amount;
    private String actDesc;
    private Integer limitOrderAmount;
    private Byte isUsed;
    private Timestamp usedTime;
    private Byte accessMode;
    private Integer accessId;
    private Timestamp notifyTime;
    private String orderSn;
    private Byte delFlag;
    private Byte getSource;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String wxOpenid;
    private String wxUnionId;

    private String actName;
    private BigDecimal denomination;

    @JsonProperty("act_code")
    private String actCode;

    @JsonProperty("least_consume")
    private String leastConsume;

    @JsonProperty("use_explain")
    private String useExplain;

    @JsonProperty("recommend_goods_id")
    private String recommendGoodsId;

    @JsonProperty("recommend_cat_id")
    private String recommendCatId;

    @JsonProperty("recommend_sort_id")
    private String recommendSortId;

    @JsonProperty("use_score")
    private String useScore;

    @JsonProperty("score_number")
    private String scoreNumber;

    @JsonProperty("is_delete")
    private String isDelete;


    private String validity;

    @JsonProperty("validity_hour")
    private String validityHour;

    @JsonProperty("validity_minute")
    private String validityMinute;

    @JsonProperty("random_max")
    private String randomMax;

    @JsonProperty("random_min")
    private String randomMin;

    @JsonProperty("coupon_type")
    private String couponType;

    @JsonProperty("receive_per_num")
    private String receivePerNum;

    @JsonProperty("receive_num")
    private String receiveNum;


}

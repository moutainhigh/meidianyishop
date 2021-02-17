package com.meidianyi.shop.service.pojo.wxapp.coupon;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2019年10月16日
 */
@Data
public class CouponTagVo {
    private String actCode;
    private BigDecimal denomination;
    private BigDecimal leastConsume;
    private Byte useConsumeRestrict;
}

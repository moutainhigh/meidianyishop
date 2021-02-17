package com.meidianyi.shop.service.pojo.wxapp.coupon;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2019-12-17
 */
@Data
public class ExpireTimeVo {
    private long remainDays;
    private long remainHours;
    private long remainMinutes;
    private long remainSeconds;
}

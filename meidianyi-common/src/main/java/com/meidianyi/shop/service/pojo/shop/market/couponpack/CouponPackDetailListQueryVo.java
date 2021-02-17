package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-21 19:08
 **/
@Data
public class CouponPackDetailListQueryVo {

    private String username;

    private String mobile;

    /** 获取方式，0：现金购买，1：积分购买，2直接领取 */
    private Byte accessMode;

    private String orderSn;

    private Timestamp createTime;

    /** 已领取优惠券数量 */
    private Integer voucherAccessCount;
}

package com.meidianyi.shop.service.pojo.wxapp.coupon.pack;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-03-04 13:37
 **/
@Setter
@Getter
public class CouponPackVoucherBo {
    /**  礼包配置内容
    /**主键 */
    private Integer id;
    /** 优惠券id */
    private Integer voucherId;
    /** 总数量 */
    private Integer totalAmount;
    /** 立即发放数量 */
    private Integer immediatelyGrantAmount;
    /** 每个时间单位间隔（1为无间隔） */
    private Integer timingEvery;
    /** 定时发放的时间单位，0：自然天，1：自然周，2自然月 */
    private Byte timingUnit;
    /** 定时发放的时间,周1-7，月1-31，自然天不填 */
    private Integer timingTime;
    /** 定时发放的数量 */
    private Integer timingAmount;

    /**  相对于totalAmount已经发放出去的优惠券数量 */
    private Integer grantCouponNumber;
    /**  最后一次发放的时间 */
    private Timestamp lastSendTime;
}

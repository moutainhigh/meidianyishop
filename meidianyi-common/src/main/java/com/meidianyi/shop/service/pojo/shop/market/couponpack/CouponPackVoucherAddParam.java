package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Data;

/**
 * @author: 王兵兵
 * @create: 2019-08-20 10:44
 **/
@Data
public class CouponPackVoucherAddParam {

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
}

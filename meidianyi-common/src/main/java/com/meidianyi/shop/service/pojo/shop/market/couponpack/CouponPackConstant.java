package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: 王兵兵
 * @create: 2020-03-04 15:17
 **/
@Getter
@Setter
public class CouponPackConstant {
    /**
     * 获取方式，0：现金购买
     */
    public static final byte ACCESS_MODE_CASH = 0;
    /**
     * 获取方式，1：积分购买
     */
    public static final byte ACCESS_MODE_SCORE = 1;
    /**
     * 获取方式，2直接领取
     */
    public static final byte ACCESS_MODE_FREE = 2;

    /**  定时发放的时间单位，0：自然天，1：自然周，2自然月 */
    public static final byte TIMING_UNIT_DAY = 0;
    public static final byte TIMING_UNIT_WEEK = 1;
    public static final byte TIMING_UNIT_MONTH = 2;

    /**  优惠券礼包订单发放状态flag,（退款后是否继续发放优惠劵），1：继续发放，0：停止发放，2：已经发放完成 */
    public static final byte STILL_SEND_FLAG_STOP = 0;
    public static final byte STILL_SEND_FLAG_CONTINUE = 1;
    public static final byte STILL_SEND_FLAG_FINISH = 2;
}

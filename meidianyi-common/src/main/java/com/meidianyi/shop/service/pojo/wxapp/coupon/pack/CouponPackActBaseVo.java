package com.meidianyi.shop.service.pojo.wxapp.coupon.pack;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-03-03 10:00
 **/
@Setter
@Getter
public class CouponPackActBaseVo {
    private Integer id;

    /** 礼包名称 */
    private String     packName;

    /** 单用户领取限制次数，0不限制 */
    private Integer    limitGetTimes;

    /** 总数量 */
    private Integer    totalAmount;

    /** 获取方式，0：现金购买，1：积分购买，2直接领取 */
    private Byte       accessMode;

    /** 价格（现金或积分，直接领取时该值为0） */
    private BigDecimal accessCost;

    /** 活动规则 */
    private String     actRule;

    private Timestamp startTime;
    private Timestamp endTime;
}

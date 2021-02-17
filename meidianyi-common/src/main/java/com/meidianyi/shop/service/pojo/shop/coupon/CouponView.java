package com.meidianyi.shop.service.pojo.shop.coupon;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-20 18:04
 **/
@Data
public class CouponView {
    /** 优惠券id */
    private Integer id;
    /** 优惠券类型，voucher指定金额券，discount折扣券 */
    private String actCode;
    /** 优惠券名称 */
    private String actName;
    /** 面额 */
    private BigDecimal denomination;
    /** 使用限制 0：无限制 1：有限制 */
    private Byte useConsumeRestrict;
    /** 满多少可用 */
    private BigDecimal leastConsume;
    /** 剩余数量 */
    private Integer surplus;
    /** 优惠券有效期类型标记，1领取后开始指定时间段内有效，0固定时间段有效 */
    private Byte validityType;
    /** 优惠券优惠券有效天数 */
    private Integer validity;
    /** 优惠券有效小时数 */
    private Integer validityHour;
    /** 优惠券有效分钟数 */
    private Integer validityMinute;
    /** 优惠券有效期开始时间 */
    private Timestamp startTime;
    /** 优惠券有效期结束时间 */
    private Timestamp endTime;
    /** 支付有礼跳转链接 1:全部商品可用 2：指定商品可用 */
    private Byte recommendType;
    /** 指定商品可用的商品ID串，为空时为全部商品可用 */
    private String recommendGoodsId;
    /** 指定平台分类可用的平台分类ID串，为空时为全部平台分类可用 */
    private String recommendCatId;
    /** 指定商家分类可用的商家分类ID串，为空时为全部商品商家分类可用 */
    private String recommendSortId;
    /**
     * 分裂优惠卷随机金额最低
     */
    private BigDecimal randomMin;
    /**
     * 分裂优惠卷随机金额最高
     */
    private BigDecimal randomMax;
    /**
     * 0:全店通用,1:指定店铺
     */
    private Byte suitGoods;
	/** 优惠券状态 0：可正常领取；1：优惠券不存在；2：优惠券过期；3：优惠券停用；4：库存为0;5：没赋值 */
	private Byte status = 5;
}

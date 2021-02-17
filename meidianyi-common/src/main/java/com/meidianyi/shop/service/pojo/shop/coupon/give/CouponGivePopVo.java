package com.meidianyi.shop.service.pojo.shop.coupon.give;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 优惠券弹窗
 * @author liangchen
 * @date 2019年8月1日
 */
@Data
public class CouponGivePopVo {
	/** 优惠券id */
	private Integer id;
    /** 优惠券类型，voucher指定金额券，discount折扣券 */
    private String actCode;
	/** 优惠券名称 */
	private String actName;
	/** 面额 */
	private BigDecimal denomination;
	/** 使用限制 0：无限制 1：有限制 */
	private Integer useConsumeRestrict;
	/** 满多少可用 */
	private BigDecimal leastConsume;
	/** 剩余数量 */
	private Integer surplus;
	/**
	 * 优惠卷类型 0普通 1分裂
	 */
	private Integer type;
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
    /** 指定商品可用的商品ID串，为空时为全部商品可用 */
    private String recommendGoodsId;
    /** 指定平台分类可用的平台分类ID串，为空时为全部平台分类可用 */
    private String recommendCatId;
    /** 指定商家分类可用的商家分类ID串，为空时为全部商品商家分类可用 */
    private String recommendSortId;
    /** 是否可以积分兑换*/
    private Integer useScore;
    /** 需要积分数*/
    private Integer scoreNumber;
    /** 是否限制库存：0限制，1不限制*/
    private Integer limitSurplusFlag;
	/**
	 * 分裂优惠卷随机金额最低
	 */
	private BigDecimal randomMin;
	/**
	 * 分裂优惠卷随机金额最高
	 */
	private BigDecimal randomMax;
	
	/** 优惠券状态 0：可正常领取；1：优惠券不存在；2：优惠券过期；3：优惠券停用；4：库存为0;5：没赋值 */
	private Byte status = 5;

}

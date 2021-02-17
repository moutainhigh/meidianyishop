package com.meidianyi.shop.service.pojo.wxapp.coupon;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 用户优惠券列表出参
 * @author 常乐
 * 2019年9月25日
 */
@Data
public class AvailCouponVo {
	/**
	 * 记录ID
	 */
	public Integer id;
    /**
     * 优惠券活动ID
     */
    public Integer actId;
    /**
     * random 分裂优惠券随机金额
     */
	private String actCode;
	/**
	 * 优惠券名称
	 */
	public String actName;
	/**
	 * 优惠券码
	 */
	public String couponSn;
	/**
	 * 有效期开始时间
	 */
	public Timestamp startTime;
	/**
	 * 有效期结束时间
	 */
	public Timestamp endTime;
	/**
	 * 优惠类型 0:减价;1打折
	 */
	public Integer type;
	/**
	 * 打折或减价量
	 */
	public BigDecimal amount;
	/**
	 * 使用条件
	 */
	public BigDecimal limitOrderAmount;
	/**
	 * 是否已使用 0 未使用 1 已使用 2过期吧 3 废除
	 */
	public Integer isUsed;

    private long remainDays;
    private long remainHours;
    private long remainMinutes;
    private long remainSeconds;
    /**
     * 持有会员卡id
     */
    private String     cardId;
    /**
     * 指定商品可用
     */
    private String     recommendGoodsId;
    /**
     * 指定品牌可用
     */
    private String     recommendCatId;
    /**
     * 指定商家分类可用
     */
    private String     recommendSortId;
    /**
     * 优惠券类型；0：普通优惠券；1：分裂优惠券
     */
    private Integer couponType;
    /**
     * 发放情况 1：发放人；0"被发放
     */
    private Integer isGrant;
    /**
     * 是否分享
     */
    private Byte isShare;
    /**
     * 是否可以分享 0：不可以分享；1：可以分享
     */
    private Integer canShare;
    /**
     * 分裂优惠券可领券人数
     */
    private Integer receiveNum;
    /**
     * 分裂优惠券领券人数是否限制 0不限制 1限制
     */
    private Byte receivePerNum;
    /**
     * 随机金额最大金额
     */
    private BigDecimal randomMax;
    /**
     * 是否可用,0可用 1不可用(只适用分裂优惠券)
     */
    private Integer divisionEnabled;
    /**
     * 是否与限时降价、首单特惠、会员价活动共用 0共用 1不共用
     */
    private Byte couponOverlay;
}

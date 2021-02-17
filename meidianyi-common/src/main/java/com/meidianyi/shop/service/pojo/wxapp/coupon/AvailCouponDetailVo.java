package com.meidianyi.shop.service.pojo.wxapp.coupon;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 优惠券详情出参类
 * @author 常乐
 * 2019年9月27日
 */
@Data
public class AvailCouponDetailVo {
	/**
	 * 记录ID
	 */
	public Integer id;
    /**
     * 优惠券活动ID
     */
    public Integer actId;
    /**
     * 优惠券编码
     */
	public String couponSn;
    /**
     * 优惠券类型；0：普通优惠券；1：分裂优惠券
     */
    public Integer couponType;
	/**
	 * 优惠券名称
	 */
	public String actName;
	/**
	 * 优惠券码
	 */
	public Timestamp startTime;
    /**
     * 优惠类型 discount:打折；voucher：优惠减价;random:分裂优惠券随机金额
     */
	public String actCode;
    /**
     * 面额/打折
     */
	public BigDecimal denomination;
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
    /**
     * 适用商品
     */
	public String recommendGoodsId;
    /**
     * 适用分类
     */
    public String recommendSortId;
    /**
     * 适用品牌
     */
    public String recommendCatId;
    /**
     * 是否积分兑换
     */
    public Byte useScore;
    /**
     * 需要积分数
     */
    public Integer scoreNumber;
    /**
     * 有效期类型
     */
    private Byte validityType;
    /**有效天数*/
    private Integer validity;
    /**有效小时*/
    private Integer validityHour;
    /**有效分钟*/
    private Integer validityMinute;

    /**是否存在使用门槛 0否 1是*/
    private Byte useConsumeRestrict;
    /**
     * 满多少可用
     */
    private BigDecimal leastConsume;
    /**
     * 用户可用积分
     */
    private Integer canUseScore;
    private long remainDays;
    private long remainHours;
    private long remainMinutes;
    private long remainSeconds;
    private String useExplain;
    private String validationCode;
    private String cardId;
    private Integer cardStatus;
    private String needGetCard;
    /**
     * 来源：0：优惠券列表；1：装修常用链接
     */
    private Integer linkSource = 0;
    /**
     * 是否可以领取 0：否；1：是
     */
    private Integer canReceive = 0;

    /**
     * 分裂优惠券可领券人数
     */
    private Integer receiveNum;
    /**
     * 分裂优惠券领券人数是否限制 0不限制 1限制
     */
    private Byte receivePerNum;
    /**
     * 是否可以分享 0：不可以分享；1：可以分享
     */
    private Integer canShare;
    /**
     * 是否分享
     */
    private Byte isShare;
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
    /**获取方式，0：发放，1：领取，2：优惠券礼包自动发放*/
    private Byte accessMode;
    /**领取时间*/
    private Timestamp createTime;
    /**活动ID*/
    private Integer accessId;
}


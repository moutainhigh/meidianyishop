package com.meidianyi.shop.service.pojo.shop.coupon;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 出参
 * @author zhaojianqiang
 * @date 2020年5月14日上午11:03:15
 */
@Data
public class CouponParamVo {

    private Integer    id;
    /**
     * 优惠类型 voucher：指定金额  discount：打折
     */
    private String     actCode;
    /**
     * 优惠券名称
     */
    private String     actName;
    /**
     * 生效时间
     */
    private Timestamp  startTime;
    /**
     * 失效时间
     */
    private Timestamp  endTime;
    /**
     * 面值
     */
    private BigDecimal denomination;
    /**
     * 总库存量
     */
    private Integer    totalAmount;
    /**
     *优惠券类型
     */
    private Byte       type;
    /**
     * 剩余库存
     */
    private Integer    surplus;
    /**
     * 使用门槛，0:无门槛；1:满金额使用
     */
    private Byte       useConsumeRestrict;
    /**
     * 满多少使用
     */
    private BigDecimal    leastConsume;
    /**
     * 使用说明
     */
    private String     useExplain;
    private Byte       enabled;

    /**
     * 每人限领张数 0:不限制
     */
    private Short      receivePerPerson;
    /**
     * 是否隐藏 0:不隐藏 1:隐藏
     */
    private Byte       suitGoods;
    /**
     * 是否与其他优惠券同时使用
     */
    private Byte       togetherUsed;
    /**
     * 是否允许分享优惠券链接
     */
    private Byte       permitShare;
    /**
     * 是否到期前提醒用户
     */
    private Byte       remindOwner;
    /**
     * 发放优惠券数量
     */
    private Short      giveoutAmount;
    /**
     * 发放优惠券人数
     */
    private Short      giveoutPerson;
    /**
     * 领取优惠券数量
     */
    private Short      receiveAmount;
    /**
     * 领取优惠券人数
     */
    private Short      receivePerson;
    /**
     * 已使用优惠券数量
     */
    private Short      usedAmount;
    /**
     * 唯一活动代码
     */
    private String     aliasCode;
    /**
     * 领取码
     */
    private String     validationCode;
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
     * 优惠券有效期类型
     */
    private Byte    validityType;
    /**
     * 优惠券有效天数
     */
    private Integer    validity;
    /**
     * 优惠券有效小时
     */
    private Integer validityHour;
    /**
     * 优惠券有效分钟数
     */
    private Integer validityMinute;
    private Byte       delFlag;
    private Byte       action;
    private String     identityId;
    /**
     * 关联商品规格
     */
    private String     recommendProductId;
    /**
     * 是否积分兑换 0：否；1:是
     */
    private Byte       useScore;
    /**
     * 需要积分
     */
    private Integer    scoreNumber;
    /**
     * 持有会员卡id
     */
    private String     cardId;
    /**
     * 是否限制库存 0:限制；1:不限制
     */
    private Byte limitSurplusFlag;
    private Timestamp  createTime;
    private Timestamp  updateTime;

    /**
     * 分裂优惠卷随机金额最低
     */
    private BigDecimal randomMin;
    /**
     * 分裂优惠卷随机金额最高
     */
    private BigDecimal randomMax;
    /**
     * 分裂优惠券领券人数是否限制 0不限制 1限制
     */
    private Byte receivePerNum;
    /**
     * 分裂优惠券可领券人数
     */
    private Integer receiveNum;
    /**
     * 是否领取优惠券用户打标签 0:否；1：是
     */
    private Byte couponTag;
    /**
     * 领取优惠券用户打标签id
     */
    private String couponTagId;
    /**
     * 是否与限时降价、首单特惠、会员价活动共用 0共用 1不共用
     */
    private Byte couponOverlay;
	/** 优惠券状态 0：可正常领取；1：优惠券不存在；2：优惠券过期；3：优惠券停用；4：库存为0;5：没赋值 */
	private Byte status = 5;
}

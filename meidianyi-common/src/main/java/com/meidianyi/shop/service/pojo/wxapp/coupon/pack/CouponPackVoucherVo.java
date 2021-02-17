package com.meidianyi.shop.service.pojo.wxapp.coupon.pack;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-03-02 16:24
 **/
@Getter
@Setter
public class CouponPackVoucherVo {
    //礼包配置内容
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

    //优惠券信息
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
    private Timestamp startTime;
    /**
     * 失效时间
     */
    private Timestamp  endTime;
    /**
     * 面值
     */
    private BigDecimal denomination;
    /**
     * 使用门槛，0:无门槛；1:满金额使用
     */
    private Byte       useConsumeRestrict;
    /**
     * 满多少使用
     */
    private BigDecimal    leastConsume;
    /**
     * 唯一活动代码
     */
    private String     aliasCode;
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
    /**
     * 关联商品规格
     */
    private String     recommendProductId;
    /**
     * 分裂优惠卷随机金额最高
     */
    private BigDecimal randomMax;
    /**
     *是否所有商品可用
     */
    private Boolean isAllGoodsUse;

    //实时发放信息
    /**已经发到手的该类型优惠券数量 */
    private Integer grantCount;
}

package com.meidianyi.shop.service.pojo.shop.member.account;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *	 会员卡的优惠券信息
 * 	@author 黄壮壮
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCardCoupon {

	/**
	 * 优惠券或优惠券礼包Id
	 */
	private Integer id;
	
	/**
	 * 优惠券或优惠券礼包名称
	 */
	private String name;
	
	
	/**
	 * 	优惠券使用条件范围描述
	 * 	全部商品 | 部分商品
	 */
	private String couponCondition;
	
	/**
	 * 0: 全部商品可用；1 部分商品可用
	 */
	private Byte suiteGoodsType;
	
	/**
	 * 优惠券使用时间类型
	 * 0: 领取之日起，1时间范围
	 */
	private Byte timeType;
	/**
	 * 	优惠券使用时间描述
	 */
	private String expireTime;
	private String desc;
	
	/**
	 * 	优惠券使用条件限制
	 *	如： 无门槛 | 满...使用
	 */
	private String useConsumeRestrictDesc;
	
	/**
	 * 优惠券使用条件限制类型
	 */
	private Byte consumeRestrictType;
	
	/**
	 * 消费最后满多少使用
	 */
	private BigDecimal leastConsume;
	
	/** 优惠券类型，voucher指定金额券，discount折扣券 */
    private String actCode;
    
    /** 面额 */
    private BigDecimal denomination;
	
}

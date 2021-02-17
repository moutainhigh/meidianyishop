package com.meidianyi.shop.service.pojo.shop.member;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 店铺积分配置文件
 * @author 黄壮壮
 * 2019-07-15 14:01
 */
@Data
public class ShopCfgParam {
	
	/**
	 * 0 永久积分
	 * 1 截至日期
	 * 2 多少时间范围
	 */
	private Byte scoreLimit;
	
	/**
	 * 1
	 * 截止日期
	 */
	private Integer scoreDay;
	private Integer scoreMonth;
	private Integer scoreYear;
	
	
	/**
	 * 2
	 * 多少时间范围内
	 */
	private Integer scoreLimitNumber;
	
	/**
	 * 多少时间内的单位，日1，周7，月30 
	 */
	private Integer scorePeriod;
	

	/**
	 * 积分支付限制类型
	 * 0 不限制
	 * 1 自定义
	 */
	private Byte scorePayLimit;
	
	
	/**
	 * 自定义积分数额
	 */
	private Integer scorePayNum;
	/**   积分抵扣比例 */
	private Integer scoreDiscountRatio;
	
	/**
	 * 购物送积分 开关，on
	 */
	private String shoppingScore;
	
	/**
	 * 0 购物满多少送积分
	 * 1 购物每满多少送多少积分
	 */
	private Byte scoreType;
	
	/**
	 * 购物满多少送多少
	 */
	private String[] buy;
	private String[] score;
	
	/**
	 * 购物每满多少送多少
	 */
	private String[] buyEach;
	private String[] scoreEach;
	
	/**
	 * 门店买单送积分开关 on
	 */
	private String storeScore;
	
	
	/**
	 * 登录送积分开关 on
	 */
	private String loginScore;
	/**
	 * 登录送积分，积分额。
	 */
	private Integer scoreLogin;
	
	/**
	 * 签到送积分开关 on
	 */
	private String signInScore;
	/**
	 * 签到积分
	 */
	@Size(max = 30)
	private String[] signScore;
	
	/**
	 * 模板页面id
	 */
	private Integer scorePageId;
	/**
	 * 积分兑换比
	 */
	@NotNull
	private Integer scoreProportion;
	
	/**
	 * 签到类型 0：连续签到；1：循环签到
	 */
	@NotNull
	private Byte signInRules;
	/**
	 * 订单折后金额是否包含运费
	 */
	@NotNull
	private Byte discountHasShipping;

	
}

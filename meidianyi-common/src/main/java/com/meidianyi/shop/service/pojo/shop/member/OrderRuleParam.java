package com.meidianyi.shop.service.pojo.shop.member;

import lombok.Data;

/**
 * 排序规则
 * @author 黄壮壮
 *
 */
@Data
public class OrderRuleParam {
	/**
	 * 按照积分排序
	 */
	public static final byte SCORE_RULE = 0;
	/**
	 * 注册时间排序
	 */
	public static final byte REGIST_TIME_RULE = 1;
	
	private byte rule;
	private boolean desc;
}

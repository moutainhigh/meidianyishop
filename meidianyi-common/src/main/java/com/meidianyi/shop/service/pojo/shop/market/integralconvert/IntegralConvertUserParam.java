package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import lombok.Data;

/**
 * 积分兑换分页查询列表
 * @author liangchen
 * @date 2019年8月14日
 */
@Data
public class IntegralConvertUserParam {
	/** 活动id */
	private Integer id;
	
	/** 用户昵称 */
	private String username;
	
	/** 手机号 */
	private String mobile;
	
	/** 分页信息 */
	private Integer currentPage;
	private Integer pageRows;
	
}

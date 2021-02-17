package com.meidianyi.shop.service.pojo.shop.member.account;
/**
* @author 黄壮壮
* @Date: 2019年8月13日
* @Description: 分页查询会员用户详细余额信息-入参
*/

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountPageListParam {
	/** 当前页 */
	private Integer currentPage;
	/** 每页显示条目数量 */
	private Integer pageRows;

	/** 会员id */
	private Integer userId;
	/** 会员名称 */
	private String userName;
	/** 订单号 */
	private String orderSn;
	
	/** 开始时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	
}

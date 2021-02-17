package com.meidianyi.shop.service.pojo.shop.member.score;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
* @author 黄壮壮
* @Date: 2019年8月13日
* @Description: 分页查询会员用户积分明细-入参
*/
@Getter
@Setter
public class ScorePageListParam {
	/** 当前页 */
	private Integer currentPage;
	/** 每页显示条目数量 */
	private Integer pageRows;

	/** 会员id */
	private Integer userId;
	/** 用户名称 */
	private String userName;
	/** 订单号 */
	private String orderSn;
	
	/** 开始时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	/**类型 */
	private String type;
	
}

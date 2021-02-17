package com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 *
 * @author 王帅
 *
 */
@Data
public class GroupOrderVo {

	private Integer id;
	/**
	 * 拼团成团人数
	 */
	private Integer groupBuyLimitAmout;
	private Integer activityId;
	private Integer goodsId;
	private Integer groupId;
	private Integer userId;
	private Byte isGrouper;
	private String orderSn;
	private Byte status;
	private Timestamp startTime;
	private Timestamp endTime;
}

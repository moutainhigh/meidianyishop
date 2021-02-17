package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 查询单个好友助力活动
 * @author liangchen
 * @date 2019年8月13日
 */
@Data
public class FriendPromoteSelectParam {
	
	/** 活动id */
	private Integer id;
	/** 开始时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
}

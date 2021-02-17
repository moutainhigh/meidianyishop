package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月8日
 * 团长的信息 
 */
@Data
@NoArgsConstructor
public class GroupperInfoPojo {
	/** 团长名称 */
	private String username;
	/** 团长手机号 */
	private String mobile;
	/** 开团时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	/** 活动 Id */
	private Integer inteActivityId;
	/** 成团状态 */
	private Byte status;
}


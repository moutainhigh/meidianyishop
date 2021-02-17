package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月7日
 * 成团明细视图
 */
@Data
@NoArgsConstructor
public class GroupIntegrationSuccessVo {
	/** 活动ID */
	private Integer actId;
	/** 团ID */
	private Integer groupId;
	/** 参团人数 */
	private Short participantNum;
	/** 消耗积分 */
	private Integer useIntegration;
	/** 开团时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	/** 团长昵称 */
	private String grouperName;
	/** 团长手机号 */
	private String mobile;
	/** 成团状态 */
	private Byte status;
	/** 成团条件 ：多少人拼团成功 */
	private Short limitAmount;
	
}


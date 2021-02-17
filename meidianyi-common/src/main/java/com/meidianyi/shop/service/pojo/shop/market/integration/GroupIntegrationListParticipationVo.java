package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月6日
 * 参团明细视图
 */
@Data
@NoArgsConstructor
public class GroupIntegrationListParticipationVo {
	private Integer id;
	private Integer userId;
	private String username;
	private String mobile;
	/** 是否是新用户 */
	private Byte isNew ;
	/** 参团时间 */
	private Timestamp startTime;
	/** 团ID */
	private Integer groupId;
	/** 邀请用户数量 */
	private Short inviteNum;
	/** 消耗积分 */
	private Integer integration;
	/** 是否是团长 1是 0否*/
	private Byte isGrouper;
	/** 该团可瓜分积分池 */
	private Integer canIntegration;
}


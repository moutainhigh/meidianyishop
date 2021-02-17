package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 字段全的类
 * 
 * @author zhaojianqiang
 * @time 下午2:14:54
 */
@Data
public class GroupIntegrationListPojo {
	private Integer id;
	private Integer inteActivityId;
	private Integer groupId;
	private Integer userId;
	private Byte isGrouper;
	private Byte status;
	private Timestamp startTime;
	private Timestamp endTime;
	private Integer integration;
	private Short inviteNum;
	private Integer inviteUser;
	private Byte isNew;
	private Byte isLook;
	private Integer canIntegration;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String startDate;
}

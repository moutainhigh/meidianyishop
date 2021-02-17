package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 小程序用的一个返回
 * 
 * @author zhaojianqiang
 * @time 下午2:14:54
 */
@Data
public class GroupIntegrationMaVo {
	private Integer id;
	private Integer inteActivityId;
	private Integer groupId;
	private Integer userId;
	private Byte isGrouper;
	private Byte status;
	private Timestamp startTime;
	private Timestamp endTime;
	private Integer integration;
	private Integer inviteNum;
	private Integer inviteUser;
	private Byte isNew;
	private Byte isLook;
	private Integer canIntegration;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String startDate;
	private String username;
	private String mobile;
	private String userAvatar;
	private Byte delFlag;
	private Short limitAmount;
	/** pin_integration_define的endTime*/
	private Timestamp groupEndTime;
	private Integer inteGroup;
	private Byte divideType;
}

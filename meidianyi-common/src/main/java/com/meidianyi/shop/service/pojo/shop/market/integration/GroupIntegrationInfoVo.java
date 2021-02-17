package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * getPinGroupByUser方法用的返回值
 * 
 * @author zhaojianqiang
 * @time 下午2:14:54
 */
@Data
public class GroupIntegrationInfoVo {
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
	private Integer pinInteId;
	private String name;
	private Short limitAmount;
	private Byte divideType;
	/** 0:拼团失败；1：进行中；2拼团成功 */
	private Byte state;
	private String msg;
	private Integer userNum;
	/**拼团成功 state为2时候返回*/
	private List<GroupIntegrationMaVo> pinInteGroupInfo;
}

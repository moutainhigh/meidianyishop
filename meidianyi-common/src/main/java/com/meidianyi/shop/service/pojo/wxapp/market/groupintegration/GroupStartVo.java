package com.meidianyi.shop.service.pojo.wxapp.market.groupintegration;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 小程序组团瓜分积分出参
 * 
 * @author zhaojianqiang
 * @time 下午4:21:49
 */
@Data
public class GroupStartVo {
	private Integer id;
	private Integer shopId;
	private String name;
	private Integer inteTotal;
	private Integer inteGroup;
	private Short limitAmount;
	private Short joinLimit;
	private Byte divideType;
	private Timestamp startTime;
	private Timestamp endTime;
	private Byte status;
	private Byte delFlag;
	private Timestamp delTime;
	private Integer inteRemain;
	private Byte isDayDivide;
	private Double paramN;
	private Byte isContinue;
	private String advertise;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String activityCopywriting;
	private Integer groupId;
	private CanPinInte canPin;
	private String inviteName;
	private Integer addInte;
	private Integer inviteUser;
}

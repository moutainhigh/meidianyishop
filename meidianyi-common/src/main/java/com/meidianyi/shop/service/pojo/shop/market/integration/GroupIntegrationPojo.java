package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 字段全的类
 * @author zhaojianqiang
 * @time   下午2:14:54
 */
@Data
public class GroupIntegrationPojo {
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

}

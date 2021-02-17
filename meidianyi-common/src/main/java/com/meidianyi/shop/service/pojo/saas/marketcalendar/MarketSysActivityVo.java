package com.meidianyi.shop.service.pojo.saas.marketcalendar;

import java.sql.Timestamp;

import lombok.Data;
/**
 * 活动字段全的返回类
 * @author zhaojianqiang
 * 2020年4月26日下午2:02:22
 */
@Data
public class MarketSysActivityVo {
	private Integer id;
	private Integer calendarId;
	private String activityType;
	private Integer shopUseNum;
	private Byte recommendType;
	private String recommendLink;
	private Byte delFlag;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String shopIds;
	private String recommendTitle;
}

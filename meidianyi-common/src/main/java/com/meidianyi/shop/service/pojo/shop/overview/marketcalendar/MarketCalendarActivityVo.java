package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;

import java.sql.Timestamp;

import lombok.Data;

/**
 * @author zhaojianqiang
 */
@Data
public class MarketCalendarActivityVo {
	private Integer id;
	private Integer calendarId;
	private Integer sysCalActId;
	private String activityType;
	private Integer activityId;
	private Byte recommendType;
	private String recommendLink;
	private Byte delFlag;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Byte isSync;
	private String recommendTitle;
}

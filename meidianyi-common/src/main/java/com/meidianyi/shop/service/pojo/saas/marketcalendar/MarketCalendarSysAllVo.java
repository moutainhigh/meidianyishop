package com.meidianyi.shop.service.pojo.saas.marketcalendar;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 字段比较全的
 * 
 * @author zhaojianqiang
 * @date 2020年4月26日下午3:04:21
 */
@Data
public class MarketCalendarSysAllVo {
	private Integer id;
	private String eventName;
	private Date eventTime;
	private String eventDesc;
	private Byte pubFlag;
	private Byte delFlag;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Integer calendarId;
	private List<SysCalendarActVo> actInfo;
}

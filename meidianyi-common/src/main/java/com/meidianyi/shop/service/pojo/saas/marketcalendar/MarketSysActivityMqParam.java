package com.meidianyi.shop.service.pojo.saas.marketcalendar;

import lombok.Data;

/**
 * 队列用
 * 
 * @author zhaojianqiang 2020年4月26日下午2:04:21
 */
@Data
public class MarketSysActivityMqParam {
	private Integer id;
	private Integer calendarId;
	private String activityType;
	private Integer shopUseNum;
	private Byte recommendType;
	private String recommendLink;
	private Byte delFlag;
}

package com.meidianyi.shop.service.pojo.saas.marketcalendar;

import java.sql.Date;

import lombok.Data;
/**
 * 
 * @author zhaojianqiang
 * @date 2020年4月26日下午3:04:21
 */
@Data
public class MarketCalendarSysVo {
	private   Integer id;
	private   String eventName;
	private   Date eventTime;
	private   String eventDesc;
	private   Byte pubFlag;
	private   Byte delFlag;
}

package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;
/**
 * 营销活动
 * @author zhaojianqiang
 * 2020年4月20日下午2:54:32
 */

import java.sql.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class MarketcalendarParam {
	@NotNull
	private String eventName;
	@NotNull
	private Date eventTime;
	private String eventDesc;
	@NotEmpty
	private List<CalendarAct> calendarAct;
	@NotNull
	private String act;
	/** 更新时用的market_calendar表的id*/
	private Integer calendarId;

}

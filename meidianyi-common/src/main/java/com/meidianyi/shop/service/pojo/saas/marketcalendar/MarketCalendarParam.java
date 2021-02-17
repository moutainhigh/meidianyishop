package com.meidianyi.shop.service.pojo.saas.marketcalendar;

import java.sql.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * system用新建编辑营销日历
 * @author zhaojianqiang
 * 2020年4月24日下午2:53:23
 */
@Data
public class MarketCalendarParam {
	@NotNull
	private String eventName;
	@NotNull
	private Date eventTime;
	private String eventDesc;
	@NotEmpty
	private List<SysCalendarAct> calendarAct;
	@NotNull
	private String act;
	/** 更新时用的market_calendar表的id*/
	private Integer calendarId;

}

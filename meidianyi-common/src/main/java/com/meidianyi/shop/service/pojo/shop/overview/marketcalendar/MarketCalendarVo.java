package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author zhaojianqiang
 */
@Data
public class MarketCalendarVo {
	private Integer id;
	private String eventName;
	private Date eventTime;
	private String eventDesc;
	private Byte pubFlag;
	private Byte delFlag;
	private Byte source;
	private Integer sourceId;
	private Timestamp createTime;
	private Timestamp updateTime;
	/**   1未开始，2进行中，3失效，4已结束 CalendarAction  */
	private Byte eventStatus;
	/**   还有多久开始 */
	private Integer downTime = 0;
}

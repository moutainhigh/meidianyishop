package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 所有营销活动的信息
 * 
 * @author zhaojianqiang 2020年4月21日下午4:26:39
 */
@Data
public class MarketVo {
	/** 营销活动的id，对应在相关活动的类里*/
	private Integer id;
	private String actName;
	private Timestamp startTime;
	private Timestamp endTime;
	/** 为1表示永久有效 */
	private Byte isPermanent = 0;

	private Byte actStatus = 3;
	/** 活动类型 */
	private String activityType;
	/** b2c_market_calendar_activity的id值*/
	private Integer calActId;
	
	private Byte recommendType;
	private String recommendLink;
	private Boolean isRecommend=false;
}

package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 * 2020年4月23日下午2:48:36
 */
@Data
public class MarketListParam {
	private List<Integer> activityIdList;
	private Integer calendarId;

}

package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang 2020年4月22日下午4:04:37
 */
@Data
public class MarketListData {
	private String month;
	private List<MarketCalendarVo> data;
}

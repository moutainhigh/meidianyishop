package com.meidianyi.shop.service.pojo.saas.marketcalendar;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 队列用
 * @author zhaojianqiang
 * 2020年4月26日下午1:57:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketMqParam {
	private MarketCalendarSysVo vo;
	private List<MarketSysActivityMqParam> list;
	
	/**
	 * 任务id
	 */
	private Integer taskJobId;
}

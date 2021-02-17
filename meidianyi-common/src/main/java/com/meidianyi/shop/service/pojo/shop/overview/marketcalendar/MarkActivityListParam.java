package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 查询可用的营销活动
 * 
 * @author zhaojianqiang 2020年4月23日下午3:31:47
 */
@Data
public class MarkActivityListParam extends MarketParam {
	@NotNull
	@NotEmpty
	private String activityType;
}

package com.meidianyi.shop.service.pojo.shop.overview.marketcalendar;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 营销活动
 * 
 * @author zhaojianqiang 2020年4月20日下午3:00:41
 */
@Data
public class CalendarAct {
	@NotNull
	private String activityType;
	@NotNull
	private Integer activityId = 0;

	/** 编辑时候传的id */
	private Integer calActId = 0;
	/** 编辑时候传的 是否已删除：0否，1是 */
	private Byte delFlag = 0;

}

package com.meidianyi.shop.service.pojo.saas.marketcalendar;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * system营销活动
 * 
 * @author zhaojianqiang 2020年4月24日下午3:11:51
 */
@Data
public class SysCalendarAct {
	@NotNull
	private String activityType;
	/** 0站内文本，1外部链接 */
	@NotNull
	private Byte recommendType = 0;
	/** 外部链接 或者存articleId */
	private String recommendLink;
	
	/** 编辑时候传的id */
	private Integer id = 0;
	/** 编辑时候传的 是否已删除：0否，1是 */
	private Byte delFlag = 0;
	
	
}

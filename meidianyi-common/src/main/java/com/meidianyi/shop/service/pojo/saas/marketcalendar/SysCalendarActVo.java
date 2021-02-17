package com.meidianyi.shop.service.pojo.saas.marketcalendar;

import lombok.Data;

/**
 * system营销活动
 * 
 * @author zhaojianqiang 2020年4月24日下午3:11:51
 */
@Data
public class SysCalendarActVo {
	private Integer id;
	private String activityType;
	/** 0站内文本，1外部链接 */
	private Integer recommendType = 0;
	/** 外部链接 或者存articleId */
	private String recommendLink;
	/** 有多少家使用 */
	private Integer shopNum=0;
	
	private String shopIds;
}

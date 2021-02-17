package com.meidianyi.shop.service.pojo.shop.overview.analysis;

import lombok.Data;

import java.util.List;

/**
 * @author liangchen
 * @date 2019年7月15日
 */
@Data
public class VisitTrendVo {
	/** 开始日期 */
	private String startTime;
	/** 结束日期 */
	private String endTime;
	/** 每日数据 */
	private List<VisitTrendDaily> dailyData;
}

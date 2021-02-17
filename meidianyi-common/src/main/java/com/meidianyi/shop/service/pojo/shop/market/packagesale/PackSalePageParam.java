package com.meidianyi.shop.service.pojo.shop.market.packagesale;

import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.service.pojo.shop.market.packagesale.PackSaleConstant.ActivityStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月12日
 */
@Data
@NoArgsConstructor
public class PackSalePageParam {
	/** 活动名称 */
	private String name;
	/** 开始时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	/** 活动状态 */
	private Byte activityStatus = ActivityStatus.ALL;
	/**
	 * 分页信息
	 */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;

    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}


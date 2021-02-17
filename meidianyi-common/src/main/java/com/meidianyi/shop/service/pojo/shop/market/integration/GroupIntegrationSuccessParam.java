package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月7日
 * 成团明细查询条件 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupIntegrationSuccessParam {
	/** 活动ID */
	@NotNull
	private Integer actId;
	/** 团ID查询条件 */
	private Integer groupId;
	/** 成团状态 查询条件：是、否*/
	private Byte status;
	/** 结束时间起始条件 */
	private Timestamp startTime;
	/** 结束时间终止条件 */
	private Timestamp endTime;
	
	/**
     * 	分页信息
     */
    private int currentPage = Page.DEFAULT_CURRENT_PAGE;
    private int pageRows = Page.DEFAULT_PAGE_ROWS;

}


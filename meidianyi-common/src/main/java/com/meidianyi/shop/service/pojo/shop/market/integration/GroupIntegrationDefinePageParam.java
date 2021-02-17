package com.meidianyi.shop.service.pojo.shop.market.integration;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * @author huangronggang
 * @date 2019年8月5日
 * 积分瓜分活动分页查询
 */
@Data
public class GroupIntegrationDefinePageParam {
	
	/** 平团活动类型 */
	private int type =GroupIntegrationDefineEnums.QueryType.ALL;
	
	/**
     * 	分页信息
     */
    private int currentPage = Page.DEFAULT_CURRENT_PAGE;
    private int pageRows = Page.DEFAULT_PAGE_ROWS;
}


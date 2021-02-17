package com.meidianyi.shop.service.pojo.shop.store.service;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月15日
 */
@Data
public class StoreServiceCategoryListQueryParam {
	/**
	 *  分类名称
	 */
	private String catName;
	
	@NotNull
	private Integer storeId;
	
	/**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}

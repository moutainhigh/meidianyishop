package com.meidianyi.shop.service.pojo.shop.market.bargain;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月26日
 */
@Data
public class BargainUserListQueryParam {

	@NotNull
	private Integer recordId;
	
	private String username;
	
	private String mobile;
	
	/**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}

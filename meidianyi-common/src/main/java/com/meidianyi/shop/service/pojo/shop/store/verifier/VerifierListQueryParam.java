package com.meidianyi.shop.service.pojo.shop.store.verifier;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 王兵兵
 *
 * 2019年7月11日
 */
@Data
@NoArgsConstructor
public class VerifierListQueryParam {
	@NotNull
	public Integer storeId;
	
	public String mobile;
	public String username;
	
	/**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}

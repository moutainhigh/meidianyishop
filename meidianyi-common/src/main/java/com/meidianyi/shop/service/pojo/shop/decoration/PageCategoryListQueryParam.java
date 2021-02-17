package com.meidianyi.shop.service.pojo.shop.decoration;

import lombok.Data;

/**
 * 
 * @author 新国
 *
 */
@Data
public class PageCategoryListQueryParam {
	public Integer currentPage;
	public Integer pageRows;
	public String keywords;
}
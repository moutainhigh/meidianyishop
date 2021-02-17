package com.meidianyi.shop.service.pojo.shop.decoration;

import lombok.Data;

/**
 * 
 * @author 新国
 *
 */
@Data
public  class PageListQueryParam {
	/**
	 * 需要删除页面ID
	 */
	public Integer del;

	/**
	 * 需要设置为首页的ID
	 */
	public Integer index;

	/**
	 * 页面ID
	 */
	public Integer pageId;

	public Integer catId;
	public Integer page;
	public String keywords;
	public String fistOpt;
};
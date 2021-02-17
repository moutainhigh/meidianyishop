package com.meidianyi.shop.service.pojo.saas.shop;

import lombok.Data;
/**
 * 
 * @author 新国
 *
 */
@Data
public class VersionListQueryParam {
	public Integer currentPage;
	public Integer pageRows;
	public String versionName;
}

package com.meidianyi.shop.service.pojo.saas.article.category;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author wangshuai
 *
 */
@Data
@NoArgsConstructor
public class ArtCategoryListQuertParam {
	public Integer pageRows;
	public Integer currentPage;
	public String keyWord;
}

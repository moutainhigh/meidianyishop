package com.meidianyi.shop.service.pojo.saas.article.category;

import lombok.Data;

/**
 * 文章状态更改
 * 
 * @author Jarome
 *
 */
@Data
public class ArticleStatusParam {
	/** ok:发布；cancel：未发布 */
	private String status;
	private Integer articleId;
}

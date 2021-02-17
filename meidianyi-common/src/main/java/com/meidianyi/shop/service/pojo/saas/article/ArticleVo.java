package com.meidianyi.shop.service.pojo.saas.article;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author wangshuai
 *
 */
@Data
@NoArgsConstructor
public class ArticleVo {
	private Integer articleId;
	private Integer categoryId;
	private String categoryName;
	private String title;
	private String author;
	private String keyword;
	private String desc;
	private String content;
	private Byte isRecommend;
	private Byte isTop;
	private Byte status;
	private String headPic;
	private Timestamp updateTime;
}

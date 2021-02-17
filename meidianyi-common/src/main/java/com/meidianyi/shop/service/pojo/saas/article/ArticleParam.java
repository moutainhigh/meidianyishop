package com.meidianyi.shop.service.pojo.saas.article;

import static com.meidianyi.shop.db.main.tables.Article.ARTICLE;

import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.db.main.tables.records.ArticleRecord;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author wangshuai
 *
 */
@Data
@NoArgsConstructor
public class ArticleParam {
	/**文章id*/
	private Integer articleId;
	/**文章分类*/
	private Integer categoryId;
	/**标题*/
	@NotBlank(message = JsonResultMessage.ARTICLE_TITLE_ISNULL)
	private String title;
	/**创建人*/
	private String author;
	/**关键词*/
	private String keyword;
	/**描述*/
	private String desc;
	/**正文*/
	private String content;
	/**推荐*/
	private Byte isRecommend;
	/**置顶*/
	private Byte isTop;
	/**发布状态*/
	private Byte status;
	/**头图*/
	private String headPic;
	
	public ArticleRecord generatedWrite(ArticleRecord record) {
		if(null != categoryId){
			record.set(ARTICLE.CATEGORY_ID, categoryId);
		}
		if(null != title){
			record.set(ARTICLE.TITLE, title);
		}
		if(null != keyword){
			record.set(ARTICLE.KEYWORD, keyword);
		}
		if(null != desc){
			record.set(ARTICLE.DESC, desc);
		}
		if(null != content){
			record.set(ARTICLE.CONTENT, content);
		}
		if(null != isRecommend){
			record.set(ARTICLE.IS_RECOMMEND, isRecommend);
		}
		if(null != isTop){
			record.set(ARTICLE.IS_TOP, isTop);
		}
		if(null != status){
			if(1 == status && record.getStatus() != 1) {
				record.set(ARTICLE.PUB_TIME, new Timestamp(System.currentTimeMillis()));
			}else if(status == 0){
				record.set(ARTICLE.PUB_TIME, null);
			}
			record.set(ARTICLE.STATUS, status);
		}
		if(null != headPic){
			record.set(ARTICLE.HEAD_PIC, headPic);
		}
		return record;
	}
}

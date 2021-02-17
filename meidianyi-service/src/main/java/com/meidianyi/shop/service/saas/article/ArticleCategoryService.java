package com.meidianyi.shop.service.saas.article;

import static com.meidianyi.shop.db.main.tables.Article.ARTICLE;
import static com.meidianyi.shop.db.main.tables.ArticleCategory.ARTICLE_CATEGORY;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectSeekStep1;
import org.jooq.SelectWhereStep;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.ArticleCategoryRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.article.category.ArtCategoryListQuertParam;
import com.meidianyi.shop.service.pojo.saas.article.category.ArticleCategoryParam;
import com.meidianyi.shop.service.pojo.saas.article.category.ArticleCategoryVo;
import com.meidianyi.shop.service.pojo.saas.article.category.ArticlesClass;

/**
 * 文章分类业务逻辑
 * @author 新国、wangshuai
 *
 */
@Service

public class ArticleCategoryService extends MainBaseService {
	
	/**
	 * 分页查询文章分类
	 * 
	 */
	public PageResult<ArticleCategoryVo> getPageList(ArtCategoryListQuertParam param) {
		SelectWhereStep<ArticleCategoryRecord> select = db().selectFrom(ARTICLE_CATEGORY);
		if(!StringUtils.isEmpty(param.getKeyWord())) {
			select.where(ARTICLE_CATEGORY.CATEGORY_NAME.like(likeValue(param.getKeyWord())));
		}
		select.orderBy(ARTICLE_CATEGORY.CATEGORY_ID.asc());	
		return getPageResult(select,param.getCurrentPage(),param.getPageRows(),ArticleCategoryVo.class);

	}
	
	
	/**
	 * 查询文章分类,不分类
	 * 
	 */
	public List<ArticleCategoryVo> getCategoryList() {
		SelectSeekStep1<Record2<Integer, String>, Integer> select = db().select(
				ARTICLE_CATEGORY.CATEGORY_ID,
				ARTICLE_CATEGORY.CATEGORY_NAME
				).from(ARTICLE_CATEGORY).orderBy(ARTICLE_CATEGORY.CATEGORY_ID.asc());
		Result<Record2<Integer, String>> fetch = select.fetch();
		List<ArticleCategoryVo> into =new ArrayList<ArticleCategoryVo>();
		if(fetch!=null) {
			 into = fetch.into(ArticleCategoryVo.class);
		}
		return into;
	}
	/**
	 * 文章类型新增
	 * @param arArticleCategory
	 * @return
	 */
	
	public boolean insertArticleCategory(ArticleCategoryParam arArticleCategory) {
		Byte useFooterNav = extracted(arArticleCategory);
		if(useFooterNav.equals(ArticlesClass.INVALID)) {
			logger().info("传入参数错误，传入参数为："+arArticleCategory.getUseFooterNav());
			return false;
		}
		int num = db().insertInto(ARTICLE_CATEGORY)
				.set(ARTICLE_CATEGORY.CATEGORY_NAME, arArticleCategory.getCategoryName())
				.set(ARTICLE_CATEGORY.USE_FOOTER_NAV, useFooterNav).execute();
		return num > 0 ? true : false;
	}


	private Byte extracted(ArticleCategoryParam arArticleCategory) {
		Byte useFooterNav=-1;
		if(StringUtils.isEmpty(arArticleCategory.getUseFooterNav())) {
			return -1;
		}
		String useFooterNav2 = arArticleCategory.getUseFooterNav();
		if(useFooterNav2.equals(ArticlesClass.OK)) {
			useFooterNav=1;
		}
		if(useFooterNav2.equals(ArticlesClass.CANCEL)) {
			useFooterNav=0;
		}
		return useFooterNav;
	}
	
	/**
	 * 删除文章分类
	 * @param arArticleCategory
	 * @return
	 */
	public boolean deleteArticleCategory(ArticleCategoryParam arArticleCategory) {
		int execute = db().deleteFrom(ARTICLE_CATEGORY)
				.where(ARTICLE_CATEGORY.CATEGORY_ID.eq(arArticleCategory.getCategoryId())
						.and(ARTICLE_CATEGORY.CATEGORY_NAME.eq(arArticleCategory.getCategoryName())))
				.execute();
		return execute > 0 ? true : false;

	}

	public boolean updateArticleCategory(ArticleCategoryParam arArticleCategory) {
		Byte useFooterNav = extracted(arArticleCategory);
		if(useFooterNav.equals(ArticlesClass.INVALID)) {
			logger().info("传入参数错误，传入参数为："+arArticleCategory.getUseFooterNav());
			return false;
		}
		int num = db().update(ARTICLE_CATEGORY).set(ARTICLE_CATEGORY.CATEGORY_NAME, arArticleCategory.getCategoryName())
				.set(ARTICLE_CATEGORY.USE_FOOTER_NAV, useFooterNav)
				.where(ARTICLE_CATEGORY.CATEGORY_ID.eq(arArticleCategory.getCategoryId())).execute();
		return  num > 0 ? true : false;
	}
	
	/**
	 * 查询分类下有多少文章,删除之前给提示用
	 * @param arArticleCategory
	 * @return
	 */
	public int findNumByCategory(ArticleCategoryParam arArticleCategory) {
		Result<Record1<Integer>> fetch = db()
				.select(ARTICLE.ARTICLE_ID).from(ARTICLE, ARTICLE_CATEGORY).where(
						ARTICLE.CATEGORY_ID.eq(ARTICLE_CATEGORY.CATEGORY_ID)
								.and(ARTICLE_CATEGORY.CATEGORY_ID.eq(arArticleCategory.getCategoryId())
										.and(ARTICLE_CATEGORY.CATEGORY_NAME.eq(arArticleCategory.getCategoryName()))))
				.fetch();
		int size = 0;
		if(fetch!=null) {
			size=fetch.size();
		}
		return size;
	}
	
	/**
	 * 判断该文章分类是否存在
	 * 
	 */
	public boolean isExist(String categoryName) {
		int num = db().fetchCount(ARTICLE_CATEGORY,ARTICLE_CATEGORY.CATEGORY_NAME.eq(categoryName));
		return num > 0 ? true : false;
	}
}

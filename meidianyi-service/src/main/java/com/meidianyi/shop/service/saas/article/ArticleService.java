package com.meidianyi.shop.service.saas.article;

import static com.meidianyi.shop.db.main.tables.Article.ARTICLE;
import static com.meidianyi.shop.db.main.tables.ArticleCategory.ARTICLE_CATEGORY;
import static com.meidianyi.shop.db.main.tables.ArticleRecord.ARTICLE_RECORD;

import java.sql.Timestamp;
import java.util.List;

import org.jooq.Record;
import org.jooq.Record13;
import org.jooq.SelectWhereStep;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.ArticleRecord;
import com.meidianyi.shop.db.main.tables.records.ArticleRecordRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.article.ArticleListQueryParam;
import com.meidianyi.shop.service.pojo.saas.article.ArticleParam;
import com.meidianyi.shop.service.pojo.saas.article.ArticleVo;
import com.meidianyi.shop.service.pojo.saas.article.category.ArticleStatusParam;
import com.meidianyi.shop.service.pojo.saas.article.category.ArticlesClass;
import com.meidianyi.shop.service.saas.image.SystemImageService;
import com.meidianyi.shop.service.saas.region.CityService;

/**
 * 
 * @author 新国
 *
 */
@Service

public class ArticleService extends MainBaseService {

	@Autowired
	public CityService city;
	@Autowired
	protected JedisManager jedis;

	@Autowired
	private SystemImageService imageService;

	/**
	 * 多条件查询文章
	 * 
	 * @param ArticleListQueryParam
	 * @return PageResult<ArticleOutPut>
	 */
	public PageResult<ArticleVo> getPageList(ArticleListQueryParam param) {
		SelectWhereStep<? extends Record> select = db()
				.select(ARTICLE.ARTICLE_ID, ARTICLE.TITLE, ARTICLE.DESC, ARTICLE.KEYWORD, ARTICLE.AUTHOR,
						ARTICLE.IS_RECOMMEND, ARTICLE.STATUS, ARTICLE.IS_TOP, ARTICLE.HEAD_PIC, ARTICLE.UPDATE_TIME,
						ARTICLE_CATEGORY.CATEGORY_NAME)
				.from(ARTICLE).leftJoin(ARTICLE_CATEGORY).on(ARTICLE.CATEGORY_ID.eq(ARTICLE_CATEGORY.CATEGORY_ID));
		select = this.buildOptions(select, param);
		PageResult<ArticleVo> pageResult = getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				ArticleVo.class);
		for (ArticleVo vo : pageResult.dataList) {
			if (!StringUtils.isEmpty(vo.getHeadPic())) {
				vo.setHeadPic(imageService.imageUrl(vo.getHeadPic()));
			}
		}
		return pageResult;

	}

	public SelectWhereStep<? extends Record> buildOptions(SelectWhereStep<? extends Record> select,
			ArticleListQueryParam param) {
		if (param == null) {
			select.orderBy(ARTICLE.ARTICLE_ID.desc());
			return select;
		}
		if (param.getCategoryId() != null && param.getCategoryId() != 0) {
			select.where(ARTICLE.CATEGORY_ID.eq(param.getCategoryId()));
		}
		if (param.getStatus() != null) {
			select.where(ARTICLE.STATUS.eq(param.getStatus()));
		}
		if (!StringUtils.isEmpty(param.getKeywords())) {
			select.where(ARTICLE.TITLE.like(likeValue(param.getKeywords()))
					.or(ARTICLE.DESC.like(likeValue(param.getKeywords()))));
		}
		if (!StringUtils.isEmpty(param.getSortName())) {
			String[] sortNames = param.getSortName().split(";");
			for (String sortName : sortNames) {
				String temp = sortName.substring(0, sortName.indexOf(","));
				if (sortName.endsWith("DESC") || sortName.endsWith("desc")) {
					select.orderBy(ARTICLE.field(temp).desc());
				} else {
					select.orderBy(ARTICLE.field(temp).asc());
				}
			}
		} else {
			select.orderBy(ARTICLE.ARTICLE_ID.desc());
		}
		return select;
	}

	public ArticleRecordRecord lastArticle(Integer sysId) {
		return db().selectFrom(ARTICLE_RECORD).where(ARTICLE_RECORD.SYS_ID.eq(sysId))
				.orderBy(ARTICLE_RECORD.ARTICLE_ID.desc()).fetchAny();
	}

	public List<Integer> getArticleIdRows(Integer sysId) {
		return db().selectFrom(ARTICLE_RECORD).where(ARTICLE_RECORD.SYS_ID.eq(sysId)).fetch(ARTICLE_RECORD.ARTICLE_ID);
	}

	/**
	 * 查找账号未读的最新一篇消息
	 * 
	 * @param sysId
	 * @return
	 */
	public Record noReadArticle(Integer sysId) {
		ArticleRecordRecord record = lastArticle(sysId);
		Integer articleId = record == null ? 0 : record.getArticleId().intValue();

		Byte publishStatus = 1;
		Integer noticeCategoryId = 1;
		return db().select().from(ARTICLE).where(ARTICLE.CATEGORY_ID.eq(noticeCategoryId))
				.and(ARTICLE.STATUS.eq(publishStatus)).and(ARTICLE.ARTICLE_ID.gt(articleId))
				.orderBy(ARTICLE.CREATE_TIME.desc()).fetchAny();
	}

	public boolean insertArticle(ArticleParam arArticle) {
		// 防止传id
		arArticle.setArticleId(null);
		ArticleRecord record = db().newRecord(ARTICLE);
		FieldsUtil.assignNotNull(arArticle, record);
		// 更新发布状态
		if (null != arArticle.getStatus()) {
			if (1 == arArticle.getStatus()) {
				record.set(ARTICLE.PUB_TIME, new Timestamp(System.currentTimeMillis()));
			} else if (arArticle.getStatus() == 0) {
				record.set(ARTICLE.PUB_TIME, null);
			}
			record.set(ARTICLE.STATUS, arArticle.getStatus());
		}
		int num = record.store();
		return num > 0 ? true : false;
	}

	public boolean deleteArticle(Integer articleId) {
		int num = db().delete(ARTICLE).where(ARTICLE.ARTICLE_ID.eq(articleId)).execute();
		return num > 0 ? true : false;
	}

	public boolean updateArticle(ArticleParam arArticle) {
		ArticleRecord record = db().fetchOne(ARTICLE, ARTICLE.ARTICLE_ID.eq(arArticle.getArticleId()));
		FieldsUtil.assignNotNull(arArticle, record);
		// 更新发布状态
		if (null != arArticle.getStatus()) {
			if (1 == arArticle.getStatus() && record.getStatus() == 0) {
				record.set(ARTICLE.PUB_TIME, new Timestamp(System.currentTimeMillis()));
			} else if (arArticle.getStatus() == 0) {
				record.set(ARTICLE.PUB_TIME, null);
			}
			record.set(ARTICLE.STATUS, arArticle.getStatus());
		}
		int num = record.store();
		return num > 0 ? true : false;
	}

	public ArticleVo get(Integer articleId) {
		Record13<Integer, Integer, String, String, String, String, String, Byte, Byte, String, String, Timestamp, Integer> article = db()
				.select(ARTICLE.ARTICLE_ID, ARTICLE.CATEGORY_ID, ARTICLE.TITLE, ARTICLE.AUTHOR, ARTICLE.KEYWORD,
						ARTICLE.DESC, ARTICLE.CONTENT, ARTICLE.IS_RECOMMEND, ARTICLE.STATUS, ARTICLE.HEAD_PIC,
						ARTICLE_CATEGORY.CATEGORY_NAME, ARTICLE.UPDATE_TIME, ARTICLE.PV)
				.from(ARTICLE).leftJoin(ARTICLE_CATEGORY).on(ARTICLE.CATEGORY_ID.eq(ARTICLE_CATEGORY.CATEGORY_ID))
				.where(ARTICLE.ARTICLE_ID.eq(articleId)).fetchOne();
		Integer integer = article.get(ARTICLE.PV);
		// 更新访问量
		db().update(ARTICLE).set(ARTICLE.PV, integer == null ? 0 : integer + 1);
		ArticleVo vo=article == null ? null : article.into(ArticleVo.class);
		if(vo!=null) {
			vo.setHeadPic(imageService.imageUrl(vo.getHeadPic()));
		}
		return vo;
	}

	/**
	 * 变更状态
	 * 
	 * @param param
	 * @return
	 */
	public boolean updateStatus(ArticleStatusParam param) {
		if (StringUtils.isEmpty(param.getStatus())) {
			return false;
		}
		String status = param.getStatus();
		ArticleRecord record = db().selectFrom(ARTICLE).where(ARTICLE.ARTICLE_ID.eq(param.getArticleId())).fetchAny();
		if (record == null) {
			return false;
		}
		int update = 0;
		if (status.equals(ArticlesClass.OK)) {
			// 状态为1
			record.setStatus(ArticlesClass.STATUS_OK);
			update = record.update();
		}
		if (status.equals(ArticlesClass.CANCEL)) {
			// 状态为1
			record.setStatus(ArticlesClass.STATUS_CANCEL);
			update = record.update();
		} else {
			return false;
		}
		return update == 0 ? false : true;
	}

}

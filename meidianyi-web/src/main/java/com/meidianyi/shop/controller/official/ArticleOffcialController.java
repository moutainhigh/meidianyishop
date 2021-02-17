package com.meidianyi.shop.controller.official;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.saas.article.ArticleListQueryParam;
import com.meidianyi.shop.service.pojo.saas.article.ArticleParam;
import com.meidianyi.shop.service.pojo.saas.article.ArticleVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author wangshuai
 *
 */
@RestController
@RequestMapping("/api/official/article")
public class ArticleOffcialController extends OfficialBaseController{

	/**
	 * 官网新闻资讯查询
	 * @param ArticleListQueryParam
	 * @returnJsonResult
	 */
	@PostMapping("/list")
    public JsonResult getList(@RequestBody ArticleListQueryParam param) {
		PageResult<ArticleVo> pageList = saas.article.getPageList(param);
		return success(pageList);
	}

    /**
	 * 官网新闻资讯文章具体查询
	 * @param ArticleParam
	 * @return JsonResult
	 */
	@PostMapping("/get")
    public JsonResult getDetail(@RequestBody ArticleParam article) {
		if(null == article.getArticleId()) {
			return fail(JsonResultCode.CODE_ARTICLE_ARTICLEID_ISNULL);
		}
		return success(saas.article.get(article.getArticleId()));
	}
}

package com.meidianyi.shop.controller.system;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.saas.article.ArticleListQueryParam;
import com.meidianyi.shop.service.pojo.saas.article.ArticleParam;
import com.meidianyi.shop.service.pojo.saas.article.ArticleVo;
import com.meidianyi.shop.service.pojo.saas.article.category.ArtCategoryListQuertParam;
import com.meidianyi.shop.service.pojo.saas.article.category.ArticleCategoryParam;
import com.meidianyi.shop.service.pojo.saas.article.category.ArticleCategoryVo;
import com.meidianyi.shop.service.pojo.saas.article.category.ArticleStatusParam;

/**
 * 文章与其分类控制器
 * @author wangshuai
 *
 */
@RestController
@RequestMapping("/api/system/article")
public class SystemArticleController extends SystemBaseController{
	@PostMapping("/list")
    public JsonResult getList(@RequestBody ArticleListQueryParam param) {
		PageResult<ArticleVo> pageList = saas.article.getPageList(param);
		return success(pageList);
	}
	/**
	 * 文章分类，分页查询
	 * @param param
	 * @return
	 */
	@PostMapping("/category/list")
	public JsonResult getCategory(@RequestBody ArtCategoryListQuertParam param) {
		PageResult<ArticleCategoryVo> pageList = saas.articleCategory.getPageList(param);
		return success(pageList);
	}
	
	/**
	 * 获取分类下对应文章数量
	 * @param arArticleCategory
	 * @return
	 */
	@PostMapping("/category/listNum")
	public JsonResult getNumCategory(@RequestBody ArticleCategoryParam arArticleCategory) {
		return success(saas.articleCategory.findNumByCategory(arArticleCategory));
	}
	/**
	 * 所有的文章分类
	 * @return
	 */
	@PostMapping("/category/allList")
	public JsonResult getCategory() {
		return success(saas.articleCategory.getCategoryList());
	}
	
	/**
	 * 删除文章分类
	 * @param input
	 * @return
	 */
	@PostMapping("/category/delete")
    public JsonResult deleteCategory(@RequestBody ArticleCategoryParam input) {
		if(null == input.getCategoryId()) {
			return fail(JsonResultCode.CODE_ARTICLE_CATEGORY_CATEGORYID_ISNULL);
		}
		return saas.articleCategory.deleteArticleCategory(input)?success():fail();
	}

	/**
	 * 添加文章分类
	 * @param input
	 * @return
	 */
	@PostMapping("/category/add")
    public JsonResult insertCategory(@RequestBody @Valid ArticleCategoryParam input) {
		if(saas.articleCategory.isExist(input.getCategoryName())) {
			return fail(JsonResultCode.CODE_ARTICLE_CATEGORY_IS_EXIST);
		}
        return saas.articleCategory.insertArticleCategory(input) ? success() : fail();
    }

	@PostMapping("/category/update")
    public JsonResult updateCategory(@RequestBody @Valid ArticleCategoryParam input) {
		if(null == input.getCategoryId()) {
			return fail(JsonResultCode.CODE_ARTICLE_CATEGORY_CATEGORYID_ISNULL);
		}
		return saas.articleCategory.updateArticleCategory(input)?success():fail();
	}

	@PostMapping("/delete")
    public JsonResult delete(@RequestBody ArticleParam article) {
		if(null == article.getArticleId()) {
			return fail(JsonResultCode.CODE_ARTICLE_ARTICLEID_ISNULL);
		}
		return saas.article.deleteArticle(article.getArticleId())?success():fail();
	}

	@PostMapping("/add")
    public JsonResult insert(@RequestBody @Valid ArticleParam article) {
		article.setAuthor(sysAuth.user().getSystemUserId().toString());
        return saas.article.insertArticle(article) ? success() : fail();
    }

	@PostMapping("/update")
    public JsonResult update(@RequestBody ArticleParam article) {
		if(null == article.getArticleId()) {
			return fail(JsonResultCode.CODE_ARTICLE_ARTICLEID_ISNULL);
		}
		return saas.article.updateArticle(article) ? success() : fail();
	}

    @PostMapping("/get")
    public JsonResult getDetail(@RequestBody ArticleParam article) {
		if(null == article.getArticleId()) {
			return fail(JsonResultCode.CODE_ARTICLE_ARTICLEID_ISNULL);
		}
		return success(saas.article.get(article.getArticleId()));
	}
    
    /**
     * 状态变更
     * @param param
     * @return
     */
    @PostMapping("/updateStatus")
    public JsonResult updateStatus(@RequestBody ArticleStatusParam param) {
		if(null == param.getArticleId()) {
			return fail(JsonResultCode.CODE_ARTICLE_ARTICLEID_ISNULL);
		}
		boolean updateStatus = saas.article.updateStatus(param);
		return updateStatus?success():fail();
	}
}

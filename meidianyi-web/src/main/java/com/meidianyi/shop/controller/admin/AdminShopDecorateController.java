package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.validator.ValidList;
import com.meidianyi.shop.db.shop.tables.records.XcxCustomerPageRecord;
import com.meidianyi.shop.service.pojo.shop.config.BottomNavigatorConfig;
import com.meidianyi.shop.service.pojo.shop.config.SearchConfig;
import com.meidianyi.shop.service.pojo.shop.config.ShopStyleConfig;
import com.meidianyi.shop.service.pojo.shop.config.SuspendWindowConfig;
import com.meidianyi.shop.service.pojo.shop.decoration.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * 装修模块
 *
 * @author 常乐
 * 2019年6月27日
 */
@RestController
@RequestMapping("/api")
public class AdminShopDecorateController extends AdminBaseController {

	/**
	 * 装修页面列表
	 *
	 * @param  param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/list")
	public JsonResult list(@RequestBody XcxCustomerPageVo param) {
		PageResult<XcxCustomerPageVo> list = shop().adminDecoration.getPageList(param);
		return success(list);
	}

	/**
	 * 新建装修页面
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/page/add")
	public JsonResult addPage(@RequestBody XcxCustomerPageVo param) {
		Integer res = shop().adminDecoration.addPage(param);
		return res > 0 ? this.success(res) : fail();
	}

	/**
	 * 装修页面详情
	 *
	 * @param  pageId
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/detail")
	public JsonResult pageDetail(Integer pageId) {
		XcxCustomerPageRecord detail = shop().adminDecoration.getPageById(pageId);
		return success(detail.intoMap());
	}

	/**
	 * 设为首页
	 *
	 * @param  param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/index/set")
	public JsonResult setIndex(@RequestBody PageIdParam param) {
		boolean res = shop().adminDecoration.setIndex(param);
		return success(res);
	}

	/**
	 * 编辑-获取页面数据
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/page/get")
	public JsonResult getPageInfo(@RequestBody @Validated PageIdParam param) {
		return success(shop().adminDecoration.getPageInfo(param));
	}

	/**
	 * 页面分类信息
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/cate/page")
	public JsonResult pageCate() {
		List<PageClassificationVo> pageCateList = shop().adminDecoration.getPageCate();
		return this.success(pageCateList);
	}

	/**
	 * 页面装修设置页面分类
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/cate/set")
	public JsonResult setPageCate(@RequestBody PageClassificationVo param) {
		int res = shop().adminDecoration.setPageCate(param);
		if(res > 0) {
			return this.success();
		}else {
			return this.fail();
		}
	}

	/**
	 * 批量设置页面分类
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/cate/batchSet")
	public JsonResult batchSetPageCate(@RequestBody BatchSetPageCateParam param) {
		boolean res = shop().adminDecoration.batchSetPageCate(param);
		return this.success(res);
	}

	/**
	 * 删除装修页面
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/page/del")
	public JsonResult delXcxPage(@RequestBody PageClassificationVo param) {
		int res = shop().adminDecoration.delXcxPage(param);
		if(res > 0) {
			return this.success();
		}else {
			return this.fail();
		}
	}

	/**
	 * 复制页面
	 *
	 * @param  param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/page/copy")
	public JsonResult copyDecoration(@RequestBody XcxCustomerPageVo param) {
		Boolean res = shop().adminDecoration.copyDecoration(param.getPageId());
		return success(res);
	}

	/**
	 * 分享装修页面获取小程序二维码
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/page/share")
	public JsonResult getPageShareCode(@RequestParam Integer pageId) {
		return success(shop().adminDecoration.getMpQrCode(pageId));
	}

    /**
     * 保存装修数据(包括更新)
     *
     * @param  param
     * @return
     */
    @PostMapping(value = "/admin/decorate/page/save")
    public JsonResult saveDecoration(@RequestBody @Valid PageStoreParam param) {
        int previewState = 2;
        // 新增系统公告
        shop().userMessageService.addAnnouncement(param);
        if(param.getPageState() == previewState){
            //预览，返回太阳码的图片链接
            return this.success(shop().adminDecoration.getPreviewCode(param));
        }else{
            //新建或更新
            int res = shop().adminDecoration.storePage(param);
            if(res > 0){
                return this.success(res);
            }else {
                return fail();
            }
        }
    }

	/**
	 * 设置店铺风格
	 *
	 * @param  config
	 * @return
	 */
	@PostMapping("/admin/decorate/style/update")
	public JsonResult updateShopStyle(@RequestBody @Valid ShopStyleConfig config) {
		shop().config.shopStyleCfg.setShopStyleConfig(config);
		return success();
	}

	/**
	 * 店铺风格查询
	 *
	 * @return
	 */
	@GetMapping("/admin/decorate/style/get")
	public JsonResult getShopStyle() {
		ShopStyleConfig config = shop().config.shopStyleCfg.getShopStyleConfig();
		return config != null ? success(config) :  fail(JsonResultCode.DECORATE_STYLE_ISNOTJSON);
	}

	/**
	 * 底部导航查询
	 *
	 * @return
	 */
	@GetMapping("/admin/bottom/get")
	public JsonResult getDecorateBottom() {
		List<BottomNavigatorConfig> cfg = shop().config.bottomCfg.getBottomNavigatorConfig();
		if (cfg != null) {
			return success(cfg);
		}
		return fail(JsonResultCode.DECORATE_BOTTOM_ISNOTJSON);
	}

	/**
	 * 底部导航设置
	 *
	 * @return
	 */
	@PostMapping("/admin/bottom/update")
	public JsonResult updateDecorateBottom(@RequestBody @Valid ValidList<BottomNavigatorConfig> bottomNavConfg) {
        int res = shop().config.bottomCfg.setBottomNavigatorConfig(bottomNavConfg);
        if (res > 0) {
            return success();
        } else {
            return fail();
        }
    }

    /**
     * 查询 搜索配置
     * @return
     */
    @GetMapping("/admin/get/searchcfg")
    public JsonResult getSearchCfg() {
    	SearchConfig searchConfig = shop().config.searchCfg.getSearchConfig();
    	if(null==searchConfig) {
    		searchConfig=new SearchConfig(1, 1, 0);
    	}
        return success(searchConfig);
    }
    /**
     * 修改搜索配置
     * @param
     * @return
     */
    @PostMapping("/admin/update/searchcfg")
    public JsonResult updateSearchCfg(@RequestBody SearchConfig config){
    	List<String> hotWords = config.getHotWords();
    	if(null!=hotWords) {
            int maxHotWordsNumber = 11;
            if(hotWords.size()> maxHotWordsNumber) {
    			return fail(JsonResultCode.SEARCHCFG_HOTWORDS_LIMIT);
    		}
    	}
        int customSearchAction = 3;
        if(config.getTitleAction().equals(customSearchAction)&&StringUtils.isEmpty(config.getTitleCustom())) {
    		//自定义，titleCustom不能为空
    		return fail(JsonResultCode.SEARCHCFG_TITLECUSTOM_NOTNULL);
    	}
        shop().config.searchCfg.setSearchConfig(config);
        return success();
    }


    /**
     * 全部页面模板
     * @return
     */
    @GetMapping(value = "/admin/decorate/templates")
    public JsonResult getTemplates() {
        return success(saas.shop.decoration.getAll());
    }

    /**
     * 模板
     *
     * @return
     */
    @GetMapping(value = "/admin/decorate/templates/get")
    public JsonResult getTemplateContent(Integer id) {
        return success(shop().adminDecoration.covertTemplate(id));
    }

    /**
     * 保存悬浮窗草稿设置
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/admin/decorate/suspend/draft/set")
    public JsonResult setSuspendWindowConfigDraft(@RequestBody SuspendWindowConfig param) {
        if (shop().adminDecoration.setSuspendWindowConfigDraft(param)) {
            return this.success();
        }
        return fail();
    }

    /**
     * 保存悬浮窗设置
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/admin/decorate/suspend/set")
    public JsonResult setSuspendWindowConfig(@RequestBody SuspendWindowConfig param) {
        if (shop().adminDecoration.setSuspendWindowConfig(param)) {
            return this.success();
        }
        return fail();
    }

    /**
     * 获取悬浮窗草稿设置
     *
     * @return
     */
    @PostMapping(value = "/admin/decorate/suspend/draft/get")
    public JsonResult getSuspendWindowConfigDraft() {
        return this.success(shop().adminDecoration.getSuspendWindowConfigDraft());
    }
}

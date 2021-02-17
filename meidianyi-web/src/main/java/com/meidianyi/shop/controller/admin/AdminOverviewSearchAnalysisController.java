package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.overview.hotwords.*;
import com.meidianyi.shop.service.pojo.shop.overview.searchanalysis.SearchHistoryParam;
import com.meidianyi.shop.service.pojo.shop.overview.searchanalysis.SearchHistoryVo;
import com.meidianyi.shop.service.pojo.shop.overview.searchanalysis.SearchHotWordsVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 搜索统计控制器
 * 
 * @author liangchen
 * @date  2019年7月15日
 */

@RestController
@RequestMapping("/api/admin/overview/search/analysis")
public class AdminOverviewSearchAnalysisController extends AdminBaseController{
	/**
	 *搜索历史统计
	 *@param param 查询时间段
	 *@return 10条搜索历史统计信息
	 */
	@PostMapping("/history")
	public JsonResult getSearchHistory(@RequestBody SearchHistoryParam param) {

		SearchHistoryVo searchHistoryVos = shop().overview.searchAnalysisService.getSearchHistory(param);
		
		return success(searchHistoryVos);
	}

	/**
	 *搜索热词统计
	 *@param param 查询时间段
	 *@return 10条搜索热词统计信息
	 */
	@PostMapping("/hot")
	public JsonResult getHotSearchHistory(@RequestBody SearchHistoryParam param) {
		SearchHotWordsVo searchHotWordsVos = shop().overview.searchAnalysisService.getHotSearchHistory(param);
		
		return success(searchHotWordsVos);
	}

}

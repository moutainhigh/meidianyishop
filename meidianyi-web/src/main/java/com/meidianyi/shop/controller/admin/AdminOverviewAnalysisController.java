package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.overview.analysis.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 概况统计控制器
 * 
 * @author liangchen
 * @date  2019年7月15日
 */

@RestController
@RequestMapping("/api/admin/overview/analysis")
public class AdminOverviewAnalysisController extends AdminBaseController{
	
	/**
	 * 昨日概况统计
	 * @return 基础信息和变化率
	 */
	@GetMapping("/yesterday")
	public JsonResult yesterdayAnalysis() {
		
		List<YesterdayStatisticsVo> yesterdayStatisticsVos = shop().overview.overviewAnalysisService.yesterdayAnalysis();
		
		return success(yesterdayStatisticsVos);
	}
	
	/**
	 *折线图综合查询
	 *@param param 种类与日期
	 *@return 折线图数据
	 */
	@PostMapping("/select")
	public JsonResult getSelect(@RequestBody @Validated VisitTrendParam param) {

		VisitTrendVo visitTrendVo = shop().overview.overviewAnalysisService.getVisitTrend(param);
		
		return success(visitTrendVo);
	}

	/**
	 *页面访问数量查询
	 *@param param 种类与日期
	 *@return 统计数据
	 */
	@PostMapping("/pagelist")
	public JsonResult getPageInfo(@RequestBody @Validated PageStatisticsParam param) {

		PageStatisticsVo pageStatisticsVo = shop().overview.overviewAnalysisService.getPageInfo(param);
		
		return i18nSuccess(pageStatisticsVo);
	}
}

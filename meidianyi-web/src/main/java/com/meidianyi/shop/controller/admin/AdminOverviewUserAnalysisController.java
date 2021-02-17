package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.overview.useranalysis.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户统计控制器
 * 
 * @author liangchen
 * @date 2019年7月18日
 */
@RestController
@RequestMapping("/api/admin/overview/user/analysis")
public class AdminOverviewUserAnalysisController extends AdminBaseController {

	/**
	 * 客户概况及趋势
	 * 
	 * @param  param 查看最近N天的数据(默认N=1) 1：一天，7：一周，30：一个月
	 * @return 相关数量及趋势
	 */
	@PostMapping("/trend")
	public JsonResult getTrend(@RequestBody DateParam param) {
	    //得到客户概况和变化趋势
		TrendVo result = shop().overview.userAnalysisService.getTrend(param);
        //返回计算结果
		return success(result);

	}

	/**
	 * 用户活跃
	 * 
	 * @param param 查看最近N天的数据(默认N=1) 1：一天，7：一周，30：一个月
	 * @return 不同类型用户数据
	 */
	@PostMapping("/active")
	public JsonResult getActive(@RequestBody DateParam param) {
        //得到用户活跃情况
	    ActiveTotalVo result = shop().overview.userAnalysisService
				.getActive(param);
        //返回计算结果
		return success(result);

	}

	/**
	 * 会员统计
	 * 
	 * @Param param 查看最近N天的数据(默认N=1) 1：一天，7：一周，30：一个月
	 * @return 会员数变化率
	 */
	@PostMapping("/vip")
	public JsonResult getVip(@RequestBody DateParam param) {
        //得到会员统计情况
		VipVo result = shop().overview.userAnalysisService
				.getVip(param);

		return success(result);

	}

	/**
	 * 成交用户分析
	 * 
	 * @Param param 查看最近N天的数据(默认N=1) 1：一天，7：一周，30：一个月
	 * @return 成交用户变化率占比等
	 */
	@PostMapping("/order")
	public JsonResult getOrder(@RequestBody DateParam param) {
        //得到成交用户情况
		OrderVo result = shop().overview.userAnalysisService
				.getOrder(param);
		return success(result);
	}

	/**
	 * 客户复购趋势
	 * 
	 * @Param param
	 * @return
	 */
	@PostMapping("/rebuy")
	public JsonResult getRebuyTrend(@RequestBody RebuyParam param) {
		RebuyVo result = shop().overview.userAnalysisService
				.getRebuyTrend(param);
		return success(result);
	}

    /**
     * RFM模型分析
     *
     * @Param param 日期
     * @return 数据
     */
    @PostMapping("/rfm")
    public JsonResult getRfmAnalysis(@RequestBody RfmParam param) {
     //判断指定日期有无数据
     Boolean data = shop().overview.userAnalysisService.getRFMData(param);
     if (!data){
         return fail(JsonResultMessage.OVERVIEW_USER_ANALYSIS_RFM_NULL);
     }
     //得到RFM数据
     List<RfmVo> rfmVoList = shop().overview.userAnalysisService.getRFMAnalysis(param);
     return success(rfmVoList);
    }
}

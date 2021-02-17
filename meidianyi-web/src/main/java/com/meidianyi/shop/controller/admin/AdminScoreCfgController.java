package com.meidianyi.shop.controller.admin;


import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.service.pojo.shop.member.score.ScoreCfgVo;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreFrontParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreFrontVo;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.member.ShopCfgParam;

/**
 * 积分管理controller
 * @author 黄壮壮
 * 2019-07-15 14:21
 */
@RestController
@RequestMapping(value="/api/admin/user/score")
public class AdminScoreCfgController extends AdminBaseController {

	@PostMapping(value="/get")
	public JsonResult getScoreConfig() {
		logger().info("获取积分配置");
		ScoreCfgVo vo = shop().score.getShopScoreCfg();
		return this.success(vo);
	}
	
	/**
	 * 更新积分配置
	 * @param param
	 * @return
	 */
	@PostMapping(value="/update")
	public JsonResult userScoreConfig(@RequestBody @Valid ShopCfgParam param) {
		
		//判断积分有效期类型
		int r = shop().score.setShopCfg(param);
		if(r == -1) {
			return this.fail();
		}
		return this.success();
	}
	
	/**
	 * 前端积分说明保存
	 * @param param
	 * @return
	 */
	@PostMapping(value="/copywriting/save")
	public JsonResult saveScoreDocument(@RequestBody ScoreFrontParam param) {
		shop().score.saveScoreDocument(param);
		return success();
	}
	
	/**
	 * 前端积分说明获取
	 */
	@PostMapping(value="/copywriting")
	public JsonResult scoreCopywriting() {
		ScoreFrontVo vo = shop().score.scoreCopywriting();
		return i18nSuccess(vo);
	}
	
	/**
	 * 积分前端页展示模板id
	 * @param scorePageId
	 * @return
	 */
	@PostMapping(value="/add")
	public JsonResult addScore(@RequestBody ShopCfgParam param) {
		shop().score.addScoreCfgForDecoration(param);
		return success();
	}
	
}

package com.meidianyi.shop.controller.system;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.saas.offical.FreeExperienceInfo;
import com.meidianyi.shop.service.pojo.saas.offical.FreeExperiencePageListParam;
import com.meidianyi.shop.service.pojo.saas.offical.ShopFreeExperienceOutPut;

/**
 * 处理system中用户申请试用列表
 * @author 黄壮壮
 * 2019-06-26 17:27
 */

@RestController
public class SystemFreeExperienceController extends SystemBaseController{

	/**
	 * 获取用户申请试用列表
	 * @param param
	 * @return
	 */
	@PostMapping("/api/system/experience/list")
	public JsonResult getFreeExperienceList(@RequestBody FreeExperiencePageListParam param) {
		PageResult<ShopFreeExperienceOutPut> pageResult = this.saas.official.freeExperienceService.getPageList(param);
		return this.success(pageResult);
	}
	
	
	/**
	 * 
	 * @param freeExperienceInfo
	 * @return
	 */
	@PostMapping("/api/system/experience/info/update")
	public JsonResult setFreeExperienceInfo(
			 @RequestBody FreeExperienceInfo freeExperienceInfo) {

		int result = this.saas.official.freeExperienceService.updateFreeExperience(freeExperienceInfo);
	
		if(result == 0) {
			return this.fail();
		}else {
			return this.success();
		} 
	}
}

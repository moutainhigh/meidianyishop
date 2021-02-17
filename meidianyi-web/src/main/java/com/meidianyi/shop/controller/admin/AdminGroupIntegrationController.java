package com.meidianyi.shop.controller.admin;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.integration.ChangeStatusParam;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationAnalysisVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationDefinePageParam;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationDefineParam;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListDetailParam;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListParticipationVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationSuccessParam;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationSuccessVo;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationVo;

/**
 * @author huangronggang  zhaojianqiang
 * @date 2019年8月5日  2020年2月24日
 */
@RestController
@RequestMapping("/api/admin/market/integration")
public class AdminGroupIntegrationController extends AdminBaseController {
    /**
     * 瓜分积分下拉框
     * @return id name
     */
    @GetMapping("/selectlist")
    public JsonResult getSelectList(){
        return success(shop().groupIntegration.getActSelectList());
    }
	/**
	 * 分页查询瓜分积分活动
	 * @param param
	 * @return
	 */
	@PostMapping("/list")
	public JsonResult getPageList(@RequestBody GroupIntegrationDefinePageParam param) {
		return success(shop().groupIntegration.getPageList(param));
	}
	/**
	 * 添加瓜分积分活动
	 * @param param
	 * @return
	 */
	@PostMapping("/add")
	public JsonResult insert(@RequestBody @Valid GroupIntegrationDefineParam param) {
		JsonResultCode insertDefine = shop().groupIntegration.insertDefine(param);
		if(insertDefine==JsonResultCode.CODE_SUCCESS) {
			return success();			
		}
		return fail(insertDefine);
	}
	/**
	 * 查指定ID的瓜分积分活动
	 * @param id
	 * @return
	 */
	@GetMapping("/select/{id}")
	public JsonResult select(@PathVariable Integer id) {
		GroupIntegrationVo defineVo = shop().groupIntegration.selectGroupIntegrationDefineById(id);
		return success(defineVo);
	}
	/**
	 * 删除指定iD 的瓜分积分活动
	 * @param id
	 * @return
	 */
	@PostMapping("/delete/{id}")
	public JsonResult delete(@PathVariable Integer id) {
		int delete = shop().groupIntegration.deleteDefine(id);
		if(delete>0) {
			return success();
		}
		return fail();
	}
	/**
	 * 更新指定ID的瓜分积分活动
	 * @param param
	 * @return
	 */
	@PostMapping("/update")
	public JsonResult update(@RequestBody @Valid GroupIntegrationDefineParam param) {
		if(param.getId() == null) {
			return fail();
		}
		int result = shop().groupIntegration.updateDefine(param);
		if(result>0) {
			return success();
		}
		return fail();
	}
	/**
	 * 停用或启用活动
	 * @param param
	 * @return
	 */
	@PostMapping("/change/status")
	public JsonResult changeStatus(@RequestBody ChangeStatusParam param) {
		int result = shop().groupIntegration.changDefineStatus(param.getId(), param.getStatus());
		if(result>0) {
			return success();
		}else {
			return fail();
		}
	}
	/**
	 * 参团明细
	 * @param param
	 * @return
	 */
	@PostMapping("/detail")
	public JsonResult getDetailPageList(@RequestBody @Valid GroupIntegrationListDetailParam param) {
		PageResult<GroupIntegrationListParticipationVo> result = shop().groupIntegration.groupIntegrationList.getDetailPageList(param);
		return success(result);
	}
	/**
	 * 成团明细
	 * @param param
	 * @return
	 */
	@PostMapping("/success")
	public JsonResult getSuccessPageList(@RequestBody @Valid GroupIntegrationSuccessParam param) {
		PageResult<GroupIntegrationSuccessVo> result = shop().groupIntegration.groupIntegrationList.getSuccessPageList(param);
		return success(result);
	}
	/**
	 * 获取分享活动的小程序码
	 * @param actId
	 * @return
	 */
	@PostMapping("/getqrcode/{actId}")
	public JsonResult getQrcode(@PathVariable Integer actId) {
		GroupIntegrationShareQrCodeVo maQrCode = shop().groupIntegration.getMaQrCode(actId,null,null);
		if(maQrCode != null) {
			return success(maQrCode);
		}
		return fail();
	}
	
	/**
	 * 获得活动效果数据
	 * @param actId
	 * @return
	 */
	@PostMapping("/getAnalysis")
	public JsonResult getAnalysis(@RequestBody @Valid  GroupIntegrationAnalysisParam param) {
		GroupIntegrationAnalysisVo analysis = shop().groupIntegration.getAnalysis(param);
		return success(analysis);
	}
	
}


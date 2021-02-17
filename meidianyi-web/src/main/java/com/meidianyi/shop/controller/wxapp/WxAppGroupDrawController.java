package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoByOsParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoByOsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawReturn;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 拼团抽奖
 * 
 * @author zhaojianqiang
 * @time 下午2:13:53
 */
@RestController
public class WxAppGroupDrawController extends WxAppBaseController {

	/**
	 * 拼团抽奖活动列表页
	 * 
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/groupdraw/list")
	public JsonResult groupDrawList(@RequestBody GroupDrawParam param) {
	    param.initScene();
		GroupDrawVo vo = shop().groupDraw.groupDrawList(param.getGroupDrawId());
		if (vo == null) {
			// 活动不存在或没有可参与的活动商品
			return fail(JsonResultCode.GROUP_DRAW_FAIL);
		}
		return success(vo);
	}

	/**
	 * 参团详情
	 * 
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/groupdraw/info")
	public JsonResult getGroupDrawInfo(@RequestBody @Valid GroupDrawInfoParam param) {
		GroupDrawReturn checkGroupDraw = shop().groupDraw.checkGroupDraw(param, wxAppAuth.user().getUserId());
		JsonResultCode code = checkGroupDraw.getCode();
		if (code != null && code.equals(JsonResultCode.CODE_SUCCESS)) {
			return success(checkGroupDraw.getVo());
		}
		return fail(code);
	}
	
	/**
	 *订单号查询用户拼团信息 
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/groupdraw/info/ordersn")
	public JsonResult getGroupDrawInfoByOrderSn(@RequestBody @Valid GroupDrawInfoByOsParam param) {
		GroupDrawInfoByOsVo vo = shop().groupDraw.getGroupByOrderSn(param.getOrderSn(),true);
		if(vo==null) {
			return fail(JsonResultCode.GROUP_DRAW_FAIL);
		}
		return success(vo);
		
	}

}

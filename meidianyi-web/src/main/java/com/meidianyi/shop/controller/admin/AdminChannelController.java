package com.meidianyi.shop.controller.admin;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelPageParam;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelParam;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelStatisticalChartVo;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelStatisticalParam;
import com.meidianyi.shop.service.pojo.shop.market.channel.QrCodeShareVo;

/**
 * @author huangronggang
 * @date 2019年8月12日 渠道页面分析
 */
@RestController
@RequestMapping("/api/admin/market/channel")
public class AdminChannelController extends AdminBaseController {
	/**
	 * 渠道页面分析分页查询
	 * 
	 * @param param
	 * @return ChannelPageVo
	 */
	@PostMapping("/list")
	public JsonResult getList(@RequestBody  ChannelPageParam param) {
		return success(shop().channelService.getPageList(param));
	}
	/** 
	 * 停用页面
	 * @param id
	 * @return JsonResult
	 */
	@PostMapping("/disable/{id}")
	public JsonResult disable(@PathVariable Integer id) {
		int result = shop().channelService.disableChannel(id);
		return result(result);
	}
	/**
	 * 启用渠道页面
	 * @param id
	 * @return JsonResult
	 */
	@PostMapping("/enable/{id}")
	public JsonResult enable(@PathVariable Integer id) {
		int result = shop().channelService.enableChannel(id);
		return result(result);
	}
	/**
	 * 渠道页面  分享
	 * @param id
	 * @return QrCodeShareVo
	 */
	@PostMapping("/share/{id}")
	public JsonResult share(@PathVariable Integer id) {
		QrCodeShareVo vo = shop().channelService.shareQrCode(id);
		return success(vo);
	}
	/**
	 * 添加渠道页面
	 * @param param
	 * @return JsonResult
	 */
	@PostMapping("/add")
	public JsonResult add(@RequestBody @Valid ChannelParam param) {
		int result = shop().channelService.insert(param);
		return result(result);
	}
	/**
	 * 页面数据统计
	 * @param param
	 * @return ChannelStatisticalAllVo
	 */
	@PostMapping("/statistical")
	public JsonResult channelStatistical(@RequestBody @Valid ChannelStatisticalParam param) {
		ChannelStatisticalChartVo vo = shop().channelStatitical.createChart(param, getLang());
		if(vo==null) {
			return fail();
		}
		return success(vo);
	}
	private JsonResult result(int result) {
		if(result ==0) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}
}

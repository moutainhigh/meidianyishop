package com.meidianyi.shop.controller.wxapp;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
/**
 * 好物圈的一些对外接口
 * @author zhaojianqiang
 * @date 2020年5月19日下午2:27:46
 */

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.recommend.goods.ListProduct;
import com.meidianyi.shop.service.pojo.wxapp.recommend.GoodsRecommendParam;
/**
 * 好物圈
 * @author zhaojianqiang
 * @date 2020年5月20日上午9:12:11
 */
@RestController
public class WxAppRecMallController extends WxAppBaseController {
	
	/**
	 * 好物圈商品
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/mall/goods")
	public JsonResult getGoodsMall(@RequestBody GoodsRecommendParam param) {
		List<ListProduct> list = shop().recommendService.goodsMallService.checkShippingRecommendGoods(param.getGoodsId());
		return success(list);
	}
	


}

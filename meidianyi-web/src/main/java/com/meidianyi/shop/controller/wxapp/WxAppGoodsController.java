package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsDetailMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsGroupListMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.recommend.RecommendGoodsParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.recommend.RecommendGoodsVo;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李晓冰
 * @date 2019年10月14日 小程序商品相关接口
 */
@RestController
public class WxAppGoodsController extends WxAppBaseController {

	@PostMapping("/api/wxapp/goods/detail")
	public JsonResult getGoodsDetailInfo(@RequestBody GoodsDetailMpParam param) {
        Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
	    param.setFromPage(EsGoodsConstant.GOODS_DETAIL_PAGE);
		return success(shop().goodsMp.getGoodsDetailMp(param));
	}

    /**
     * 小程序-商品推荐
     * @param param 用户和店铺信息
     * @return 推荐商品信息
     */
	@PostMapping("/api/wxapp/goods/recommend")
    public JsonResult getRecommendGoods(@RequestBody RecommendGoodsParam param) {
        RecommendGoodsVo result = shop().goodsMp.mpGoodsRecommendService.getRecommendGoods(param);
        return success(result);
    }

    /**
     * 小程序-商品分组组件-获取单个组内数据
     * @param param 商品分组过滤数据
     * @return 商品数据
     */
    @PostMapping("/api/wxapp/goods/group/list")
    public JsonResult getGoodsGroupList(@RequestBody GoodsGroupListMpParam param) {
	    if (param.getSortGroupArr()==null||param.getSortGroupArr().size()==0){
	        return fail();
        }
        Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
        return success(shop().goodsMp.goodsGroupMpService.getGoodsGroupList(param));
    }
}

package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.service.pojo.wxapp.collection.AddAndCancelCollectionParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.wxapp.collection.CancleCollectParam;
import com.meidianyi.shop.service.pojo.wxapp.collection.CollectListParam;
import com.meidianyi.shop.service.pojo.wxapp.collection.CollectListVo;



/**
 * @author changle
 */
@RestController
@RequestMapping("/api/wxapp")
public class WxAppCollectController extends WxAppBaseController{
	/**
	 * 商品收藏列表
	 * @return
	 */
	@PostMapping("/collect/list")
	public JsonResult collectList(@RequestBody CollectListParam param) {
		Integer userId = wxAppAuth.user().getUserId();

		PageResult<CollectListVo> list = shop().collect.collectList(param,userId);
		return this.success(list);
	}
	
	/**
	 * 取消收藏
	 * @param param
	 * @return
	 */
	@PostMapping("/collect/cancle")
	public JsonResult cancleCollect(@RequestBody CancleCollectParam param) {
		int res = shop().collect.cancalCollect(param);
		return this.success(res);
	}

    /**
     * 添加商品收藏
     * @author 李晓冰
     * @param param {@link AddAndCancelCollectionParam}
     * @return {@link JsonResult}
     */
	@PostMapping("/add/collect")
	public JsonResult addCollection(@RequestBody AddAndCancelCollectionParam param){
	    Integer userId=wxAppAuth.user().getUserId();
	    String userName = wxAppAuth.user().getUsername();
        shop().collect.addCollection(param,userId,userName);
        return success();
    }

    /**
     * 取消商品收藏
     * @param param {@link AddAndCancelCollectionParam}
     * @return  {@link JsonResult}
     */
    @PostMapping("/cancel/collect")
    public JsonResult cancelCollection(@RequestBody AddAndCancelCollectionParam param) {
        shop().collect.cancelCollection(param,wxAppAuth.user().getUserId());
        return success();
    }
}

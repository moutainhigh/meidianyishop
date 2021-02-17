package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.GoodsSearchMpParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李晓冰
 * @date 2019年12月09日
 */
@RestController
public class WxAppGoodsSearchController extends WxAppBaseController {
    /**
     * 搜索界面获取初始化搜索数据
     *
     * @return {@link com.meidianyi.shop.service.pojo.wxapp.goods.search.GoodsSearchFilterConditionMpVo}
     */
    @PostMapping("/api/wxapp/goods/search/init")
    public JsonResult getGoodsSearchFilterCondition() {
        return success(shop().goodsMp.goodsSearchMpService.getGoodsSearchFilterCondition());
    }


    /**
     * 小程序-商品搜索
     * @param param 商品搜索条件
     * @return 搜索结果
     */
    @PostMapping("/api/wxapp/goods/search")
    public JsonResult searchGoods(@RequestBody GoodsSearchMpParam param) {
        Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
        return success(shop().goodsMp.goodsSearchMpService.searchGoodsGate(param));
    }
}

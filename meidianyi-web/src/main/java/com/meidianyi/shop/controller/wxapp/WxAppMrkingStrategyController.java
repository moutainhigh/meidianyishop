package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.market.fullcut.MrkingStrategyGoodsListParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 王兵兵
 * @create: 2020-02-18 09:53
 **/
@RestController
public class WxAppMrkingStrategyController extends WxAppBaseController {
    /**
     * 	满折满减活动页的商品列表接口
     */
    @PostMapping("/api/wxapp/fullprice/goodslist")
    public JsonResult mrkingStrategyGoodsList(@RequestBody @Validated MrkingStrategyGoodsListParam param) {
        return success(shop().mrkingStrategy.getWxAppGoodsList(param,wxAppAuth.user().getUserId()));
    }

    /**
     * 	满折满减活动页的已选择商品列表接口
     */
    @PostMapping("/api/wxapp/fullprice/checkedlist")
    public JsonResult mrkingStrategyCheckedList(@RequestBody @Validated MrkingStrategyGoodsListParam param) {
        return success(shop().mrkingStrategy.getWxAppCheckedGoodsList(param,wxAppAuth.user().getUserId()));
    }
}

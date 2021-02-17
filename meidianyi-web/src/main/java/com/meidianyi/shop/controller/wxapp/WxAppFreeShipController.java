package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.market.freeshipping.FreeShippingGoodsListParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 满包邮
 * @author 孔德成
 * @date 2019/12/11 10:20
 */
@RestController
@RequestMapping("/api/wxapp/freeship")
public class WxAppFreeShipController extends WxAppBaseController{


    /**
     * 商品列表
     * @param param
     * @return
     */
    @PostMapping("/goods/list")
    public JsonResult getFreeShippingGoods(@RequestBody @Valid FreeShippingGoodsListParam param){
        Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
        return success(shop().freeShipping.freeShipGoods.freeShipGoodsList(param,getLang()));
    }

    /**
     * 购物车满包邮商品
     * @return
     */
    @PostMapping("/cart/goods/list")
    public JsonResult getCartGoodsList(@RequestBody @Valid  FreeShippingGoodsListParam param){
        Integer userId = wxAppAuth.user().getUserId();
        WxAppCartBo cartGoodsList = shop().freeShipping.freeShipGoods.getCartGoodsList(userId, param.getRuleId());
        return success(cartGoodsList);
    }
}

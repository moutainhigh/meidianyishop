package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.market.gift.GiftGoodsListParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 王兵兵
 * @create: 2020-03-24 16:08
 **/
@RestController
public class WxAppGiftController extends WxAppBaseController {
    @PostMapping("/api/wxapp/gift/goodslist")
    public JsonResult giftGoodsList(@RequestBody @Validated GiftGoodsListParam param) {
        return success(shop().gift.getWxAppGoodsList(param,wxAppAuth.user().getUserId()));
    }

    @PostMapping("/api/wxapp/gift/checkedlist")
    public JsonResult giftCheckedGoodsList(@RequestBody @Validated GiftGoodsListParam param) {
        return success(shop().gift.giftCheckedGoodsList(param,wxAppAuth.user().getUserId()));
    }
}

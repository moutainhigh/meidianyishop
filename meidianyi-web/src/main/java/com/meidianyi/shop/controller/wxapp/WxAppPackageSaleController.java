package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.market.packagesale.PackageSaleGoodsAddParam;
import com.meidianyi.shop.service.pojo.wxapp.market.packagesale.PackageSaleGoodsDeleteParam;
import com.meidianyi.shop.service.pojo.wxapp.market.packagesale.PackageSaleGoodsListParam;
import com.meidianyi.shop.service.pojo.wxapp.market.packagesale.PackageSaleIdParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 王兵兵
 * @create: 2020-02-20 09:52
 **/
@RestController
public class WxAppPackageSaleController extends WxAppBaseController  {
    /**
     * 	一口价活动页的商品列表接口
     */
    @PostMapping("/api/wxapp/packagesale/goodslist")
    public JsonResult packageSaleGoodsList(@RequestBody @Validated PackageSaleGoodsListParam param) {
        return success(shop().packSale.getWxAppGoodsList(param,wxAppAuth.user().getUserId()));
    }

    /**
     * 	一口价活动页的已选商品列表接口
     */
    @PostMapping("/api/wxapp/packagesale/checkedlist")
    public JsonResult packageSaleCheckedList(@RequestBody @Validated PackageSaleGoodsListParam param) {
        return success(shop().packSale.getCheckedGoodsList(param, wxAppAuth.user().getUserId()));
    }

    /**
     * 商品加购
     */
    @PostMapping("/api/wxapp/packagesale/add")
    public JsonResult addPackageGoods(@RequestBody @Validated PackageSaleGoodsAddParam param) {
        return success(shop().packSale.addPackageGoodsToCart(param, wxAppAuth.user().getUserId()));
    }

    /**
     * 商品删除
     */
    @PostMapping("/api/wxapp/packagesale/delete")
    public JsonResult deletePackageGoods(@RequestBody @Validated PackageSaleGoodsDeleteParam param) {
        return success(shop().packSale.deletePackageGoods(param, wxAppAuth.user().getUserId()));
    }

    /**
     * 结算前确认
     */
    @PostMapping("/api/wxapp/packagesale/checkout")
    public JsonResult packageGoodsCheckout(@RequestBody @Validated PackageSaleIdParam param) {
        return success(shop().packSale.toCheckout(param.getPackageId(), wxAppAuth.user().getUserId()));
    }
}

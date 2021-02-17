package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.couponpack.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author: 王兵兵
 * @create: 2019-08-20 11:22
 **/
@RestController
public class AdminCouponPackController extends AdminBaseController {

    private static final String LANGUAGE_TYPE_EXCEL= "excel";

    /**
     * 优惠券礼包活动分页查询列表
     *
     */
    @PostMapping(value = "/api/admin/market/couponpack/list")
    public JsonResult getCouponPackPageList(@RequestBody @Validated CouponPackPageListQueryParam param) {
        return success(shop().couponPack.getPageList(param));
    }


    /**
     * 优惠券礼包活动下拉框
     */
    @PostMapping(value = "/api/admin/market/couponpack/list/all")
    public JsonResult getAllCouponPackPageList() {
    	return success(shop().couponPack.getAllValidCouponPack());
    }


    /**
     *添加 优惠券礼包活动
     *
     */
    @PostMapping(value = "/api/admin/market/couponpack/add")
    public JsonResult addCouponPack(@RequestBody @Validated CouponPackAddParam param) {
        shop().couponPack.addCouponPack(param);
        return success();
    }

    /**
     *删除 优惠券礼包活动
     *
     */
    @PostMapping(value = "/api/admin/market/couponpack/del")
    public JsonResult delCouponPack(@RequestBody @Validated SimpleCouponPackParam param) {
        shop().couponPack.delCouponPack(param.getId());
        return success();
    }

    /**
     *取单个优惠券礼包活动信息
     *
     */
    @PostMapping(value = "/api/admin/market/couponpack/get")
    public JsonResult getCouponPackById(@RequestBody @Validated SimpleCouponPackParam param) {
        CouponPackUpdateVo couponPackUpdateVo = shop().couponPack.getCouponPackById(param.getId());
        if(couponPackUpdateVo != null) {
            return success(couponPackUpdateVo);
        }else {
            return fail();
        }
    }

    /**
     *更新 优惠券礼包活动
     *
     */
    @PostMapping(value = "/api/admin/market/couponpack/update")
    public JsonResult updateCouponPack(@RequestBody @Validated CouponPackUpdateParam param) {
        shop().couponPack.updateCouponPack(param);
        return success();
    }

    /**
     * 优惠券礼包活动订单列表
     *
     */
    @PostMapping(value = "/api/admin/market/couponpack/order")
    public JsonResult getCouponPackOrderPageList(@RequestBody @Validated CouponPackOrderListQueryParam param) {
        return success(shop().couponPack.getCouponPackOrderPageList(param));
    }

    /**
     * 导出优惠券礼包活动订单列表
     *
     */
    @PostMapping(value = "/api/admin/market/couponpack/order/export")
    public void exportCouponPackOrderList(@RequestBody @Valid CouponPackOrderListQueryParam param, HttpServletResponse response) throws IOException {
        Workbook workbook = shop().couponPack.exportCouponPackOrderList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.COUPON_PACK_ORDER_FILENAME, LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }

    /**
     * 优惠券礼包活动领取详情列表
     *
     */
    @PostMapping(value = "/api/admin/market/couponpack/detail")
    public JsonResult getCouponPackDetailPageList(@RequestBody @Validated CouponPackDetailListQueryParam param) {
        return success(shop().couponPack.getCouponPackDetailPageList(param));
    }

    /**
     * 取活动分享二维码
     */
    @GetMapping("/api/admin/market/couponpack/share")
    public JsonResult getCouponPackShareCode(Integer id) {
        return success(shop().couponPack.getMpQrCode(id));
    }
}

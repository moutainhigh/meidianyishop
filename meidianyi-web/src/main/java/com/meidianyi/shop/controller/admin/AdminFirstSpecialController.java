package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.FirstSpecialAddParam;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.FirstSpecialPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.FirstSpecialUpdateParam;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.FirstSpecialVo;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.SimpleFirstSpecialParam;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.validated.FirstSpecialAddValidatedGroup;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.validated.FirstSpecialUpdateValidatedGroup;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author: 王兵兵
 * @create: 2019-08-16 15:07
 **/
@RestController
public class AdminFirstSpecialController extends AdminBaseController {
    /**
     * 首单特惠活动分页查询列表
     *
     */
    @PostMapping(value = "/api/admin/market/firstspecial/list")
    public JsonResult getFirstSpecialPageList(@RequestBody @Validated FirstSpecialPageListQueryParam param) {
        return success(shop().firstSpecial.getPageList(param));
    }

    /**
     *添加 首单特惠活动
     *
     */
    @PostMapping(value = "/api/admin/market/firstspecial/add")
    public JsonResult addFirstSpecial(@RequestBody @Validated({FirstSpecialAddValidatedGroup.class}) FirstSpecialAddParam param) {
        shop().firstSpecial.addFirstSpecial(param);
        return success();
    }
    /**
     *取单个首单特惠活动信息
     *
     */
    @PostMapping(value = "/api/admin/market/firstspecial/get")
    public JsonResult getFirstSpecialById(@RequestBody @Validated SimpleFirstSpecialParam param) {
        FirstSpecialVo firstSpecialVo = shop().firstSpecial.getFirstSpecialById(param.getId());
        if(firstSpecialVo != null) {
            return success(firstSpecialVo);
        }else {
            return fail();
        }
    }
    /**
     *更新 首单特惠活动
     *
     */
    @PostMapping(value = "/api/admin/market/firstspecial/update")
    public JsonResult updateFirstSpecial(@RequestBody @Validated({FirstSpecialUpdateValidatedGroup.class}) FirstSpecialUpdateParam param) {
        shop().firstSpecial.updateFirstSpecial(param);
        return success();
    }

    /**
     *删除 首单特惠活动
     *
     */
    @PostMapping(value = "/api/admin/market/firstspecial/del")
    public JsonResult delFirstSpecial(@RequestBody @Validated SimpleFirstSpecialParam param) {
        shop().firstSpecial.delFirstSpecial(param.getId());
        return success();
    }

    /**
     * 首单特惠订单列表
     *
     */
    @PostMapping(value = "/api/admin/market/firstspecial/order")
    public JsonResult getFirstSpecialOrderList(@RequestBody @Validated MarketOrderListParam param) {
        return success(shop().firstSpecial.getFirstSpecialOrderList(param));
    }

    /**
     * 获取首单特惠用户仅可购买活动商品的数量
     *
     */
    @GetMapping(value = "/api/admin/market/firstspecial/limit/goods/get")
    public JsonResult getFirstSpecialLimitGoods() {
        return success(shop().config.firstSpecialConfigService.getFirstLimitGoods());
    }

    /**
     * 设置首单特惠用户仅可购买活动商品的数量
     *
     */
    @GetMapping(value = "/api/admin/market/firstspecial/limit/goods/set")
    public JsonResult setFirstSpecialLimitGoods(Integer firstLimitGoods) {
        if(shop().config.firstSpecialConfigService.setFirstLimitGoods(firstLimitGoods) > 0){
            return success();
        }else{
            return fail();
        }
    }

    /**
     * 活动订单
     * 取将要导出的行数
     */
    @PostMapping("/api/admin/market/firstspecial/order/export/rows")
    public JsonResult getActivityOrderExportTotalRows(@RequestBody @Valid MarketOrderListParam param) {
        return success(shop().readOrder.marketOrderInfo.getMarketOrderListSize(param, BaseConstant.ACTIVITY_TYPE_FIRST_SPECIAL));
    }

    /**
     * 活动订单
     * 订单导出
     */
    @PostMapping("/api/admin/market/firstspecial/order/export")
    public void activityOrderExport(@RequestBody @Valid MarketOrderListParam param, HttpServletResponse response) {
        Workbook workbook =shop().firstSpecial.exportFirstSpecialOrderList(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.FIRST_SPECIAL_ORDER_LIST_FILENAME , OrderConstant.LANGUAGE_TYPE_EXCEL,OrderConstant.LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }
}

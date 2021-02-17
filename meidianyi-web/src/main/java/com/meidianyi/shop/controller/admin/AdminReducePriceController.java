package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.reduceprice.*;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author: 王兵兵
 * @create: 2019-08-14 10:56
 **/
@RestController
public class AdminReducePriceController extends AdminBaseController {

    /**
     * 限时降价活动活动分页查询列表
     *
     */
    @PostMapping(value = "/api/admin/market/reduceprice/list")
    public JsonResult getReducePricePageList(@RequestBody @Validated ReducePricePageListQueryParam param) {
        return success(shop().reducePrice.getPageList(param));
    }

    /**
     *添加 限时降价活动
     *
     */
    @PostMapping(value = "/api/admin/market/reduceprice/add")
    public JsonResult addReducePrice(@RequestBody @Validated ReducePriceAddParam param) {
        shop().reducePrice.addReducePrice(param);
        return success();
    }

    /**
     *取单个限时降价活动信息
     *
     */
    @PostMapping(value = "/api/admin/market/reduceprice/get")
    public JsonResult getReducePriceById(@RequestBody @Validated SimpleReducePriceParam param) {
        ReducePriceVo reducePriceVo = shop().reducePrice.getReducePriceById(param.getId());
        if(reducePriceVo != null) {
            return success(reducePriceVo);
        }else {
            return fail();
        }
    }

    /**
     *更新 限时降价活动
     *
     */
    @PostMapping(value = "/api/admin/market/reduceprice/update")
    public JsonResult updateReducePrice(@RequestBody @Validated ReducePriceUpdateParam param) {
        shop().reducePrice.updateReducePrice(param);
        return success();
    }

    /**
     *删除 限时降价活动
     *
     */
    @PostMapping(value = "/api/admin/market/reduceprice/del")
    public JsonResult delReducePrice(@RequestBody @Validated SimpleReducePriceParam param) {
        shop().reducePrice.delReducePrice(param.getId());
        return success();
    }

    /**
     * 限时降价订单列表
     */
    @PostMapping(value = "/api/admin/market/reduceprice/order")
    public JsonResult getReducePriceOrderList(@RequestBody @Validated MarketOrderListParam param) {
        return success(shop().reducePrice.getReducePriceOrderList(param));
    }

    /**
     * 活动订单
     * 订单导出
     */
    @PostMapping("/api/admin/market/reduceprice/order/export")
    public void activityOrderExport(@RequestBody @Valid MarketOrderListParam param, HttpServletResponse response) {
        Workbook workbook = shop().reducePrice.exportReducePriceOrderList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.REDUCE_PRICE_ORDER_LIST_FILENAME, OrderConstant.LANGUAGE_TYPE_EXCEL, OrderConstant.LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }

    /**
     * 活动效果分析
     */
    @PostMapping("/api/admin/market/reduceprice/analysis")
    public JsonResult getReduceAnalysisData(@RequestBody @Validated ReducePriceAnalysisParam param) {
        return success(shop().reducePrice.getReduceAnalysisData(param));
    }
}

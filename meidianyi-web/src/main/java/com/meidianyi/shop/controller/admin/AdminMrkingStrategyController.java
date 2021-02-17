package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.fullcut.*;
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
 * @create: 2019-08-09 19:00
 **/
@RestController
public class AdminMrkingStrategyController extends AdminBaseController {

    /**
     * 满折满减活动分页查询列表
     *
     */
    @PostMapping(value = "/api/admin/market/fullcut/list")
    public JsonResult getMrkingStrategyPageList(@RequestBody @Validated MrkingStrategyPageListQueryParam param) {
        return success(shop().mrkingStrategy.getPageList(param));
    }

    /**
     * 添加满折满减活动
     * @return
     */
    @PostMapping("/api/admin/market/fullcut/add")
    public JsonResult addMrkingStrategy(@RequestBody @Validated MrkingStrategyAddParam param) {
        shop().mrkingStrategy.addMrkingStrategy(param);
        return this.success();
    }

    /**
     *取单个满折满减活动信息
     *
     */
    @PostMapping(value = "/api/admin/market/fullcut/get")
    public JsonResult getMrkingStrategyById(@RequestBody @Validated SimpleMrkingStrategyParam param) {
        MrkingStrategyVo mrkingStrategyVo = shop().mrkingStrategy.getMrkingStrategyById(param.getId());
        if(mrkingStrategyVo != null) {
            return success(mrkingStrategyVo);
        }else {
            return fail();
        }
    }

    /**
     *更新 满折满减活动
     *
     */
    @PostMapping(value = "/api/admin/market/fullcut/update")
    public JsonResult updateMrkingStrategy(@RequestBody @Validated MrkingStrategyUpdateParam param) {
        shop().mrkingStrategy.updateMrkingStrategy(param);
        return success();
    }

    /**
     * 删除 满折满减活动
     */
    @PostMapping(value = "/api/admin/market/fullcut/del")
    public JsonResult delMrkingStrategy(@RequestBody @Validated SimpleMrkingStrategyParam param) {
        shop().mrkingStrategy.delMrkingStrategy(param.getId());
        return success();
    }

    /**
     * 满折满减活动订单
     */
    @PostMapping(value = "/api/admin/market/fullcut/order")
    public JsonResult mrkingStrategyOrder(@RequestBody @Validated MrkingStrategyOrderParam param) {
        return success(shop().mrkingStrategy.getMrkingStrategyOrderList(param, getLang()));
    }

    /**
     * 活动订单
     * 订单导出
     */
    @PostMapping("/api/admin/market/fullcut/order/export")
    public void activityOrderExport(@RequestBody @Valid MrkingStrategyOrderParam param, HttpServletResponse response) {
        Workbook workbook = shop().mrkingStrategy.exportOrderList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.MRKING_STRATEGY_ORDER_LIST_FILENAME, OrderConstant.LANGUAGE_TYPE_EXCEL, OrderConstant.LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }

    /**
     * 活动效果分析
     */
    @PostMapping("/api/admin/market/fullcut/analysis")
    public JsonResult getAnalysisData(@RequestBody @Validated MrkingStrategyAnalysisParam param) {
        return success(shop().mrkingStrategy.getAnalysisData(param));
    }
}

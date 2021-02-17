package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.seckill.*;
import com.meidianyi.shop.service.pojo.shop.market.seckill.analysis.SeckillAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


/**
 * @author 王兵兵
 *
 * 2019年8月6日
 */
@RestController
public class AdminSeckillController extends AdminBaseController {

    /**
     * 秒杀活动分页查询列表
     *
     */
    @PostMapping(value = "/api/admin/market/seckill/list")
    public JsonResult getSeckillPageList(@RequestBody @Validated SeckillPageListQueryParam param) {
        return success(shop().seckill.getPageList(param));
    }

    /**
     * 砍价活动分页查询列表(装修页弹窗选择)
     *
     */
    @PostMapping(value = "/api/admin/decorate/seckill/list")
    public JsonResult getSeckillPageListDialog(@RequestBody @Validated SeckillPageListQueryParam param) {
        return success(shop().seckill.getPageListDialog(param));
    }

    /**
     *添加 秒杀活动
     *
     */
    @PostMapping(value = "/api/admin/market/seckill/add")
    public JsonResult addSeckill(@RequestBody @Validated SeckillAddParam param) {
        shop().seckill.addSeckill(param);
        return success();
    }

    /**
     *更新 秒杀活动
     *
     */
    @PostMapping(value = "/api/admin/market/seckill/update")
    public JsonResult updateSeckill(@RequestBody @Validated SeckillUpdateParam param) {
        shop().seckill.updateSeckill(param);
        return success();
    }

    /**
     *删除 秒杀活动
     *
     */
    @PostMapping(value = "/api/admin/market/seckill/del")
    public JsonResult delSeckill(@RequestBody @Validated SimpleSeckillParam param) {
        shop().seckill.delSeckill(param.getSkId());
        return success();
    }

    /**
     *取单个秒杀活动信息
     *
     */
    @PostMapping(value = "/api/admin/market/seckill/get")
    public JsonResult getSeckillById(@RequestBody @Validated SimpleSeckillParam param) {
        SeckillVo seckillVo = shop().seckill.getSeckillById(param.getSkId());
        if(seckillVo != null) {
            return success(seckillVo);
        }else {
            return fail();
        }
    }

    /**
     * 秒杀拉新用明细
     *
     */
    @PostMapping(value = "/api/admin/market/seckill/source")
    public JsonResult getSeckillSourceUserList(@RequestBody @Validated MarketSourceUserListParam param) {
        return success(shop().seckill.getSeckillSourceUserList(param));
    }

    /**
     * 秒杀订单
     *
     */
    @PostMapping(value = "/api/admin/market/seckill/order")
    public JsonResult getSeckillOrderList(@RequestBody @Validated MarketOrderListParam param) {
        return success(shop().seckill.getSeckillOrderList(param));
    }

    /**
     * 秒杀参与明细
     *
     */
    @PostMapping(value = "/api/admin/market/seckill/detail")
    public JsonResult getSeckillDetailPageList(@RequestBody @Validated SeckillDetailPageListQueryParam param) {
        return success(shop().seckill.seckillList.getSeckillDetailPageList(param));
    }

    /**
     * 取活动分享二维码
     */
    @GetMapping("/api/admin/market/seckill/share")
    public JsonResult getSeckillShareCode(Integer id) {
        return success(shop().seckill.getMpQrCode(id));
    }

    /**
     * 秒杀效果分析
     *
     */
    @PostMapping("/api/admin/market/seckill/analysis")
    public JsonResult getSeckillAnalysisData(@RequestBody @Validated SeckillAnalysisParam param) {
        return success(shop().seckill.getSeckillAnalysisData(param));
    }

    /**
     * 活动订单
     * 取将要导出的行数
     */
    @PostMapping("/api/admin/market/seckill/order/export/rows")
    public JsonResult getActivityOrderExportTotalRows(@RequestBody @Valid MarketOrderListParam param) {
        return success(shop().readOrder.marketOrderInfo.getMarketOrderListSize(param, BaseConstant.ACTIVITY_TYPE_SEC_KILL));
    }

    /**
     * 活动订单
     * 订单导出
     */
    @PostMapping("/api/admin/market/seckill/order/export")
    public void activityOrderExport(@RequestBody @Valid MarketOrderListParam param, HttpServletResponse response) {
        Workbook workbook =shop().seckill.exportSeckillOrderList(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.SECKILL_ORDER_LIST_FILENAME , OrderConstant.LANGUAGE_TYPE_EXCEL,OrderConstant.LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }
}

package com.meidianyi.shop.controller.system;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.pojo.saas.order.MainInquiryOrderStatisticsParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderStatisticsVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author yangpengcheng
 * @date 2020/8/14
 **/
@RestController
public class SystemInquiryOrderController  extends SystemBaseController{

    /**
     * 咨询订单统计报表
     * @param param
     * @return
     */
    @PostMapping("/api/system/inquiry/order/statistics")
    public JsonResult list(@RequestBody MainInquiryOrderStatisticsParam param){
        return success(saas.mainInquiryOrderService.orderStatistics(param));
    }

    /**
     * 报表导出
     * @param param
     * @param response
     */
    @PostMapping("/api/system/inquiry/order/statistics/export")
    public void orderStatisticsExport(@RequestBody MainInquiryOrderStatisticsParam param, HttpServletResponse response){
        Workbook workbook=saas.mainInquiryOrderService.orderStatisticsExport(param,getLang());
        String fileName = InquiryOrderStatisticsVo.EXPORT_FILE_NAME+ DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }

    /**
     * 咨询报表总数total查询
     * @param param
     * @return
     */
    @PostMapping("/api/system/inquiry/order/statistics/total")
    public JsonResult orderStatisticsTotal(@RequestBody MainInquiryOrderStatisticsParam param){
        return success(saas.mainInquiryOrderService.orderStatisticsTotal(param));
    }

    @PostMapping("/api/system/inquiry/order/test/{shopId}")
    public void testMapping(@PathVariable Integer shopId){
        saas.getShopApp(shopId).shopTaskService.tableTaskService.inquiryOrderSynchronize(shopId);
    }
}

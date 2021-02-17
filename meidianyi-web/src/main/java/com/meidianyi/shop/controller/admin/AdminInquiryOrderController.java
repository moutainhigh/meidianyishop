package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockKeys;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderOnParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderStatisticsParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderStatisticsVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 问诊订单
 * @author yangpengcheng
 */
@RestController
public class AdminInquiryOrderController extends AdminBaseController{
    /**
     * 获取订单详情
     */
    @PostMapping("/api/admin/inquiry/order/detail")
    public JsonResult orderDetail(@RequestBody InquiryOrderOnParam inquiryOrderOnParam){
        if(StringUtils.isBlank(inquiryOrderOnParam.getOrderSn())){
            return fail(JsonResultCode.INQUIRY_ORDER_SN_IS_NULL);
        }
        InquiryOrderDetailVo inquiryOrderDetailVo= shop().inquiryOrderService.getDetailByOrderSn(inquiryOrderOnParam.getOrderSn());
        return success(inquiryOrderDetailVo);
    }
    /**
     * 查询问诊订单
     */
    @PostMapping("/api/admin/inquiry/order/list")
    public JsonResult orderList(@RequestBody InquiryOrderListParam param){
        return success(shop().inquiryOrderService.getInquiryOrderList(param));
    }

    /**
     * 问诊订单退款
     */
    @RedisLock(prefix = JedisKeyConstant.INQUIRY_ORDER_REFUND_LOCK)
    @PostMapping("/api/admin/inquiry/order/refund")
    public JsonResult refund(@RequestBody @RedisLockKeys InquiryOrderOnParam inquiryOrderOnParam){
        try {
            shop().inquiryOrderService.refund(inquiryOrderOnParam);
        } catch (MpException e) {
            return fail(e.getErrorCode(),e.getCodeParam());
        }
        return success();
    }

    /**
     * 咨询订单统计报表详情
     * @param param
     * @return
     */
    @PostMapping("/api/admin/inquiry/order/statistics")
    public JsonResult orderStatistics(@RequestBody InquiryOrderStatisticsParam param){
        return success(shop().inquiryOrderService.orderStatistics(param));
    }

    /**
     * 问诊订单统计报表详情导出
     * @param param
     * @param response
     */
    @PostMapping("/api/admin/inquiry/order/statistics/export")
    public void orderStatisticsExport(@RequestBody InquiryOrderStatisticsParam param, HttpServletResponse response){
        Workbook workbook=shop().inquiryOrderService.orderStatisticsExport(param,getLang());
        String fileName = InquiryOrderStatisticsVo.EXPORT_FILE_NAME+ DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }

    /**
     * 咨询报表总数total查询
     * @param param
     * @return
     */
    @PostMapping("/api/admin/inquiry/order/statistics/total")
    public JsonResult orderStatisticsTotal(@RequestBody InquiryOrderStatisticsParam param){
        return success(shop().inquiryOrderService.orderStatisticsTotal(param));
    }
}

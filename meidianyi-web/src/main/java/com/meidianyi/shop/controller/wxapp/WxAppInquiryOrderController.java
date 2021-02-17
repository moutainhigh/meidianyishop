package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RequestUtil;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockKeys;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderOnParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryToPayParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问诊订单
 * @author yangpengcheng
 */
@RestController
public class WxAppInquiryOrderController extends WxAppBaseController{
    /**
     * 问诊订单支付
     * @return
     */
    @PostMapping("/api/wxapp/inquiry/order/pay")
    public JsonResult payOrder(@RequestBody @Validated InquiryToPayParam payParam){
        WxAppSessionUser user = wxAppAuth.user();
        payParam.setUser(user);
        payParam.setClientIp(RequestUtil.getIp(request));
        try {
            return success(shop().inquiryOrderService.payInquiryOrder(payParam));
        } catch (MpException e) {
            return fail(e.getErrorCode());
        }
    }
    /**
     * 问诊订单退款
     */
    @RedisLock(prefix = JedisKeyConstant.INQUIRY_ORDER_REFUND_LOCK)
    @PostMapping("/api/wxapp/inquiry/order/refund")
    public JsonResult refund(@RequestBody @RedisLockKeys InquiryOrderOnParam inquiryOrderOnParam){
        try {
             shop().inquiryOrderService.doctorRefund(inquiryOrderOnParam);
        } catch (MpException e) {
            return fail(e.getErrorCode(),e.getCodeParam());
        }
        return success();
    }

    /**
     * 查询问诊订单
     */
    @PostMapping("/api/wxapp/inquiry/order/list")
    public JsonResult refundOrder(@RequestBody InquiryOrderListParam param){
        return success(shop().inquiryOrderService.getInquiryOrderList(param));
    }

    /**
     * 获取订单详情
     */
    @PostMapping("/api/wxapp/inquiry/order/detail")
    public JsonResult orderDetail(@RequestBody InquiryOrderOnParam inquiryOrderOnParam){
        if(StringUtils.isBlank(inquiryOrderOnParam.getOrderSn())){
            return fail(JsonResultCode.INQUIRY_ORDER_SN_IS_NULL);
        }
        InquiryOrderDetailVo inquiryOrderDetailVo= shop().inquiryOrderService.getDetailByOrderSn(inquiryOrderOnParam.getOrderSn());
        return success(inquiryOrderDetailVo);
    }
    /**
     * 更改问诊订单状态
     */
    @PostMapping("/api/wxapp/inquiry/order/status/update")
    public JsonResult updateStatus(@RequestBody InquiryOrderOnParam inquiryOrderOnParam){
        shop().inquiryOrderService.updateOrder(inquiryOrderOnParam);
        return success();
    }

    /**
     * 查询未完成的问诊
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/inquiry/order/undone/get")
    public JsonResult getUndoneOrder(@RequestBody InquiryOrderParam param){
        return success(shop().inquiryOrderService.getUndoneOrder(param));
    }
    /**
     * 新增
     * @param inquiryOrderDo
     * @return
     */
    @PostMapping("/api/wxapp/inquiry/order/insert")
    public JsonResult insert(@RequestBody InquiryOrderDo inquiryOrderDo){
        shop().inquiryOrderService.insert(inquiryOrderDo);
        return success();
    }

}

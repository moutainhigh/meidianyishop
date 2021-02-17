package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.order.virtual.AnalysisParam;
import com.meidianyi.shop.service.pojo.shop.order.virtual.MemberCardOrderParam;
import com.meidianyi.shop.service.pojo.shop.order.virtual.VirtualOrderRefundParam;
import com.meidianyi.shop.service.shop.order.virtual.VirtualOrderService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 虚拟订单 - 会员卡订单
 *
 * @author 郑保乐
 */
@RestController
@RequestMapping("/api/admin/order/member_card")
public class AdminMemberCardOrderController extends AdminBaseController {

    @PostMapping("/list")
    public JsonResult orderPageList(@RequestBody MemberCardOrderParam param) {
        return success(shop().memberCardOrder.getMemberCardOrderList(param));
    }

    @PostMapping("/refund")
    public JsonResult orderRefund(@RequestBody VirtualOrderRefundParam param) throws MpException {
        if (shop().couponPackOrder.checkVirtualOrderRefundParam(param)) {
            JsonResultCode result = shop().memberCardOrder.memberCardOrderRefund(param);
            return result == null ? success() : fail(result);
        } else {
            return fail(JsonResultMessage.REFUND_REQUEST_PARAMETER_ERROR);
        }
    }

    @PostMapping("/analysis")
    public JsonResult orderAnalysis(@RequestBody AnalysisParam param) {
        return success(shop().memberCardOrder.getAnalysisData(param, VirtualOrderService.GOODS_TYPE_MEMBER_CARD));
    }

    /**
     * 取将要导出的行数
     */
    @PostMapping("/export/rows")
    public JsonResult getExportTotalRows(@RequestBody @Valid MemberCardOrderParam param) {
        return success(shop().memberCardOrder.getExportRows(param));
    }

    @PostMapping("/export")
    public void export(@RequestBody @Valid MemberCardOrderParam param, HttpServletResponse response) {
        Workbook workbook = shop().memberCardOrder.exportOrderList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.VIRTUAL_ORDER_MEMBER_CARD_FILE_NAME, "excel", "excel") + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }
}

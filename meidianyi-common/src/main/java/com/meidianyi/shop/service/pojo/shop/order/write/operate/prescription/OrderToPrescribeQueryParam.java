package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription;

import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.AbstractOrderOperateQueryParam;
import lombok.Data;

/**
 * @author yangpengcheng
 * @date 2020/7/24
 **/
@Data
public class OrderToPrescribeQueryParam extends AbstractOrderOperateQueryParam {
    private Integer orderId;
    private Byte orderStatus=OrderConstant.ORDER_TO_AUDIT_OPEN;
    private Byte auditType = OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE;
    private Byte auditStatus =OrderConstant.MEDICAL_AUDIT_DEFAULT;

    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;

    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}

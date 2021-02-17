package com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 批量发货对象
 * @author 王帅
 */
@Getter
@Setter
@ToString
@ExcelSheet
public class BatchShipPojo {
    /** 订单号 */
    @ExcelColumn(columnIndex = 0, columnName = "order.batch.ship.orderSn")
    private String orderSn;

    /** 快递公司 */
    @ExcelColumn(columnIndex = 1, columnName = "order.batch.ship.express")
    private String express;

    /** 快递单号 */
    @ExcelColumn(columnIndex = 2, columnName = "order.batch.ship.trackingNo")
    private String trackingNo;
}

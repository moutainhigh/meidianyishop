package com.meidianyi.shop.service.pojo.shop.market.bargain;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-02-17 16:16
 **/
@ExcelSheet
@Data
public class BargainOrderExportVo {

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_ORDER_LIST_ORDER_SN,columnIndex = 0)
    private String orderSn;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_ORDER_LIST_BARGAIN_GOODS,columnIndex = 1)
    private String goodsName;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_ORDER_LIST_PRICE,columnIndex = 2)
    private BigDecimal price;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_ORDER_LIST_CREATE_TIME,columnIndex = 3)
    private Timestamp createTime;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_ORDER_LIST_ORDER_USER,columnIndex = 4)
    private String username;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_ORDER_LIST_CONSIGNEE,columnIndex = 5)
    private String consignee;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_ORDER_LIST_ORDER_STATUS,columnIndex = 6)
    private String orderStatus;

}

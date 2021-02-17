package com.meidianyi.shop.service.pojo.shop.market.groupdraw.order;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liangchen
 */
@Data
@ExcelSheet
public class OrderExport {
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.GROUP_ORDER_SN)
    private String orderSn;
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.GROUP_GOODS)
    private String goodsName;
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.GROUP_IS_GROUPED)
    private String grouped;
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.GROUP_USER_INFO)
    private String userInfo;
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.GROUP_IS_WIN_DRAW)
    private String isWinDraw;
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.GROUP_ORDER_TIME)
    private Timestamp createTime;
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.GROUP_CODE_NUMBER)
    private Short codeCount;
    @ExcelColumn(columnIndex = 7, columnName = JsonResultMessage.GROUP_ORDER_STATUS)
    private String orderStatus;
}

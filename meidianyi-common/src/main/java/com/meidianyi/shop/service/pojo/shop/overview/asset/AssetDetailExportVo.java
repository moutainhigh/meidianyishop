package com.meidianyi.shop.service.pojo.shop.overview.asset;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/5
 */
@Data
@ExcelSheet
public class AssetDetailExportVo {
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_TRADE_TIME)
    private Timestamp tradeTime;
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_TRADE_SN)
    private String tradeSn;
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_TRADE_NUM)
    private BigDecimal tradeNum;
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_USERNAME)
    private String username;
    @ExcelIgnore
    private Integer userId;
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_TRADE_TYPE)
    private Byte tradeType;
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_TRADE_FLOW)
    private Byte tradeFlow;
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_TRADE_STATUS)
    private Byte tradeStatus;
    @ExcelColumn(columnIndex = 7, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_REAL_NAME)
    private String realName;
    @ExcelColumn(columnIndex = 8, columnName = JsonResultMessage.ASSETS_EXPORT_COLUMN_MOBILE)
    private String mobile;
}

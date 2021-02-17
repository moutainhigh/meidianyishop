package com.meidianyi.shop.service.pojo.shop.market.bargain;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @description: 帮忙砍价用户列表导出pojo
 * @author: 王兵兵
 * @create: 2019-08-01 11:26
 **/
@ExcelSheet
@Data
public class BargainUserExportVo {

    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_USER_LIST_ID,columnIndex = 0)
    private Integer userId;

    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_USER_LIST_USERNAME,columnIndex = 1)
    private String username;

    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_USER_LIST_MOBILE,columnIndex = 2)
    private String mobile;

    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_USER_LIST_CREATE_TIME,columnIndex = 3)
    private Timestamp createTime;

    /**
     * 帮砍金额
     */
    @ExcelColumn(columnName = JsonResultMessage.BARGAIN_USER_LIST_BARGAIN_MONEY,columnIndex = 4)
    private BigDecimal bargainMoney;
}

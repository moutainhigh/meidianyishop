package com.meidianyi.shop.service.pojo.shop.market.form;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/13
 */
@ExcelSheet
@Data
public class FormFeedExportVo {
    @ExcelIgnore
    private Integer userId;
    @ExcelIgnore
    private Integer pageId;
    @ExcelIgnore
    private Integer submitId;
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.FORM_FEED_NICKNAME)
    private String nickName;
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.FORM_FEED_MOBILE)
    private String mobile;
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.FORM_FEED_CREATE_TIME)
    private Timestamp createTime;
}

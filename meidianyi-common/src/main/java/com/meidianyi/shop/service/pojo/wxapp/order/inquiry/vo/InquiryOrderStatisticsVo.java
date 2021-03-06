package com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/8/3
 **/
@Data
@ExcelSheet
public class InquiryOrderStatisticsVo {
    @ExcelIgnore
    public static final String EXPORT_FILE_NAME="咨询统计导出-";

    @ExcelColumn(columnName = "日期", columnIndex = 0)
    private Timestamp createTime;
    @ExcelIgnore
    private Integer doctorId;
    @ExcelIgnore
    private Integer departmentId;
    @ExcelColumn(columnName = "医生姓名", columnIndex = 1)
    private String doctorName;
    @ExcelIgnore
    private String departmentName;
    /**
     * 咨询单数
     */
    @ExcelColumn(columnName = "咨询单数", columnIndex = 2)
    private String amount;
    /**
     * 咨询单次价格
     */
    @ExcelColumn(columnName = "咨询单次价格", columnIndex = 3)
    private BigDecimal oncePrice;
    /**
     * 咨询总金额
     */
    @ExcelColumn(columnName = "咨询总金额", columnIndex = 4)
    private BigDecimal amountPrice;

}

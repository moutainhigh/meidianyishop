package com.meidianyi.shop.service.pojo.shop.rebate;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/9/9
 **/
@Data
@ExcelSheet
public class InquiryOrderRebateReportVo {
    @ExcelIgnore
    public static final String EXPORT_FILE_NAME="咨询返利导出-";
    @ExcelColumn(columnName = "医生姓名", columnIndex = 0)
    private String doctorName;
    @ExcelColumn(columnName = "手机号", columnIndex = 1)
    private String mobile;
    @ExcelColumn(columnName = "咨询订单号", columnIndex = 2)
    private String orderSn;
    @ExcelColumn(columnName = "咨询费", columnIndex = 3)
    private BigDecimal totalMoney;
    @ExcelColumn(columnName = "下单用户昵称", columnIndex = 4)
    private String userName;
    @ExcelColumn(columnName = "返利金额", columnIndex = 5)
    private BigDecimal totalRebateMoney;
    @ExcelIgnore
    private Byte status;
    @ExcelColumn(columnName = "返利状态", columnIndex = 6)
    private String statusName;
    @ExcelColumn(columnName = "返利时间", columnIndex = 7)
    private Timestamp rebateTime;
}

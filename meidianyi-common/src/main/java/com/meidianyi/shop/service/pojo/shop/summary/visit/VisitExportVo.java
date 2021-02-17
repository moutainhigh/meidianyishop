package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

/**
 * 访问分析数据导出
 * @author liangchen
 * @date 2020.02.03
 */
@Data
@ExcelSheet
public class VisitExportVo {
    /** 粒度(1/7/30) */
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_GRADING)
    private String grading;
    /** 时间 */
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_DATE)
    private String date;
    /** 打开次数 */
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_SESSION_COUNT)
    private Double sessionCount;
    /** 访问次数 */
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_PV)
    private Double pv;
    /** 访问人数 */
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_UV)
    private Double uv;
    /** 新用户数 */
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_UV_NEW)
    private Double uvNew;
    /** 人均停留时长 */
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_STAY_TIME_UV)
    private Double stayTimeUv;
    /** 次均停留时长 */
    @ExcelColumn(columnIndex = 7, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_STAY_TIME_SESSION)
    private Double stayTimeSession;
    /** 平均访问深度 */
    @ExcelColumn(columnIndex = 8, columnName = JsonResultMessage.VISIT_EXPORT_COLUMN_VISIT_DEPTH)
    private Double visitDepth;
}

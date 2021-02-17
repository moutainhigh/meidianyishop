package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Data;

/**
 * 访问分析数据导出
 * @author liangchen
 * @date 2020.02.03
 */
@Data
public class VisitExportParam {
    /** 日期类型 7:最近7天 30:最近30天 0:自定义 */
    private Integer type = 7;
    /** 开始时间 */
    private String startDate;
    /** 结束时间 */
    private String endDate;
    /** 时间粒度 日/周/月 */
    private Integer grading = 1;
}

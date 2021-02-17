package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Data;

import java.util.List;

/**
 * 访问分析数据导出
 * @author liangchen
 * @date 2020.02.03
 */
@Data
public class VisitExportTempVo {
    /** 粒度(1/7/30) */
    private Integer grading;
    /** 活动类型 */
    private Integer action;
    private List<String> date;
    private List<Double> list;
}

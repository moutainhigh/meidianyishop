package com.meidianyi.shop.service.pojo.shop.summary.visit;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author liufei
 * @date 1/19/20
 */
@Data
@Builder
public class SourceAnalysisVo {
    public List<LineChartVo> lineChart;
    public String startDate;
    public String endDate;
}

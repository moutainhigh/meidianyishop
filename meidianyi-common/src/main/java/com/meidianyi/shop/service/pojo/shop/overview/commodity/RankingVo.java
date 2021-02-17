package com.meidianyi.shop.service.pojo.shop.overview.commodity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author liufei
 * @date 2/10/2020
 */
@Data
@Builder
public class RankingVo {
    /**
     * 图表数据
     * 销售额
     * key：dayData，weekData，monthData，yearData
     */
    Map<String, ChartData> salesChar;
    /**   销售订单 */
    Map<String, ChartData> salesOrderChar;

    /**   表格数据 */
    Map<String, TableData> salesTable;

    Map<String, TableData> salesOrderTable;

}

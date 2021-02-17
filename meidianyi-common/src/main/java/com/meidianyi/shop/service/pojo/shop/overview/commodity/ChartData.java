package com.meidianyi.shop.service.pojo.shop.overview.commodity;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liufei
 * @date 2/10/2020
 * 图表数据（折线图，柱状图...）
 */
@Data
@Builder
public class ChartData {
    /**   top10商品名称列表 */
    @Builder.Default
    List<String> columns = new ArrayList<>();
    /**   key：日期date，value：2020-01-01 */
    /**   key：商品名称，value：销售额/销售订单量（付款商品件数） */
    List<Map<String, Object>> rows;

    /*@PostConstruct
    public void init() {
        columns.add("Date");
    }*/
}

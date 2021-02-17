package com.meidianyi.shop.service.pojo.shop.anchor;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 孔德成
 * @date 2020/9/10 13:44
 */
@Data
public class AnchorPointsChartReportVo {


    private List<String> legendData;

    /**
     * 横坐标
     */
    private List<String> xAxisData;

    private List<SeriesData> seriesList;

    @Data
    public static class SeriesData{
        private String name;
        private String type;
        private String stack;
        private List<String> dataList =new ArrayList<>();
        private Map<String,String> dataMap =new LinkedHashMap (16);

    }
}

package com.meidianyi.shop.service.shop.summary.visit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.MpDistributionVisitRecord;
import com.meidianyi.shop.service.pojo.shop.summary.ChartData;
import com.meidianyi.shop.service.pojo.shop.summary.ChartInfo;
import com.meidianyi.shop.service.pojo.shop.summary.KeyValueChart;
import com.meidianyi.shop.service.pojo.shop.summary.ValueKeyChart;
import com.meidianyi.shop.service.pojo.shop.summary.visit.*;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.MpDistributionVisit.MP_DISTRIBUTION_VISIT;
import static com.meidianyi.shop.service.pojo.shop.summary.visit.AccessSource.MP_HISTORY_LIST;
import static com.meidianyi.shop.service.pojo.shop.summary.visit.DistributionIndex.*;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * 访问分布
 *
 * @author 郑保乐
 */
@Service
public class DistributionService extends BaseVisitService {
    /** 日期标识符 */
    private static final Integer CUSTOM_DAYS = 0;
    /**
     *得到之前的某一天(字符串类型)
     *@param days N天前
     *@return preDay(String)
     */
    public String getDate(Integer days) {
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //获取当前时间
        Calendar c = Calendar.getInstance();
        //计算指定日期
        c.add(Calendar.DATE, - days);
        Date time = c.getTime();
        //返回格式化后的String日期
        return sdf.format(time);
    }

    /**
     * Gets select option.来源分析-来源下拉框
     *
     * @return the select option
     */
    public List<VisitInfoItem> getSelectOption() {
        return new ArrayList<VisitInfoItem>() {{
            Arrays.stream(AccessSource.values()).forEach(e -> add(VisitInfoItem.builder().key(e.getKey()).name(e.getName()).build()));
        }};
    }

    /**
     * Gets source analysis.来源分析折线图
     *
     * @param param the param
     * @return the source analysis
     */
    public SourceAnalysisVo querySourceAnalysis(VisitDistributionParam param) {
        logger().info("获取来源分析折线图数据");
        param.setSourceId(Objects.isNull(param.getSourceId()) ? MP_HISTORY_LIST.getIndex() : param.getSourceId());
        if (!param.getType().equals(CUSTOM_DAYS)) {
            param.setStartDate(getDate(param.getType()));
            param.setEndDate(getDate(INTEGER_ZERO));
        }
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        logger().info("日期转换完成：startDate：{}， endDate：{}", startDate, endDate);
        Result<MpDistributionVisitRecord> result = getDistributionRecord(startDate, endDate);
        Integer sourceId = param.getSourceId();
        List<LineChartVo> lineChart = new ArrayList<>();
        for (MpDistributionVisitRecord record : result) {
            String refDate = record.getRefDate();
            String list = record.getList();
            /* 转换统计 JSON */
            Map<String, Map<Integer, Integer>> indexes = Util.parseJson(list, new TypeReference<Map<String, Map<Integer, Integer>>>() {
            });
            for (Map.Entry<String, Map<Integer, Integer>> item : Objects.requireNonNull(indexes).entrySet()) {
                String indexName = item.getKey();
                if (ACCESS_SOURCE_PV.equals(indexName)) {
                    Map<Integer, Integer> values = item.getValue();
                    Integer v = values.get(sourceId);
                    lineChart.add(LineChartVo.builder().refDate(refDate).openTimes(Objects.isNull(v) ? INTEGER_ZERO : v).build());
                }
            }
        }
        logger().info("数据获取完成");
        return SourceAnalysisVo.builder().lineChart(lineChart).startDate(startDate).endDate(endDate).build();
    }
    public VisitDistributionVo getVisitDistribution(VisitDistributionParam param) {
        //得到时间
        if (!param.getType().equals(CUSTOM_DAYS)){
            param.setStartDate(getDate(param.getType()));
            param.setEndDate(getDate(INTEGER_ZERO));
        }
        VisitDistributionVo vo = new VisitDistributionVo();
        /* 访问人数来源 */
        Map<String, Integer> sourceUvMap = new TreeMap<>();
        /* 访问次数来源 */
        Map<String, Integer> sourcePvMap = new TreeMap<>();
        /* 停留时长 */
        Map<String, Integer> stayTimeMap = new TreeMap<>();
        /*  平均访问深度 */
        Map<String, Integer> depthMap = new TreeMap<>();
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        List<Integer> cancelSources = param.getCancelBtn();
        Result<MpDistributionVisitRecord> result = getDistributionRecord(startDate, endDate);
        for (MpDistributionVisitRecord record : result) {
            String list = record.getList();
            /* 转换统计 JSON */
            Map<String,Map<Integer,Integer>> indexes = Util.parseJson(list, new TypeReference<Map<String,Map<Integer,Integer>>>() {});
            for (Map.Entry<String,Map<Integer,Integer>> item : Objects.requireNonNull(indexes).entrySet()) {
                String indexName = item.getKey();
                switch (indexName) {
                    case ACCESS_SOURCE_UV:
                        groupingIndex(sourceUvMap, item.getValue(), AccessSource.values());
                        break;
                    case ACCESS_SOURCE_PV:
                        groupingIndex(sourcePvMap, item.getValue(), AccessSource.values());
                        break;
                    case VISIT_DURATION:
                        groupingIndex(stayTimeMap, item.getValue(), VisitDuration.values());
                        break;
                    case VISIT_DEPTH:
                        groupingIndex(depthMap, item.getValue(), AccessDepth.values());
                        break;
                    default:
                }
            }
        }
        // 移除参数中忽略的访问来源
        cancelSources.forEach(s -> sourcePvMap.remove(AccessSource.findByIndex(s).getSource()));
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);
        vo.setVisitSource(keyValueChart(sourcePvMap));
        vo.setVisitDepth(valueKeyChart(depthMap));
        vo.setVisitStayTime(valueKeyChart(stayTimeMap));
        vo.setAccessSourceSessionCnt(getInfoDict(sourcePvMap, AccessSource.values()));
        vo.setAccessDepthInfo(getInfoDict(depthMap, AccessDepth.values()));
        vo.setAccessStayTimeInfo(getInfoDict(stayTimeMap, VisitDuration.values()));
        return vo;
    }

    /**
     * 由 map 转换到 chart (x: key, y: value)
     */
    private KeyValueChart keyValueChart(Map<String, Integer> map) {
        KeyValueChart chart = new KeyValueChart();
        fillChart(map, chart);
        return chart;
    }

    /**
     * 由 map 转换到 chart (y: key, x: value)
     */
    private ValueKeyChart valueKeyChart(Map<String, Integer> map) {
        ValueKeyChart chart = new ValueKeyChart();
        fillChart(map, chart);
        return chart;
    }

    /**
     * 填充数据，生成图表
     */
    private void fillChart(Map<String, Integer> map, ChartData chart) {
        List<String> keys = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        map.forEach((k, v) -> {
            keys.add(k);
            values.add(v);
        });
        chart.setKeys(keys);
        chart.setValues(values);
    }

    /**
     * 将统计数据分组
     */
    private void groupingIndex(Map<String, Integer> map, Map<Integer, Integer> item ,ChartInfo[] info) {
        List<DistributionIndexItem> items = new ArrayList<>();
        for (Map.Entry<Integer,Integer> tempMap : item.entrySet()){
            items.add(new DistributionIndexItem(){{
                setKey(tempMap.getKey());
                setValue(tempMap.getValue());
            }});
        }
        logger().info("当前items:{}",items);
        items.forEach(i -> {
            Integer key = i.getKey();
            ChartInfo kv = Arrays.stream(info)
                    .filter(k -> k.getKey().equals(key)).findFirst().orElseThrow(RuntimeException::new);
            String sourceName = kv.getName();
            map.compute(sourceName, (k, ov) -> null == ov ? i.getValue() : ov + i.getValue());
        });
    }

    /**
     * 生成同时包含 name、index 和 value 的统计数据
     *
     * @param map       存放 name 和 value 的统计数据
     * @param chartInfo 图表图例
     * @param <T>       具体的枚举类
     */
    private <T extends ChartInfo> List<VisitInfoItem> getInfoDict(Map<String, Integer> map, T[] chartInfo) {
        return Arrays.stream(chartInfo)
            .map(s -> VisitInfoItem.builder().name(s.getName()).key(s.getKey()).value(map.get(s.getName())).build())
            .filter(i -> null != i.getValue()).collect(Collectors.toList());
    }

    private Result<MpDistributionVisitRecord> getDistributionRecord(String startDate, String endDate) {
        return db().select(MP_DISTRIBUTION_VISIT.REF_DATE, MP_DISTRIBUTION_VISIT.LIST)
                .from(MP_DISTRIBUTION_VISIT)
                .where(MP_DISTRIBUTION_VISIT.REF_DATE.between(startDate).and(endDate))
            .orderBy(MP_DISTRIBUTION_VISIT.REF_DATE)
                .fetch().into(MP_DISTRIBUTION_VISIT);
    }
}

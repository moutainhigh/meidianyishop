package com.meidianyi.shop.service.pojo.saas.order.report;

import com.meidianyi.shop.service.pojo.shop.report.MedicalSalesReportParam;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 销售报表
 * @author 孔德成
 * @date 2020/7/31 14:59
 */
@Data
public class OrderBakSalesReportParam extends MedicalSalesReportParam {

    /**
     * 天
     */
    public final static byte ANALYZE_TYPE_DAY=1;
    /**
     * 星期
     */
    public final static byte ANALYZE_TYPE_WEEK=2;
    /**
     * 月
     */
    public final static byte ANALYZE_TYPE_MONTH=3;
    /**
     * 季度
     */
    public final static byte ANALYZE_TYPE_QUARTER=4;
    /**
     * 年
     */
    public final static byte ANALYZE_TYPE_YEAR=5;

    /**
     * 开始时间
     */
    private Timestamp startTime;
    /**
     * 结束时间
     */
    private Timestamp endTime;
    /**
     * 分析时段 1 天 默认 2 周 3月 4 季度 5年
     */
    private Byte analyzeType;

    /**
     * 店铺id
     */
    private Integer shopId;


    //*****计算用

    private Map<Timestamp,Timestamp> map;

    private long totalRows;


}

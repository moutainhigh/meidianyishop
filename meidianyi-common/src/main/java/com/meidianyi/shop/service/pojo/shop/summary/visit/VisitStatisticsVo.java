package com.meidianyi.shop.service.pojo.shop.summary.visit;

import java.util.List;

import lombok.Data;

/**
 * 访问统计
 *
 * @author 郑保乐
 */
@Data
public class VisitStatisticsVo {
    private Double totalNum;
    private Double averageNum;
    private String startDate;
    private String endDate;
    private List<String> date;
    private List<Double> list;
}

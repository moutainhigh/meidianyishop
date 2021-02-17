package com.meidianyi.shop.service.pojo.shop.overview.analysis;

import lombok.Data;


/**
 * 概况统计-昨日概况-出参格式规范
 * @author liangchen
 * @date  2019.12.10
 */
@Data
public class YesterdayStatisticsVo {
    /** 当前统计内容的名称 */
    private String dataName;
    /** 当前统计内容的数据 */
    private Integer dataNumber;
    /** 日增长率 */
    private Double dayRate;
    /** 周增长率 */
    private Double weekRate;
    /** 月增长率 */
    private Double monthRate;
}

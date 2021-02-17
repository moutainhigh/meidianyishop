package com.meidianyi.shop.service.pojo.shop.overview.analysis;

import lombok.Data;

/**
 * 概况统计-访问分析
 * @author liangchen
 * @date 2019.12.11
 */
@Data
public class VisitTrendDaily {
    /** 日期 */
    private String date;
    /** 数据 */
    private Integer number;
}

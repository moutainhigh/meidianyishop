package com.meidianyi.shop.service.pojo.shop.overview.searchanalysis;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liangchen
 * @date 2019年7月23日
 */
@Data
public class SearchHotWordsParam {
    /** 日期类型 7:最近7天 30:最近30天 0:自定义 */
    @NotNull
    private Integer type = 7;
    /** 开始日期 */
    private String startTime;
    /** 结束日期 */
    private String endTime;
}

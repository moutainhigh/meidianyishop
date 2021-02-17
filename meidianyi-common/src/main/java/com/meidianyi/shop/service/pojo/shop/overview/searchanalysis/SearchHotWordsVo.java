package com.meidianyi.shop.service.pojo.shop.overview.searchanalysis;

import lombok.Data;

import java.util.List;

/**
 * @author liangchen
 * @date 2019年7月26日
 */
@Data
public class SearchHotWordsVo {
    /** 是否开启搜索热词统计 */
    private Integer isOpenHotWords;
    /** 数据 */
    private List<SearchHotWordsData> data;
    /** 开始日期 */
    private String startTime;
    /** 结束日期 */
    private String endTime;
}

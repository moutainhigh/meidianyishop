package com.meidianyi.shop.service.pojo.shop.overview.searchanalysis;

import lombok.Data;

import java.util.List;

/**
 * @author liangchen
 * @date 2019年7月23日
 */
@Data
public class SearchHistoryVo {
    /** 是否开启搜索历史统计 */
	private Integer isOpenHistory;
    /** 数据 */
    private List<SearchHistoryData> data;
    /** 开始日期 */
    private String startTime;
    /** 结束日期 */
    private String endTime;
}

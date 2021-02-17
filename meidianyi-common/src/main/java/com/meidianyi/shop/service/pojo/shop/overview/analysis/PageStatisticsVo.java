package com.meidianyi.shop.service.pojo.shop.overview.analysis;

import lombok.Data;

import java.util.List;

/**
 * 
 * @author liangchen
 * @date  2019年7月16日
 */
@Data
public class PageStatisticsVo {
    /** 开始日期 */
    private String startTime;
    /** 结束日期 */
    private String endTime;
    /** 单个页面数据 */
	private List<PageListVo> list;
}

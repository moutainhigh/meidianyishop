package com.meidianyi.shop.service.pojo.shop.market.channel;

import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月14日
 */
@Data
@NoArgsConstructor
public class ChannelPageParam {
	/** 渠道页名称 */
	private String channelName;
	/** 来源页面 */
	private String sourcePage;
	/**  来源类型 0自定义 1商品 */
	private Byte sourceType;
	/** 注册时间 起始搜索值 */
	private Timestamp startTime;
	/** 注册时间 终止搜索值 */
	private Timestamp endTime;
	
	/**
     * 	分页信息
     */
    private int currentPage = Page.DEFAULT_CURRENT_PAGE;
    private int pageRows = Page.DEFAULT_PAGE_ROWS;
}


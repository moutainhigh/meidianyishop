package com.meidianyi.shop.service.pojo.shop.market.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月19日
 * 渠道页面数据统计 页面下拉框
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPageInfo{
	/** 渠道ID */
	private Integer id;
	/** 源页面名称 */
	private String pageName;
}
package com.meidianyi.shop.service.pojo.shop.market.channel;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月27日
 * 渠道页面分析页面折线图显示数据 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelStatisticalSeriesVo {
	/** 渠道名称 */
	private String channelName;
	/** 访问量数据 */
	private List<Integer> accessData = new LinkedList<>();
}


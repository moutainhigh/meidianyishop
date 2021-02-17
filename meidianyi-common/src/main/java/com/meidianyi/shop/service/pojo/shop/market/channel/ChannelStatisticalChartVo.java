package com.meidianyi.shop.service.pojo.shop.market.channel;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月27日
 * 渠道页面分析折线图数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelStatisticalChartVo {
	/** 渠道页面名称，最后两个元素为总量和无渠道(legend)*/
	private List<String> channelNameList = new LinkedList<>();
	/** 图表横坐标 日期 */
	private List<Date> dateList = new LinkedList<>();
	/** 渠道访问量数据 */
	private List<ChannelStatisticalSeriesVo> series = new LinkedList<>();
	/** 渠道页面分析 统计页面中的表格数据 */
	private List<ChannelStatisticalInfoVo> statisticalList = new LinkedList<>();
	
	/**
	 * 根据传入的渠道页面访问数和总访问数据 计算出无渠道访问数据 
	 * @param channelSeries
	 * @param allSeries
	 * @param noChannelSeriesName
	 * @return
	 */
	public ChannelStatisticalSeriesVo calNoChannelSeries(List<ChannelStatisticalSeriesVo> channelSeries,ChannelStatisticalSeriesVo allSeries,String noChannelSeriesName) {
		ChannelStatisticalSeriesVo noSeries = new ChannelStatisticalSeriesVo();
		noSeries.setChannelName(noChannelSeriesName);
		List<Integer> allAccessData = allSeries.getAccessData();
		List<Integer> noAccessData = noSeries.getAccessData();
		for (int i = 0; i < allAccessData.size(); i++) {
			int data = allAccessData.get(i);
			for (ChannelStatisticalSeriesVo channelSeriesVo : channelSeries) {
				data -= channelSeriesVo.getAccessData().get(i);
			}
			noAccessData.add(data);
		}
		return noSeries;
	}
}


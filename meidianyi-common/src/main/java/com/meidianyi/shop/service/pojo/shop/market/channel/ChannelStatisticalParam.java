package com.meidianyi.shop.service.pojo.shop.market.channel;


import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月16日
 * 渠道页面数据统计 查询参数
 */
@Data
@NoArgsConstructor
public class ChannelStatisticalParam {
	/** 时间范围类型 2: 最近7天 ，3：最近30天，4，自定义时间 */
	@NotNull
	private Byte timeType =ChannelStatisticalConstant.TIME_TYPE_WEEK;
	/** 开始时间 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	/** 截至时间 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	/** 查询指标 访问次数0 访问人数 1 默认为0 */
	private Byte indicator =ChannelConstant.ACCESS_TIMES;
	/** 访客类型 */
	private Byte visitorType = ChannelConstant.ALL_VISITOR;
	/** 查询页面ID */
	@NotNull
	private Integer channelId;
	
}


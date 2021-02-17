package com.meidianyi.shop.service.pojo.shop.market.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月28日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelStatisticalInfoVo {
	/** 渠道名称 */
	private String name;
	/** 访问次数 （新客，老客都包含在内 */
	private Integer allPv = 0;
	/** 访问人数 */
	private Integer allUv = 0;
	/** 新客访问次数 */
	private Integer newPv = 0;
	/** 老客访问次数 */
	private Integer oldPv = 0;
}

package com.meidianyi.shop.service.pojo.shop.market.integration;

import lombok.Data;

/**
 * 小程序端积分规则显示
 * @author zhaojianqiang
 * @time   上午10:23:59
 */
@Data
public class GroupInteMaVo {
	private Short limitAmount;
	private Integer inteGroup;
	private Short joinLimit;
	private Byte divideType;
	private ActivityCopywriting activityInfo;
}

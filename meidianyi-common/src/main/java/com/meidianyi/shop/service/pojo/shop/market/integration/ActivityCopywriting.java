package com.meidianyi.shop.service.pojo.shop.market.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 瓜分积分的活动规则说明
 * @author zhaojianqiang
 * @time   上午10:51:05
 */
@Data
public class ActivityCopywriting {
	private String document;
	@JsonProperty("is_use_default")
	private String isUseDefault;
}

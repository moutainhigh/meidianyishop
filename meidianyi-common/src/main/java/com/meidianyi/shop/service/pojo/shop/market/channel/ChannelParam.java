package com.meidianyi.shop.service.pojo.shop.market.channel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月15日
 */
@Data
@NoArgsConstructor
public class ChannelParam {
	/** 渠道页面名称 */
	@NotBlank
	private String channelName;
	/** 渠道来源页面类型 0：自定义，1：商品 */ 
	@NotNull
	@Min(0)@Max(1)
	private Byte sourceType;
	/** 当页面类型为0时，填页面ID，当页面类型为1时填商品ID */
	private Integer contentId;
}


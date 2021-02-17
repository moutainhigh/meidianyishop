package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.math.BigDecimal;

/**
 * reward_content字段的类
 * 
 * @author zhaojianqiang
 * @time 下午5:53:37
 */
@Data
public class FpRewardContent {

	@JsonProperty(value = "goods_ids")
	private Integer goodsIds;
	@JsonProperty(value = "reward_ids")
	private Integer rewardIds;
	@JsonProperty(value = "market_price")
	private BigDecimal marketPrice;
	@JsonProperty(value = "market_store")
	private Integer marketStore;
}

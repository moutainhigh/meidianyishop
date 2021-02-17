package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 小程序拼团抽奖参团详情入参
 * 
 * @author zhaojianqiang
 * @time 下午2:18:35
 */
@Data
public class GroupDrawInfoParam {
	@NotNull
	@JsonProperty(value = "group_draw_id")
	private Integer groupDrawId;
	@NotNull
	@JsonProperty(value = "goods_id")
	private Integer goodsId;
	@NotNull
	@JsonProperty(value = "group_id")
	private Integer groupId;
	@NotNull
	private Map<String, String> options;
}

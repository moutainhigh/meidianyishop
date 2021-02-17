package com.meidianyi.shop.service.pojo.saas.shop.version;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author 新国
 *
 */
@Data
public class VersionNumberConfig {
	@JsonProperty(value = "picture_num")
	public Integer pictureNum = -1;

	@JsonProperty(value = "video_num")
	public Integer videoNum = -1;

	@JsonProperty(value = "goods_num")
	public Integer goodsNum = -1;

	@JsonProperty(value = "store_num")
	public Integer storeNum = -1;

	@JsonProperty(value = "decorate_num")
	public Integer decorateNum = -1;

	@JsonProperty(value = "form_num")
	public Integer formNum = -1;

	@JsonProperty(value = "picture_num_plus")
	public Integer pictureNumPlus = 0;

	@JsonProperty(value = "video_num_plus")
	public Integer videoNumPlus = 0;

	@JsonProperty(value = "goods_num_plus")
	public Integer goodsNumPlus = 0;

	@JsonProperty(value = "store_num_plus")
	public Integer storeNumPlus = 0;

	@JsonProperty(value = "decorate_num_plus")
	public Integer decorateNumPlus = 0;

	@JsonProperty(value = "form_num_plus")
	public Integer formNumPlus = 0;
}

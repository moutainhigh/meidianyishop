package com.meidianyi.shop.service.pojo.shop.config.store;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月11日
 */
@Data
public class StoreServiceConfig {

	/**
	 *  门店服务评论配置：0不用审核    1先发后审   2先审后发
	 */
	@JsonProperty(value = "service_comment")
	public Byte serviceComment;

	/**
	 *  门店买单开关 0或1
	 */
	@JsonProperty(value = "store_buy")
	public Byte storeBuy;
	
	/**
	 *  门店职称配置
	 */
	@JsonProperty(value = "technician_title")
	public String technicianTitle;
	
	/**
	 *   选择开启“扫码购”功能的门店ID列表，逗号分隔
	 */
	@JsonProperty(value = "store_scan_ids")
	public String storeScanIds;

	@JsonProperty(value = "store_distance")
	public Double storeDistance;
	
	
}

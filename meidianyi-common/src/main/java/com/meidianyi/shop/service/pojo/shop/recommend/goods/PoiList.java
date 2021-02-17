package com.meidianyi.shop.service.pojo.shop.recommend.goods;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年11月11日 下午3:14:16
 */
@Data
public class PoiList {

	private Double longitude;
	private Double latitude;
	private Byte radius;
	@JsonProperty(value = "business_name")
	private String businessName;
	@JsonProperty(value = "branch_name")
	private String branchName;
	private String address;
}

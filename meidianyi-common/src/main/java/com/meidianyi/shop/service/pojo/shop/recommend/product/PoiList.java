/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.product;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Auto-generated: 2019-11-18 9:48:1
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
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
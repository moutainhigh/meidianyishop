/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Auto-generated: 2019-11-12 10:52:53
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class ExpressGoodsInfoList {

	@JsonProperty(value = "item_code")
    private String itemCode;
	
	@JsonProperty(value = "sku_id")
    private String skId;

}
/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.collect;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auto-generated: 2019-11-13 16:15:35
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonCollectBean {

	@JsonProperty(value = "user_open_id")
    private String userOpenId;
	
	@JsonProperty(value = "sku_product_list")
    private List<SkuProductList> skuProductList;
}
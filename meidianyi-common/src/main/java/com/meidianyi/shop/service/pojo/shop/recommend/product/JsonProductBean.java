/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.product;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auto-generated: 2019-11-18 9:48:1
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonProductBean {

	@JsonProperty(value = "product_list")
    private List<ProductList> productList;

}
/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.product;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.recommend.SkuAttrList;

import lombok.Data;

/**
 * Auto-generated: 2019-11-18 9:48:1
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SkuList {

	@JsonProperty(value = "sku_id")
    private String skuId;
	
    private BigDecimal price;
    
    @JsonProperty(value = "original_price")
    private BigDecimal originalPrice;
    
    private int status;
    
    @JsonProperty(value = "sku_attr_list")
    private List<SkuAttrList> skuAttrList;
    
    private int version;
    
    @JsonProperty(value = "poi_list")
    private List<PoiList> poiList;
    
    @JsonProperty(value = "bar_code_info")
    private BarCodeInfo barCodeInfo;
}
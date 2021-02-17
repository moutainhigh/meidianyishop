/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.collect;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.recommend.SkuAttrList;

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
public class SkuInfo {

	@JsonProperty(value = "sku_id")
    private String skuId;
	
    private BigDecimal price;
    
    @JsonProperty(value = "original_price")
    private BigDecimal originalPrice;
    
    @JsonProperty(value = "bar_code_info")
    private BarCodeInfo barCodeInfo;
    private int status;
    
    @JsonProperty(value = "poi_list")
    private List<PoiList> poiList;
    
    @JsonProperty(value = "sku_attr_list")
    private List<SkuAttrList> skuAttrList;
    
    private int version;

}
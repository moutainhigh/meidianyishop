/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.order;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Auto-generated: 2019-11-12 10:52:53
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class ItemList {

	@JsonProperty(value = "item_code")
    private String itemCode;
	
	@JsonProperty(value = "sku_id")
    private String skuId;
	
    private int amount;
    
    @JsonProperty(value = "total_fee")
    private BigDecimal totalFee;
    
    @JsonProperty(value = "thumb_url")
    private String thumbUrl;
    
    private String title;
    private String desc;
    
    @JsonProperty(value = "unit_price")
    private BigDecimal unitPrice;
    
    @JsonProperty(value = "original_price")
    private BigDecimal originalPrice;
    
    @JsonProperty(value = "poi_list")
    private List<PoiList> poiList;
    
    @JsonProperty(value = "stock_attr_info")
    private List<StockAttrInfo> stockAttrInfo;
    
    @JsonProperty(value = "category_list")
    private List<String> categoryList;
    
    @JsonProperty(value = "item_detail_page")
    private ItemDetailPage itemDetailPage;
    
    @JsonProperty(value = "bar_code_info")
    private BarCodeInfo barCodeInfo;
    
    @JsonProperty(value = "platform_category_list")
    private List<PlatformCategoryList> platformCategoryList;

}
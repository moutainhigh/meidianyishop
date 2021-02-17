/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.collect;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Auto-generated: 2019-11-13 16:15:35
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SkuProductList {

	@JsonProperty(value = "item_code")
    private String itemCode;
	
    private String title;
    private String desc;
    
    @JsonProperty(value = "category_list")
    private List<String> categoryList;
    
    @JsonProperty(value = "image_list")
    private List<String> imageList;
    
    @JsonProperty(value = "src_wxapp_path")
    private String srcWxappPath;
    
    @JsonProperty(value = "attr_list")
    private List<Attrlist> attrList;
    
    private int version;
    
    @JsonProperty(value = "update_time")
    private long updateTime;
    
    @JsonProperty(value = "platform_category_list")
    private List<PlatformCategoryList> platformCategoryList;
    
    @JsonProperty(value = "sku_info")
    private SkuInfo skuInfo;
    
    @JsonProperty(value = "can_be_search")
    private boolean canBeSearch;
    
    @JsonProperty(value = "brand_info")
    private BrandInfo brandInfo;
    /**
     * 删除里面有的
     */
    @JsonProperty(value = "sku_id")
    private String skuId;
}
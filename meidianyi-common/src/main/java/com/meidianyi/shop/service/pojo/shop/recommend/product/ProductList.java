/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.product;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Auto-generated: 2019-11-18 9:48:1
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class ProductList {

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
    private List<AttrList> attrList;
    
    private int version;
    
    @JsonProperty(value = "sku_list")
    private List<SkuList> skuList;
    
    @JsonProperty(value = "platform_category_list")
    private List<PlatformCategoryList> platformCategoryList;

}
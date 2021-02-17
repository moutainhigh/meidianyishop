package com.meidianyi.shop.service.pojo.shop.recommend.goods;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 好物圈数据组装类
 * 
 * @author zhaojianqiang
 *
 *         2019年11月11日 下午3:05:34
 */
@Data
public class Product {
	@JsonProperty(value = "item_code")
	private String itemCode;
	
	@JsonProperty(value = "title")
	private String title;
	
	@JsonProperty(value = "desc")
	private String desc;
	
	@JsonProperty(value = "category_list")
	private List<String> categoryList;
	
	@JsonProperty(value = "image_list")
	private List<String> imageList;
	
	@JsonProperty(value = "src_mini_program_path")
	private String srcMiniProgramPath;
	
	@JsonProperty(value = "sku_list")
	private List<SkuList> skuList;
	
	@JsonProperty(value = "brand_info")
	private BrandInfo brandInfo;
}

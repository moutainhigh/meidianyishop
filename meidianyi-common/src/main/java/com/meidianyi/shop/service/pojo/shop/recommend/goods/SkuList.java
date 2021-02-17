package com.meidianyi.shop.service.pojo.shop.recommend.goods;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.recommend.SkuAttrList;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年11月11日 下午3:09:48
 */
@Data
public class SkuList {
	@JsonProperty(value = "sku_id")
	private String skuId;
	
	@JsonProperty(value = "price")
	private BigDecimal price;
	
	@JsonProperty(value = "original_price")
	private BigDecimal originalPrice;
	
	@JsonProperty(value = "status")
	private Byte status;
	
	@JsonProperty(value = "poi_list")
	private List<PoiList> poiList;
	
	@JsonProperty(value = "sku_attr_list")
	private List<SkuAttrList> skuAttrList; 
}

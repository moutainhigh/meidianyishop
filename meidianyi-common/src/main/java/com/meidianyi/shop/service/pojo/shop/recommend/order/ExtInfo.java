/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年11月12日 上午10:56:40
 */
@Data
public class ExtInfo {

	@JsonProperty(value = "product_info")
    private ProductInfo productInfo;
	
	@JsonProperty(value = "express_info")
    private ExpressInfo expressInfo;
	
	@JsonProperty(value = "promotion_info")
    private PromotionInfo promotionInfo;
	
	@JsonProperty(value = "brand_info")
    private BrandInfo brandInfo;
	
	@JsonProperty(value = "invoice_info")
    private InvoiceInfo invoiceInfo;
	
	@JsonProperty(value = "payment_method")
    private int paymentMethod;
	
	@JsonProperty(value = "user_open_id")
    private String userOpenId;
	
	@JsonProperty(value = "order_detail_page")
    private OrderDetailPage orderDetailPage;

}
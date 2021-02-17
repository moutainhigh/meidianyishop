package com.meidianyi.shop.service.pojo.wxapp.decorate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.wxapp.config.ShareConfig;
import com.meidianyi.shop.service.pojo.wxapp.coupon.SendCoupon;
import com.meidianyi.shop.service.pojo.wxapp.coupon.ShopCollectInfo;
import lombok.Data;

import java.util.Map;

/**
 * 
 * @author lixinguo
 *
 */
@Data
public class WxAppPageVo {
	@JsonProperty(value = "page_info")
	Map<String,Object> pageInfo;
	
	@JsonProperty(value = "page_id")
	Integer pageId;
	
	@JsonProperty(value = "page_name")
	String pageName;
	
	@JsonProperty(value = "page_type")
	Byte pageType;
	
	@JsonProperty(value = "is_first_page")
	Byte isFirstPage;
	
	@JsonProperty(value = "scene")
	String scene;
	
	@JsonProperty(value = "send_coupon")
	SendCoupon sendCoupon = new SendCoupon();
	
	@JsonProperty(value = "is_pictorial_show")
	Byte isPictorialShow = 0;
	
	@JsonProperty(value = "shop_collect")
	ShopCollectInfo collectInfo = new ShopCollectInfo();
	
	@JsonProperty(value = "share_info")
	ShareConfig shareInfo = new ShareConfig();
}

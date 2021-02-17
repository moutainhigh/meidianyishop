package com.meidianyi.shop.service.pojo.wxapp.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 小程序前端店铺分享配置
 * 
 * @author lixinguo
 */
@Data
public class ShareConfig {

	/**
	 * 分享类型：shop
	 */
	@JsonProperty(value = "module_action")
	String moduleAction = "shop";

	/**
	 * 分享样式：1默认样式，2自定义样式
	 */
	@JsonProperty(value = "share_action")
	Byte shareAction = 1;

	/**
	 * 分享文案
	 */
	@JsonProperty(value = "share_doc")
	String shareDoc;

	/**
	 *  分享图类型：1店铺首页截图，2自定义图片
	 */
	@JsonProperty(value = "share_img_action")
	Byte shareImgAction;

	/**
	 * 自定义图片链接
	 */
	@JsonProperty(value = "share_img")
	String shareImg;
	
	/**
	 * 分享文案,与shareDoc相同，用于输出
	 */
	@JsonProperty(value = "share_title")
	String shareTitle;

}

package com.meidianyi.shop.service.pojo.shop.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 王兵兵
 * @description	{"share_action":"2","share_doc":"\u5e97\u94faaa","share_img_action":"1","share_img":null}
 * 2019年7月9日
 */
@Data
@Component
public class ShopShareConfig {
	
	/**
	 * 分享样式
	 * 1默认样式    2自定义样式
	 */
	@JsonProperty(value = "share_action")
	public Byte shareAction = 1;
	
	/**
	 * 分享文案
	 *  
	 */
	@JsonProperty(value = "share_doc")
	public String shareDoc = "";
	
	/**
	 * 分享图类型
	 *  1店铺首页截图    2自定义图片
	 */
	@JsonProperty(value = "share_img_action")
	public Byte shareImgAction = 1;
	
	/**
	 * 分享图路径,当为NULL时默认店铺首页截图
	 *  
	 */
	@JsonProperty(value = "share_img")
	public String shareImg = "";
}

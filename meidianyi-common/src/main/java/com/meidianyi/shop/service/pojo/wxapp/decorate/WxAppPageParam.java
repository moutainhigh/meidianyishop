package com.meidianyi.shop.service.pojo.wxapp.decorate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppCommonParam;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class WxAppPageParam extends WxAppCommonParam {

	/**
	 * 装修页面Id，为0时代表首页
	 */
	@JsonProperty(value = "page")
	Integer pageId = 0;
	
	/**
	 * 场景值
	 */
	@JsonProperty(value = "scene")
	String scene;
	
}

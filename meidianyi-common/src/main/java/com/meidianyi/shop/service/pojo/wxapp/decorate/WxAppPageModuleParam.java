package com.meidianyi.shop.service.pojo.wxapp.decorate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class WxAppPageModuleParam extends WxAppPageParam {
	/**
	 * 页面模块索引
	 */
	@JsonProperty(value = "idx")
	String moduleIndex;
}

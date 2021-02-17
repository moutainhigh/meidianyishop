package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
@Data
public class ModuleBase {

	/**
	 * 当前模块索引
	 */
	@JsonProperty(value = "cur_idx")
	String curIndex;
	
	/**
	 * 模块名称
	 */
	@JsonProperty(value = "module_name")
	String moduleName;
	
	/**
	 * 模块是否需要单独请求
	 */
	@JsonProperty(value = "need_request")
	Boolean needRequest = false;
	
}

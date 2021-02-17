package com.meidianyi.shop.service.pojo.shop.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年7月22日 下午1:41:15
 */
@Data
public class ShopMsgTempConfig {

	@JsonProperty(value = "A")
	public String[] a;
	@JsonProperty(value = "B")
	public String[] b;

}

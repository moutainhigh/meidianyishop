package com.meidianyi.shop.service.pojo.shop.store.authority;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * shop表中的STORE_CLERK_PRIVILEGE_LIST字段对应类
 * 
 * @author zhaojianqiang
 * @time 上午11:13:13
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreCfgParam {

	@JsonProperty("menu_cfg")
	private List<String> menuCfg;

	@JsonProperty("sub_menu_cfg")
	private List<String> subMenuCfg;

	@JsonProperty("fun_cfg")
	private List<String> funCfg;

}

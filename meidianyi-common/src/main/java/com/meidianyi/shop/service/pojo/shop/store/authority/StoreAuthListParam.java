package com.meidianyi.shop.service.pojo.shop.store.authority;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 * @time   下午3:22:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreAuthListParam {
	@NotNull
	@JsonProperty("menu_cfg")
	private List<String> menuCfg;

	@NotNull
	@JsonProperty("sub_menu_cfg")
	private List<String> subMenuCfg;

	@NotNull
	@JsonProperty("fun_cfg")
	private List<String> funCfg;
}

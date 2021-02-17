package com.meidianyi.shop.service.pojo.saas.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionMainConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionNumberConfig;

import lombok.Data;
/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
public class VersionEditParam {
	@JsonProperty(value = "main_config")
	public VersionMainConfig mainConfig = new VersionMainConfig();

	@JsonProperty(value = "num_config")
	public VersionNumberConfig numConfig = new VersionNumberConfig();
	
	public Integer shopId;
}

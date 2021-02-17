package com.meidianyi.shop.service.pojo.saas.shop.version;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author 新国
 *
 */
@Data
public class VersionConfig {
	@JsonProperty(value = "main_config")
	public VersionMainConfig mainConfig = new VersionMainConfig();

	@JsonProperty(value = "num_config")
	public VersionNumberConfig numConfig = new VersionNumberConfig();
	
	private String shopType;
	private String versionName;
}

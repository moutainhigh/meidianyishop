package com.meidianyi.shop.service.pojo.saas.shop.version;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author 新国
 *
 */
@Data
public class VersionMainConfig {
	@JsonProperty(value = "sub_0")
	public List<String> sub0 = new ArrayList<String>();

	@JsonProperty(value = "sub_1")
	public List<String> sub1 = new ArrayList<String>();

	@JsonProperty(value = "sub_2")
	public List<String> sub2 = new ArrayList<String>();

	@JsonProperty(value = "sub_3")
	public List<String> sub3 = new ArrayList<String>();

	@JsonProperty(value = "sub_4")
	public List<String> sub4 = new ArrayList<String>();

	@JsonProperty(value = "sub_5")
	public List<String> sub5 = new ArrayList<String>();
}

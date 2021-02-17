package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class MenuInnerParam {
	@JsonProperty("en_name")
	private String enName;
	
	@JsonProperty("pre_name")
	private List<String> preName;


}

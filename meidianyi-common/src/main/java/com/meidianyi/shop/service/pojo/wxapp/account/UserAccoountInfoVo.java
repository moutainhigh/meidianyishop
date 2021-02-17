package com.meidianyi.shop.service.pojo.wxapp.account;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年10月12日 下午4:22:28
 */
@Data
public class UserAccoountInfoVo {

	@JsonProperty(value = "module_data")
	private List<Map<String, Object>> moduleData;
	
	@JsonProperty(value = "other_data")
	private String[] otherData;
}

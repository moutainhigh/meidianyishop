package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopSelectResp{

	public List<?> dataList;
	public Map<String, String> versionMap;
	

}

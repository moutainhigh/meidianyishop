package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年7月31日 下午3:10:22
 */
@Data
@NoArgsConstructor
public class ShopPriPassParam {
	public String prName;
	public String name;
	public List<String> includeApi;
	public Integer topIndex;
}

package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年7月31日 下午3:10:29
 */
@Data
@NoArgsConstructor
public class ShopMenuParam {
	public String enName;
	public String name;
	public String linkUrl;
	public Integer topIndex;
	public List<String> includeApi;
}

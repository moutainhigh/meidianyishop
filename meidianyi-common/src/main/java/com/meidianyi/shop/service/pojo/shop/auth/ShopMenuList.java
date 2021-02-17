package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年10月28日 下午3:22:57
 */
@Data
public class ShopMenuList {

	private List<List<ShopMenuParam>> shopMenuList;
	private List<List<ShopPriPassParam>> shopPriPassList;

}

package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年11月4日 下午1:47:18
 */
@Data
@NoArgsConstructor
public class ShopVersionListVo {
	@JsonProperty(value = "sub_0")
	public List<ShopVersionParam> sub0 = new ArrayList<ShopVersionParam>();

	@JsonProperty(value = "sub_1")
	public List<ShopVersionParam> sub1 = new ArrayList<ShopVersionParam>();

	@JsonProperty(value = "sub_2")
	public List<ShopVersionParam> sub2 = new ArrayList<ShopVersionParam>();

	@JsonProperty(value = "sub_3")
	public List<ShopVersionParam> sub3 = new ArrayList<ShopVersionParam>();

	@JsonProperty(value = "sub_4")
	public List<ShopVersionParam> sub4 = new ArrayList<ShopVersionParam>();

	@JsonProperty(value = "sub_5")
	public List<ShopVersionParam> sub5 = new ArrayList<ShopVersionParam>();
}

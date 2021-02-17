package com.meidianyi.shop.service.pojo.shop.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 王兵兵
 * @description 购买按钮样例{"show_cart":1,"cart_type":"3"} 
 *
 * 2019年7月9日
 */
@Data
@Component
public class ShowCartConfig {
	/**
	 * 商品搜索页以及推荐商品列表中会显示购买按钮
	 *  0：关，1：开
	 */
	@JsonProperty(value = "show_cart")
	public Byte showCart = 0;
	
	/**
	 * 购买按钮类型
	 *  0，1，2，3  四种类型
	 */
	@JsonProperty(value = "cart_type")
	public Integer cartType = 0;

}

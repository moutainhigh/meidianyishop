package com.meidianyi.shop.service.pojo.shop.config;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import lombok.Data;

/**
 * 
 * @author 新国
 * @description 店铺风格样例 {"shopStyleId":"5","shopStyleValue":"#feb609,#333333"} 
 */
@Data
public class ShopStyleConfig {
	
	/**
	 * 店铺风格ID
	 */
	@NotNull(message = JsonResultMessage.DECORATE_STYLE_PARAM_UPDATE_ID_NULL)
	public Integer shopStyleId;
	
	/**
	 * 店铺风格值
	 */
	@NotBlank(message = JsonResultMessage.DECORATE_STYLE_PARAM_UPDATE_VALUE_NULL)
	public String shopStyleValue;
}

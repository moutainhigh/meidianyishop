package com.meidianyi.shop.service.pojo.shop.goods.deliver;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
/**
 * @author liangchen
 * @date 2019年7月12日
 */

@Data
public class GoodsDeliverTemplateFeeParam {
	@JsonProperty(value = "has_fee_0_condition")
	private Integer hasFee0Condition;
	
	
}

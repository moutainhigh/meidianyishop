package com.meidianyi.shop.service.pojo.shop.member.card.show;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;

import lombok.Data;

/**
 * 	用户卡能够使用，不能使用原因
 * @author 黄壮壮
 */
@Data
public class CardUseCondition {
	/**
	 * 	是否能使用 true: 可以，false 不能使用
	 */
	private boolean usable;
	
	 /**
	  * 不能使用使用的原因
	  */
	 private JsonResultCode errorCode;
}	

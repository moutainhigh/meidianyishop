package com.meidianyi.shop.service.pojo.shop.goods.label;

import lombok.Getter;

/**
 * @author 黄荣刚
 * @date 2019年7月11日
 *
 */
@Getter
public enum GoodsLabelTypeEnum {
	
	//指定商品
	PART((byte)0),
	
	//	全部商品
	ALL((byte)1);
	private Byte code;
	GoodsLabelTypeEnum(byte code) {
		this.code = code;
	}
}

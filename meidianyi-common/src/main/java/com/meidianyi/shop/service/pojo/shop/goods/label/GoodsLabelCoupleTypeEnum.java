package com.meidianyi.shop.service.pojo.shop.goods.label;

/**
 * @author 黄荣刚
 * @date 2019年7月11日
 *
 */
public enum GoodsLabelCoupleTypeEnum{
	//关联商品
	GOODSTYPE(1),
	//平台分类
	CATTYPE(2),
	//商家分类
	SORTTYPE(3),
	//全部商品
	ALLTYPE(4);
	
	private Byte code;
	
	GoodsLabelCoupleTypeEnum(int code){
		this.code = (byte) code;
	}
	
	public Byte getCode() {
		return code;
	}
}

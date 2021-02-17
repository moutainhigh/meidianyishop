package com.meidianyi.shop.service.pojo.shop.store.comment;

/**
 * @author 黄荣刚
 * @date 2019年7月18日
 *
 */
public enum CommentFlagEnum {

	UNAPPROVAL((byte)0),
	PASS((byte)1),
	REFUSE((byte)2);
	
	private Byte value;
	
	CommentFlagEnum(Byte value){
		this.value = value;
	}
	public Byte getValue() {
		return value;
	}
}

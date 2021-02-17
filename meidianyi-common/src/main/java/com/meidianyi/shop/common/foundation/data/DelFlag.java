package com.meidianyi.shop.common.foundation.data;

import lombok.Getter;
/**
 * 删除标识枚举类
 * @author wangshuai
 *
 */
@Getter
public enum DelFlag {
	//正常状态
	NORMAL((byte)0),
	//删除状态
	DISABLE((byte)1);
	
	private byte code; 


	private DelFlag(byte code) {
		this.code = code;
	}

    /**
     *  数据库记录假删除时，在记录唯一字段添加的前缀值
     * @author 李晓冰
     */
    public static final String DEL_ITEM_SPLITER="_";
	public static final String DEL_ITEM_PREFIX="del"+DEL_ITEM_SPLITER;
    /**
     *  数据库假删除标志，jooq中该字段设置值的时候使用枚举会报错。
     * @author 李晓冰
     */
    public static final Byte NORMAL_VALUE=0;
	public static final Byte DISABLE_VALUE=1;

}

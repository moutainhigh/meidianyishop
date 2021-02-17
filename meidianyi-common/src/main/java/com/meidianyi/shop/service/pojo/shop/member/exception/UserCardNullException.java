package com.meidianyi.shop.service.pojo.shop.member.exception;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.foundation.exception.MpException;

/**
* @author 黄壮壮
* @Date: 2019年10月31日
* @Description: 用户卡为空
*/
public class UserCardNullException extends MpException {
	public UserCardNullException() {
		super(JsonResultCode.CODE_USER_CARD_NONE);
	}
	public UserCardNullException(JsonResultCode errorCode) {
		super(errorCode);
	}

}

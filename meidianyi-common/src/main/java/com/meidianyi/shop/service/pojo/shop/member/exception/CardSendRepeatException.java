package com.meidianyi.shop.service.pojo.shop.member.exception;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.foundation.exception.MpException;

/**
* @author 黄壮壮
* @Date: 2019年11月4日
* @Description: 会员卡不能重复发放
*/
public class CardSendRepeatException extends MpException {
	public CardSendRepeatException() {
		super(JsonResultCode.CODE_CARD_SEND_REPEAT);
	}
	public CardSendRepeatException(JsonResultCode errorCode) {
		super(errorCode);
	}
}

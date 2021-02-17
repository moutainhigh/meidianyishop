package com.meidianyi.shop.service.pojo.shop.member.exception;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.foundation.exception.MpException;

/**
* @author 黄壮壮
* @Date: 2019年11月4日
* @Description: 限次卡发放领取次数已达上限
*/
public class LimitCardAvailSendNoneException extends MpException {
	public LimitCardAvailSendNoneException() {
		super(JsonResultCode.CODE_LIMIT_CARD_AVAIL_SEND_NONE);
	}
	public LimitCardAvailSendNoneException(JsonResultCode errorCode) {
		super(errorCode);
	}
}

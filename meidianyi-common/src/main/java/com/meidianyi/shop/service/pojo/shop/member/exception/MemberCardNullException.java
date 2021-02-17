package com.meidianyi.shop.service.pojo.shop.member.exception;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.foundation.exception.MpException;

/**
* @author 黄壮壮
* @Date: 2019年11月1日
* @Description: 会员卡为空
*/
public class MemberCardNullException extends MpException {
	public MemberCardNullException() {
		super(JsonResultCode.CODE_MEMBER_CARD_DELETE);
	}
	public MemberCardNullException(JsonResultCode errorCode) {
		super(errorCode);
	}
}

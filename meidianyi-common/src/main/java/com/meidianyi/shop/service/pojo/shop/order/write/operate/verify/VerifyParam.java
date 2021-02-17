package com.meidianyi.shop.service.pojo.shop.order.write.operate.verify;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;

import lombok.Getter;
import lombok.Setter;
/**
 * 核销参数
 * @author 王帅
 *
 */
@Getter
@Setter
public class VerifyParam extends OrderOperateQueryParam{
	@NotNull(message = JsonResultMessage.MSG_ORDER_VERIFY_IFCHECK_NOT_NULL)
	private Boolean isCheck;
	private String verifyCode;
	@NotNull
	private Byte platform;

}

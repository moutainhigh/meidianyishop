package com.meidianyi.shop.service.pojo.shop.order.write.operate.extend;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;

import lombok.Getter;
import lombok.Setter;

/**
 * admin延收货
 * 
 * @author 王帅
 *
 */
@Getter
@Setter
public final class ExtendReceiveParam extends OrderOperateQueryParam{
	@NotNull(message = JsonResultMessage.MSG_ORDER_EXTEND_RECEIVE_TIME_NOT_NULL)
	public Timestamp extendTime;
}

package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author huangronggang
 * @date 2019年8月2日
 */
@Data
@NoArgsConstructor
public class CouponPackOrderRefundParam {
	
	/** 订单ID */
	@NotNull
	private Integer orderId;
	
	/** 订单号 */
    @NotNull
	private String orderSn;

    @NotNull
	private VirtualOrderRefundParam virtualOrderRefundParam;

	/** 退款后是否仍然发放优惠劵 */
	private Byte stillSendFlag=1;
}


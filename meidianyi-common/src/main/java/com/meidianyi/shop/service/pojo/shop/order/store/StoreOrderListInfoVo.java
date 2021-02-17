package com.meidianyi.shop.service.pojo.shop.order.store;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 买单订单列表展示订单信息
 * @author wangshuai
 */

@Data
public class StoreOrderListInfoVo {

	private Integer orderId;
	private String orderSn;
	private Byte orderStatus;
	private Integer storeId;
	private String username;
	private Timestamp payTime;
	private BigDecimal moneyPaid;
	private String payCode;
	private String currency;
}

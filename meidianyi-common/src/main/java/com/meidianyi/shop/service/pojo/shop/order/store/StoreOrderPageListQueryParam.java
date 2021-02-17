package com.meidianyi.shop.service.pojo.shop.order.store;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @author wangshuai
 */
@Data
public class StoreOrderPageListQueryParam {
	public Integer currentPage;
	public Integer pageRows;
	private String orderSn;
	private Timestamp payTimeStart;
	private Timestamp payTimeEnd;
	private String userName;
	private Integer storeId;
	/**订单状态:0未支付,1已支付,2已退款*/
	private Byte[] orderStatus;
}

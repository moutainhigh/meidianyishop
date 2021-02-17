package com.meidianyi.shop.service.pojo.shop.order.refund;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 	退款订单操记录
 * @author 王帅
 *
 */
@Data
public class OperatorRecord {
	private Byte type;
	private Byte status;
	private Timestamp createTime;
}

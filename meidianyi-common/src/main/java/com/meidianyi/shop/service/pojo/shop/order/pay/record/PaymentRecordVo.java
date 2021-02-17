package com.meidianyi.shop.service.pojo.shop.order.pay.record;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @author 王帅
 *
 */
@Data
public class PaymentRecordVo {
	private Integer id;
	private Integer shopId;
	private String paySn;
	private String payCode;
	private String payCodeAlias;
	private String tradeNo;
	private Byte trdaeStatus;
	private String trdaeOriginStatus;
	private String subject;
	private Integer quantity;
	private String orderSn;
	private String mainOrderSn;
	private BigDecimal totalFee;
	private String buyerId;
	private String buyerAccount;
	private String sellerId;
	private String sellerAccount;
	private Timestamp gmtCreate;
	private Timestamp notifyTime;
	private Timestamp gmtPayTime;
	private Timestamp gmtCloseTime;
	private Timestamp createTime;
	private String remark1;
	private String remark2;
}

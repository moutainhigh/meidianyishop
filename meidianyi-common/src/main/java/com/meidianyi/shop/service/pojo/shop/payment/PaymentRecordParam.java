package com.meidianyi.shop.service.pojo.shop.payment;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author lixinguo
 */
@Builder
@Data
public class PaymentRecordParam {
	   private String    paySn;
	    private String    payCode;
	    private String    tradeNo;
	    private Byte      trdaeStatus;
	    private String    trdaeOriginStatus;
	    private String    subject;
	    private Integer   quantity;
	    private String    orderSn;
	    private String    mainOrderSn;
	    private String    totalFee;
	    private String    buyerId;
	    private String    buyerAccount;
	    private String    sellerId;
	    private String    sellerAccount;
	    private Timestamp gmtCreate;
	    private Timestamp notifyTime;
	    private Timestamp gmtPayTime;
	    private Timestamp gmtCloseTime;
	    private Timestamp created;
	    private String    remark1;
	    private String    remark2;
}

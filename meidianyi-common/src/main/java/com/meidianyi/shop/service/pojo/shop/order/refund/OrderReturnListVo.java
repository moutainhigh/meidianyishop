package com.meidianyi.shop.service.pojo.shop.order.refund;

import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 
 * @author wangshuai
 */
@Getter
@Setter
public class OrderReturnListVo extends OrderListInfoVo {

    public static final String ORDER_USERNAME="orderUserName";
    public static final String ORDER_MOBILE="orderMobile";


	private Integer retId;
	private String returnOrderSn;
	private Timestamp applyTime;
	private BigDecimal money;
	private Byte returnType;
	private Byte reasonType;
	private String reasonDesc;
	private Timestamp shippingOrRefundTime;
    private Byte returnSource;
    private String orderUserName;
    private String orderMobile;
}

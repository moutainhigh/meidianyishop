package com.meidianyi.shop.service.pojo.shop.store.service.order;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月18日
 */
@Data
public class ServiceOrderUpdateParam {
	@NotNull
	private Integer    orderId;
    private Integer    storeId;
    @NotNull
    private String     orderSn;
    private Integer    userId;
    private Byte       orderStatus;
    private String     orderStatusName;
    private String     subscriber;
    private String     mobile;
    private Integer    serviceId;
    private Integer    technicianId;
    private String     technicianName;
    private String     serviceDate;
    private String     servicePeriod;
    private String     addMessage;
    private String     adminMessage;
    private String     verifyCode;
    private String     verifyAdmin;
    private String     payCode;
    private String     payName;
    private String     paySn;
    private BigDecimal moneyPaid;
    private BigDecimal discount;
    private Integer    couponId;
    private BigDecimal orderAmount;
    private Timestamp  payTime;
    private Timestamp  cancelledTime;
    private Timestamp  finishedTime;
    private String     prepayId;
    private Byte       delFlag;
    private Byte       verifyType;
    private String     cancelReason;
    private Byte       type;
    private Byte       verifyPay;
    private String     aliTradeNo;
}

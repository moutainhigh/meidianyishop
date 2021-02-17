package com.meidianyi.shop.service.pojo.shop.store.service.order;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 个人中心获取用户最新的预约服务
 * 
 * @author zhaojianqiang
 *
 *         2019年11月29日 下午1:10:43
 */
@Data
public class StoreUserCertVo {

	private Integer orderId;
	private Integer storeId;
	private String orderSn;
	private Integer userId;
	private Byte orderStatus;
	private String orderStatusName;
	private String subscriber;
	private String mobile;
	private Integer serviceId;
	private Integer technicianId;
	private String technicianName;
	private String serviceDate;
	private String servicePeriod;
	private String addMessage;
	private String adminMessage;
	private String verifyCode;
	private String verifyAdmin;
	private String payCode;
	private String payName;
	private String paySn;
	private BigDecimal moneyPaid;
	private BigDecimal discount;
	private Integer couponId;
	private BigDecimal orderAmount;
	private Timestamp payTime;
	private Timestamp cancelledTime;
	private Timestamp finishedTime;
	private String prepayId;
	private Byte delFlag;
	private Byte verifyType;
	private String cancelReason;
	private Byte type;
	private Byte verifyPay;
	private String aliTradeNo;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String memberCardNo;
	private BigDecimal memberCardBalance;
	private BigDecimal useAccount;
	private String serviceName;
	private BigDecimal servicePrice;
	private BigDecimal serviceSubsist;
	private String serviceImg;
	private String storeName;
	private String latitude;
	private String longitude;
	private String address;
	private String districtCode;

}

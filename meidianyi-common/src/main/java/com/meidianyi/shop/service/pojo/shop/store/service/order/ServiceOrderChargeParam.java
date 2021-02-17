package com.meidianyi.shop.service.pojo.shop.store.service.order;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月19日
 */
@Data
public class ServiceOrderChargeParam {
	
	@NotNull
	private String orderSn;
	@NotNull
	private Integer orderId;
	@NotNull
	private Integer userId;
	/**
	 *核销码 
	 */
	@NotNull
	private String verifyCode;
	
	/**
	 * 核销支付方式 0门店买单 1会员卡 2余额
	 */
	@NotNull
	@Max(2)
	@Min(0)
	private Byte verifyPay;
	
	private Integer cardId;
	
	private String cardNo;
	
	/**
	 * 使用的会员卡金额或次数
	 */
	private BigDecimal reduce;
	
	/**
	 *账户余额使用金额 
	 */
	private BigDecimal balance;
	
	/**
	 *会员卡或余额扣费原因 
	 */
	private String reason;
	
	/**
	 *扣除之前的会员账号余额 
	 */
	private BigDecimal account;
	
	/**
	 *扣除之前的会员卡使用次数剩余 
	 */
	private BigDecimal countDis;

}

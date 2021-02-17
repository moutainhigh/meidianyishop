package com.meidianyi.shop.service.pojo.shop.auth;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopRenewReq {
	@NotNull
	private Integer shopId;
	@NotNull
	private Integer sysId;
	private String mobile;
	@NotNull
	private BigDecimal renewMoney;
	@NotNull
	private Timestamp expireTime;
	private String renewDesc;
	/**
	 * 续费类型
	 */
	@NotNull
	private Byte renewType;
	/**
	 * 续费时长的年
	 */
	private Integer year;
	/**
	 * 续费时长的月
	 */
	private Integer month;
	/**
	 * 赠送类型
	 */
	@NotNull
	private Byte sendType;
	/**
	 * 续费时长的年
	 */
	private Integer sendYear;
	/**
	 * 续费时长的月
	 */
	private Integer sendMonth;
	
	
	

}

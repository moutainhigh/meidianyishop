package com.meidianyi.shop.service.pojo.shop.auth;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopRenewVo {
	private Integer id;
	private Integer shopId;
	private Integer sysId;
	private String mobile;
	private BigDecimal renewMoney;
	private Timestamp renewDate;
	private Timestamp expireTime;
	private Integer operator;
	private String operatorName;
	private String renewDesc;
	private Byte renewType;
	private String renewDuration;
	private Byte sendType;
	private String sendContent;
	private String accountName;
}

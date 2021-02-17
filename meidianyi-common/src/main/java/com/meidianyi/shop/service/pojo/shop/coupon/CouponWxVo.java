package com.meidianyi.shop.service.pojo.shop.coupon;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.ToString;

/**
 * 查询即将优惠券的vo类
 * 
 * @author zhaojianqiang
 * @time 上午10:31:47
 */
@Data
@ToString
public class CouponWxVo {
	private Integer id;
	private String couponSn;
	private Integer userId;
	private Integer actType;
	private Integer actId;
	private Timestamp startTime;
	private Timestamp endTime;
	private Byte type;
	private BigDecimal amount;
	private String actDesc;
	private Integer limitOrderAmount;
	private Byte isUsed;
	private Timestamp usedTime;
	private Byte accessMode;
	private Integer accessId;
	private Timestamp notifyTime;
	private String orderSn;
	private Byte delFlag;
	private Byte getSource;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String actName;
	private String wxOpenid;
	private String wxUnionId;

}

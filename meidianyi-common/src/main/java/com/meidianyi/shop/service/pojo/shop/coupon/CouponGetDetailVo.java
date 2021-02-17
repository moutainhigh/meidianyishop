package com.meidianyi.shop.service.pojo.shop.coupon;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author changle
 */
@Data
public class CouponGetDetailVo {
	private String userName;
	private String mobile;
	private String     couponSn;
    private Integer    userId;
    private Integer    actType;
    private Integer    actId;
    private Timestamp  startTime;
    private Timestamp  endTime;
    private Byte       type;
    private BigDecimal amount;
    private String     actDesc;
    private Integer    limitOrderAmount;
    private Timestamp  created;
    private Byte       isUsed;
    private Timestamp  usedTime;
    private Byte       accessMode;
    private Integer    accessId;
    private Timestamp  notifyTime;
    private String     orderSn;
    private Byte       delFlag;
    private Byte       getSource;
}

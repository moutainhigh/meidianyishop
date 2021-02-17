package com.meidianyi.shop.service.pojo.shop.coupon.give;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 定向发券明细
 * @author liangchen
 * @date 2019年7月30日
 */
@Data
public class CouponGiveDetailVo {
	private String username;
	private String mobile;
	private String couponName;
	private Integer accessMode;
	private Integer isUsed;
	private String orderSn;
	private Timestamp startTime;
	private Timestamp endTime;
	private Timestamp createTime;
	private Timestamp usedTime;
	private Integer delFlag;
}

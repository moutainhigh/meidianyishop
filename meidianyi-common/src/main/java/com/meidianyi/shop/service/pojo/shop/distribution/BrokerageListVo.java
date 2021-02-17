package com.meidianyi.shop.service.pojo.shop.distribution;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 佣金统计出参
 * @author 常乐
 * 2019年8月8日
 */
@Data
public class BrokerageListVo {
    private Integer partnerId;
    private Integer userId;
	private String distributorName;
	private String distributorMobile;
	private String realName;
	private String groupName;
	private String orderSn;
	/**
	 * 订单总金额
	 */
	private BigDecimal orderAmount;
	private String userMobile;
	private String orderUserName;
	/**
	 * 返利关系
	 */
	private Integer rebateLevel;
	/**
	 * 返利商品总金额
	 */
	private BigDecimal totalRebateMoney;
	/**
	 * 返利佣金金额
	 */
	private BigDecimal realRebateMoney;
	private Timestamp createTime;
	/**
	 * 返利时间
	 */
	private Timestamp rebateTime;
	/**返利状态*/
	private Integer settlementFlag;


}

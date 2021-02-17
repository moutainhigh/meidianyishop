package com.meidianyi.shop.service.pojo.shop.distribution;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 用户消费金额
 * @author 常乐
 * 2019年8月1日
 */
@Data
public class DistributorSpendVo {
	private BigDecimal card;
	private BigDecimal paid;
	private BigDecimal account;
	private BigDecimal total;
}

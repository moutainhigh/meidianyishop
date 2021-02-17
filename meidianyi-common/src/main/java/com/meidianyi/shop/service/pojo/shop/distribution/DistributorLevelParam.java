package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author changle
 */
@Data
public class DistributorLevelParam {
	private Integer id;
	private Byte levelId;
	private String levelName;
	private Integer levelUpRoute;
	private Integer inviteNumber;
	private BigDecimal totalDistributionMoney;
	private BigDecimal totalBuyMoney;
	private String levelUserIds;
	private Integer users;
	private Integer levelStatus;
}

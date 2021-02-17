package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author changle
 */
@Data
public class DistributorLevelVo {
	private Integer    id;
	private Byte       levelId;
	private String     levelName;
	private Byte       levelUpRoute;
	private Integer    inviteNumber;
	private BigDecimal totalDistributionMoney;
	private BigDecimal totalBuyMoney;
	private String     levelUserIds;
	private Byte       levelStatus;
}

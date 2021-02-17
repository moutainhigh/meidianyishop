package com.meidianyi.shop.service.pojo.shop.market.packagesale;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月13日
 * 活动明细视图
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackSaleDetailVo {
	
	private Integer userId;
	private String username;
	private String mobile;
	private String orderSn;
	private Timestamp createTime;
	private BigDecimal moneyPaid;
	
}


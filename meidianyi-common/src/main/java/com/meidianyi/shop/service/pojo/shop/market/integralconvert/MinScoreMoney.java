package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回最小积分值和对应规格的金额值
 * 
 * @author zhaojianqiang
 * @time 下午5:13:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinScoreMoney {
	private Integer minScore;
	private BigDecimal minMoney;
}

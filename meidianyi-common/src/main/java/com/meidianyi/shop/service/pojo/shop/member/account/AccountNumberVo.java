package com.meidianyi.shop.service.pojo.shop.member.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author 黄壮壮
 * 用户余额以及积分
 */
@Data
@Builder
@AllArgsConstructor
public class AccountNumberVo {
	@Builder.Default
	private BigDecimal account = BigDecimal.ZERO;
	@Builder.Default
	private Integer score = 0;
	
	public AccountNumberVo(){
		this.account = BigDecimal.ZERO;
		this.score = 0;
	}
}

package com.meidianyi.shop.service.pojo.shop.member.card;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author 黄壮壮
 * @Date: 2019年7月29日
 * @Description: 卡充值送积分策略json数据
 */
@Data
public class PowerCardJson {
	/**
	 * 充值类型 0：充值满多少送多少 ；1: 充值每满多少送多少；2：仅充值；
	 */
	private Byte offsetMoney;
	/** 充值满多少元 */
	private BigDecimal[] money;
	/** 充值满送多少元 */
	private BigDecimal[] getMoney;
	/** 每充值多少元 */
	private BigDecimal perMoney;
	/** 每充值送多少元 */
	private BigDecimal perGetMoney;
	
}

package com.meidianyi.shop.service.pojo.shop.member.card;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author 黄壮壮
 * @Date: 2019年7月29日
 * @Description: 购物送积分策略json序列化对象
 */
@Data
public class ScoreJson {
	/**
	 * 购物送积分 0：购物满多少送多少积分；1：购物每满多少送多少积分
	 */
	private Byte offset;
	/** 购物满多少 */
	private List<BigDecimal> goodsMoney;
	/** 购物满多少送多少积分 */
	private List<BigDecimal> getScores;
	/** 购物每满多少 */
	private BigDecimal perGoodsMoney;
	/** 购物每满多少送多少积分 */
	private BigDecimal perGetScores;
	
}

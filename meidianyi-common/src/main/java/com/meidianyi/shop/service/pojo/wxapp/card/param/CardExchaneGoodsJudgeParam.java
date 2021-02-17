package com.meidianyi.shop.service.pojo.wxapp.card.param;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 限次卡兑换商品判断
 * @author 黄壮壮
 *
 */
@Data
public class CardExchaneGoodsJudgeParam {
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 兑换的商品ID
	 */
	private Integer goodsId;
	/**
	 * 兑换商品数量
	 */
	private Integer goodsNumber;
	/**
	 * 卡号
	 */
	@NotNull
	private String cardNo;
	
	/**
	 * 1是不考虑已选列表，2是直接兑换
	 */
	private Byte isList;
	
}

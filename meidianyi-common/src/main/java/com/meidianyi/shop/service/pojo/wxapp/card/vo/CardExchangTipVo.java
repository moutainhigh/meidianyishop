package com.meidianyi.shop.service.pojo.wxapp.card.vo;

import lombok.Data;

/**
 * 会员卡兑换商品使用情况
 * @author 黄壮壮
 *
 */
@Data
public class CardExchangTipVo {
	/**
	 * 周期类型
	 */
	private Byte periodLimit;
	
	/**
	 * 周期内可兑换商品数量
	 */
	private Integer periodNumber;
	
	/**
	 * 周期内已经兑换的数量
	 */
	private Integer periodExchangGoodsNum;
}

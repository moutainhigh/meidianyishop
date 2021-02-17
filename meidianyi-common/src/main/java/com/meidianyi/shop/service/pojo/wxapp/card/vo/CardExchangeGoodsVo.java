package com.meidianyi.shop.service.pojo.wxapp.card.vo;

import java.util.Map;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;

import lombok.Data;

/**
 * 会员卡兑换商品信息
 * @author 黄壮壮
 */
@Data
public class CardExchangeGoodsVo {
	/**
	 * 兑换商品列表
	 */
	private PageResult<? extends GoodsListMpVo> goodsPageResult;
	
	/**
	 * 会员卡相关字段
	 */
	private Map<String, Object> cardInfo;
	
	/**
	 * 已选择兑换商品的数量
	 */
	private Integer totalNumber;
	
	/**
	 * 会员卡兑换商品提示
	 */
	private CardExchangTipVo cardExchangeTip;
}

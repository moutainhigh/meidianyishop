package com.meidianyi.shop.service.pojo.wxapp.card.param;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;
/**
 * 	会员卡兑换商品查询参数
 * @author 黄壮壮
 *
 */
@Data
public class CardExchangeGoodsParam {
	private Integer userId;
	private Integer shopId;
	@NotNull
	private String cardNo;
	private String search;
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}

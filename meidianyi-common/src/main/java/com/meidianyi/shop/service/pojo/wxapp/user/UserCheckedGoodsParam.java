package com.meidianyi.shop.service.pojo.wxapp.user;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * 用户勾选商品查询参数
 * @author 黄壮壮
 */
@Data
public class UserCheckedGoodsParam {
	/**
	 * 用户Id
	 */
	private Integer userId;
	
	/**
	 * 活动ID
	 */
	private String identityId;
	
	/**
	 * 活动类型: 1：限次卡兑换
	 */
	private Byte action;
	/**
	 * 规格ID
	 */
	private Integer productId;
	
	/**
	 * 商品ID
	 */
	private Integer goodsId;
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}

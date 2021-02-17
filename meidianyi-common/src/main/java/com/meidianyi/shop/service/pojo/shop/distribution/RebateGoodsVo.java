package com.meidianyi.shop.service.pojo.shop.distribution;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 返利商品统计出参
 * @author 常乐
 * 2019年8月10日
 */
@Data
public class RebateGoodsVo {
	/**
	 * 商品id
	 */
	private Integer goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 商品价格
	 */
	private BigDecimal shopPrice;
	/**
	 * 所属分类
	 */
	private String catName;
	
	private Integer sortId;
	/**
	 * 商品总销量
	 */
	private Integer goodsSaleNum;
	/**
	 * 已返利总数量
	 */
	private Integer saleNumber;
	/**
	 * 已返利总佣金
	 */
	private BigDecimal prdTotalFanli;
	/**
	 * 商品规格价
	 */
	private BigDecimal prdPrice;
}

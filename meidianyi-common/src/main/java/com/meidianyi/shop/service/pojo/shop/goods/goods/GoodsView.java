package com.meidianyi.shop.service.pojo.shop.goods.goods;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author 黄荣刚
 * @date 2019年7月9日
   *本类仅提供商品类的一个简单的视图 ，因为商品类有较多的字段，但在商品标签、商品推荐 情况下很多这些字段都是不必要，仅提供查询操作
 */
@Data
public class GoodsView {
	private Integer goodsId;
	private String goodsName;
	/** 商品主图 */
	private String goodsImg;
	/** 商品库存 */
	private Integer goodsNumber;
	
	/** 商品价格 */
	private BigDecimal shopPrice;
	/** 单位 */
	private String unit;
}

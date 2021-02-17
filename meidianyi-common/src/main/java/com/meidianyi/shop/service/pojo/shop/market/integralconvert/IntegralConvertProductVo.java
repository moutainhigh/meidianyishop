package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 查询指定积分活动
 * @author liangchen
 * @date 2019年8月16日
 */
@Data
public class IntegralConvertProductVo {
    /** 规格id */
    private Integer prdId;

	/** 规格描述 */
	private String prdDesc;

    /** 商品规格价格 */
    private BigDecimal prdPrice;

    /** 商品规格库存 */
    private Integer prdNumber;
	
	/** 商品兑换金额 */
	private BigDecimal money;
	
	/** 商品兑换积分 */
	private Integer score;
	
	/** 兑换商品库存 */
	private Short stock;
	private Integer remainStock;
	private Short saleNum;


	
}

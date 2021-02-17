package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import lombok.Data;

import java.math.BigDecimal;

/***
 * 积分兑换订单
 * @author liangchen
 * @date 2019年8月16日
 */
@Data
public class OrderTempVo {
	
    private String goodsName;
    private String prdDesc;
    private String goodsImg;
    private BigDecimal prdPrice;
	/** 商品数量 */
	private Integer prdNumber;
}

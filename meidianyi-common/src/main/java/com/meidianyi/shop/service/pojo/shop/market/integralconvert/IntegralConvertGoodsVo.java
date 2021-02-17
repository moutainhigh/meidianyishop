package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import lombok.Data;

/**
 * 添加积分活动
 * @author liangchen
 * @date 2019年8月15日
 */
@Data
public class IntegralConvertGoodsVo {
    /** 规格id */
    private Integer prdId;
	/** 规格描述 */
	private String prdDesc;
	
	/** 原价 */
	private String prdPrice;
	
	/** 原库存 */
	private Integer prdNumber;
	
}

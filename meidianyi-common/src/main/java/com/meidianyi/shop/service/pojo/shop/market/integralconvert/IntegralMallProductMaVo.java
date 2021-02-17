package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 字段比较全
 * 
 * @author zhaojianqiang
 * @time 下午3:34:21
 */
@Data
public class IntegralMallProductMaVo {
	private Integer id;
	private Integer integralMallDefineId;
	private Integer productId;
	private Integer score;
	private Short stock;
	private BigDecimal money;
	private Timestamp createTime;
	private Timestamp updateTime;
	private BigDecimal prdPrice;
	private Integer prdNumber;
}

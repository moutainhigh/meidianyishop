package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 多一些值
 * 
 * @author zhaojianqiang
 * @time 下午3:34:21
 */
@Data
public class IntegralMallMaAllVo extends IntegralMallMaVo {
	private Integer stockSum;
	private Integer score;
	private BigDecimal money;
	private BigDecimal prdPrice;
}

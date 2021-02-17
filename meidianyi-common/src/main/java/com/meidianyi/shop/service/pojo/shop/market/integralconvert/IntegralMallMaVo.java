package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 字段比较全 INTEGRAL_MALL_DEFINE
 * 
 * @author zhaojianqiang
 * @time 下午3:34:21
 */
@Data
public class IntegralMallMaVo {
	private Integer id;
	private Integer goodsId;
	private Short maxExchangeNum;
	private Timestamp startTime;
	private Timestamp endTime;
	private Byte status;
	private Byte delFlag;
	private Timestamp delTime;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String name;
	private String shareConfig;
	private String goodsName;
	private String goodsImg;
	private BigDecimal shopPrice;
}

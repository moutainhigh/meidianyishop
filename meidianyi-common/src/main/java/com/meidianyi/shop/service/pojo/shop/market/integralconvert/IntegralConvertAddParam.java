package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 添加积分活动
 * @author liangchen
 * @date 2019年8月15日
 */
@Data
public class IntegralConvertAddParam {
	/** 活动id */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** 开始日期 */
	private Timestamp startTime;
	/** 结束日期 */
	private Timestamp endTime;
	/** 单个用户最多可兑换数量 填0则不限制 */
	private Short maxExchangeNum;
	/** 商品id */
	private Integer goodsId;
	/** 商品兑换配置 */
	public List<IntegralConvertProductVo> product;
	/** 店铺分享配置 */
	public IntegralConvertShareConfig shareConfig;
	
}

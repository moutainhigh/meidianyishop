package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 积分兑换分页查询列表
 * @author liangchen
 * @date 2019年8月14日
 */
@Data
public class IntegralConvertUserVo {
	
	/** 用户id */
	private Integer userId;
	
	/** 订单编号 */
	private String orderSn;
	
	/** 商品id */
	private Integer goodsId;
	
	/** 商品图片 */
	private String goodsImg;
	
	/** 商品名称 */
	private String goodsName;
	
	/** 兑换现金 */
	private BigDecimal money;
	
	/** 兑换积分 */
	private Integer score;
	
	/** 用户昵称 */
	private String username;
	
	/** 手机号 */
	private String mobile;
	
	/** 兑换商品数 */
	private Integer number;
	
	/** 兑换时间 */
	private Timestamp createTime;
	
	
}

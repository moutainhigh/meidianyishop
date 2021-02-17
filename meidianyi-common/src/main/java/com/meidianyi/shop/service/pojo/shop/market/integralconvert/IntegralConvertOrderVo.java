package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import java.math.BigDecimal;


import lombok.Data;

/***
 * 积分兑换订单
 * @author liangchen
 * @date 2019年8月16日
 */
@Data
public class IntegralConvertOrderVo {
	
	/** 订单编号 */
	private String orderSn;
    private String goodsName;
    private String prdDesc;
    private String goodsImg;
    private BigDecimal goodsPrice;
	/** 商品数量 */
	private Integer number;

	/** 兑换现金 */
	private BigDecimal money;
	/** 兑换积分数量 */
	private Integer score;
    /** 下单人信息 */
    private Integer userId;
    private String username;
    private String userMobile;
	/** 收件人姓名 */
	private String consignee;
	/** 收件人手机 */
	private String mobile;
	/** 订单状态 */
	private Byte orderStatus;
}

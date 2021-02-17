package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author huangronggang
 * @date 2019年8月1日
 * 优惠劵礼包订单
 */
@Data
public class CouponPackOrderVo {
	/** 订单ID */
	private Integer orderId;


	/** 优惠劵包ID */
	private Integer virtualGoodsId;
	/** 优惠劵礼包名称 b2c_coupon_pack*/
	private String packName;
	/** 订单号 */
	private String orderSn;
	/** 用户ID */
	private Integer userId;
	/** 用户名称 */
	private String username;
	/** 用户电话 */
	private String mobile;
	
	/** 订单付款现金金额 */
	private BigDecimal moneyPaid;
	/**用户余额消费金额 */
	private BigDecimal useAccount;
	/** 用户消费的积分 */
	private Integer useScore;
	/** 用户消费的会员卡 金额*/
	private BigDecimal  memberCardBalance;
	
	/** 会员卡 卡号 */
	private String cardNo;
	
	/** 支付代号 */
	private String payCode;
	
	/** 支付名称*/
	private String payName;
	
	/** 微信支付ID */
	private String prepayId;
	
	/** 支付流水号 */
	private String paySn;
	
	
	/** 订单总金额 */
	private BigDecimal orderAmount;
	
	/** 下单时间 */
	private Timestamp createTime;
	private Timestamp payTime;
	/**退款标志位 0:未申请退款，1：退款失败，2：退款成功，3是自定义临时状态，代表订单已经部分退款 */
	private Byte returnFlag;
	/**是否能退款 1能退 0不能退*/
	private Byte canReturn;
	/**未发放优惠劵数量 */
	private Integer surplusAmount;
	/** 已退积分*/
	private Integer returnScore;
	/** 已退余额 */
	private BigDecimal returnAccount;
	/** 已退现金 */
	private BigDecimal returnMoney;
	/** 会员卡已退金额 */
	private BigDecimal returnCardBalance;
	
	/** 退款时间 */
	private Timestamp returnTime;

    /** 币种 */
    private String currency;
	
	
	
}


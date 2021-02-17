package com.meidianyi.shop.service.pojo.shop.order.store;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 买单订单展示订单信息
 *
 * @author wangshuai
 */
@Getter
@Setter
public class StoreOrderInfoVo extends StoreOrderListInfoVo {
	private String addMessage;
	private String storeName;
	/**会员卡抵扣*/
	private BigDecimal memberCardBalance;
	/**会员卡折扣*/
	private BigDecimal memberCardReduce;
	/**积分抵扣金额*/
	private BigDecimal scoreDiscount;
	/**用户消费余额*/
	private BigDecimal useAccount;
	/**总价*/
	private BigDecimal orderAmount;
	/** 支付金额 */
	private BigDecimal moneyPaid;
	/**
	 * 发票信息
	 */
	/**发票类型 0企业发票，1个人发票*/
	private Byte type;
	/**发票抬头*/
	private String title;
	/**公司税号*/
	private String taxNumber;
	/**公司地址*/
	private String companyAddress;

    /**
     * 门店地址信息
     */
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private String address;
    private String latitude;
    private String longitude;


}

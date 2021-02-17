package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 成交用户分析-上一段时间数据
 * @author liangchen
 * @date 2019年7月19日
 */
@Data
public class OrderTotalVo {
	
	/** 全部成交客户数 */
	private Integer orderUserData;
	/** 新成交客户数 */
	private Integer newOrderUserData;
	/** 老成交客户数 */
	private Integer oldOrderUserData;

    /** 全部客户数占比 */
    private Double orderUserDataRate;
    /** 新客户数占比 */
    private Double newOrderUserDataRate;
    /** 老客户数占比 */
    private Double oldOrderUserDataRate;

    /** 全部客户单价 */
    private BigDecimal unitPrice;
    /** 新客户单价 */
    private BigDecimal newUnitPrice;
    /** 老客户单价 */
    private BigDecimal oldUnitPrice;

	/** 全部付款金额 */
	private BigDecimal totalPaidMoney;
	/** 新付款金额 */
	private BigDecimal newPaidMoney;
	/** 老付款金额 */
	private BigDecimal oldPaidMoney;

    /** 全部客户转化率 */
    private Double transRate;
    /** 新客户转化率 */
    private Double newTransRate;
    /** 老客户转化率 */
    private Double oldTransRate;

    /** 累计人数 */
    private Integer userData;
    /** 访问人数 */
    private Integer loginData;
}

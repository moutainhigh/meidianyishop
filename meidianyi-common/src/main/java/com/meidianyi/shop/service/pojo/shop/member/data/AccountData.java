package com.meidianyi.shop.service.pojo.shop.member.data;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
* @author 黄壮壮
* @Date: 2019年8月26日
* @Description: 余额数据
*/
@Builder(builderMethodName = "newBuilder")
@Setter
@Getter
public class AccountData extends BaseData {
	/** 用户Id */
	protected Integer userId;
	/** 操作员id */
	protected Integer adminUser;
	/** 备注模板code */
	private Integer remarkCode;
	/** 备注模板数据 */
	private String remarkData;
	/** 用户输入的备注 */
	//protected String remark;
	/** 交易类型 */
	protected Byte tradeType;
	/** 资金流向 */
	protected Byte tradeFlow;
	/** 原有金额 */
	private BigDecimal account;
	/** 增加的金额 */
	private BigDecimal amount;
	/** 订单编号 */
	private String orderSn;
	/** 支付方式 */
	private String payment;
	/** 支付类型，0：充值，1：消费  如： {@link com.meidianyi.shop.common.pojo.shop.operation.RecordTradeEnum.RECHARGE }*/
	private Byte isPaid;
	/** 语言-用于国际化 */
	private String language;
    /**是否消息推送*/
    private Boolean noSendMsg;
}

package com.meidianyi.shop.service.pojo.shop.member.account;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

/**
 * 余额修改接收参数
 * @author 黄壮壮
 * 2019-07-17 18:03
 */
@Data
public class AccountParam {
	/** 原有金额 */
	private BigDecimal account;
	/** 更新金额  区分正负号 */
	private BigDecimal amount;
	/** 用户id */
	private Integer userId;
	/** 
	 * 备注remark需要国际化
	 */
	/** 备注模板id {@link com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate} */
	private Integer remarkId;
	/** 备注模板数据 */
	private String remarkData;
	/** 用户输入的备注 */
	@JsonAlias("remark")
	private String userInputRemark;
	/** 订单编号 */
	private String orderSn;
	/** 支付方式 */
	private String payment;
	/** 支付类型，0：充值，1：消费  如： {@link com.meidianyi.shop.common.pojo.shop.operation.RecordTradeEnum.RECHARGE }*/
	private Byte isPaid;
}

package com.meidianyi.shop.service.pojo.shop.member.account;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年8月13日
* @Description: 分页查询会员用户详细余额信息-出参
*/
@Data
public class AccountPageListVo {
	/** 用户名称 */
	private String username;
	/** 手机号码 */
	private String mobile;
	/** 记录id */
	private Integer id;
	/** 用户id */
	private Integer userId;
	/** 操作员 */
	private String adminUser;
	/** 订单号 */
	private String orderSn;
	/** 余额变化 */
	private BigDecimal amount;
	/** 操作员备注 */
	private String adminNote;
	/** 支付方式 */
	private String payment;
	/** 支付类型，0：充值，1：消费*/
	private Byte isPaid;
	/** 备注 */
	private String remark;
	/** 1:分销来源，0:充值 */
	private Byte source;
	/**
	 * 0未提现或部分统计1已统计
	 */
    private Byte withdrawStatus;
	/** 更新后的余额 */
	private BigDecimal settleAccount;
	/** 添加时间 */
	private Timestamp createTime;
	
}

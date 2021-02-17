package com.meidianyi.shop.service.pojo.shop.distribution;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 提现审核列表入参
 * @author 常乐
 * 2019年8月13日
 */
@Data
public class DistributorWithdrawListParam {
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 申请人昵称
	 */
	private String username;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 真实姓名
	 */
	private String realName;
	/**
	 * 提现单号
	 */
	private String orderSn;
	/**
	 * 申请时间开始值
	 */
	private Timestamp startCreateTime;
	/**
	 * 申请时间结束
	 */
	private Timestamp endCreateTime;
    /** 操作时间开始*/
    private Timestamp startOptTime;
    /** 操作结束时间*/
    private Timestamp endOptTime;
	/**
	 * 提现金额范围最小值
	 */
	private BigDecimal startWithdrawCash;
	/**
	 * 提现金额范围最大值
	 */
	private BigDecimal endWithdrawCash;
	/**
	 * 处理状态
	 */
	private Byte status;
	
	private Integer currentPage;
	
	private Integer pageRows;
}

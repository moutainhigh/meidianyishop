package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.util.PageResult;

/**
 * 返利提现审核详情出参
 * @author 常乐
 * 2019年8月14日
 */
@Data
public class DistributorWithdrawDetailVo {
	private String username;
	private String mobile;
	private String realName;
	private Timestamp createTime;
	private String orderSn;
	private Byte type;
	/**
	 * 用户提现序号
	 */
	private String withdrawUserNum;
	/**
	 * 流水号
	 */
	private String withdrawNum;
	/**
	 * 提现金额
	 */
	private BigDecimal withdrawCash;
	private Timestamp checkTime;
	private Byte status;
	private String refuseDesc;
	private String desc;
	/**
	 * 可提现金额
	 */
	private BigDecimal withdraw;

	private Timestamp updateTime;
	
	/**
	 * 当前用户提现列表
	 */
	private PageResult<DistributorWithdrawListVo> userWithdrawList;
}

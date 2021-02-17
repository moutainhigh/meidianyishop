package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 返利提现审核列表出参
 * @author 常乐
 * 2019年8月14日
 */
@Data
public class DistributorWithdrawListVo {
	
	private int id;
	/**
	 * 申请人
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
	 * 申请时间
	 */
	private Timestamp createTime;
	/**
	 * 提现单号
	 */
	private String orderSn;
	/**
	 * 提现金额
	 */
	private BigDecimal withdrawCash;
	/**
	 * 审核时间
	 */
	private Timestamp checkTime;
	/**
	 * 处理状态 1待审核 2拒绝 3已审核待出账 4出账成功 5失败
	 */
	private Byte status;
	/**
	 * 驳回原因
	 */
	private String refuseDesc;
	/**
	 * 备注
	 */
	private String desc;
	/**
	 * 流水号
	 */
	private String withdrawNum;
	/**
	 * 提现序号
	 */
	private String withdrawUserNum;
	/**
	 * 出账类型
	 */
	private int type;
}

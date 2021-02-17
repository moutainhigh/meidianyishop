package com.meidianyi.shop.service.pojo.shop.distribution;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 分销员佣金统计入参
 * @author 常乐
 * 2019年8月8日
 */
@Data
public class BrokerageListParam {
    /**
     * 分销员ID
     */
    private Integer userId;
	/**
	 * 分销员名称
	 */
	private String distributorName;
	/**
	 * 分销员电话
	 */
	private String distributorMobile;
	/**
	 * 下单用户名
	 */
	private String username;
	/**
	 * 下单用户手机号
	 */
	private String mobile;
	/**
	 * 下单开始时间
	 */
	private Timestamp startCreateTime;
	/**
	 * 下单结束时间
	 */
	private Timestamp endCreateTime;
	/**
	 * 订单号
	 */
	private String orderSn;
	/**
	 * 开始返利时间
	 */
	private Timestamp startRebateTime;
	/**
	 * 返利结束时间
	 */
	private Timestamp endRebateTime;
	/**
	 * 返利状态 0：待返利；1：不返利；2：已返利
	 */
	private String settlementFlag;
	/**
	 * 分销员分组
	 */
	private Integer distributorGroup;
	/**
	 * 返利关系 0：自购返利；1：直接返利；2：间接返利；
	 */
	private Byte rebateLevel;
    /** 导出开始数*/
    private Integer startNum;
    /** 导出结束数*/
    private Integer endNum;

	private Integer currentPage;
	private Integer pageRows;

}

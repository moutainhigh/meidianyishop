package com.meidianyi.shop.service.pojo.shop.member.userimp;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 会员导入明细
 * 
 * @author zhaojianqiang
 * @time 上午9:23:37
 */
@Data
public class UiGetNoActListParam {
	/** 批次Id */
	private Integer batchId;
	private Timestamp startTime;
	private Timestamp endTime;
	/** 是否激活  0：没有；1：已激活*/
	private Byte isActivate;

	private String mobile;

	private String realName;
	/** 是否是分销员  0:不是；1：是*/
	private Byte isDistributor;
	/** 分销员分组Id */
	private Integer groupId;
	/** -每页总数 */
	public Integer pageRows;
	/** -当前页 */
	public Integer currentPage;
}

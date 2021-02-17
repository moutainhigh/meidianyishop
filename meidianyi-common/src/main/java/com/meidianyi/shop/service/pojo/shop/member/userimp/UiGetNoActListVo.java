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
public class UiGetNoActListVo {
	/** 批次Id */
	private Integer batchId;
	private Timestamp createTime;
	/** 是否激活  0：没有；1：已激活*/
	private Byte isActivate;

	private String mobile;

	private String name;
	/** 是否是分销员  0:不是；1：是*/
	private Byte isDistributor;
	/** 分销员分组Id */
	private Integer groupId;
	/** 分销员分组名称*/
	private String groupName;
}

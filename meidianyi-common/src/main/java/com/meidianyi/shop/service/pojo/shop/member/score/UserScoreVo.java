package com.meidianyi.shop.service.pojo.shop.member.score;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;


/**
 * 
 * @author zhaojianqiang
 *
 *         2019年10月10日 上午10:36:26
 */
@Data
public class UserScoreVo {
	private Integer id;
	private Integer userId;
	private Integer score;
	private Byte status;
	private String flowNo;
	private Integer usableScore;
	private String identityId;
	private Integer goodsId;
	private String orderSn;
	private Integer shopId;
	private String desc;
	/** 备注模板code */
	private Integer remarkCode;
	/** 备注模板数据 */
	@JsonAlias("remark")
	private String remarkData;
	private Timestamp createTime;
	private Timestamp expireTime;
	private String adminUser;
	/**
	 * 入参用
	 */
	private Boolean isFromCrm = false;
	private Boolean isFromOverdue = false;
	private Boolean isFromRefund=false;
	private Integer scoreDis;
}

package com.meidianyi.shop.service.pojo.wxapp.account;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author zhaojianqiang
 */
@Data
public class UserMainVo {
	private Long	id;
	private Integer	shopId;
	private Integer	userId;
	private String	username;
	private String	userPwd;
	private String	userCid;
	private String	mobile;
	private String	userCode;
	private String	wxOpenid;
	private String	email;
	private Timestamp	createTime;
	private String	wechat;
	private Integer	fanliGrade;
	private Integer	userGrade;
	private Integer	invite;
	private String	inviteSource;
	private Integer	invitationCode;
	private BigDecimal	account;
	private Integer	discount;
	private Integer	discountGrade;
	private Byte	delFlag;
	private Timestamp	delTime;
	private Integer	growth;
	private Integer	score;
	private Integer	source;
	private Integer	inviteId;
	private Date	inviteExpiryDate;
	private String	wxUnionId;
	private Timestamp	updateTime;
	private Byte	isDistributor;
	private Integer	inviteActId;
	private Byte	distributorLevel;
	private String	aliUserId;


}

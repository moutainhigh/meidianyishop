package com.meidianyi.shop.service.pojo.wxapp.account;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年10月16日 上午10:36:34
 */
@Data
public class UserInfo {
	private Integer userId;
	private String username;
	private Integer inviteId;
	private String userCid;
	private String mobile;
	private String userCode;
	private String wxOpenid;
	private Timestamp createTime;
	private String wechat;
	private Integer fanliGrade;
	private Integer userGrade;
	
	private BigDecimal account;
	private Integer discount;
	private String wxUnionId;
	private String device;
	private String unitPrice;
	private String isDistributor;
	private String inviteGroup;
	private Byte distributorLevel;
	private Timestamp inviteTime;
	private String discountGrade;
	private Byte delFlag;
	private Timestamp delTime;
	
	private String growth;
	private String score;
	private String sex;
	private Integer birthdayYear;
	private Integer birthdayMonth;
	private Integer birthdayDay;
	
	private String realName;
	private Integer provinceCode;
	private Integer cityCode;
	private Integer districtCode;
	private String address;
	
	private Byte maritalStatus;
	private Integer monthlyIncome;
	private String cid;
    private Byte education;

    private Byte industryInfo;
    private String bigImage;
    private String bankUserName;
    private String shopBank;
    private String bankNo;
    private String userAvatar;
    private String inviteName;
    private Integer invitationCode;

}

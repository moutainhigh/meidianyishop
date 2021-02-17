package com.meidianyi.shop.service.pojo.wxapp.account;

import lombok.Data;

/**
 * @author zhaojianqiang
 */
@Data
public class UserDetailMainVo {
	private Integer id;
	private Integer userId;
	private Integer shopId;
	private String username;
	private String sex;
	private Integer birthdayYear;
	private Integer birthdayMonth;
	private Integer birthdayDay;
	private String email;
	private String realName;
	private Integer provinceCode;
	private Integer cityCode;
	private Integer districtCode;
	private String address;
	private Byte maritalStatus;
	private Byte monthlyIncome;
	private String cid;
	private Byte education;
	private Byte industryInfo;
	private String bigImage;
	private String bankUserName;
	private String shopBank;
	private String bankNo;
	private String withdrawPasswd;
	private String userAvatar;

}

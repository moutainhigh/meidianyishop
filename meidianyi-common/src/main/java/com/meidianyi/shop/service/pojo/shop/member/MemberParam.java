package com.meidianyi.shop.service.pojo.shop.member;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月9日
* @Description: 请求和更新会员信息最简单的参数
*/
@Data
public class MemberParam {
	/** -会员id */
	private Integer userId;
	/** -会员名称  */
	private String username;
	
	/** ------邀请人id-----------*/
	private Integer inviteId;
	
	/** 生日 */
	private Integer birthdayYear;
	private Integer birthdayMonth;
	private Integer birthdayDay;
	
	/** 婚姻状况：1未婚，2已婚，3保密  */
	private Byte maritalStatus;
	/** 月收入 1：2000元以下 2：2000-3999元  3： 4000-5999元  4：6000-7999元  5： 8000元以上*/
	private Byte monthlyIncome;
	/** 身份证 */
	private String cid;
	/** 性别：女f,男m */
	private String sex;
	/** 受教育程度 */
	private Byte education;
	/** 真实姓名 */
	private String realName;
	/** 所在行业 */
	private Byte industory;
	/** 省市区 */
	private Integer provinceCode;
	private Integer cityCode;
	private Integer districtCode;
	
	
}

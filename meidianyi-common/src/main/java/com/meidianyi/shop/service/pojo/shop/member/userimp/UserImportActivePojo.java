package com.meidianyi.shop.service.pojo.shop.member.userimp;

import java.math.BigDecimal;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumnNotNull;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;
/**
 * 激活会员
 * @author zhaojianqiang
 * @time   下午2:06:07
 */
@ExcelSheet
@Data
public class UserImportActivePojo {
	/** 手机号 */
	@ExcelColumn(columnIndex = 0, columnName = "user.import.mobile")
	@ExcelColumnNotNull
	private String mobile;

	/** 姓名 */
	@ExcelColumn(columnIndex = 1, columnName = "user.import.name")
	private String name;

	/** 邀请人手机号 */
	@ExcelColumn(columnIndex = 2, columnName = "user.import.invite_user_mobile")
	private String inviteUserMobile;

	/** 积分 */
	@ExcelColumn(columnIndex = 3, columnName = "user.import.score")
	@ExcelColumnNotNull
	private Integer score;

	/** 性别 */
	@ExcelColumn(columnIndex = 4, columnName = "user.import.sex")
	private String sex;

	/** 生日 */
	@ExcelColumn(columnIndex = 5, columnName = "user.import.birthday")
	private String birthday;

	/** 省 */
	@ExcelColumn(columnIndex = 6, columnName = "user.import.province")
	private String province;

	/** 市 */
	@ExcelColumn(columnIndex = 7, columnName = "user.import.city")
	private String city;

	/** 区 */
	@ExcelColumn(columnIndex = 8, columnName = "user.import.district")
	private String district;

	/** 详细地址 */
	@ExcelColumn(columnIndex = 9, columnName = "user.import.address")
	private String address;

	/** 身份证 */
	@ExcelColumn(columnIndex = 10, columnName = "user.import.id_number")
	private String idNumber;

	/** 教育程度 */
	@ExcelColumn(columnIndex = 11, columnName = "user.import.education")
	private String education;

	/** 所在行业 */
	@ExcelColumn(columnIndex = 12, columnName = "user.import.industry")
	private String industry;

	/** 婚姻状况 */
	@ExcelColumn(columnIndex = 13, columnName = "user.import.marriage")
	private String marriage;

	/** 收入 */
	@ExcelColumn(columnIndex = 14, columnName = "user.import.income")
	private BigDecimal income;

	/** 是否是分销员 */
	@ExcelColumn(columnIndex = 15, columnName = "user.import.is_distributor")
	private String isDistributor;
	
	/** 备注 */
	@ExcelColumn(columnIndex = 16, columnName = "user.import.remark")
	private String remark;
	
	/** 错误信息 */
	@ExcelColumn(columnIndex = 17, columnName = "user.import.errorMsg")
	private String errorMsg;
	
	/** 错误信息 */
	@ExcelColumn(columnIndex = 17, columnName = "user.import.isactive")
	private String isActivate;
}

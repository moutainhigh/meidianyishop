package com.meidianyi.shop.service.pojo.shop.member.card.export.examine;

import java.sql.Timestamp;

import lombok.Data;

import org.jooq.types.UInteger;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;
/**
 * 	会员卡审核数据下载对象
 * @author 黄壮壮
 *
 */
@ExcelSheet
@Data
public class CardExamineDownVo {
	/**
	 * ID
	 */
	@ExcelColumn(columnIndex = 0, columnName = "card.examine.export.id")
	private Integer id;
	
	/**
	 * 	昵称
	 */
	@ExcelColumn(columnIndex = 1, columnName = "card.examine.export.username")
	private String username;
	
	/**
	 *	 申请时间
	 */
	@ExcelColumn(columnIndex = 2, columnName = "card.examine.export.createTime")
	private Timestamp createTime;
	
	/**
	 * 	真实姓名
	 */
	@ExcelColumn(columnIndex = 3, columnName = "card.examine.export.realname")
	private String realName;
	
	/**
	 * 	手机号
	 */
	@ExcelColumn(columnIndex = 4, columnName = "card.examine.export.mobile")
	private String mobile;
	
	/**
	 * 	身份证号
	 */
	@ExcelColumn(columnIndex = 5, columnName = "card.examine.export.cid")
	private String cid;
	
	/**
	 * 	性别
	 */
	@ExcelColumn(columnIndex = 6, columnName = "card.examine.export.sex")
	private String sex;
	
	/**
	 * 	生日
	 */
	@ExcelColumn(columnIndex = 7, columnName = "card.examine.export.birthday")
	private String birthday;
	
	
	/**
	 * 	婚姻状况
	 */
	@ExcelColumn(columnIndex = 8, columnName = "card.examine.export.maritalStatus")
	private String maritalStatus;
	
	/**
	 * 	教育程度
	 */
	@ExcelColumn(columnIndex = 9, columnName = "card.examine.export.education")
	private String educationStr;
	
	/**
	 * 所在行业
	 */
	@ExcelColumn(columnIndex = 10, columnName = "card.examine.export.industry")
	private String industry;
	
	/**
	 * 所在地
	 */
	@ExcelColumn(columnIndex = 11, columnName = "card.examine.export.address")
	private String address;
	
	/**
	 * 自定义审核内容
	 */
	@ExcelColumn(columnIndex = 12, columnName = "card.examine.export.customContent")
	private String customContent;
	
	/**
	 * 审核时间
	 */
	@ExcelColumn(columnIndex = 13, columnName = "card.examine.export.examineTime")
	private Timestamp examineTime;
	
	/**
	 * 审核人
	 */
	@ExcelColumn(columnIndex = 14, columnName = "card.examine.export.examinePerson")
	private String examinePerson;
	
	/**
	 * 审核状态
	 */
	@ExcelColumn(columnIndex = 15, columnName = "card.examine.export.examineStatus")
	private String examineStatus;
	
	@ExcelIgnore
	private Integer birthdayYear;
	@ExcelIgnore
	private Integer birthdayMonth;
	@ExcelIgnore
	private Integer birthdayDay;
	@ExcelIgnore
	private Byte education;
	@ExcelIgnore
	private Byte industryInfo;
	@ExcelIgnore
	private Integer provinceCode;
	@ExcelIgnore
	private Integer cityCode;
	@ExcelIgnore
	private Integer districtCode;
	@ExcelIgnore
	private String customOptions;
	@ExcelIgnore
	private Timestamp passTime;
	@ExcelIgnore
	private Timestamp refuseTime;
	@ExcelIgnore
	private UInteger sysId;
	@ExcelIgnore
	private Byte status;
}

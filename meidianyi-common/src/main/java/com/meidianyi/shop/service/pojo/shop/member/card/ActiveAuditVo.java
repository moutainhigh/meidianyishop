package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.foundation.util.I18N;
import com.meidianyi.shop.service.pojo.wxapp.card.param.CardCustomActionParam;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月27日
* @Description: 卡激活审核-出参
*/

@Data
public class ActiveAuditVo {
	/** ID */
	private Integer id;
	/** 真实姓名 */
	private String realName;
	/** 状态 */
	private Byte status;
	/** 会员卡号 */
	private String cardNo;
	/** 身份证号 */
	private String cid;
	/** 受教育程度 - 状态信号 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Byte education;
	/** 受教育程度 - 字符串 */
	@I18N(propertiesFileName = "member")
	@JsonProperty("education")
	private String educationStr;
	/** 行业 - 状态信号 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Byte industryInfo;
	/** 行业 名称 */
	@I18N(propertiesFileName = "member")
	private String industry;
	/** 手机号 */
	private String mobile;
	/** 用户昵称 */
	private String username;
	/** 申请时间 */
	private Timestamp createTime;
	/** 性别 */
	private String sex;
	/** 婚姻状态 */
	private Byte maritalStatus;
	/** 地址 */
	private String address;
	/** 生日 */
	private Integer birthDayYear;
	private Integer birthDayMonth;
	private Integer birthDayDay;
	
	/** 地址 */
	private Integer provinceCode;
	private String province;
	private Integer cityCode;
	private String city;
	private Integer districtCode;
	private String district;
	
	/** 拒绝原因 */
	private String refuseDesc;
	
	/** 自定义激活数据 */
	private List<CardCustomActionParam> customOptions;
	/**  审核人 */
    private String accountName;
    /**  审核时间 */
    private Timestamp examineTime;
}

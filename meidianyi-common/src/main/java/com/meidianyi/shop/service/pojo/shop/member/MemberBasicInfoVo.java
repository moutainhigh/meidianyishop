package com.meidianyi.shop.service.pojo.shop.member;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.foundation.util.I18N;

import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import lombok.Getter;
import lombok.Setter;
/**
* @author 黄壮壮
* @Date: 2019年8月14日
* @Description: 会员用户基本信息-出参
*/

@Getter
@Setter
public class MemberBasicInfoVo {
	private Integer userId;
	/** 昵称 */
	private String username;
	private String wxUnionId;
	/** 成为客户的时间  */
	private Timestamp createTime;
	/** 手机号 */
	private String mobile;
	/** 积分 */
	private Integer score;
	/** 余额 */
	private BigDecimal account;
	/** OpenId */
	private String wxOpenid;
	/** 门店来源-1未录入0后台>0为门店 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Byte source;
	/** 邀请来源 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String inviteSource;
	/** 来源名称  如： {@link com.meidianyi.shop.common.pojo.shop.member.SourceNameEnum.NOT_ACQUIRED } */
	@JsonProperty("source")
	private String sourceName;
	
	private Integer scene;
	
	/** 是否是分销员 0：否，1：是 */
	private Byte isDistributor;
	
	/** 邀请人昵称 */
	private String inviteUserName;
	/**
	 *	邀请人手机
	 */
	private String inviteUserMobile;
	/**
	 * 邀请人分销分组名称
	 */
	private String inviteGroupName;
	/** 邀请人id */
	private String inviteId;
	
	/** 真实姓名 */
	private String realName;
	/** 用户头像 */
	private String userAvatar;
	
	/** b2c_user_login_record */
	/** 最近浏览时间 */
	private Timestamp updateTime;
	
	/** b2c_user_score */
	/** 累计积分 */
	private Integer totalScore;
	
	/** 累计消费金额 */
	private BigDecimal totalConsumpAmount;
	
	
	/** b2c_user_address */
	/** 用户详细地址 */
	private List<String> addressList;
	
	
	/** b2c_user_detail */
	/** 受教育程度 */
	@JsonProperty("educationId")
	private Byte education;
	
	@I18N(propertiesFileName = "member")
	@JsonProperty("education")
	private String educationStr;
	
	/** 省市区，编号 */
	private Integer provinceCode;
	private String provinceName;
	private Integer cityCode;
	private String cityName;
	private Integer districtCode;
	private String distictName;
	
	
	/** 生日 */
	private Integer birthdayYear;
	private Integer birthdayMonth;
	private Integer birthdayDay;
	
	/** 性别：女f,男m */
	private String sex;
	
	/** 婚姻状况：1未婚，2已婚，3保密  */
	private Integer maritalStatus;
	/** 月收入 1：2000元以下 2：2000-3999元  3： 4000-5999元  4：6000-7999元  5： 8000元以上*/
	private Integer monthlyIncome;
	/** 身份证 */
	private String cid;
	/** 客单价 */
	private BigDecimal unitPrice;
	
	/** 可用优惠券数量 */
	private Integer canUseCouponNum;
	
	/** 行业  {@link com.meidianyi.shop.service.pojo.shop.member.MemberIndustryEnum}*/
	@I18N(propertiesFileName = "member")
	private String industryInfo;
	private Integer industryId;
    private List<PatientOneParam> patientList;
}

package com.meidianyi.shop.service.pojo.shop.member;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
/**
 * 
 * @author 黄壮壮
 * @Date:  2019年8月29日
 * @Description: 会员列表查询出参
 */
@Data
public class MemberInfoVo {
	/** ID */
	private Integer userId;
	/** 昵称 */
	private String userName;
	/** 手机号 */
	private String mobile;
	/** 邀请人 */
	private String inviteUserName;
	/** 余额 */
	private BigDecimal account;
	/** 积分 */
	private Integer score;
	/** 来源 : -1 未录入 ; 0 后台; >0为门店id */
	private Integer source;
	/** 创建时间 */
	private Timestamp createTime;
	/** 用户持有的会员卡 */
	private String cardName;
	
	/** 邀请来源 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String inviteSource;
	
	/** 邀请来源活动id */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Integer inviteActId;
	
	/**
	 *  用户微信来源 -1搜索、公众号等入口（主动）进入，-2分享（被动）进入，-3扫码进入 -4未获取 
	 * 	com.meidianyi.shop.service.pojo.shop.member.SourceNameEnum
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Integer scene;
	
	
	/** 来源名称  如： {@link com.meidianyi.shop.common.pojo.shop.member.SourceNameEnum.NOT_ACQUIRED } */
	private String sourceName;
	/** 0：恢复登录 ；1：禁止登录  {@link com.meidianyi.shop.common.pojo.shop.member.MemberConstant.DELETE_YES } */
	private Byte delFlag;
	
	/** 真实姓名 */
	private String realName;

	/** 患者数 */
	private Integer patientNum;
	
}

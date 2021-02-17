package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月24日
* @Description: 持卡会员-出参
*/
@Data
public class CardHolderVo {
	/**
	 * 	会员ID
	 */
	private Integer userId;
	
	/** 
	 * 	 昵称
	 */
	private String username;
	
	/**
	 * 	 手机号
	 */
	private String mobile;
	
	/** 
	 * 	 邀请人
	 */
	private String invitedName;
	
	/** 
	 * 	 会员卡号 
	 */
	private String cardNo;
	
	/** 
	 * 0:正常，1:删除 2 已过期{@link com.meidianyi.shop.common.pojo.shop.member.card.CardConstant.CARD_USING }
	 */
	private Byte flag;
	
	/**	
	 * 	领卡时间 
	 */
	private Timestamp createTime;
	
	/** 
	 * 	过期时间 
	 */
	private Timestamp expireTime;
	
	/**	
	 * 	更新时间 
	 */
	private Timestamp updateTime;
	
	/**
	 * 	会员卡类型
	 */
	private Byte cardType;
	/**
	 * 	卡的审核状态 null 没有提交申请，1： 审核中，2，审核成功，3： 审核失败 
	 * {@link com.meidianyi.shop.service.pojo.shop.member.card.CardVerifyConstant}
	 */
	private Byte status;
	
	/**
	 * 转赠成功时间
	 */
	private Timestamp getTime;
	
	/**
	 * 转赠领取人
	 */
	private String giveName;
	
	/**
	 * 转赠人Id
	 */
	private Integer getUserId;
}

package com.meidianyi.shop.service.pojo.shop.member.account;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年8月8日
* @Description:
*/
@Data
public class MemberCardVo {
	/** 普通会员卡 */
	List<MemberCard> normalCard;
	/** 限次会员卡 */
	List<MemberCard> limitNumCard;
	/** 等级会员卡 */
	List<MemberCard> rankCard;
	
	public MemberCardVo() {
		normalCard = new ArrayList<MemberCard>();
		limitNumCard = new ArrayList<MemberCard>();
		rankCard = new ArrayList<MemberCard>();
	}
}

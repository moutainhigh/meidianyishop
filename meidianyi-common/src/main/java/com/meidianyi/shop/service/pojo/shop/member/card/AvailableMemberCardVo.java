package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Getter;
import lombok.Setter;

/**
* @author 黄壮壮
* @Date: 2019年9月18日
* @Description:
*/
@Getter
@Setter
public class AvailableMemberCardVo {
	/** 会员卡id */
	public Integer id;
	/** 0:普通会员卡，1:次卡,2:登记卡   {@link com.meidianyi.shop.common.pojo.shop.member.card.CardConstant.NORMAL_TYPE }*/
	public Byte cardType;
	/** 会员卡名称 */
	public String cardName;
	
	
	public AvailableMemberCardVo(Integer id,Byte cardType,String cardName) {
		this.id = id;
		this.cardType = cardType;
		this.cardName = cardName;
	}
}

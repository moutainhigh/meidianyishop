package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月2日
* @Description: 用于会员卡弹窗的基本信息
*/
@Data
public class CardBasicVo {
	/** 会员卡id */
	public Integer id;
	/** 会员卡id */
	public String cardName;
	/** 会员卡等级 */
	public String grade;
}

package com.meidianyi.shop.service.pojo.shop.member.account;

import javax.validation.constraints.AssertTrue;

import lombok.Data;

/**
 * @author huangzhuangzhuang
 */
@Data
public class UserCardGetParam {
	private Byte getType;
	/** card info */
	private CardInfo cardInfo;
	private Integer cardId;
	
	@AssertTrue(message="cardId must need.")
	private boolean isValid() {
		System.out.println("正在进行校验");
		if(cardId != null || cardInfo != null) {
			return true;
		}
		return false;
	}
}

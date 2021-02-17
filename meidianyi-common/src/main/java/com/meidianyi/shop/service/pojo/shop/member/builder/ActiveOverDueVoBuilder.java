package com.meidianyi.shop.service.pojo.shop.member.builder;

import com.meidianyi.shop.service.pojo.shop.member.card.ActiveOverDueVo;

/**
* @author 黄壮壮
* @Date: 2019年11月5日
* @Description:
*/

public class ActiveOverDueVoBuilder {
	private ActiveOverDueVo activeOverDue;
	
	private ActiveOverDueVoBuilder(){
		activeOverDue = new ActiveOverDueVo();
	}
	private ActiveOverDueVoBuilder(ActiveOverDueVo activeOverDue) {
		this.activeOverDue = activeOverDue;
	}
	
	
	public static ActiveOverDueVoBuilder create() {
		return new ActiveOverDueVoBuilder();
	}
	
	
	public static ActiveOverDueVoBuilder create(ActiveOverDueVo activeOverDue) {
		return new ActiveOverDueVoBuilder(activeOverDue);
	}

	public ActiveOverDueVoBuilder cardName (String cardName) {
		activeOverDue.setCardName(cardName);
		return this;
	}

	public ActiveOverDueVoBuilder cardId (Integer cardId) {
		activeOverDue.setCardId(cardId);
		return this;
	}

	public ActiveOverDueVoBuilder cardNum (Integer cardNum) {
		activeOverDue.setCardNum(cardNum);
		return this;
	}

	public ActiveOverDueVo build() {
		return activeOverDue;
	}
}

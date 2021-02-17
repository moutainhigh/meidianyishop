package com.meidianyi.shop.service.pojo.shop.member.builder;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;
import com.meidianyi.shop.service.pojo.shop.member.account.WxAppUserCardVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;

/**
* @author 黄壮壮
* @Date: 2019年11月4日
* @Description:
*/


public class WxAppUserCardVoBuilder {
	private WxAppUserCardVo wxUserCard;
	
	private WxAppUserCardVoBuilder(){
		wxUserCard = new WxAppUserCardVo();
	}
	private WxAppUserCardVoBuilder(WxAppUserCardVo wxUserCard) {
		this.wxUserCard = wxUserCard;
	}
	
	
	public static WxAppUserCardVoBuilder create() {
		return new WxAppUserCardVoBuilder();
	}
	
	
	public static WxAppUserCardVoBuilder create(WxAppUserCardVo wxUserCard) {
		return new WxAppUserCardVoBuilder(wxUserCard);
	}

	public WxAppUserCardVoBuilder expire (Byte expire) {
		wxUserCard.setExpire(expire);
		return this;
	}

	public WxAppUserCardVoBuilder renewal (Byte renewal) {
		wxUserCard.setRenewal(renewal);
		return this;
	}

	public WxAppUserCardVoBuilder startDate (LocalDate startDate) {
		wxUserCard.setStartDate(startDate);
		return this;
	}

	public WxAppUserCardVoBuilder endDate (LocalDate endDate) {
		wxUserCard.setEndDate(endDate);
		return this;
	}

	public WxAppUserCardVoBuilder avatar (String avatar) {
		wxUserCard.setAvatar(avatar);
		return this;
	}

	public WxAppUserCardVoBuilder goodsList (List<GoodsSmallVo> goodsList) {
		wxUserCard.setGoodsList(goodsList);
		return this;
	}

	public WxAppUserCardVoBuilder storeInfoList (List<StoreBasicVo> storeInfoList) {
		wxUserCard.setStoreInfoList(storeInfoList);
		return this;
	}

	public WxAppUserCardVoBuilder cumulativeScore (Integer cumulativeScore) {
		wxUserCard.setCumulativeScore(cumulativeScore);
		return this;
	}

	public WxAppUserCardVoBuilder cumulativeConsumptionAmounts (BigDecimal cumulativeConsumptionAmounts) {
		wxUserCard.setCumulativeConsumptionAmounts(cumulativeConsumptionAmounts);
		return this;
	}

	public WxAppUserCardVoBuilder cardVerifyStatus (Byte cardVerifyStatus) {
		wxUserCard.setCardVerifyStatus(cardVerifyStatus);
		return this;
	}


	public WxAppUserCardVo build() {
		return wxUserCard;
	}
}

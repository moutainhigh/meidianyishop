package com.meidianyi.shop.service.pojo.shop.member.builder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;

/**
* @author 黄壮壮
* @Date: 2019年11月4日
* @Description:
*/

public class UserCardParamBuilder {
	private UserCardParam userCard;
	
	protected UserCardParamBuilder(){
		userCard = new UserCardParam();
	}
	protected UserCardParamBuilder(UserCardParam userCard) {
		this.userCard = userCard;
	}
	
	
	public static UserCardParamBuilder create() {
		return new UserCardParamBuilder();
	}
	
	
	public static UserCardParamBuilder create(UserCardParam userCard) {
		return new UserCardParamBuilder(userCard);
	}

	public UserCardParamBuilder cardName (String cardName) {
		userCard.setCardName(cardName);
		return this;
	}

	public UserCardParamBuilder cardType (Byte cardType) {
		userCard.setCardType(cardType);
		return this;
	}

	public UserCardParamBuilder bgType (Byte bgType) {
		userCard.setBgType(bgType);
		return this;
	}

	public UserCardParamBuilder bgColor (String bgColor) {
		userCard.setBgColor(bgColor);
		return this;
	}

	public UserCardParamBuilder bgImg (String bgImg) {
		userCard.setBgImg(bgImg);
		return this;
	}

	public UserCardParamBuilder discount (BigDecimal discount) {
		userCard.setDiscount(discount);
		return this;
	}

	public UserCardParamBuilder sorce (Integer sorce) {
		userCard.setSorce(sorce);
		return this;
	}

	public UserCardParamBuilder buyScore (String buyScore) {
		userCard.setBuyScore(buyScore);
		return this;
	}

	public UserCardParamBuilder expireType (Byte expireType) {
		userCard.setExpireType(expireType);
		return this;
	}

	public UserCardParamBuilder startTime (Timestamp startTime) {
		userCard.setStartTime(startTime);
		return this;
	}

	public UserCardParamBuilder endTime (Timestamp endTime) {
		userCard.setEndTime(endTime);
		return this;
	}

	public UserCardParamBuilder receiveDay (Integer receiveDay) {
		userCard.setReceiveDay(receiveDay);
		return this;
	}

	public UserCardParamBuilder dateType (Byte dateType) {
		userCard.setDateType(dateType);
		return this;
	}

	public UserCardParamBuilder activation (Byte activation) {
		userCard.setActivation(activation);
		return this;
	}

	public UserCardParamBuilder receiveCode (String receiveCode) {
		userCard.setReceiveCode(receiveCode);
		return this;
	}

	public UserCardParamBuilder desc (String desc) {
		userCard.setDesc(desc);
		return this;
	}

	public UserCardParamBuilder mobile (String mobile) {
		userCard.setMobile(mobile);
		return this;
	}

	public UserCardParamBuilder userCardCreateTime (Timestamp createTime) {
		userCard.setUserCardCreateTime(createTime);
		return this;
	}

	public UserCardParamBuilder userCardUpdateTime (Timestamp updateTime) {
		userCard.setUserCardUpdateTime(updateTime);
		return this;
	}

	public UserCardParamBuilder userCardFlag (Byte flag) {
		userCard.setUserCardFlag(flag);
		return this;
	}

	public UserCardParamBuilder sendMoney (Integer sendMoney) {
		userCard.setSendMoney(sendMoney);
		return this;
	}

	public UserCardParamBuilder chargeMoney (String chargeMoney) {
		userCard.setChargeMoney(chargeMoney);
		return this;
	}

	public UserCardParamBuilder useTime (Integer useTime) {
		userCard.setUseTime(useTime);
		return this;
	}

	public UserCardParamBuilder storeList (String storeList) {
		userCard.setStoreList(storeList);
		return this;
	}

	public UserCardParamBuilder count (Integer count) {
		userCard.setCount(count);
		return this;
	}

	public UserCardParamBuilder delFlag (Byte delFlag) {
		userCard.setDelFlag(delFlag);
		return this;
	}

	public UserCardParamBuilder grade (String grade) {
		userCard.setGrade(grade);
		return this;
	}

	public UserCardParamBuilder gradeCondition (String gradeCondition) {
		userCard.setGradeCondition(gradeCondition);
		return this;
	}

	public UserCardParamBuilder activationCfg (String activationCfg) {
		userCard.setActivationCfg(activationCfg);
		return this;
	}

	public UserCardParamBuilder examine (Byte examine) {
		userCard.setExamine(examine);
		return this;
	}

	public UserCardParamBuilder discountGoodsId (String discountGoodsId) {
		userCard.setDiscountGoodsId(discountGoodsId);
		return this;
	}

	public UserCardParamBuilder discountCatId (String discountCatId) {
		userCard.setDiscountCatId(discountCatId);
		return this;
	}

	public UserCardParamBuilder discountSortId (String discountSortId) {
		userCard.setDiscountSortId(discountSortId);
		return this;
	}

	public UserCardParamBuilder discountIsAll (Byte discountIsAll) {
		userCard.setDiscountIsAll(discountIsAll);
		return this;
	}

	public UserCardParamBuilder isPay (Byte isPay) {
		userCard.setIsPay(isPay);
		return this;
	}

	public UserCardParamBuilder payType (Byte payType) {
		userCard.setPayType(payType);
		return this;
	}

	public UserCardParamBuilder payFee (BigDecimal payFee) {
		userCard.setPayFee(payFee);
		return this;
	}

	public UserCardParamBuilder payOwnGood (Byte payOwnGood) {
		userCard.setPayOwnGood(payOwnGood);
		return this;
	}

	public UserCardParamBuilder receiveAction (Byte receiveAction) {
		userCard.setReceiveAction(receiveAction);
		return this;
	}

	public UserCardParamBuilder isExchang (Byte isExchang) {
		userCard.setIsExchang(isExchang);
		return this;
	}

	public UserCardParamBuilder storeUseSwitch (Byte storeUseSwitch) {
		userCard.setStoreUseSwitch(storeUseSwitch);
		return this;
	}

	public UserCardParamBuilder exchangGoods (String exchangGoods) {
		userCard.setExchangGoods(exchangGoods);
		return this;
	}

	public UserCardParamBuilder exchangFreight (Byte exchangFreight) {
		userCard.setExchangFreight(exchangFreight);
		return this;
	}

	public UserCardParamBuilder exchangCount (Integer exchangCount) {
		userCard.setExchangCount(exchangCount);
		return this;
	}

	public UserCardParamBuilder stock (Integer stock) {
		userCard.setStock(stock);
		return this;
	}

	public UserCardParamBuilder limit (Integer limit) {
		userCard.setLimit(limit);
		return this;
	}

	public UserCardParamBuilder discountBrandId (String discountBrandId) {
		userCard.setDiscountBrandId(discountBrandId);
		return this;
	}

	public UserCardParamBuilder userId (Integer userId) {
		userCard.setUserId(userId);
		return this;
	}

	public UserCardParamBuilder cardId (Integer cardId) {
		userCard.setCardId(cardId);
		return this;
	}

	public UserCardParamBuilder flag (Byte flag) {
		userCard.setFlag(flag);
		return this;
	}

	public UserCardParamBuilder cardNo (String cardNo) {
		userCard.setCardNo(cardNo);
		return this;
	}

	public UserCardParamBuilder expireTime (Timestamp expireTime) {
		userCard.setExpireTime(expireTime);
		return this;
	}

	public UserCardParamBuilder isDefault (Byte isDefault) {
		userCard.setIsDefault(isDefault);
		return this;
	}

	public UserCardParamBuilder money (BigDecimal money) {
		userCard.setMoney(money);
		return this;
	}

	public UserCardParamBuilder surplus (Integer surplus) {
		userCard.setSurplus(surplus);
		return this;
	}

	public UserCardParamBuilder activationTime (Timestamp activationTime) {
		userCard.setActivationTime(activationTime);
		return this;
	}

	public UserCardParamBuilder exchangSurplus (Integer exchangSurplus) {
		userCard.setExchangSurplus(exchangSurplus);
		return this;
	}

	public UserCardParamBuilder createTime (Timestamp createTime) {
		userCard.setCreateTime(createTime);
		return this;
	}

	public UserCardParamBuilder updateTime (Timestamp updateTime) {
		userCard.setUpdateTime(updateTime);
		return this;
	}

	public UserCardParam build() {
		return userCard;
	}
}

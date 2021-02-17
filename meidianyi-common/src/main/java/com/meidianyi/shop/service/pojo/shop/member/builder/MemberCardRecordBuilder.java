// generate by generateRecordBuild.py
package com.meidianyi.shop.service.pojo.shop.member.builder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;

/**
 * @author 黄壮壮
 * @Date: 2019年11月1日
 * @Description: 
 */
public class MemberCardRecordBuilder {
	private MemberCardRecord record;
	
	private MemberCardRecordBuilder(){
		record = new MemberCardRecord();
	}
	private MemberCardRecordBuilder(MemberCardRecord record) {
		this.record = record;
	}
	
	
	public static MemberCardRecordBuilder create() {
		return new MemberCardRecordBuilder();
	}
	
	
	public static MemberCardRecordBuilder create(MemberCardRecord record) {
		return new MemberCardRecordBuilder(record);
	}

	public MemberCardRecordBuilder id (Integer id) {
		record.setId(id);
		return this;
	}

	public MemberCardRecordBuilder cardName (String cardName) {
		record.setCardName(cardName);
		return this;
	}

	public MemberCardRecordBuilder cardType (Byte cardType) {
		record.setCardType(cardType);
		return this;
	}

	public MemberCardRecordBuilder bgType (Byte bgType) {
		record.setBgType(bgType);
		return this;
	}

	public MemberCardRecordBuilder bgColor (String bgColor) {
		record.setBgColor(bgColor);
		return this;
	}

	public MemberCardRecordBuilder bgImg (String bgImg) {
		record.setBgImg(bgImg);
		return this;
	}

	public MemberCardRecordBuilder discount (BigDecimal discount) {
		record.setDiscount(discount);
		return this;
	}

	public MemberCardRecordBuilder sorce (Integer sorce) {
		record.setSorce(sorce);
		return this;
	}

	public MemberCardRecordBuilder buyScore (String buyScore) {
		record.setBuyScore(buyScore);
		return this;
	}

	public MemberCardRecordBuilder expireType (Byte expireType) {
		record.setExpireType(expireType);
		return this;
	}

	public MemberCardRecordBuilder startTime (Timestamp startTime) {
		record.setStartTime(startTime);
		return this;
	}

	public MemberCardRecordBuilder endTime (Timestamp endTime) {
		record.setEndTime(endTime);
		return this;
	}

	public MemberCardRecordBuilder receiveDay (Integer receiveDay) {
		record.setReceiveDay(receiveDay);
		return this;
	}

	public MemberCardRecordBuilder dateType (Byte dateType) {
		record.setDateType(dateType);
		return this;
	}

	public MemberCardRecordBuilder activation (Byte activation) {
		record.setActivation(activation);
		return this;
	}

	public MemberCardRecordBuilder receiveCode (String receiveCode) {
		record.setReceiveCode(receiveCode);
		return this;
	}

	public MemberCardRecordBuilder desc (String desc) {
		record.setDesc(desc);
		return this;
	}

	public MemberCardRecordBuilder mobile (String mobile) {
		record.setMobile(mobile);
		return this;
	}

	public MemberCardRecordBuilder createTime (Timestamp createTime) {
		record.setCreateTime(createTime);
		return this;
	}

	public MemberCardRecordBuilder updateTime (Timestamp updateTime) {
		record.setUpdateTime(updateTime);
		return this;
	}

	public MemberCardRecordBuilder flag (Byte flag) {
		record.setFlag(flag);
		return this;
	}

	public MemberCardRecordBuilder sendMoney (Integer sendMoney) {
		record.setSendMoney(sendMoney);
		return this;
	}

	public MemberCardRecordBuilder chargeMoney (String chargeMoney) {
		record.setChargeMoney(chargeMoney);
		return this;
	}

	public MemberCardRecordBuilder useTime (Integer useTime) {
		record.setUseTime(useTime);
		return this;
	}

	public MemberCardRecordBuilder storeList (String storeList) {
		record.setStoreList(storeList);
		return this;
	}

	public MemberCardRecordBuilder count (Integer count) {
		record.setCount(count);
		return this;
	}

	public MemberCardRecordBuilder delFlag (Byte delFlag) {
		record.setDelFlag(delFlag);
		return this;
	}

	public MemberCardRecordBuilder grade (String grade) {
		record.setGrade(grade);
		return this;
	}

	public MemberCardRecordBuilder gradeCondition (String gradeCondition) {
		record.setGradeCondition(gradeCondition);
		return this;
	}

	public MemberCardRecordBuilder activationCfg (String activationCfg) {
		record.setActivationCfg(activationCfg);
		return this;
	}

	public MemberCardRecordBuilder examine (Byte examine) {
		record.setExamine(examine);
		return this;
	}

	public MemberCardRecordBuilder discountGoodsId (String discountGoodsId) {
		record.setDiscountGoodsId(discountGoodsId);
		return this;
	}

	public MemberCardRecordBuilder discountCatId (String discountCatId) {
		record.setDiscountCatId(discountCatId);
		return this;
	}

	public MemberCardRecordBuilder discountSortId (String discountSortId) {
		record.setDiscountSortId(discountSortId);
		return this;
	}

	public MemberCardRecordBuilder discountIsAll (Byte discountIsAll) {
		record.setDiscountIsAll(discountIsAll);
		return this;
	}

	public MemberCardRecordBuilder isPay (Byte isPay) {
		record.setIsPay(isPay);
		return this;
	}

	public MemberCardRecordBuilder payType (Byte payType) {
		record.setPayType(payType);
		return this;
	}

	public MemberCardRecordBuilder payFee (BigDecimal payFee) {
		record.setPayFee(payFee);
		return this;
	}

	public MemberCardRecordBuilder payOwnGood (Byte payOwnGood) {
		record.setPayOwnGood(payOwnGood);
		return this;
	}

	public MemberCardRecordBuilder receiveAction (Byte receiveAction) {
		record.setReceiveAction(receiveAction);
		return this;
	}

	public MemberCardRecordBuilder isExchang (Byte isExchang) {
		record.setIsExchang(isExchang);
		return this;
	}

	public MemberCardRecordBuilder storeUseSwitch (Byte storeUseSwitch) {
		record.setStoreUseSwitch(storeUseSwitch);
		return this;
	}

	public MemberCardRecordBuilder exchangGoods (String exchangGoods) {
		record.setExchangGoods(exchangGoods);
		return this;
	}

	public MemberCardRecordBuilder exchangFreight (Byte exchangFreight) {
		record.setExchangFreight(exchangFreight);
		return this;
	}

	public MemberCardRecordBuilder exchangCount (Integer exchangCount) {
		record.setExchangCount(exchangCount);
		return this;
	}

	public MemberCardRecordBuilder stock (Integer stock) {
		record.setStock(stock);
		return this;
	}

	public MemberCardRecordBuilder limit (Integer limit) {
		record.setLimit(limit);
		return this;
	}

	public MemberCardRecordBuilder discountBrandId (String discountBrandId) {
		record.setDiscountBrandId(discountBrandId);
		return this;
	}

	public MemberCardRecordBuilder sendCouponSwitch (Byte sendCouponSwitch) {
		record.setSendCouponSwitch(sendCouponSwitch);
		return this;
	}

	public MemberCardRecordBuilder sendCouponType (Byte sendCouponType) {
		record.setSendCouponType(sendCouponType);
		return this;
	}

	public MemberCardRecordBuilder sendCouponIds (String sendCouponIds) {
		record.setSendCouponIds(sendCouponIds);
		return this;
	}

	public MemberCardRecordBuilder customRights (String customRights) {
		record.setCustomRights(customRights);
		return this;
	}

	public MemberCardRecordBuilder freeshipLimit (Byte freeshipLimit) {
		record.setFreeshipLimit(freeshipLimit);
		return this;
	}

	public MemberCardRecordBuilder freeshipNum (Integer freeshipNum) {
		record.setFreeshipNum(freeshipNum);
		return this;
	}

	public MemberCardRecordBuilder renewMemberCard (Byte renewMemberCard) {
		record.setRenewMemberCard(renewMemberCard);
		return this;
	}

	public MemberCardRecordBuilder renewType (Byte renewType) {
		record.setRenewType(renewType);
		return this;
	}

	public MemberCardRecordBuilder renewNum (BigDecimal renewNum) {
		record.setRenewNum(renewNum);
		return this;
	}

	public MemberCardRecordBuilder renewTime (Integer renewTime) {
		record.setRenewTime(renewTime);
		return this;
	}

	public MemberCardRecordBuilder renewDateType (Byte renewDateType) {
		record.setRenewDateType(renewDateType);
		return this;
	}

	public MemberCardRecordBuilder cannotUseCoupon (Byte cannotUseCoupon) {
		record.setCannotUseCoupon(cannotUseCoupon);
		return this;
	}

	public MemberCardRecordBuilder customRightsFlag (Byte customRightsFlag) {
		record.setCustomRightsFlag(customRightsFlag);
		return this;
	}

	public MemberCardRecordBuilder customOptions (String customOptions) {
		record.setCustomOptions(customOptions);
		return this;
	}

	public MemberCardRecordBuilder cardTag (Byte cardTag) {
		record.setCardTag(cardTag);
		return this;
	}

	public MemberCardRecordBuilder cardTagId (String cardTagId) {
		record.setCardTagId(cardTagId);
		return this;
	}

	public MemberCardRecordBuilder cardGiveAway (Byte cardGiveAway) {
		record.setCardGiveAway(cardGiveAway);
		return this;
	}

	public MemberCardRecordBuilder cardGiveContinue (Byte cardGiveContinue) {
		record.setCardGiveContinue(cardGiveContinue);
		return this;
	}

	public MemberCardRecordBuilder mostGiveAway (Integer mostGiveAway) {
		record.setMostGiveAway(mostGiveAway);
		return this;
	}

	public MemberCardRecordBuilder cannotUseAction (String cannotUseAction) {
		record.setCannotUseAction(cannotUseAction);
		return this;
	}

	public MemberCardRecordBuilder periodLimit (Byte periodLimit) {
		record.setPeriodLimit(periodLimit);
		return this;
	}

	public MemberCardRecordBuilder periodNum (Integer periodNum) {
		record.setPeriodNum(periodNum);
		return this;
	}

	public MemberCardRecord build() {
		return record;
	}
}


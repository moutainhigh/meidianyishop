// generate by generateRecordBuilder.py
package com.meidianyi.shop.service.pojo.shop.member.builder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;

/**
 * @author 黄壮壮
 * @Date: 2019年11月1日
 * @Description: 用户卡builder
 */


public class UserCardRecordBuilder {
	private UserCardRecord record;
	
	private UserCardRecordBuilder(){
		record = new UserCardRecord();
	}
	private UserCardRecordBuilder(UserCardRecord record) {
		this.record = record;
	}
	
	
	public static UserCardRecordBuilder create() {
		return new UserCardRecordBuilder();
	}
	
	
	public static UserCardRecordBuilder create(UserCardRecord record) {
		return new UserCardRecordBuilder(record);
	}

	public UserCardRecordBuilder userId (Integer userId) {
		if(userId != null){
			record.setUserId(userId);
		}
		return this;
	}

	public UserCardRecordBuilder cardId (Integer cardId) {
		if(cardId != null){
			record.setCardId(cardId);
		}
		return this;
	}

	public UserCardRecordBuilder flag (Byte flag) {
		if(flag != null){
			record.setFlag(flag);
		}
		return this;
	}

	public UserCardRecordBuilder cardNo (String cardNo) {
		if(cardNo != null){
			record.setCardNo(cardNo);
		}
		return this;
	}

	public UserCardRecordBuilder expireTime (Timestamp expireTime) {
		if(expireTime != null){
			record.setExpireTime(expireTime);
		}
		return this;
	}

	public UserCardRecordBuilder isDefault (Byte isDefault) {
		if(isDefault != null){
			record.setIsDefault(isDefault);
		}
		return this;
	}

	public UserCardRecordBuilder money (BigDecimal money) {
		if(money != null){
			record.setMoney(money);
		}
		return this;
	}

	public UserCardRecordBuilder surplus (Integer surplus) {
		if(surplus != null){
			record.setSurplus(surplus);
		}
		return this;
	}

	public UserCardRecordBuilder activationTime (Timestamp activationTime) {
		if(activationTime != null){
			record.setActivationTime(activationTime);
		}
		return this;
	}

	public UserCardRecordBuilder exchangSurplus (Integer exchangSurplus) {
		if(exchangSurplus != null){
			record.setExchangSurplus(exchangSurplus);
		}
		return this;
	}

	public UserCardRecordBuilder createTime (Timestamp createTime) {
		if(createTime != null){
			record.setCreateTime(createTime);
		}
		return this;
	}

	public UserCardRecordBuilder updateTime (Timestamp updateTime) {
		if(updateTime != null){
			record.setUpdateTime(updateTime);
		}
		return this;
	}

	public UserCardRecordBuilder id (Integer id) {
		if(id != null){
			record.setId(id);
		}
		return this;
	}

	public UserCardRecordBuilder freeLimit (Byte freeLimit) {
		if(freeLimit != null){
			record.setFreeLimit(freeLimit);
		}
		return this;
	}

	public UserCardRecordBuilder freeNum (Integer freeNum) {
		if(freeNum != null){
			record.setFreeNum(freeNum);
		}
		return this;
	}

	public UserCardRecordBuilder giveAwayStatus (Byte giveAwayStatus) {
		if(giveAwayStatus != null){
			record.setGiveAwayStatus(giveAwayStatus);
		}
		return this;
	}

	public UserCardRecordBuilder giveAwaySurplus (Integer giveAwaySurplus) {
		if(giveAwaySurplus != null){
			record.setGiveAwaySurplus(giveAwaySurplus);
		}
		return this;
	}

	public UserCardRecordBuilder cardSource (Byte cardSource) {
		if(cardSource != null){
			record.setCardSource(cardSource);
		}
		return this;
	}

	public UserCardRecord build() {
		return record;
	}
}


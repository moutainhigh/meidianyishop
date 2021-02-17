package com.meidianyi.shop.service.pojo.shop.member.builder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.db.shop.tables.records.UserAccountRecord;

/**
* @author 黄壮壮
* @Date: 2019年11月25日
* @Description:
*/


public class UserAccountRecordBuilder {
	private UserAccountRecord record;
	
	private UserAccountRecordBuilder(){
		record = new UserAccountRecord();
	}
	private UserAccountRecordBuilder(UserAccountRecord record) {
		this.record = record;
	}
	
	
	public static UserAccountRecordBuilder create() {
		return new UserAccountRecordBuilder();
	}
	
	
	public static UserAccountRecordBuilder create(UserAccountRecord record) {
		return new UserAccountRecordBuilder(record);
	}

	public UserAccountRecordBuilder id (Integer id) {
		record.setId(id);
		return this;
	}

	public UserAccountRecordBuilder userId (Integer userId) {
		record.setUserId(userId);
		return this;
	}

	public UserAccountRecordBuilder adminUser (String adminUser) {
		record.setAdminUser(adminUser);
		return this;
	}

	public UserAccountRecordBuilder orderSn (String orderSn) {
		if(orderSn != null) {
			record.setOrderSn(orderSn);
		}
		return this;
	}

	public UserAccountRecordBuilder amount (BigDecimal amount) {
		record.setAmount(amount);
		return this;
	}

	public UserAccountRecordBuilder adminNote (String adminNote) {
		record.setAdminNote(adminNote);
		return this;
	}

	public UserAccountRecordBuilder payment (String payment) {
		record.setPayment(payment);
		return this;
	}

	public UserAccountRecordBuilder isPaid (Byte isPaid) {
		record.setIsPaid(isPaid);
		return this;
	}

	public UserAccountRecordBuilder remarkId (String remarkId) {
		record.setRemarkId(remarkId);
		return this;
	}

	public UserAccountRecordBuilder remarkData (String remarkData) {
		record.setRemarkData(remarkData);
		return this;
	}

	public UserAccountRecordBuilder source (Byte source) {
		record.setSource(source);
		return this;
	}

	public UserAccountRecordBuilder withdrawStatus (Byte withdrawStatus) {
		record.setWithdrawStatus(withdrawStatus);
		return this;
	}

	public UserAccountRecordBuilder settleAccount (BigDecimal settleAccount) {
		record.setSettleAccount(settleAccount);
		return this;
	}

	public UserAccountRecordBuilder createTime (Timestamp createTime) {
		record.setCreateTime(createTime);
		return this;
	}

	public UserAccountRecordBuilder updateTime (Timestamp updateTime) {
		record.setUpdateTime(updateTime);
		return this;
	}

	public UserAccountRecord build() {
		return record;
	}
}


package com.meidianyi.shop.service.pojo.shop.member.builder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.db.shop.tables.records.ChargeMoneyRecord;

/**
 * @author 黄壮壮
 * @Date: 2019年11月1日
 * @Description:
 */


public class ChargeMoneyRecordBuilder {
	private ChargeMoneyRecord record;
	
	private ChargeMoneyRecordBuilder(){
		record = new ChargeMoneyRecord();
	}
	private ChargeMoneyRecordBuilder(ChargeMoneyRecord record) {
		this.record = record;
	}
	
	
	public static ChargeMoneyRecordBuilder create() {
		return new ChargeMoneyRecordBuilder();
	}
	
	
	public static ChargeMoneyRecordBuilder create(ChargeMoneyRecord record) {
		return new ChargeMoneyRecordBuilder(record);
	}

	public ChargeMoneyRecordBuilder id (Integer id) {
		record.setId(id);
		return this;
	}

	public ChargeMoneyRecordBuilder userId (Integer userId) {
		record.setUserId(userId);
		return this;
	}

	public ChargeMoneyRecordBuilder cardId (Integer cardId) {
		record.setCardId(cardId);
		return this;
	}

	public ChargeMoneyRecordBuilder charge (BigDecimal charge) {
		record.setCharge(charge);
		return this;
	}

	public ChargeMoneyRecordBuilder count (Short count) {
		record.setCount(count);
		return this;
	}

	public ChargeMoneyRecordBuilder payment (String payment) {
		record.setPayment(payment);
		return this;
	}

	public ChargeMoneyRecordBuilder type (Byte type) {
		record.setType(type);
		return this;
	}

	public ChargeMoneyRecordBuilder reasonId (String reasonId) {
		record.setReasonId(reasonId);
		return this;
	}

	public ChargeMoneyRecordBuilder reason (String reason) {
		record.setReason(reason);
		return this;
	}

	public ChargeMoneyRecordBuilder prepayId (String prepayId) {
		record.setPrepayId(prepayId);
		return this;
	}

	public ChargeMoneyRecordBuilder message (String message) {
		record.setMessage(message);
		return this;
	}

	public ChargeMoneyRecordBuilder orderSn (String orderSn) {
		record.setOrderSn(orderSn);
		return this;
	}

	public ChargeMoneyRecordBuilder orderStatus (Byte orderStatus) {
		record.setOrderStatus(orderStatus);
		return this;
	}

	public ChargeMoneyRecordBuilder moneyPaid (BigDecimal moneyPaid) {
		record.setMoneyPaid(moneyPaid);
		return this;
	}

	public ChargeMoneyRecordBuilder chargeType (Byte chargeType) {
		record.setChargeType(chargeType);
		return this;
	}

	public ChargeMoneyRecordBuilder cardNo (String cardNo) {
		record.setCardNo(cardNo);
		return this;
	}

	public ChargeMoneyRecordBuilder aliTradeNo (String aliTradeNo) {
		record.setAliTradeNo(aliTradeNo);
		return this;
	}

	public ChargeMoneyRecordBuilder exchangCount (Short exchangCount) {
		record.setExchangCount(exchangCount);
		return this;
	}

	public ChargeMoneyRecordBuilder createTime (Timestamp createTime) {
		record.setCreateTime(createTime);
		return this;
	}

	public ChargeMoneyRecordBuilder updateTime (Timestamp updateTime) {
		record.setUpdateTime(updateTime);
		return this;
	}

	public ChargeMoneyRecordBuilder returnMoney (BigDecimal returnMoney) {
		record.setReturnMoney(returnMoney);
		return this;
	}

	public ChargeMoneyRecordBuilder afterChargeMoney (BigDecimal afterChargeMoney) {
		record.setAfterChargeMoney(afterChargeMoney);
		return this;
	}

	public ChargeMoneyRecordBuilder changeType (Byte changeType) {
		record.setChangeType(changeType);
		return this;
	}

	public ChargeMoneyRecord build() {
		return record;
	}
}


package com.meidianyi.shop.service.pojo.shop.member.builder;
import java.sql.Timestamp;
import com.meidianyi.shop.db.shop.tables.records.CardReceiveCodeRecord;

/**
 * @author huangzhuangzhuang
 */
public class CardReceiveCodeRecordBuilder {
	private CardReceiveCodeRecord record;
	
	private CardReceiveCodeRecordBuilder(){
		record = new CardReceiveCodeRecord();
	}
	private CardReceiveCodeRecordBuilder(CardReceiveCodeRecord record) {
		this.record = record;
	}
	
	
	public static CardReceiveCodeRecordBuilder create() {
		return new CardReceiveCodeRecordBuilder();
	}
	
	
	public static CardReceiveCodeRecordBuilder create(CardReceiveCodeRecord record) {
		return new CardReceiveCodeRecordBuilder(record);
	}

	public CardReceiveCodeRecordBuilder id (Integer id) {
		record.setId(id);
		return this;
	}

	public CardReceiveCodeRecordBuilder cardId (Integer cardId) {
		record.setCardId(cardId);
		return this;
	}

	public CardReceiveCodeRecordBuilder batchId (Integer batchId) {
		record.setBatchId(batchId);
		return this;
	}

	public CardReceiveCodeRecordBuilder groupId (Integer groupId) {
		record.setGroupId(groupId);
		return this;
	}

	public CardReceiveCodeRecordBuilder code (String code) {
		record.setCode(code);
		return this;
	}

	public CardReceiveCodeRecordBuilder cardNo (String cardNo) {
		record.setCardNo(cardNo);
		return this;
	}

	public CardReceiveCodeRecordBuilder cardPwd (String cardPwd) {
		record.setCardPwd(cardPwd);
		return this;
	}

	public CardReceiveCodeRecordBuilder userId (Integer userId) {
		record.setUserId(userId);
		return this;
	}

	public CardReceiveCodeRecordBuilder receiveTime (Timestamp receiveTime) {
		record.setReceiveTime(receiveTime);
		return this;
	}

	public CardReceiveCodeRecordBuilder errorMsg (String errorMsg) {
		record.setErrorMsg(errorMsg);
		return this;
	}

	public CardReceiveCodeRecordBuilder status (Byte status) {
		record.setStatus(status);
		return this;
	}

	public CardReceiveCodeRecordBuilder delFlag (Byte delFlag) {
		record.setDelFlag(delFlag);
		return this;
	}

	public CardReceiveCodeRecordBuilder delTime (Timestamp delTime) {
		record.setDelTime(delTime);
		return this;
	}

	public CardReceiveCodeRecordBuilder createTime (Timestamp createTime) {
		record.setCreateTime(createTime);
		return this;
	}

	public CardReceiveCodeRecordBuilder updateTime (Timestamp updateTime) {
		record.setUpdateTime(updateTime);
		return this;
	}

	public CardReceiveCodeRecord build() {
		return record;
	}
}

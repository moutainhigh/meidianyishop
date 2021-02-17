// generate by generateRecordBuild.py
package com.meidianyi.shop.service.pojo.shop.member.builder;

import java.sql.Timestamp;

import com.meidianyi.shop.db.shop.tables.records.GiveCardRecordRecord;
/**
 * @author 黄壮壮
 * @Date: 2020年04月15日
 * @Description: 会员卡赠送Builder
 */


public class GiveCardRecordRecordBuilder {
	private GiveCardRecordRecord record;
	
	private GiveCardRecordRecordBuilder(){
		record = new GiveCardRecordRecord();
	}
	private GiveCardRecordRecordBuilder(GiveCardRecordRecord record) {
		this.record = record;
	}
	
	
	public static GiveCardRecordRecordBuilder create() {
		return new GiveCardRecordRecordBuilder();
	}
	
	
	public static GiveCardRecordRecordBuilder create(GiveCardRecordRecord record) {
		return new GiveCardRecordRecordBuilder(record);
	}

	public GiveCardRecordRecordBuilder id (Integer id) {
		if(id != null){
			record.setId(id);
		}
		return this;
	}

	public GiveCardRecordRecordBuilder userId (Integer userId) {
		if(userId != null){
			record.setUserId(userId);
		}
		return this;
	}

	public GiveCardRecordRecordBuilder createTime (Timestamp createTime) {
		if(createTime != null){
			record.setCreateTime(createTime);
		}
		return this;
	}

	public GiveCardRecordRecordBuilder cardNo (String cardNo) {
		if(cardNo != null){
			record.setCardNo(cardNo);
		}
		return this;
	}

	public GiveCardRecordRecordBuilder getUserId (Integer getUserId) {
		if(getUserId != null){
			record.setGetUserId(getUserId);
		}
		return this;
	}

	public GiveCardRecordRecordBuilder getTime (Timestamp getTime) {
		if(getTime != null){
			record.setGetTime(getTime);
		}
		return this;
	}

	public GiveCardRecordRecordBuilder getCardNo (String getCardNo) {
		if(getCardNo != null){
			record.setGetCardNo(getCardNo);
		}
		return this;
	}

	public GiveCardRecordRecordBuilder flag (Byte flag) {
		if(flag != null){
			record.setFlag(flag);
		}
		return this;
	}

	public GiveCardRecordRecordBuilder deadline (Timestamp deadline) {
		if(deadline != null){
			record.setDeadline(deadline);
		}
		return this;
	}

	public GiveCardRecordRecord build() {
		return record;
	}
}


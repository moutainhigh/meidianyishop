package com.meidianyi.shop.service.pojo.shop.operation.builder;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.db.shop.tables.records.TradesRecordRecord;

/**
* @author 黄壮壮
* @Date: 2019年11月18日
* @Description: TradesRecordRecord的builder
*/

public class TradesRecordRecordBuilder {
	private TradesRecordRecord record;
	
	private TradesRecordRecordBuilder(){
		record = new TradesRecordRecord();
	}
	private TradesRecordRecordBuilder(TradesRecordRecord record) {
		this.record = record;
	}
	
	
	public static TradesRecordRecordBuilder create() {
		return new TradesRecordRecordBuilder();
	}
	
	
	public static TradesRecordRecordBuilder create(TradesRecordRecord record) {
		return new TradesRecordRecordBuilder(record);
	}

	public TradesRecordRecordBuilder id (Integer id) {
		record.setId(id);
		return this;
	}

	public TradesRecordRecordBuilder tradeTime (Timestamp tradeTime) {
		record.setTradeTime(tradeTime);
		return this;
	}

	public TradesRecordRecordBuilder tradeNum (BigDecimal tradeNum) {
		record.setTradeNum(tradeNum);
		return this;
	}

	public TradesRecordRecordBuilder userId (Integer userId) {
		record.setUserId(userId);
		return this;
	}

	public TradesRecordRecordBuilder tradeContent (Byte tradeContent) {
		record.setTradeContent(tradeContent);
		return this;
	}

	public TradesRecordRecordBuilder tradeType (Byte tradeType) {
		record.setTradeType(tradeType);
		return this;
	}

	public TradesRecordRecordBuilder tradeFlow (Byte tradeFlow) {
		record.setTradeFlow(tradeFlow);
		return this;
	}

	public TradesRecordRecordBuilder tradeStatus (Byte tradeStatus) {
		record.setTradeStatus(tradeStatus);
		return this;
	}

	public TradesRecordRecordBuilder tradeSn (String tradeSn) {
		record.setTradeSn(tradeSn);
		return this;
	}

	public TradesRecordRecordBuilder createTime (Timestamp createTime) {
		record.setCreateTime(createTime);
		return this;
	}

	public TradesRecordRecordBuilder updateTime (Timestamp updateTime) {
		record.setUpdateTime(updateTime);
		return this;
	}

	public TradesRecordRecord build() {
		return record;
	}
}

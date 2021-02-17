package com.meidianyi.shop.service.pojo.shop.member.builder;

import com.meidianyi.shop.db.shop.tables.records.CardUpgradeRecord;

/**
 * @author 黄壮壮
 * @Date: 2019年11月1日
 * @Description:
 */
public class CardUpgradeRecordBuilder {
	private CardUpgradeRecord record;

	private CardUpgradeRecordBuilder() {
		record = new CardUpgradeRecord();
	}

	private CardUpgradeRecordBuilder(CardUpgradeRecord record) {
		this.record = record;
	}

	public static CardUpgradeRecordBuilder create() {
		return new CardUpgradeRecordBuilder();
	}

	public static CardUpgradeRecordBuilder create(CardUpgradeRecord record) {
		return new CardUpgradeRecordBuilder(record);
	}

	public CardUpgradeRecordBuilder userId(Integer userId) {
		record.setUserId(userId);
		return this;
	}

	public CardUpgradeRecordBuilder oldCardId(Integer oldCardId) {
		record.setOldCardId(oldCardId);
		return this;
	}

	public CardUpgradeRecordBuilder newCardId(Integer newCardId) {
		record.setNewCardId(newCardId);
		return this;
	}

	public CardUpgradeRecordBuilder oldGrade(String oldGrade) {
		record.setOldGrade(oldGrade);
		return this;
	}

	public CardUpgradeRecordBuilder newGrade(String newGrade) {
		record.setNewGrade(newGrade);
		return this;
	}

	public CardUpgradeRecordBuilder oldCardName(String oldCardName) {
		record.setOldCardName(oldCardName);
		return this;
	}

	public CardUpgradeRecordBuilder newCardName(String newCardName) {
		record.setNewCardName(newCardName);
		return this;
	}

	public CardUpgradeRecordBuilder gradeCondition(String gradeCondition) {
		record.setGradeCondition(gradeCondition);
		return this;
	}

	public CardUpgradeRecordBuilder operate(String operate) {
		record.setOperate(operate);
		return this;
	}

	public CardUpgradeRecord build() {
		return record;
	}

}

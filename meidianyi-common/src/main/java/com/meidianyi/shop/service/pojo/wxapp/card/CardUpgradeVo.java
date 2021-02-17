package com.meidianyi.shop.service.pojo.wxapp.card;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 
 * @author 黄壮壮
 *	升级出参
 */
@Data
public class CardUpgradeVo {
	/**
	 * 新卡名称
	 */
	@JsonProperty("new_card_name")
	private String newCardName;
	
	/**
	 * 升级变动时间
	 */
	@JsonProperty("create_time")
	private Timestamp createTime;
	
	/**
	 * 升级后卡等级
	 */
	@JsonProperty("new_grade")
	private String newGrade;
	
	/**
	 * 升级前卡等级
	 */
	@JsonProperty("old_grade")
	private String oldGrade;
	
	/**
	 * 升级前卡id
	 */
	@JsonProperty("old_card_id")
	private Integer oldCardId;
	
	/**
	 * 升级后卡id
	 */
	@JsonProperty("new_card_id")
	private Integer newCardId;
	

}

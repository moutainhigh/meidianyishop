package com.meidianyi.shop.service.pojo.saas.shop;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @author 新国
 *
 */
@Data
public class ShopListQueryParam {
	public String accountKey;
	public String keywords;
	public Integer isUse;
	public String shopType;
	public Byte shopFlag;
	public Byte isEnabled;
	public Byte hidBottom;
	public Integer currentPage;
	public Integer pageRows;
	/**
	 * 区分体验版还是付费版
	 * 1为体验版，2为付费版
	 */
	public String shopTypes;
	
	public Timestamp expireStartTime;
	public Timestamp expireEndTime;
	/** 营销日历对应的id*/
	private Integer calendarActId;
}

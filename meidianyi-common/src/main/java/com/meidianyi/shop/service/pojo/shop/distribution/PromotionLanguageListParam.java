package com.meidianyi.shop.service.pojo.shop.distribution;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 添加分销推广语入参
 * @author 常乐
 * 2019年8月13日
 */
@Data
public class PromotionLanguageListParam {
	private String promotionLanguage;
	private Timestamp startCreateTime;
	private Timestamp endCreateTime;
	private Timestamp startUpdateTime;
	private Timestamp endUpdateTime;
	
	private Integer currentPage;
	private Integer pageRows;
	
}

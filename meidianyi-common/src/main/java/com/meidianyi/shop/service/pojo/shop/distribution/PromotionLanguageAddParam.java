package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * 添加分销推广语入参
 * @author 常乐
 * 2019年8月13日
 */
@Data
public class PromotionLanguageAddParam {
	private Integer id;
	private String title;
	private String promotionLanguage;
	
	private Integer currentPage;
	private Integer pageRows;
}

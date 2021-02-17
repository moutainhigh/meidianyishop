package com.meidianyi.shop.service.pojo.shop.distribution;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 分销推广语列表出参
 * @author 常乐
 * 2019年8月13日
 */
@Data
public class PromotionLanguageListVo {
	private Integer id;
	private String title;
	private String promotionLanguage;
	private Byte isBlock;
	private Byte delFlag;
	private Timestamp createTime;
	private Timestamp updateTime;
    /**是否默认推广语*/
    private Byte isDefault = 0;

}

package com.meidianyi.shop.service.pojo.shop.member.card;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月25日
* @Description: 会员卡批次 列表
*/
@Data
public class CardBatchVo {
	
	/** 批次id */
	@JsonProperty("batchId")
	private Integer id;
	/** 批次名称 */
	private String name;
	/**
	 * 是否是卡号+密码批次
	 */
	private Boolean pwdBatch;
	/**
	 * 	领取码获得方式 1：自动生成 2：导入
	 */
	private Byte action;
}

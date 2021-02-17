package com.meidianyi.shop.service.pojo.shop.member.ucard;

import javax.validation.constraints.NotBlank;

import lombok.Data;
/** 
 * @author 黄壮壮
 * 	设置默认会员卡
 */
@Data
public class DefaultCardParam {
	/**
	 * 	会员卡号
	 */
	@NotBlank
	private String cardNo;
	private Integer userId;
	private Integer cardId;
}

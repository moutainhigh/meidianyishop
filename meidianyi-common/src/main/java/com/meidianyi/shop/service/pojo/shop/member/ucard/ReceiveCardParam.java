package com.meidianyi.shop.service.pojo.shop.member.ucard;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author huangzhuangzhuang
 */
@Data
public class ReceiveCardParam {
	@NotNull
	private Integer cardId;
	private String cardNo;       
	private String cardPwd;    
	private String code;    
	private Integer userId;
}

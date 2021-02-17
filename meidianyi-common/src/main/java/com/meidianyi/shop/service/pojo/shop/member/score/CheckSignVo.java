package com.meidianyi.shop.service.pojo.shop.member.score;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 检查签到送积分 返回信息
 * @author zhaojianqiang
 *
 * 2019年10月12日 上午10:09:48
 */
@Data
public class CheckSignVo {
	@JsonProperty(value = "is_open_sign")
	private Integer isOpenSign;
	
	@JsonProperty(value = "sign_data")
	private SignData signData;
	
	@JsonProperty(value = "sign_rule")
	private String[] signRule;
}

package com.meidianyi.shop.service.pojo.shop.member.score;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 检查签到送积分 返回信息
 * @author zhaojianqiang
 *
 * 2019年10月12日 上午10:04:30
 */
@Data
public class SignData {
	
	@JsonProperty(value = "is_sign_in")
	private Integer isSignIn;
	
	private Integer day;

	@JsonProperty(value = "receive_score")
	private String receiveScore;
	
	@JsonProperty(value = "tomoroow_receive")
	private String tomoroowReceive;
	
	@JsonProperty(value = "max_sign_day")
	private Integer maxSignDay;
	
	@JsonProperty(value = "score_value")
	private Integer scoreValue;
	
	/**
	 * 签到类型 0：连续签到；1：循环签到
	 */
	@JsonProperty(value = "sign_in_rules")
	private Byte signInRules;
}

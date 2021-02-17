package com.meidianyi.shop.service.pojo.shop.member.score;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用于生成Json存储
 * 
 * @author 黄壮壮 2019-07-16 11:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserScoreSetValue {
	private Byte enable;
	private String[] score;
	@JsonProperty(value = "sign_in_rules")
	private Byte signInRules;
}

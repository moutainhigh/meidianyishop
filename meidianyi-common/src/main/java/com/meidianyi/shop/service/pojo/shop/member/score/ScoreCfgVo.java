package com.meidianyi.shop.service.pojo.shop.member.score;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 黄壮壮 2019-07-16 19:11
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreCfgVo {
	
	@JsonAlias("score_limit")
	public String scoreLimit;
	
	@JsonAlias("score_protect")
	public String scoreProtect;
	
	@JsonAlias("score_day")
	public String scoreDay;
	
	@JsonAlias("score_month")
	public String scoreMonth;
	
	@JsonAlias("score_year")
	public String scoreYear;
	
	@JsonAlias("score_type")
	public String scoreType;
	
	@JsonAlias("score_login")
	public String scoreLogin;
	
	@JsonAlias("store_score")
	public String storeScore;
	
	@JsonAlias("shopping_score")
	public String shoppingScore;
	
	@JsonAlias("login_score")
	public String loginScore;
	
	@JsonAlias("score_limit_number")
	public String scoreLimitNumber;
	
	@JsonAlias("score_period")
	public String scorePeriod;
	
	@JsonAlias("score_pay_limit")
	public String scorePayLimit;
	
	@JsonAlias("score_pay_num")
	public String scorePayNum;
	
	@JsonAlias("score_discount_ratio")
	public String scoreDiscountRatio;
	
	@JsonAlias("score_page_id")
	public String scorePageId;
	
	/**   模板页面名称 */
	public String pageName;
	
	public ArrayList<String> buy = new ArrayList<>();
	public ArrayList<String> buyScore = new ArrayList<>();
	public ArrayList<String> buyEach = new ArrayList<>();
	public ArrayList<String> buyEachScore = new ArrayList<>();
	
	/**   签到积分开关 */
	public String signInScore;
	/**   签到积分数据 如第一天签到送多少积分 */
	public String[] signScore;
	/**
	 * 签到类型 0：连续签到；1：循环签到
	 */
	private Byte signInRules;
	/**
	 * 积分兑换比
	 */
	private Integer scoreProportion;
	/**
	 * 订单折后金额是否包含运费
	 */
	private Byte discountHasShipping;

}

package com.meidianyi.shop.service.pojo.shop.member.score;



import java.sql.Timestamp;

import lombok.Data;

/**
 * 用户签到积分查询出参数
 * @author 黄壮壮
 *
 */
@Data
public class ScoreSignVo {
	private Integer userId;
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * 用户全部标签
	 */
	private String userTag;
	/**
	 * 用于显示的用户标签
	 */
	private String userShowTag;
	
	/**
	 * 当天获得签到积分数
	 */
	private Integer usableScore;
	
	/**
	 * 连续签到天数
	 */
	private Integer continueDays;
	
	/**
	 * 本次连续签到获得的总积分
	 */
	private Integer totalScore;
	
	/**
	 * 签到时间
	 */
	private Timestamp createTime;
	
}

package com.meidianyi.shop.service.pojo.shop.member.score;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 用户签到积分查询列表参数
 * @author 黄壮壮
 *
 */
@Data
public class ScoreSignParam {
	/** 
	 * 当前页
	 *  
	 */ 
	private Integer currentPage;
	/** 
	 * 每页显示条目数量
	 */
	private Integer pageRows;
	
	/**
	 * 用户或手机号查询参数
	 */
	private String search;
	
	/**
	 * 签到开始时间
	 */
	private Timestamp startTime;
	
	/**
	 * 签到结束时间
	 */
	private Timestamp endTime;
	
	/**
	 * 用户标签ID列表
	 */
	private List<Integer> tagIds;
}

package com.meidianyi.shop.service.pojo.shop.assess;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 测评活动列表出参
 * @author 常乐
 * 2019年8月16日
 */
@Data
public class AssessActivityListVo {
	private Integer id;
	/**
	 * 活动名称
	 */
	private String actName;
	/**
	 * 测评题目
	 */
	private Integer topicNum;
	/**
	 * 测评结果
	 */
	private Integer resultNum;
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	/**
	 * 反馈数
	 */
	private Integer recordNum;
	/**
	 * 活动开始时间
	 */
	private Timestamp startTime;
	/**
	 * 活动结束时间
	 */
	private Timestamp endTime;
	/**
	 * 活动状态
	 */
	private byte isBolck;
	/**
	 * 发布状态
	 */
	private byte pubFlag;
}

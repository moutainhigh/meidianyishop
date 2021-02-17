package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author zhaojianqiang
 * @time   上午11:14:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupIntegrationVo {
	/** 瓜分积分活动ID*/
	private Integer id;
	 /** 活动名称 */
	private String name;
	/** 活动宣传语 */
	private String advertise;
	
	/** 开始时间*/
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	 /** 总抽奖积分 */
	private Integer inteTotal ;
	
	 /** 每一个团的总积分 */
	private Integer inteGroup ;
	
	/** 成团人数*/
	private Short limitAmount ;
	
	 /** 参团限制 */
	private Short joinLimit ;
	
	/** 瓜分方式 0：按邀请好友数量瓜分，1：好友均分，2：随机瓜分 */
	private Byte divideType;
	
	/** 用户开团24小时后,拼团未满员是否可以瓜分积分 */
	private Byte isDayDivide ; 
	/** 瓜分积分的说明*/
	private ActivityCopywriting activityCopywriting;
}


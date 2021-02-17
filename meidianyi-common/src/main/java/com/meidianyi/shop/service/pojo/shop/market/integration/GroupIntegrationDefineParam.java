package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月5日
 * 增加或修改瓜分积分活动
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupIntegrationDefineParam {
	/** 瓜分积分活动ID*/
	private Integer id;
	 /** 活动名称 */
	@NotBlank
	private String name;
	/** 活动宣传语 */
	private String advertise;
	
	/** 开始时间*/
	@NotNull
	private Timestamp startTime;
	/** 结束时间 */
	@NotNull
	private Timestamp endTime;
	 /** 总抽奖积分 */
	@PositiveOrZero
	private Integer inteTotal ;
	
	 /** 每一个团的总积分 */
	@Positive
	private Integer inteGroup ;
	
	/** 成团人数*/
	@Min(2)
	@Max(20)
	private Short limitAmount ;
	
	 /** 参团限制 */
	@PositiveOrZero
	private Short joinLimit=1 ;
	
	/** 瓜分方式 0：按邀请好友数量瓜分，1：好友均分，2：随机瓜分 */
	private Byte divideType = GroupIntegrationDefineEnums.DivideTypeEnum.NUM_INVITED_FRIENDS.vlaue();
	
	/** 用户开团24小时后,拼团未满员是否可以瓜分积分 */
	private Byte isDayDivide ; 
	
	private ActivityCopywriting activityCopywriting;

}


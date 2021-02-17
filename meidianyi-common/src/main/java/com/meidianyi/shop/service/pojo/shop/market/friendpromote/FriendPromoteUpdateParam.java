package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 修改好友助力活动
 * @author liangchen
 * @date 2019年8月13日
 */
@Data
public class FriendPromoteUpdateParam {
	
	/** 活动id */
	private Integer id;
	/** 活动名称 */
	private String actName;
	/** 活动开始时间 */
	private Timestamp startTime;
	/** 活动结束时间 */
	private Timestamp endTime;
	/** 奖励类型 */
	private Byte rewardType;
	/** 奖励内容 */
	private String rewardContent;
	/** 奖励有效期 */
	private Integer rewardDuration;
	/** 奖励有效期单位 */
	private Byte rewardDurationUnit;
	/** 单次助力值类型：0平均，1随机 */
	private Byte promoteType;
	/** 所需助力值 */
	private BigDecimal promoteAmount;
	/** 所需助力次数 */
	private Integer promoteTimes;
	/** 发起次数限制时长，0不限制 */
	private Integer launchLimitDuration;
	/** 发起次数限制时长单位：0天，1周，2月，3年 */
	private Byte launchLimitUnit;
	/** 发起限制次数，0不限制  */
	private Byte launchLimitTimes;
	/** 好友分享可获得助力次数 */
	private Byte shareCreateTimes;
	/** 好友助力条件：0可不授权个人信息，1必须授权 */
	private Byte promoteCondition;
	/**	优惠叠加策略 0：不可以 1：可以 */
	private Byte useDiscount;
	/**	积分抵扣策略 0：不可以 1：可以 */
	private Byte useScore;
	/** 助力失败赠送类型：0不赠送，1优惠券，2积分 */
	private Byte failedSendType;
	/** 助力失败赠送内容 */
	private Integer failedSendContent;
	/** 助力活动分享样式类型：0默认样式，1自定义样式 */
	private Byte activityShareType;
	/** 自定义分享样式文案 */
	private String customShareWord;
	/** 自定义分享图片类型：0首页截图，1自定义图片 */
	private Byte shareImgType;
	/** 自定义分享样式图片路径 */
	private String customImgPath;
    /** 奖励内容 */
    private FpRewardContent fpRewardContent;
    /** 单个用户每天最多可帮忙助力次数 */
    private Integer promoteTimesPerDay;
    /** 活动说明 */
    private PromoteActCopywriting actCopywriting;
}

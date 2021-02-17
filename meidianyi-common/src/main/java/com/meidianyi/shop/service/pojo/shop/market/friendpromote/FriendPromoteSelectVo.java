package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 好友助力活动公众号需要用
 * 
 * @author zhaojianqiang
 * @time 下午2:59:17
 */
@Data
public class FriendPromoteSelectVo {
	private Integer id;
	private Integer userId;
	private Integer promoteId;
	private Byte promoteStatus;
	private String orderSn;
	private Timestamp launchTime;
	private Timestamp successTime;
	private Byte delFlag;
	private Timestamp delTime;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String actCode;
	private String actName;
	private String rewardContent;
	private Byte rewardType;
	private Byte promoteType;
    private Timestamp startTime;
    private Timestamp endTime;
    /** 奖励有效期 */
    private Integer rewardDuration;
    /** 奖励有效期单位 */
    private Byte rewardDurationUnit;
    /** 所需助力总值 */
    private BigDecimal promoteAmount;
    /** 所需助力次数 */
    private Integer promoteTimes;
    /** 发起限制次数，0不限制 */
    private Byte launchLimitTimes;
    /** 发起次数限制时长，0不限制 */
    private Integer launchLimitDuration;
    /** 发起次数限制时长单位：0小时 1天，2周，3月，4年 */
    private Byte launchLimitUnit;
    /** 0可不授权个人信息，1必须授权 */
    private Byte promoteCondition;
    /** 分享可得助力次数*/
    private Byte shareCreateTimes;
    private Byte useDiscount;
    private Byte useScore;
    private Byte activityShareType;
    /** 自定义分享样式文案 */
    private String customShareWord;
    /** 自定义分享图片类型：0首页截图，1自定义图片 */
    private Byte shareImgType;
    /** 自定义分享样式图片路径 */
    private String customImgPath;
    private Integer promoteTimesPerDay;
	private Byte failedSendType;
	private Integer failedSendContent;
    private String activityCopywriting;
    private PromoteActCopywriting actCopywriting;
}

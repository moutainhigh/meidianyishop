package com.meidianyi.shop.service.pojo.shop.market.friendpromote;
import java.sql.Timestamp;

import lombok.Data;
/***
 * 好友助力活动列表
 * @author liangchen
 * @date 2019年8月7日
 */
@Data
public class FriendPromoteListVo {
	/** 活动Id */
	private Integer id;
    /** 活动码 */
    private String actCode;
	/*** 活动名称 */
	private String actName;
	/** 活动开始时间 */
	private Timestamp startTime;
	/** 活动结束时间 */
	private Timestamp endTime;
	/** 奖励类型：0赠送商品，1折扣商品，2赠送优惠券 */
	private Integer rewardType;
	/** 奖励内容 */
	private String rewardContent;
	/** 已领取奖励数量 */
	private Integer recNum;
	/** 是否停用 0停用 1未停用 */
	private Integer isBlock;
	/** 活动状态 1进行中，2未开始，3已过期 4停用 */
	private Integer actState;
    /** 奖励内容 */
    private FpRewardContent fpRewardContent;
}

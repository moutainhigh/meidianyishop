package com.meidianyi.shop.service.pojo.shop.market.sharereward;

import lombok.Data;

/**
 * @author 二公子
 * @date 9/4/2019
 */
@Data
public class ShareConstant {


    /**
     * 分享有礼活动，分享规则json串常量，json串格式如下
     * {@code {  "invite_num": 1,  "reward_type": 1,  "score": 20,  "score_num": 100,  "coupon": 0,  "coupon_num": 0,  "lottery": 0,  "lottery_num": 0}}
     *
     * 邀请数量
     */
    public static final String INVITE_NUM = "invite_num";
    /**
     * 奖励类型
     */
    public static final String REWARD_TYPE = "reward_type";
    /**
     * 奖励类型：积分
     */
    public static final String SCORE = "score";
    /**
     * 奖励类型：积分数量
     */
    public static final String SCORE_NUM = "score_num";
    /**
     * 奖励类型：优惠券
     */
    public static final String COUPON = "coupon";
    /**
     * 奖励类型：优惠券数量
     */
    public static final String COUPON_NUM = "coupon_num";
    /**
     * 奖励类型：幸运大抽奖
     */
    public static final String LOTTERY = "lottery";
    /**
     * 奖励类型：幸运大抽奖数量
     */
    public static final String LOTTERY_NUM = "lottery_num";

    /**
     * 分享有礼活动-每日用户可分享次数上限参数常量
     */
    public static final String DAILY_SHARE_AWARD = "daily_share_award";
}

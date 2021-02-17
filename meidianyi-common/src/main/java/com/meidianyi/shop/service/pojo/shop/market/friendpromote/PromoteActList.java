package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 助力活动列表
 * @author liangchen
 * @date 2020.02.28
 */
@Data
public class PromoteActList {
    private Integer id;
    private String actCode;
    private String actName;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte rewardType;
    private String rewardContent;
    private FpRewardContent fpRewardContent;
    private GoodsInfo goodsInfo;
    private CouponInfo couponInfo;
    private Byte actStatus;
}

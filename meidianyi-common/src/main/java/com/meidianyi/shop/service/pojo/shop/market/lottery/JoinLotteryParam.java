package com.meidianyi.shop.service.pojo.shop.market.lottery;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/8/6 17:10
 */
@Data
public class  JoinLotteryParam {

    private Integer userId;
    @NotNull
    private Integer lotteryId;

    /**
     * 抽奖来源 1开瓶有礼 2支付有礼3分享
     */
    private Byte lotterySource;
    /**
     * 活动记录来源
     */
    private Integer activityRecordId;
}

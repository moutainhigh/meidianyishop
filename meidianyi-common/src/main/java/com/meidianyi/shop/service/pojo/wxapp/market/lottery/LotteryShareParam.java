package com.meidianyi.shop.service.pojo.wxapp.market.lottery;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 抽奖分享参数
 * @author 孔德成
 * @date 2020/2/5
 */
@Getter
@Setter
public class LotteryShareParam {
    /**
     * 活动id
     */
    @NotNull
    private Integer lotteryId;
}

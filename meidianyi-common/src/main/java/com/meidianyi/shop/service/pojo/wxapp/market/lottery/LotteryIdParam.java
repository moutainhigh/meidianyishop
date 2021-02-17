package com.meidianyi.shop.service.pojo.wxapp.market.lottery;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 抽奖活动id
 * @author 孔德成
 * @date 2020/1/16 15:50
 */
@Getter
@Setter
public class LotteryIdParam {
    /**
     * 抽奖活动id
     */
    @NotNull
    private Integer id;
}

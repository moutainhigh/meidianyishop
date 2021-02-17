package com.meidianyi.shop.service.pojo.wxapp.market.lottery;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * 用户的中奖列表
 * @author 孔德成
 * @date 2020/2/10
 */
@Getter
@Setter
public class LotteryListUserParam  extends BasePageParam {
    /**
     * 活动id
     */
    @NotNull
    private Integer lotteryId;


    private Integer userId;
}

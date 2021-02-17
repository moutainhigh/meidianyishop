package com.meidianyi.shop.service.pojo.wxapp.market.lottery;

import com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 抽奖信息
 *  ·活动信息
 *  ·用户抽奖次数
 * @author 孔德成
 * @date 2020/2/3
 */
@Getter
@Setter
public class LotteryInfoVo {

    /**
     * 活动信息
     */
   private LotteryVo lotteryInfo;
    /**
     *用户抽奖次数
     */
   private LotteryUserTimeInfo lotteryUserTimeInfo;

}

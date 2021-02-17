package com.meidianyi.shop.service.pojo.wxapp.market.lottery;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.lottery.record.LotteryRecordPageListVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 抽奖次数信息
 * @author 孔德成
 * @date 2020/2/3
 */
@Getter
@Setter
public class LotteryUserTimeInfo {

    /**
     * 已使用次数
     */
    private Integer usedTime=0;
    /**
     * 免费次数
     */
    private Integer freeTime=0;
    /**
     * 已使用免费次数
     */
    private Integer usedFreeTime=0;
    /**
     * 分享次数
     */
    private Integer shareTime=0;
    /**
     * 已使用分享次数
     */
    private Integer usedShareTime=0;
    /**
     * 最大分享次数
     */
    private Integer shareMaximum=0;
    /**
     * 积分次数
     */
    private Integer scoreTime=0;
    /**
     * 积分
     */
    private Integer score=0;
    /**
     * 已使用积分
     */
    private Integer usedScoreTime=0;
    /**
     * 最大兑换次数
     */
    private Integer scoreMaximum=0;
    /**
     * 中奖纪录
     */
    private PageResult<LotteryRecordPageListVo> lotteryRecord;
}

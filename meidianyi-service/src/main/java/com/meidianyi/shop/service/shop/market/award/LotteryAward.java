package com.meidianyi.shop.service.shop.market.award;

import org.springframework.stereotype.Service;

/**
 * @author liufei
 * @date 1/13/20
 */
@Service
public class LotteryAward implements Award {
    @Override
    public void sendAward(AwardParam param) {
        // TODO 抽奖直接返回抽奖活动id即可
    }
}

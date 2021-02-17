package com.meidianyi.shop.service.shop.task.market;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.shop.market.prize.PrizeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 我的奖品
 * @author 孔德成
 * @date 2020/1/7 11:38
 */
@Component
public class MyPrizeTaskService  extends ShopBaseService {

    @Autowired
    private PrizeRecordService prizeRecordService;

    /**
     * 定时关闭奖品商品
     */
    public void closePrizeGoods(){
        prizeRecordService.closePrizeGoods();
    }

}

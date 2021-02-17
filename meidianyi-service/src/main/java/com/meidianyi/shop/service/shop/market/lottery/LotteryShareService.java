package com.meidianyi.shop.service.shop.market.lottery;

import com.meidianyi.shop.db.shop.tables.records.LotteryShareRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.shop.Tables.LOTTERY_RECORD;
import static com.meidianyi.shop.db.shop.Tables.LOTTERY_SHARE;

/**
 *  用户抽奖分享次数
 * @author 孔德成
 * @date 2019/8/6 15:21
 */
@Service
public class LotteryShareService extends ShopBaseService {


    /**
     * 获取用户抽奖活动记录
     * @param userId 用户id
     * @param lotteryId 活动id
     * @return
     */
    public LotteryShareRecord getLotteryShareByUser(Integer userId, Integer lotteryId){
        return db().fetchOne(LOTTERY_SHARE,LOTTERY_SHARE.USER_ID.eq(userId).and(LOTTERY_SHARE.LOTTERY_ID.eq(lotteryId)));
    }

    /**
     * 添加分享记录
     * @param userId
     * @param lotteryId
     */
    public void addShareRecord(Integer userId, Integer lotteryId) {
        LotteryShareRecord lotteryShareByUser = getLotteryShareByUser(userId, lotteryId);
        if (lotteryShareByUser!=null){
            lotteryShareByUser.setShareTimes(lotteryShareByUser.getShareTimes()+1);
            lotteryShareByUser.update();
        }else {
            db().insertInto(LOTTERY_SHARE)
                    .set(LOTTERY_SHARE.USER_ID,userId)
                    .set(LOTTERY_SHARE.LOTTERY_ID,lotteryId)
                    .set(LOTTERY_SHARE.SHARE_TIMES,LOTTERY_SHARE.SHARE_TIMES.add(1))
                    .execute();
        }

    }
}

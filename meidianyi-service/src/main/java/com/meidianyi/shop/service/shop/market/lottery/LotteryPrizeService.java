package com.meidianyi.shop.service.shop.market.lottery;

import com.meidianyi.shop.db.shop.tables.records.LotteryPrizeRecord;
import com.meidianyi.shop.db.shop.tables.records.LotteryRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.lottery.JoinLottery;
import com.meidianyi.shop.service.pojo.shop.market.lottery.prize.LotteryPrizeVo;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.meidianyi.shop.db.shop.Tables.LOTTERY_PRIZE;
import static com.meidianyi.shop.service.pojo.shop.market.lottery.LotteryConstant.*;

/**
 * @author 孔德成
 * @date 2019/8/7 15:22
 */
@Service
public class LotteryPrizeService  extends ShopBaseService {

    private static final Random RANDOM = new Random();

    /**
     * 根据lotteryId获取最大分母
     * @param lotteryId
     * @return
     */
    public Integer getLotteryMaxDenominatorById(Integer lotteryId){
        return db().select(LOTTERY_PRIZE.CHANCE_DENOMINATOR).from(LOTTERY_PRIZE).where(LOTTERY_PRIZE.LOTTERY_ID.eq(lotteryId))
                .orderBy(LOTTERY_PRIZE.CHANCE_DENOMINATOR.desc()).fetchAnyInto(Integer.class);
    }


    public Result<LotteryPrizeRecord> getPrizeByLotteryId(Integer lotteryId) {
        return  getPrizeByLotteryId(lotteryId,(byte)0);
    }
    public Result<LotteryPrizeRecord> getPrizeByLotteryId(Integer lotteryId,Byte delFlag) {
        return  db().selectFrom(LOTTERY_PRIZE)
                .where(LOTTERY_PRIZE.LOTTERY_ID.eq(lotteryId))
                .and(LOTTERY_PRIZE.DEL_FLAG.eq(delFlag))
                .orderBy(LOTTERY_PRIZE.LOTTERY_GRADE).fetch();
    }


    /**
     * 抽奖核心逻辑
     *
     * @param joinValid  抽奖校验
     */
    void joinLotteryAction(JoinLottery joinValid) {
        LotteryRecord lottery = joinValid.getLottery();
        //取最大的分母
        Integer maxChance = getLotteryMaxDenominatorById(lottery.getId());
        int randNumber = RANDOM.nextInt(maxChance);
        Result<LotteryPrizeRecord> prizeRecords = getPrizeByLotteryId(lottery.getId());
        for (LotteryPrizeRecord record : prizeRecords) {
            int chanceNumerator = record.getChanceNumerator()*maxChance/record.getChanceDenominator();
            if (randNumber <chanceNumerator) {
                if (record.getAwardTimes() == null) {
                    record.setAwardTimes(0);
                }
                //中奖了
                if (record.getAwardTimes() >= record.getLotteryNumber()) {
                    //奖品发完了
                    joinValid.setResultsType(LOTTERY_TYPE_SEND_OUT);
                    return;
                }else {
                    logger().info("抽奖中奖了,扣库存");
                    updateLotteryStock(record.getId());
                }
                joinValid.setResultsType(record.getLotteryType());
                joinValid.setLotteryPrize(record);
                joinValid.setLotteryGrade(record.getLotteryGrade());
                return;
            }
            randNumber -= chanceNumerator;
        }
        //没中奖
        joinValid.setResultsType(LOTTERY_TYPE_NULL);
    }

    /**
     * 更新奖品库存
     * @return
     */
    public int updateLotteryStock(Integer id){
        return  db().update(LOTTERY_PRIZE).set(LOTTERY_PRIZE.AWARD_TIMES, LOTTERY_PRIZE.AWARD_TIMES.add(1))
                .where(LOTTERY_PRIZE.ID.eq(id))
                .and(LOTTERY_PRIZE.AWARD_TIMES.lt(LOTTERY_PRIZE.LOTTERY_NUMBER)).execute();
    }
}

package com.meidianyi.shop.service.shop.market.award;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.BYTE_THREE;
import static com.meidianyi.shop.service.shop.store.store.StoreWxService.BYTE_TWO;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;

/**
 * The type Award factory.
 *
 * @author liufei
 * @date 1 /14/20
 */
@Repository
public class AwardFactory {
    @Autowired
    ScoreAward scoreAward;
    @Autowired
    CouponAward couponAward;
    @Autowired
    LotteryAward lotteryAward;

    private static ConcurrentHashMap<Byte, Award> awardFactory;

    @PostConstruct
    private void init() {
        awardFactory = new ConcurrentHashMap<>(8);
        awardFactory.put(BYTE_ONE, scoreAward);
        awardFactory.put(BYTE_TWO, couponAward);
        awardFactory.put(BYTE_THREE, lotteryAward);
    }

    /**
     * Gets award.
     *
     * @param awardType the award type
     * @return the award
     */
    public static Award getAward(Byte awardType) {
        return awardFactory.get(awardType);
    }
}

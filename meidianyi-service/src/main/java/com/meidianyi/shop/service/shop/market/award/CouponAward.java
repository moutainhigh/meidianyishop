package com.meidianyi.shop.service.shop.market.award;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueBo;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.market.sharereward.ShareRule;
import com.meidianyi.shop.service.shop.coupon.CouponGiveService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant.COUPON_GIVE_SOURCE_PAY_AWARD;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * @author liufei
 * @date 1/13/20
 */
@Slf4j
@Service
public class CouponAward implements Award {
    @Autowired
    private CouponGiveService couponGiveService;
    @Autowired
    private CouponService couponService;

    @Override
    public void sendAward(AwardParam param) {
        ShareRule rule = param.getRule();
        // 发放的优惠券详情
/*        List<CouponView> couponViews = couponService.getCouponViewByIds(new ArrayList<Integer>() {{
            add(rule.getCoupon());
        }});*/
        CouponGiveQueueParam couponGive = new CouponGiveQueueParam();
        couponGive.setUserIds(Collections.singletonList(param.getUserId()));
        couponGive.setCouponArray(new String[]{String.valueOf(rule.getCoupon())});
        couponGive.setActId(param.getActivityId());
        couponGive.setAccessMode(BYTE_ZERO);
        couponGive.setGetSource(COUPON_GIVE_SOURCE_PAY_AWARD);
        // 发送优惠卷
        CouponGiveQueueBo sendData = couponGiveService.handlerCouponGive(couponGive);
        // 一张都没发成功
        if (sendData.getSuccessSize().compareTo(INTEGER_ZERO) <= INTEGER_ZERO) {
            log.debug("优惠券发送全部失败");
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }
    }
}

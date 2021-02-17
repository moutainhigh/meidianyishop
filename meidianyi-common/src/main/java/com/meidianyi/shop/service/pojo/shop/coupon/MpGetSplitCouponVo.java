package com.meidianyi.shop.service.pojo.shop.coupon;

import lombok.Getter;
import lombok.Setter;

/**
 * 领取分裂
 * @author 孔德成
 * @date 2020/4/17
 */
@Getter
@Setter
public class MpGetSplitCouponVo {

    /**
     * 状态 1领取成功  2优惠券已领完 3优惠券发送失败
     */
    private Byte status =1;

}

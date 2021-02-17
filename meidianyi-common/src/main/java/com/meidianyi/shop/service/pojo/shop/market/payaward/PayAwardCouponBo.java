package com.meidianyi.shop.service.pojo.shop.market.payaward;

import lombok.Getter;
import lombok.Setter;

/**
 * 支付有礼赠送的优惠卷信息
 * @author 孔德成
 * @date 2019/10/31 17:30
 */
@Getter
@Setter
public class PayAwardCouponBo {
    /**
     * 优惠卷id
     */
    private Integer id;
    /**
     *  voucher：减价；discount:打折|
     */
    private String actCode;
    /**
     * 奖品份数
     */
    private Integer denomination;
    /**
     * 门槛
     */
    private String couponCenterLimit;
    /**
     * 优惠卷数量
     */
    private String couponCenterNumber;
}

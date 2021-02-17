package com.meidianyi.shop.service.pojo.wxapp.coupon.pack;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-04-28 09:15
 **/
@Getter
@Setter
public class CouponPackCartVo {

    /**
     * 领券最高减多少元
     */
    private BigDecimal maxReduce;

    /**
     *礼包列表
     */
    private List<PackInfo> packList = new ArrayList<>();

    @Getter
    @Setter
    public static class PackInfo {
        /**
         * 活动ID
         */
        private Integer actId;
        /**
         * 礼包名称
         */
        private String packName;
        /**
         * 包含优惠券张数
         */
        private Integer totalAmount;
        /**
         * 包含的优惠券总面额
         */
        private BigDecimal totalValue;
        /**
         * 礼包是否只包含折扣券（此时totalValue为空）
         */
        private Boolean onlyDiscount;
        /**
         * 用户是否已领取（购买）过该礼包
         */
        private Boolean isReceive;
    }
}

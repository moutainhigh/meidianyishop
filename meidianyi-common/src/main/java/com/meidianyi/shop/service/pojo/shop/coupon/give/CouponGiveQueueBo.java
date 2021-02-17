package com.meidianyi.shop.service.pojo.shop.coupon.give;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.meidianyi.shop.db.shop.tables.records.CustomerAvailCouponsRecord;

import lombok.Getter;
import lombok.Setter;

/**
 * 发送优惠卷信息
 * @author 孔德成
 * @date 2019/12/20 15:10
 */
@Getter
@Setter
public class CouponGiveQueueBo {
    /**
     * 发送成功数量
     */
    private Integer successSize;
    /**
     * 发送成功的优惠卷set
     */
    private Set<Integer> couponSet =new HashSet<>();
    /**
     * 发送优惠券的编号
     */
    private List<String> couponSn =new ArrayList<>();
    /**
     * 发送优惠券记录
     */
    private List<CustomerAvailCouponsRecord> sendCoupons;
}

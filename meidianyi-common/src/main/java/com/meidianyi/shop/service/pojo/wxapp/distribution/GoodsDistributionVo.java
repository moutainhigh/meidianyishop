package com.meidianyi.shop.service.pojo.wxapp.distribution;

import com.meidianyi.shop.db.shop.tables.records.MrkingVoucherRecord;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateRatioVo;
import lombok.Data;

import java.util.List;

/**
 * @Author 常乐
 * @Date 2020-03-04
 */
@Data
public class GoodsDistributionVo {
    /**
     * 是否分销改价 0：否；1是
     */
    private Byte canRebate;
    /**
     * 是否是分销员
     */
    private Byte isDistributor;
    /**
     * 返利比例
     */
    private RebateRatioVo rebateRatio;
    /**
     * 赠送优惠券信息
     */
    private List<CouponListVo> sendCoupon;
    /**
     * 分销推广语
     */
    private String promotionLanguage;
}

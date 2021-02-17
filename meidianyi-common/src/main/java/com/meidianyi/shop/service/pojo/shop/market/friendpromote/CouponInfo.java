package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 好友助力优惠券详情
 * @author liangchen
 * @date 2020.02.26
 */
@Data
public class CouponInfo {
    /** 优惠券id */
    private Integer couponId;
    /** 优惠券名称 */
    private String actName;
    /** 优惠券类型：打折or减价 */
    private String actCode;
    /** 唯一活动码 */
    private String aliasCode;
    /** 面额 */
    private BigDecimal denomination;
    /** 是否使用受限制 */
    private Byte useConsumeRestrict;
    /** 最低使用价格 */
    private BigDecimal leastConsume;
    /** 展示库存 */
    private Integer marketStore;
}

package com.meidianyi.shop.service.pojo.shop.coupon.hold;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2019/8/13 17:15
 */
@Data
public class CouponHoldListVo {

    private Integer id;
    /**优惠券活动ID*/
    private Integer actId;
    private String username;
    private Integer userId;
    private String mobile;
    private String couponSn;
    private String couponName;
    private Integer delFlag;
    /**
     * 获取方式，0：发放，1：领取
     */
    private Integer accessMode;
    /**
     * 优惠券状态 0 未使用 1 使用
     */
    private Integer isUsed;
    /**
     * 优惠券状态 0 未使用 1 使用 2 过期 3 废除
     */
    private Integer status;
    /**优惠券类型 0：普通优惠券；1：分裂优惠券*/
    private Byte couponType;
    private Integer type;
    /**是否分享*/
    private Integer isShare;
    /**领取用户数*/
    private Integer hasReceive;
    private BigDecimal denomination;
    /**优惠卷金额*/
    private BigDecimal amount;
    /**voucher减价；discount；打折*/
    private String actCode;
    /**是否有使用门槛 0:无门槛；1：满金额使用*/
    private Byte useConsumeRestrict;
    /**最低消费*/
    private BigDecimal leastConsume;
    private Integer scoreNumber;
    private String orderSn;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp createTime;
    private Timestamp usedTime;
}

package com.meidianyi.shop.service.pojo.shop.coupon.hold;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/8/13 16:46
 */
@Data
public class CouponHoldListParam {

    /** 优惠券规则Id */
    private Integer actId;
    /** 发放活动id*/
    private Integer accessId;
    /**
     * 1表单送券2支付送券3活动送券4积分兑换5直接领取6分裂优惠券7crm领券8幸运大抽奖9定向发券
     */
    private Byte getSource;

    /**
     * 用户id
     */
    private Integer userId;
    /** 手机号 */
    private String mobile;
    /** 用户昵称 */
    private String username;
    /**
     * 是否已经使用 1 未使用 2 已使用 3 已过期 4已废除
     */
    private Byte status;
    /** 优惠券类型 0：普通优惠券；1：分裂优惠券*/
    private Byte couponType = 0;


    /**
     * 	分页信息
     */
    private int currentPage;
    private int pageRows;

    private String actName;




}

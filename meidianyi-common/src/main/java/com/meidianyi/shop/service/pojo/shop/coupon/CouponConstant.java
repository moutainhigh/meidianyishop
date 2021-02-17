package com.meidianyi.shop.service.pojo.shop.coupon;

/**
 *  优惠卷常量
 * @author 孔德成
 * @date 2019/11/25 15:34
 */
public class CouponConstant {


    /**  ******优惠卷类型 0普通 1分裂
    /**
     * 普通优惠卷
     */
    public static final byte COUPON_TYPE_NORMAL =0;
    /**
     * 分裂优惠卷
     */
    public static final byte COUPON_TYPE_SPILT =1;

    /**  ******b2c_customer_avail_coupons 的获取方式，0：发放，1：领取，2：优惠券礼包活动 */
    public static final byte ACCESS_MODE_GRANT = 0;
    public static final byte ACCESS_MODE_RECEIVE = 1;
    public static final byte ACCESS_MODE_COUPON_PACK = 2;

    /**  *********发卷来源   1表单送券2支付送券3活动送券4积分兑换5直接领取6分裂优惠券7crm领券8幸运大抽奖9定向发券10表单统计 */
    /**
     * 表单统计
     */
    public static final byte COUPON_GIVE_SOURCE_FORM_STATISTICS=1;
    /**
     * 支付送券
     */
    public static final byte COUPON_GIVE_SOURCE_PAY_AWARD=2;
    /**
     * 分裂优惠券
     */
    public static final byte COUPON_GIVE_SOURCE_SPLIT_COUPON=6;
    /**
     * 幸运大抽奖
     */
    public static final byte COUPON_GIVE_SOURCE_LOTTERY_AWARD=8;

    /**
     * 优惠券状态 0停用 1启用
     */
    public static final Byte ENABLED = 1;
    /**
     * 指定时间段生效
     */
    public static final Byte FIXED_TIME = 0;
    /**
     * 领取后开始生效
     */
    public static final Byte AFTER_RECEIVING = 1;
    /**
     * 限制库存
     */
    public static final Byte LIMIT_SURPLUS = 0;
    /**
     * 不限制库存
     */
    public static final Byte NOT_LIMIT_SURPLUS = 1;
    /**
     * 默认库存为0
     */
    public static final Integer SURPLUS = 0;

    /**
     * 优惠券类型-指定金额减价
     */
    public static final String ACT_CODE_VOUCHER = "voucher";
    /**
     * 优惠券类型-打折
     */
    public static final String ACT_CODE_DISCOUNT = "discount";
    /**
     * 优惠券类型-分裂优惠券的随机金额
     */
    public static final String ACT_CODE_RANDOM = "random";
    /**
     * 进行状态：进行中
     */
    public static final byte COUPON_STATUS_PROCESSING =0;
    /**
     * 进行状态：未开始
     */
    public static final byte COUPON_STATUS_NOT_START =1;
}

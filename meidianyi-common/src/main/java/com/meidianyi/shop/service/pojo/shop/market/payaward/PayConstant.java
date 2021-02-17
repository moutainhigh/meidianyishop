package com.meidianyi.shop.service.pojo.shop.market.payaward;

/**
 * 支付有礼静态成员
 *
 * @author 孔德成
 * @date 2019/10/31 13:56
 */
public class PayConstant {

    // 时间类型
    /**
     * 永久有效
     */
    public static final Byte TIME_TYPE_ALWAYS = 0;
    /**
     * 定时有效
     */
    public static final Byte TIME_TYPE_TIMING = 1;


    //礼物类型 0 无奖品 1普通优惠卷  2分裂优惠卷 3幸运大抽奖 4 余额 5 商品 6积分 7 自定义
    /**
     * 无奖品
     */
    public static final Byte GIFT_TYPE_NOTHING = 0;
    /**
     * 普通优惠卷
     */
    public static final Byte GIFT_TYPE_COUPON = 1;
    /**
     * 分裂优惠卷
     */
    public static final Byte GIFT_TYPE_SPLIT_COUPON = 2;
    /**
     * 幸运大抽奖
     */
    public static final Byte GIFT_TYPE_LOTTERY = 3;

    /**
     * 账户余额
     */
    public static final Byte GIFT_TYPE_ACCOUNT = 4;
    /**
     * 积分
     */
    public static final Byte GIFT_TYPE_SCORE = 5;
    /**
     * 商品
     */
    public static final Byte GIFT_TYPE_GOODS = 6;
    /**
     * 其他
     */
    public static final Byte GIFT_TYPE_OTHER = 7;

}

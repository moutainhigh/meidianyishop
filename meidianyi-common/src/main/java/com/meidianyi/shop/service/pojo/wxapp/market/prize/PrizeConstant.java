package com.meidianyi.shop.service.pojo.wxapp.market.prize;

/**
 * @author 孔德成
 * @date 2020/1/3 13:56
 */
public class PrizeConstant {
    //****************奖品来源0幸运大抽奖，1好友助力，2测评 3支付有礼

    /**
     * 幸运大抽奖
     */
    public final static byte PRIZE_SOURCE_LOTTERY=0;
    /**
     * 好友助力
     */
    public final static byte PRIZE_SOURCE_FRIEND_POWER=1;
    /**
     * 测评
     */
    public final static byte PRIZE_SOURCE_EVALUATION=2;
    /**
     * 支付有礼
     */
    public final static byte PRIZE_SOURCE_PAY_AWARD=3;
    /**
     * 奖品类型 todo
     */
    public final static byte PRIZE_SOURCE=9;


    //**************状态

    /**
     * 待领取
     */
    public final static Byte PRIZE_STATUS_UNCLAIMED =0;
    /**
     * 已领取
     */
    public final static Byte PRIZE_STATUS_RECEIVED=1;
    /**
     * 已过期
     */
    public final static Byte PRIZE_STATUS_EXPIRE=2;
}

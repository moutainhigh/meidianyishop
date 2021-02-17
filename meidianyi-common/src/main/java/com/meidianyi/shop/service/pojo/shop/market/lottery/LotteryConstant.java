package com.meidianyi.shop.service.pojo.shop.market.lottery;



/**
 * @author 孔德成
 * @date 2019/8/5 10:58
 */
public class LotteryConstant {
    /**  *****************每人n次 每人每天n次******
    /**
     * 每人
     */
    public final static Byte CHANCE_TYPE_EVERYONE=0;
    /**
     * 每人每天
     */
    public final static Byte CHANCE_TYPE_EVERYONE_DAY=1;

    /***********抽奖来源*****/
    public final static Byte LOTTERY_TIME_ALL=0;
    /**
     * 免费
     */
    public final static Byte LOTTERY_TIME_FREE=1;
    /**
     * 分享
     */
    public final static Byte LOTTERY_TIME_SHARE=2;
    /**
     * 积分
     */
    public final static Byte LOTTERY_TIME_SCORE=3;


    /**  ***********奖品类型*****************
    /**
     * 未中奖
     */
    public final static byte LOTTERY_TYPE_NULL=-2;
    /**
     * 奖品已经送完
     */
    public final static byte LOTTERY_TYPE_SEND_OUT=-1;
    /**
     * 积分
     */
    public final static byte LOTTERY_TYPE_SCORE=1;
    /**
     * 账户
     */
    public final static byte LOTTERY_TYPE_BALANCE  =2;
    /**
     * 优惠卷
     */
    public final static byte LOTTERY_TYPE_COUPON  =3;
    /**
     * 商品
     */
    public final static byte LOTTERY_TYPE_GOODS  =4;
    /**
     * 自定义
     */
    public final static byte LOTTERY_TYPE_CUSTOM  =5;


    /**  *************奖品领取状态************:0.待领取，1：已领取，2.已过期',  */
    public final static byte LOTTERY_PRIZE_STATUS_UNCLAIMED  =0;
    public final static byte LOTTERY_PRIZE_STATUS_RECEIVED  =1;
    public final static byte LOTTERY_PRIZE_STATUS_OVERDUE  =2;

}

package com.meidianyi.shop.service.pojo.shop.market.givegift;

/**
 * 我要送礼模块 常量定义
 *
 * @author 孔德成
 * @date 2019/8/19 15:18
 */
public class GiveGiftConstant {

    //---------------------------------------------------------------------
    //礼物活动常量
    //---------------------------------------------------------------------


    //--------------------------------------------------------------------
    //收礼记录状态
    //--------------------------------------------------------------------
    /**
     * 未提交地址
     */
    public final static Byte GIFT_RECEIVE_HAVE_GIFT = 1;
    /**
     * 已送礼
     */
    public final static Byte GIFT_RECEIVE_UNSUBMITTED_ADDRESS = 0;
    /**
     *待开奖
     */
    public final static Byte GIFT_RECEIVE_WAITING_LOTTERY = 2;
    /**
     *未中奖
     */
    public final static Byte GIFT_RECEIVE_LOSING_LOTTERY = 3;



}

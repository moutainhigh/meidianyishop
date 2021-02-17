package com.meidianyi.shop.service.pojo.shop.market.presale;

/**
 * 预售静态类
 * @author 孔德成
 * @date 2020/3/30
 */
public class PresaleConstant {
    //*******************活动类型
    /** 定金膨胀 **/
    public static final byte PRESALE = 0;
    /** 全款购买 **/
    public static final byte FULL = 1;

    //********************************分享样式
    /** 默认样式 **/
    public static final byte STYLE_DEFAULT = 1;
    /** 自定义样式 **/
    public static final byte STYLE_CUSTOMIZE = 2;

    //********************************分享图片样式
    /** 分享活动商品图 **/
    public static final byte SHARE_GOODS_IMG = 1;
    /** 分享自定义图片 **/
    public static final byte SHARE_CUSTOMIZE_IMG = 2;

    /** 指定时间发货 **/
    public static final byte DELIVER_SPECIFIC = 1;
    /** 尾款支付完成几天后发货 **/
    public static final byte DELIVER_POSTPONE = 2;
    /** 优惠不可叠加 **/
    public static final byte DISCOUNT_NO_SUPERIMPOSED = 0;
    /** 优惠可叠加 **/
    public static final byte DISCOUNT_SUPERIMPOSED = 1;
    /** 预售数量展示 **/
    public static final byte PRESALE_NUM_SHOW = 1;
    /** 预售数量不展示 **/
    public static final byte PRESALE_NUM_HIDE = 0;
    /** 支持原价购买 **/
    public static final byte ORIGINAL_PRICE_BUY_ENABLE = 1;
    /** 不支持原价购买 **/
    public static final byte ORIGINAL_PRICE_BUY_DISABLE = 0;
    /** 自动退定金 **/
    public static final byte AUTO_RETURN_PRESALE_MONEY = 1;
    /** 不退定金 **/
    public static final byte NOT_RETURN_PRESALE_MONEY = 0;

    /**全款付*/
    public static final Byte PRE_SALE_TYPE_ALL_MONEY = 1;
    /**定金付*/
    public static final Byte PRE_SALE_TYPE_SPLIT = 0;
    /**只有一个阶段*/
    public static final Byte PRE_SALE_ONE_PHASE = 1;
    /**有两个阶段*/
    public static final Byte PRE_SALE_TWO_PHASE = 2;
    /**使用优惠券、会员卡折扣叠加*/
    public static final Byte PRE_SALE_USE_COUPON = 1;
    /**自定退订金*/
    public static final Byte PRE_SALE_RETURN_DEPOSIT= 1;
    /**展示预售数量*/
    public static final Integer PRE_SALE_SHOW_SALE_NUM= 1;
    /**可以原价购买*/
    public static final Byte PRE_SALE_ORIGINAL_BUY= 1;

    /**发货时间类型：0 指定日期*/
    public static final Byte DELIVER_TYPE_TIME = 0;
    /**发货时间类型：1 指定下单后的天数*/
    public static final Byte DELIVER_TYPE_DAYS = 1;
    /**定金期数2*/
    public static final Byte PRESALE_MONEY_INTERVAL = 2;
    private static final byte NOT_DELETED = 0;


}

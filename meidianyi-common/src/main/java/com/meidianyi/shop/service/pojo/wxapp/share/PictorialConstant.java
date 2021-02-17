package com.meidianyi.shop.service.pojo.wxapp.share;

/**
 * 海报和分享信息相关常量
 * @author 李晓冰
 * @date 2019年12月27日
 */
public class PictorialConstant {

    /** 拼团分享 */
    public static final Byte GROUP_BUY_ACTION_SHARE =2;
    /** 砍价分享 */
    public static final Byte BARGAIN_ACTION_SHARE = 4;
    /** 拼团抽奖分享 */
    public static final Byte GROUP_DRAW_ACTION_SHARE = 6;
    /** 预售分享*/
    public static final Byte PRE_SALE_ACTION_SHARE = 8;
    /** 限时降价 */
    public static final Byte REDUCE_PRICE_ACTION_SHARE = 10;
    /** 首单特惠 */
    public static final Byte FIRST_SPECIAL_ACTION_SHARE = 12;
    /**秒杀*/
    public static final Byte SECKILL_ACTION_SHARE = 14;
    /** 表单统计 */
    public static final Byte FORM_STATISTICS_ACTION_SHARE = 16;
    /**分销*/
    public static final Byte REBATE_ACTION_SHARE = 18;


    /********分享或海报异常问题*********/
    /**活动已删除*/
    public static final Byte ACTIVITY_DELETED = 1;
    /**商品已删除*/
    public static final Byte GOODS_DELETED = 2;
    /**读取指定图片异常*/
    public static final Byte GOODS_PIC_ERROR= 3;
    /**读取二维码异常*/
    public static final Byte QRCODE_ERROR= 4;
    /**读取用户头像信息异常*/
    public static final Byte USER_PIC_ERROR = 5;
}

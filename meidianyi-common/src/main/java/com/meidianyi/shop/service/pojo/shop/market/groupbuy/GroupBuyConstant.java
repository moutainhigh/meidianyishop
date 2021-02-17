package com.meidianyi.shop.service.pojo.shop.market.groupbuy;

/**
 * @author 孔德成
 * @date 2019/12/10 16:18
 */
public interface GroupBuyConstant {

    String GROUP_ID_PREFIX="GROUP_ID";

    //*********** 是否是团长  **************
    /**
     * 是
     */
    public static final Byte IS_GROUPER_Y = 1;
    /**
     * 否
     */
    public static final Byte IS_GROUPER_N = 0;

    //************ 是否团长优惠***********************/0：不开启，1：开启
    /**
     * 是
     */
    public static final Byte IS_GROUPER_CHEAP_Y = 1;
    /**
     * 否
     */
    public static final Byte IS_GROUPER_CHEAP_N = 0;

    //********** 开团限制 0不限制**************

    public static final Byte OPEN_LIMIT_N = 0;

    //*********** 参团限制 0不限制 **************

    public static final Byte JOIN_LIMIT_N = 0;


    //*********** 拼团状态 ******************
    /**
     * 未支付
     */
    public static final Byte STATUS_WAIT_PAY = -1;
    /**
     * 拼团中
     */
    public static final Byte STATUS_ONGOING = 0;
    /**
     * 拼团成功
     */
    public static final Byte STATUS_SUCCESS = 1;
    /**
     * 拼团成功 (默认成团)
     */
    public static final Byte STATUS_DEFAULT_SUCCESS =3;
    /**
     * 拼团失败
     */
    public static final Byte STATUS_FAILED = 2;
    //**拼团类型1：普通拼团，2：老带新团
    /**
     * 1：普通拼团，
     */
    public static final Byte ACTIVITY_TYPE_GENERAL = 1;
    /**
     * 2：老带新团
     */
    public static final Byte ACTIVITY_TYPE_OLD_NEW = 2;

}

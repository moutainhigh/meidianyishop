package com.meidianyi.shop.service.pojo.shop.member.card;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

/**
 *
 * @author 黄壮壮
 * @Date: 2019年8月6日
 * @Description: 会员卡用到的常量定义
 */
public class CardConstant {
    /** 所有会员卡类型 */
    public static final Byte MCARD_TP_ALL = -1;
    /** 普通会员卡 */
    public static final Byte MCARD_TP_NORMAL = 0;
    /** 限次会员卡 */
    public static final Byte MCARD_TP_LIMIT = 1;
    /**	等级会员卡 */
    public static final Byte MCARD_TP_GRADE = 2;

    /** is_pay 直接领取 */
    public static final Byte MCARD_ISP_DEFAULT = 0;
    /** is_pay 需要购买 */
    public static final Byte MCARD_ISP_BUY = 1;
    /** is_pay 需要领取码 */
    public static final Byte MCARD_ISP_CODE = 2;
    /** receive_action 领取码 1*/
    public static final Byte MCARD_REA_CODE = 1;
    /** receive_action 卡号+密码 2*/
    public static final Byte MCARD_REA_PWD = 2;

    /**  ******* 0:不支持现金购买，1:支持现金购买
    /**
     * 0:不支持现金购买
     */
    public static final Byte MCARD_CARD_PAY_NO_CASH = 0;
    /**
     *1:支持现金购买
     */
    public static final Byte MCARD_CARD_PAY_CASH = 1;

    /** 过期类型  */
    /** expire_type固定日期 */
    public static final Byte MCARD_ET_FIX = 0;
    /** expire_type 自领取多少内有效 */
    public static final Byte MCARD_ET_DURING = 1;
    /** expire_type 永久有效 */
    public static final Byte MCARD_ET_FOREVER = 2;

    /** 背景色类型 */
    public static final Byte MCARD_BGT_COLOR = 0;
    /** 背景图片类型 */
    public static final Byte MCARD_BGT_IMG = 1;

    /** discount_is_all 全部商品 打折 */
    public static final Byte MCARD_DIS_ALL = 1;
    /** discount_is_all 部分商品 打折 */
    public static final Byte MCARD_DIS_PART = 0;

    /**
     * 门店类型 全部门店，部分门店，不可在门店使用
     */
    /**   全部门店 */
    public static final Byte MCARD_STP_ALL= 0;
    /**   部分门店 */
    public static final Byte MCARD_STP_PART = 1;
    /**  不可在门店使用 */
    public static final Byte MCARD_STP_BAN = -1;
    /**
     * store_use_switch 可否在门店使用  0不可以 1可以
     */
    /**   1可以 在门店使用 */
    public static final Byte MCARD_SUSE_OK = 1;
    /**   0不可以 在门店使用 */
    public static final Byte MCARD_SUSE_NO = 0;


    /**
     * receive_action 领取方式 1:领取码 2：卡号+密码
     */
    /**   领取方式 1:领取码 */
    public static final Byte MCARD_RA_CODE = 1;
    /**   领取方式 2:卡号+密码 */
    public static final Byte MCARD_RA_PWD = 2;

    /**
     * 激活： 0：不用激活，1：需要激活
     */
    /**   不用激活 */
    public static final Byte MCARD_ACT_NO = 0;
    /**   需要激活 */
    public static final Byte MCARD_ACT_YES = 1;
    /**
     * 	 是否审核 0不审核 1审核
     */
    /**   0不审核 */
    public static final Byte MCARD_EXAMINE_OFF=0;
    /**    1审核 */
    public static final Byte MCARD_EXAMINE_ON=1;

    /** date_type 天数类型 0:日*/
    public static final Byte MCARD_DT_DAY = 0;
    /** date_type 天数类型 1:周  */
    public static final Byte MCARD_DT_WEEK = 1;
    /** date_type 天数类型2: 月 */
    public static final Byte MCARD_DT_MONTH = 2;

    /**
     * is_exchang 限次会员卡适用商品 0： 不可兑换商品 ；1 ：部分商品；2：全部商品
     */
    /**   不可兑换商品 */
    public static final Byte MCARD_ISE_NON = 0;
    /**   部分商品 */
    public static final Byte MCARD_ISE_PART = 1;
    /**   全部商品 */
    public static final Byte MCARD_ISE_ALL = 2;

    /** del_flag 删除状态： 0：没有删除 */
    public static final Byte MCARD_DF_NO = 0;
    /** del_flag 删除状态： 1：确定删除 */
    public static final Byte MCARD_DF_YES = 1;


    /** member_card表flag 1正常使用，2停止使用 */
    public static final Byte MCARD_FLAG_USING = 1;
    public static final Byte MCARD_FLAG_STOP = 2;
    /** -会员卡已经过期 */
    public static final Byte MCARD_EXPIRED = 3;
    /** send_coupon_type 送惠类型：0优惠券 */
    public static final Byte MCARD_CTP_COUPON = 0;
    /** send_coupon_type 送惠类型：1优惠券礼包 */
    public static final Byte MCARD_CTP_PACKAGE = 1;

    /** send_coupon_switch 开卡送券：0不是，1是 */
    public static final Byte MCARD_SEND_COUPON_OFF = 0;
    public static final Byte MCARD_SEND_COUPON_ON = 1;

    /** send_coupon_type 送惠类型：0优惠券，1优惠券礼包*/
    public static final Byte MCARD_COUPON_TYPE = 0;
    public static final Byte MCARD_COUPON_PACK_TYPE = 1;

    /**   是否专属购买商品 0 不是 1 是

    /** -专享商品:标签关联类型  */
    /**   1：关联商品 */
    public static final Byte COUPLE_TP_GOODS = 1;
    /**    2：关联商家分类 */
    public static final Byte COUPLE_TP_STORE = 2;
    /**   3：关联平台分类 */
    public static final Byte COUPLE_TP_PLAT = 3;
    /**   4， 关联品牌分类 */
    public static final Byte COUPLE_TP_BRAND = 4;
    public static final String BUTTON_ON = "on";
    public static final String BUTTON_OFF = "off";


    /** 天数 */
    public static final Integer DAY = 1;
    public static final Integer WEEK = 7;
    public static final Integer MONTH = 30;

    /** 勾选 */
    public static final Byte CHECKED = 1;

    /** 现金购买 */
    public static final Byte BUY_BY_CRASH = 0;
    /** 积分购买 */
    public static final Byte BUY_BY_SCORE = 1;


    /** jackson */
    public static final ObjectMapper MAPPER = new ObjectMapper();
    /** 当前时间 */
    public static final LocalDate CURRENT_DATE = LocalDate.now();


    /** 可否在门店使用  0不可以 1可以 */
    public static final Byte UNAVAILABLE_IN_STORE = 0;
    public static final Byte AVAILABLE_IN_STORE = 1;


    /** 国际化语言前缀 */
    public static final String LANGUAGE_TYPE_MEMBER="member";

    /** 是否专属购买商品 0不是 1是*/
    public static final Byte PAY_OWN_GOOD_ON = 1 ;


    /** user_card表 flag 0 正常使用，1已经删除  2转赠中,3已转赠,4 已过期	*/
    public static final Byte UCARD_FG_USING = 0;
    public static final Byte UCARD_FG_STOP = 1;
    public static final Byte UCARD_FG_GIVING = 2;
    public static final Byte UCARD_FG_GIVED = 3;
    public static final Byte UCARD_FG_EXPIRED = 4;
    /** user_card 激活 */
    public static final Boolean UCARD_ACT_NO = false;
    public static final Boolean UCARD_ACT_TRUE = true;


    /**   member_card

    /** 是否专属购买商品 0不是 1是 */
    public static final Byte PAY_OWN_GOOD_YES=1;
    public static final String LOWEST_GRADE = "v0";
    public static final Byte SUPPORT_PAY_BY_CASH = 1;
    public static final Byte NOT_SUPPORT_PAY_BY_CASH = 0;


    /**   card_receive_code */
    public static final Integer ALL_BATCH = 0;

    /**   card_examine */
    public static final Byte UNDER_REVIEW = 1;
    public static final Byte VERIFIED = 2;
    public static final Byte REFUSED = 3;


    /**   card_consumer */
    public static final Short SHORT_ZERO = 0;
    public static final Byte EXCHANG_COUNT_TYPE = 1;
    public static final Byte COUNT_TYPE = 2;

    public static final String NUM_LETTERS = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**用户未领取会员卡*/
    public static final Byte USER_CARD_STATUS_NOT_HAS = 0;
    /**用户已领取会员卡且可使用*/
    public static final Byte USER_CARD_STATUS_HAS = 1;
    /**用户需要激活会员卡*/
    public static final Byte USER_CARD_STATUS_NEED_ACTVATION = 2;
    /**用户会员卡已过期*/
    public static final Byte USER_CARD_STATUS_OUT_OF_EXPIRE = 4;
    /**限次卡免运得*/
    public static final Byte FREE_SHIPPING = 0;
    /**
     * 无限兑换商品数量
     */
    public static final Integer INFINITE_EXCHANGE = 0;

    /**
     * 会员卡续费回调订单标识
     */
    public static final String USER_CARD_RENEW_ORDER = "x";
    /**
     * 会员卡续费订单已完成
     */
    public static final Byte CARD_RENEW_ORDER_STATUS_OK = 1;
    public static final Byte ZERO = 0;
    public static final Byte ONE = 1;
    public static final Byte TWO = 2;

    /**
     * 会员卡续费金额类型-现金
     */
    public static final Byte USER_CARD_RENEW_TYPE_CASH = 0;
    /**
     * 会员卡续费金额类型-积分
     */
    public static final Byte USER_CARD_RENEW_TYPE_INTEGRAL = 1;

    /**
     * 会员卡续费时长类型-日
     */
    public static final Byte USER_CARD_RENEW_DATE_TYPE_DAY = 0;
    /**
     * 会员卡续费时长类型-周
     */
    public static final Byte USER_CARD_RENEW_DATE_TYPE_WEEK = 1;
    /**
     * 会员卡续费时长类型-月
     */
    public static final Byte USER_CARD_RENEW_DATE_TYPE_MONTH = 2;

    /**
     * 	卡充值 charge_money
     */
    /**
     * 	change_type: 1 发卡
     */
    public static final Byte CHARGE_SEND_CARD = 1;
    /**
     * change_type: 2 	用户充值
     */
    public static final Byte CHARGE_USER_POWER = 2;
    /**
     * change_type: 3  管理员操作
     */
    public static final Byte CHARGE_ADMIN_OPT = 3;
    /**
     * change_type: 4  退款入卡
     */
    public static final Byte CHARGE_TYPE_REFUND = 4;

}

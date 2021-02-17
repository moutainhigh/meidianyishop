package com.meidianyi.shop.common.foundation.data;

/**
 * 基础静态常量存放处
 *
 * @date 2019/10/24 16:42
 * @author kongdecheng
 */
public final  class  BaseConstant {

    public static final String LANGUAGE_TYPE_PARAM="param";
    public static final String LANGUAGE_TYPE_EXCEL = "excel";

    /**外部接口数据状态*/
    /**启用*/
    public static final Integer EXTERNAL_ITEM_STATE_ENABLE = 1;
    /**禁用*/
    public static final Integer EXTERNAL_ITEM_STATE_DISABLE = 2;
    /**删除*/
    public static final Integer EXTERNAL_ITEM_STATE_DELETE= 3;

    /**
     * 启用
     */
    public static final Byte ACTIVITY_STATUS_NORMAL = 1;
    /**
     * 禁用
     */
    public static final Byte ACTIVITY_STATUS_DISABLE = 0;

    //******************商品goodsType对应信息*****************************/
    /** 普通商品 */
    public final static Byte ACTIVITY_TYPE_GENERAL = 0;
    /** 拼团商品 */
    public final static Byte ACTIVITY_TYPE_GROUP_BUY = 1;
    /** 返利商品 */
    public final static Byte ACTIVITY_TYPE_REBATE = 2;
    /** 砍价商品 */
    public final static Byte ACTIVITY_TYPE_BARGAIN = 3;
    /** 积分兑换商品 */
    public final static Byte ACTIVITY_TYPE_INTEGRAL = 4;
    /** 秒杀商品 */
    public final static Byte ACTIVITY_TYPE_SEC_KILL = 5;
    /** 限时降价 */
    public final static Byte ACTIVITY_TYPE_REDUCE_PRICE = 6;
    /** 加价购 */
    public final static Byte ACTIVITY_TYPE_PURCHASE_PRICE = 7;
    /** 拼团抽奖 */
    public final static Byte ACTIVITY_TYPE_GROUP_DRAW = 8;
    /** 一口价 */
    public final static Byte ACTIVITY_TYPE_PACKAGE_SALE = 9;
    /** 定金膨胀 */
    public final static Byte ACTIVITY_TYPE_PRE_SALE = 10;
    /** 赠品 */
    public final static Byte ACTIVITY_TYPE_GIFT = 11;
    /** 幸运大抽奖 */
    public final static Byte ACTIVITY_TYPE_LOTTERY_PRESENT = 12;
    /** 限次卡兑换 */
    public final static Byte ACTIVITY_TYPE_EXCHANG_ORDER = 13;
    /** 好友助力 */
    public final static Byte ACTIVITY_TYPE_PROMOTE_ORDER = 14;
    /** 满包邮 */
    public final static Byte ACTIVITY_TYPE_FREESHIP_ORDER = 15;
    /** 测评 */
    public final static Byte ACTIVITY_TYPE_ASSESS_ORDER = 16;
    /** 送礼 */
    public final static Byte ACTIVITY_TYPE_GIVE_GIFT = 17;
    /** 首单特惠 */
    public final static Byte ACTIVITY_TYPE_FIRST_SPECIAL = 18;
    /** 支付有礼 */
    public final static Byte  ACTIVITY_TYPE_PAY_AWARD = 19;
    /** 优惠券活动 */
    public final static Byte ACTIVITY_TYPE_COUPON = 20;
    /** 满折满减*/
    public final static Byte ACTIVITY_TYPE_FULL_REDUCTION = 21;
    /** 等级会员价格（不是营销活动）*/
    public final static Byte ACTIVITY_TYPE_MEMBER_GRADE = 22;
    /** 会员专享型（不是营销活动）*/
    public final static Byte ACTIVITY_TYPE_MEMBER_EXCLUSIVE = 23;
    /** 我的奖品 */
    public final static Byte ACTIVITY_TYPE_MY_PRIZE = 24;
    /** 分销员推广的商品 */
    public final static Byte ACTIVITY_TYPE_DISTRIBUTOR_GOODS = 26;
    /** 药方 */
    public final static Byte ACTIVITY_TYPE_PRESCRIPTION = 30;
    /** 加价购商品*/
    public final static Byte ACTIVITY_TYPE_PURCHASE_GOODS = 97;
    /** 限时降价和等级会员价共存 */
    public final static Byte ACTIVITY_TYPE_GRADE_REDUCE_PRICE = 98;
    /** 代付订单 */
    public final static Byte ACTIVITY_TYPE_PAY_FOR_ANOTHER = 99;
    /** 扫码购订单 */
    public final static Byte ACTIVITY_TYPE_SWEEP_CODE_BUY = 100;


    //*************************首单特惠 超出限购数量后，买家不可继续添加购买该商品 1禁止超限购买，0超限全部恢复原价
    /**
     * 继续购买
     */
    public final static Byte LIMIT_FLAG_CONTINUE =0;
    /**
     * 限制购买
     */
    public final static Byte LIMIT_FLAG_CONFINE =1;


    //******************普通标识********************************

    /**
     * 肯定
     */
    public static final Byte YES = 1;

    /**
     * 否定
     */
    public static final Byte NO = 0;

    //*************************优惠券*****************************
    //状态 1启用 0停用
    /**
     * 禁用
     */
    public final static Byte COUPON_ENABLED_DISABLED =0;
    /**
     * 可用
     */
    public final static Byte COUPON_ENABLED_NORMAL =1;

    //优惠券有效期类型标记，1领取后开始指定时间段内有效，0固定时间段有效
    /**
     * 固定时间段有效
     */
    public final static Byte COUPON_VALIDITY_TYPE_FIXED =0;
    /**
     * 领取后开始指定时间段内有效
     */
    public final static Byte COUPON_VALIDITY_TYPE_FLEXIBLE =1;
    //是否限制库存：0限制，1不限制
    /**
     * 0限制
     */
    public final static Byte COUPON_LIMIT_SURPLUS_FLAG_LIMITED =0;
    /**
     * 1不限制
     */
    public final static Byte COUPON_LIMIT_SURPLUS_FLAG_UNLIMITED =1;
    //优惠券类型，0普通优惠券；1分裂优惠券
    /**
     * 0普通优惠券
     */
    public final static Byte COUPON_TYPE_NORMAL =0;
    /**
     * 1分裂优惠券
     */
    public final static Byte COUPON_TYPE_SPLIT =1;

    //b2c_customer_avail_coupons的优惠券来源get_source
    /**
     * 1表单送券
     */
    public final static Byte GET_SOURCE_FORM =1;
    /**
     * 2支付送券
     */
    public final static Byte GET_SOURCE_PAY =2;
    /**
     * 3活动送券
     */
    public final static Byte GET_SOURCE_ACT =3;
    /**
     * 4积分兑换
     */
    public final static Byte GET_SOURCE_INTEGRAL =4;
    /**
     * 5直接领取
     */
    public final static Byte GET_SOURCE_DIRECT_RECEIVE =5;
    /**
     * 6分裂优惠券
     */
    public final static Byte GET_SOURCE_SPLIT =6;
    /**
     * 7crm领券
     */
    public final static Byte GET_SOURCE_CRM =7;
    /**
     * 8幸运大抽奖
     */
    public final static Byte GET_SOURCE_LUCKY_DRAW =8;
    /**
     * 9定向发券
     */
    public final static Byte GET_SOURCE_GIVE =9;
    /**
     * 10激活发券
     */
    public final static Byte GET_SOURCE_ACTIVATION =10;
    /**
     * 11好友帮助砍价发券
     */
    public final static Byte GET_SOURCE_BARGAIN_CUT =11;
    /**
     * 12砍价失败激励
     */
    public final static Byte GET_SOURCE_BARGAIN_FAILED =12;
    /**
     * 13拼团失败激励
     */
    public final static Byte GET_SOURCE_GROUP_BUY_FAILED =13;
    /**
     * 14拼团抽奖失败激励
     */
    public final static Byte GET_SOURCE_GROUP_DRAW_FAILED =14;
    /**
     * 15优惠券礼包
     */
    public final static Byte GET_SOURCE_COUPON_PACK =15;
    /**
     * 16好友助力成功奖励
     */
    public final static Byte GET_SOURCE_PROMOTE_SUCCESS =16;
    /**
     * 17好友助力失败奖励
     */
    public final static Byte GET_SOURCE_PROMOTE_FAILED =17;
    /**
     * 18测评奖励
     */
    public final static Byte GET_SOURCE_ASSESS =18;
    /**
     * 19开通会员卡
     */
    public final static Byte GET_SOURCE_MEMBER_CARD =19;
    /**
     * 20评价有礼送券
     */
    public final static Byte GET_SOURCE_COMMENT_AWARD =20;
    /**
     * 21分享有礼送券
     */
    public final static Byte GET_SOURCE_SHARE_AWARD =21;
    /**
     * 22分销送券
     */
    public final static Byte GET_SOURCE_DISTRIBUTION =22;
    /**
     * 23收藏有礼送券
     */
    public final static Byte GET_SOURCE_COLLECT =23;
    /**领券用户不打标签*/
    public final static Byte NOT_COUPON_TAG = 0;
    /**领券用户打标签*/
    public final static Byte COUPON_TAG = 1;


    //accessMode
    /**
     * 0发放
     */
    public final static Byte ACCESS_MODE_ISSUE =0;
    /**
     * 1领取
     */
    public final static Byte ACCESS_MODE_RECEIVE =1;
    /**
     * 2优惠券礼包自动发放
     */
    public final static Byte ACCESS_MODE_COUPON_PACK =2;

    //*********************前端活动状态***************************************/
    /**
     * 进行中
     */
    public final static byte NAVBAR_TYPE_ONGOING= 1;
    /**
     * 未开始
     */
    public final static byte NAVBAR_TYPE_NOT_STARTED= 2;
    /**
     * 已过期
     */
    public final static byte NAVBAR_TYPE_FINISHED= 3;
    /**
     * 已停用
     */
    public final static byte NAVBAR_TYPE_DISABLED= 4;
    /**
     * 进行中,未开始
     */
    public final static byte NAVBAR_TYPE_AVAILABLE= 5;

    //************************活动是否永久有效***************************************/
    /**
     * 活动永久有效
     */
    public final static Byte ACTIVITY_IS_FOREVER =1;
    /**
     * 活动不是永久有效
     */
    public final static Byte ACTIVITY_NOT_FOREVER =0;

    //**************************商品范围******************************************/
    /**
     * 全部商品
     */
    public final static Byte GOODS_AREA_TYPE_ALL=1;
    /**
     * 部分商品
     */
    public final static Byte GOODS_AREA_TYPE_SECTION=0;

    //************************小程序-商品详情-活动状态码值***************************************/
    /** 0正常 */
    public final static Byte ACTIVITY_STATUS_CAN_USE = 0;
    /** 1该活动不存在 */
    public final static Byte ACTIVITY_STATUS_NOT_HAS = 1;
    /** 2该活动已停用 */
    public final static Byte ACTIVITY_STATUS_STOP= 2;
    /** 3该活动未开始 */
    public final static Byte ACTIVITY_STATUS_NOT_START = 3;
    /** 4该活动已结束 */
    public final static Byte ACTIVITY_STATUS_END = 4;
    /** 5商品已抢光 */
    public final static Byte ACTIVITY_STATUS_NOT_HAS_NUM = 5;
    /** 6该用户已达到活动参与上限或购买数量上限 */
    public final static Byte ACTIVITY_STATUS_MAX_COUNT_LIMIT = 6;
    /** 7该秒杀为会员专属，该用户没有对应会员卡 */
    public final static Byte ACTIVITY_STATUS_NOT_HAS_MEMBER_CARD = 7;
    /** 8参与活动的某规格无可使用活动数量 */
    public final static Byte ACTIVITY_STATUS_PRD_NOT_HAS_NUM = 8;
    /** 9有待支付对应订单 */
    public final static Byte ACTIVITY_STATUS_HAS_ORDER_READY_TO_PAY = 9;
    /**活动没有有效规格进行使用，由于活动规格和商品规格无可用交集*/
    public final static Byte ACTIVITY_STATUS_NO_PRD_TO_USE = 10;

    public static boolean needToConsiderNotHasNum(Byte actState){
        if (ACTIVITY_STATUS_CAN_USE.equals(actState) || ACTIVITY_STATUS_NOT_START.equals(actState)) {
            return true;
        } else {
            return false;
        }
    }

}

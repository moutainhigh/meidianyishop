package com.meidianyi.shop.service.pojo.shop.goods;

import com.meidianyi.shop.common.foundation.data.BaseConstant;

/**
 * @author 李晓冰
 * @date 2019年10月14日
 * 商品及相关类的通用常量对应类
 */
public class GoodsConstant {
    /****** 商品列表各种活动的处理优先级 *****/
    /**
     * 秒杀
     */
    public final static Byte ACTIVITY_SEC_KILL_PRIORITY = 1;
    /**
     * 预售
     */
    public final static Byte ACTIVITY_PRE_SALE_PRIORITY = 5;
    /**
     * 砍价
     */
    public final static Byte ACTIVITY_BARGAIN_PRIORITY = 10;
    /**
     * 拼团
     */
    public final static Byte ACTIVITY_GROUP_BUY_PRIORITY = 15;
    /**
     * 会员专享处理
     */
    public final static Byte ACTIVITY_CARD_EXCLUSIVE_PRIORITY = 20;
    /**
     * 首单特惠
     */
    public final static Byte ACTIVITY_FIRST_SPECIAL_PRIORITY = 25;
    /**
     * 限时降价
     */
    public final static Byte ACTIVITY_REDUCE_PRICE_PRIORITY = 30;
    /**
     * 等级卡会员价
     */
    public final static Byte ACTIVITY_MEMBER_GRADE_PRIORITY = 35;
    /**
     * 优惠券
     */
    public final static Byte ACTIVITY_COUPON_PRIORITY = 40;
    /**
     * 满折满减
     */
    public final static Byte ACTIVITY_FULL_REDUCTION_PRIORITY = 45;

    /**
     * 满包邮
     */
    public final static Byte ACTIVITY_FREE_SHIP_PRIORITY = 47;

    /**
     * 积分兑换
     */
    public final static Byte ACTIVITY_INTEGER_MALL_PRIORITY = 50;

    /**
     * 分销
     */
    public final static Byte ACTIVITY_DISTRIBUTION_PRIORITY = Byte.MAX_VALUE - 1;
    /**************** 结束 *****************/

    public final static Integer ZERO = 0;

    /** 小程序海报分享码跳转地址，商品详情页或对应活动页 */
    /**商品详情页*/
    public static final Byte GOODS_ITEM = 1;
    /**活动页*/
    public static final Byte ACTIVITY_PAGE = 2;

    /**
     * 活动不预告
     */
    public static final Integer ACTIVITY_NOT_PRE = 0;


    /**************** 小程序、装修-商品分组- 顶部展示分组，展示全部商品栏*****************/
    public final static Byte GOODS_GROUP_LIST_TOP_POSITION = 0;
    public final static Byte GOODS_GROUP_LIST_SHOW_ALL_COLUMN = 1;

    /**************** 小程序-装修商品-手动推荐或自动推荐 *****************/
    public final static Byte AUTO_RECOMMEND = 0;
    public final static Byte POINT_RECOMMEND = 1;

    public final static Byte AUTO_RECOMMEND_COMMON = 0;
    public final static Byte AUTO_RECOMMEND_PRESCRIPTION = 1;

    /**************** 结束 *****************/

    /*** 销售状态：在售*/
    public static final Byte ON_SALE = 1;
    /**
     * 下架
     */
    public static final Byte OFF_SALE = 0;

    /** 商品仓库类型 在售 */
    public static final Byte SALE_TYPE_ON_SALE = 0;
    /** 商品仓库类型 指定时间  */
    public static final Byte SALE_TYPE_ON_TIME = 1;
    /** 商品仓库类型 仓库中 */
    public static final Byte SALE_TYPE_NOT_ON_SALE = 2;
    /** 商品所属商家分类默认值 */
    public static final Byte GOODS_SORT_DEFAULT_VALUE = 0;

    /**
     * 售罄
     */
    public static final Byte SALE_OUT=1;
    /**
     * 未售罄
     */
    public static final Byte NOT_SALE_OUT=0;
    /**
     * 加入仓库 设置saleType使用
     */
    public static final Byte IN_STOCK = 2;

    /**
     * 砍价活动，指定人数砍价
     */
    public static final Byte BARGAIN_TYPE_FIXED = 0;

    /**
     * 拼团活动-开团后-最大等待成团时间 秒
     */
    public static final long GROUP_BUY_LIMIT_TIME = 24*60*60;

    /**
     * 商品默认品牌id值
     */
    public static final Integer GOODS_DEFAULT_BRAND_ID=0;
    /**
     * 商品标签可以展示的最大数量
     */
    public static final Integer GOODS_LABEL_MAX_COUNT=5;
    /**
     * 商品违规下架
     */
    public static final Byte INVALIDATE_OFF_SALE = 2;

    /**
     * 指定时间上架
     */
    public static final Byte POINT_TIME_TO_ON_SALE = 1;
    /**非指定时间上架*/
    public static final Byte NOT_TIME_TO_ON_SALE = 0;

    /*** 会员专享状态：是*/
    public static final Byte CARD_EXCLUSIVE = 1;
    public static final Byte NOT_CARD_EXCLUSIVE = 0;

    /*** 可在商品列表,详情展示时标签该字段的值*/
    public static final Byte SHOW_LABEL = 1;
    public static final Integer LABEL_GTA_DEFAULT_VALUE = 0;


    /*** 是推荐品牌,普通品牌，推荐分类，普通分类码*/
    public static final Byte RECOMMEND_BRAND = 1;
    public static final Byte NORMAL_BRAND = 0;
    public static final Byte RECOMMEND_SORT = 1;
    public static final Byte NORMAL_SORT = 0;
    public static final Byte HAS_CHILD = 1;
    public static final Byte HAS_NO_CHILD = 0;
    public static final Short ROOT_LEVEL = 0;
    public static final Short SECOND_LEVEL = 1;
    public static final Short THIRD_LEVEL = 2;
    /*** 未关联品牌分类时的分类id*/
    public static final Integer NO_CLASSIFY_ID = 0;

    /*** 一级分类的父节点id值：0*/
    public static final Integer ROOT_PARENT_ID = 0;

    /***展示售罄商品*/
    public static final Byte SOLD_OUT_GOODS_SHOW = 1;

    /**
     * 1默认规格，0自定义规格（多规格）
     */
    public static final Byte IS_DEFAULT_PRODUCT_Y = 1;
    public static final Byte IS_DEFAULT_PRODUCT_N = 0;

    /**团长*/
    public static final Byte GROUP_CAPTAIN = 1;

    /**空字符串*/
    public static final String BLACK = "";

    /**
     * 小程序-商品分类页面-目录项类型码
     * 1全部品牌，2推荐品牌，3推荐品牌列表展示，4推荐品牌按分类展示，
     * 5推荐分类，6普通分类，7商品内容
     */
    public static final Byte ALL_BRAND_TYPE = 1;
    public static final Byte RECOMMEND_BRAND_TYPE = 2;
    public static final Byte RECOMMEND_BRAND_LIST_TYPE = 3;
    public static final Byte RECOMMEND_BRAND_CLASSIFY_TYPE = 4;
    public static final Byte RECOMMEND_SORT_TYPE = 5;
    public static final Byte NORMAL_SORT_TYPE = 6;
    public static final Byte GOODS_TYPE = 7;
    public static final Byte NORMAL_CHRONIC_TYPE = 8;

    public static boolean isGoodsTypeIn13510(Byte goodsType) {
        return BaseConstant.ACTIVITY_TYPE_GROUP_BUY.equals(goodsType) || BaseConstant.ACTIVITY_TYPE_BARGAIN.equals(goodsType)
            || BaseConstant.ACTIVITY_TYPE_SEC_KILL.equals(goodsType) || BaseConstant.ACTIVITY_TYPE_PRE_SALE.equals(goodsType);
    }

    /**
     * 是否是需要告知前端活动id的活动,目前有 1,3,5,6,10,18
     *
     * @param goodsType 活动类型
     */
    public static boolean isNeedReturnActivity(Byte goodsType) {
        return BaseConstant.ACTIVITY_TYPE_GROUP_BUY.equals(goodsType) || BaseConstant.ACTIVITY_TYPE_BARGAIN.equals(goodsType)
            || BaseConstant.ACTIVITY_TYPE_SEC_KILL.equals(goodsType) || BaseConstant.ACTIVITY_TYPE_PRE_SALE.equals(goodsType);

    }
}

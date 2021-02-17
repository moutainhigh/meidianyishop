package com.meidianyi.shop.service.pojo.shop.qrcode;

/**
 * @author 李晓冰
 * @date 2019年08月09日
 */
public enum QrCodeTypeEnum {

    /*小程序首页*/
    PAGE_BOTTOM((byte) 1, "pages/bottom/bottom"),
    /*首页加邀请码*/
    INVITE((byte) 1, "pages/bottom/bottom"),
    /*商品*/
    GOODS_ITEM((byte) 2, "pages/item/item"),
    /*服务*/
    SERVICE_APPOINTMENT((byte) 3, "pages1/appointment/appointment"),
    /*会员卡*/
    USER_CARD_INFO((byte) 4, "pages/cardinfo/cardinfo"),
    /*优惠券*/
    DISCOUN_COUPON((byte) 5, "pages/getCoupon/getCoupon"),
    /*门店买单*/
    SHOP_CHECKOUT((byte) 6, "pages/shopcheckout/shopcheckout"),
    /*小程序页面*/
    INDEX((byte) 7, "pages/index/index"),
    /*拼团*/
    GROUP_BOOKING((byte) 8, "pages/groupbuyitem/groupbuyitem"),
    /*表单*/
    FORM((byte) 9, "pages1/form/form"),
    /*砍价商品*/
    BARGAIN_ITEM((byte) 10, "pages/bargainitem/bargainitem"),
    /*海报-商品*/
    POSTER_GOODS_ITEM((byte) 11, "pages/item/item"),
    /*海报-拼团*/
    POSTER_GROUP_BOOKING((byte) 12, "pages/groupbuyitem/groupbuyitem"),
    /*海报-砍价详情页*/
    POSTER_BARGAIN_ITEM((byte) 13, "pages/bargainitem/bargainitem"),
    /*海报-砍价页*/
    POSTER_BARGAIN_INFO((byte) 14, "pages/bargaininfo/bargaininfo"),
    /*海报-拼团中*/
    POSTER_GROUP_BOOKING_INFO((byte) 15, "pages1/groupbuyinfo/groupbuyinfo"),
    /*积分商品详情*/
    INTEGRAL_ITEM_INFO((byte) 16, "pages/integralitem/integralitem"),
    /*抽奖*/
    LOTTERY((byte) 17, "pages1/lottery/lottery"),
    /*返利推广文案*/
    REBATE_POPULARIZE_DOCUMENT((byte) 18, "pages/distributionspread/distributionspread"),
    /*秒杀商品详情页*/
    SECKILL_GOODS_ITEM_INFO((byte) 19, "pages/seckillitem/seckillitem"),
    /*erp来源*/
    ERP_SOURCE((byte) 20, "pages/bottom/bottom"),
    /*加价购主商品*/
    RAISE_PRICE_BUY_MAIN_GOODS((byte) 21, "pages/maingoodslist/maingoodslist"),
    /*瓜分积分活动*/
    PARTATION_INTEGRAL((byte) 22, "pages1/pinintegration/pinintegration"),
    /*拼团抽奖详情*/
    GROUP_BOOKING_LOTTERY((byte) 23, "pages/pinlotteryitem/pinlotteryitem"),
    /*定金膨胀详情*/
    DOWN_PAYMENT_INFO((byte) 24, "pages/presaleitem/presaleitem"),
    /*一口价详情*/
    BUY_NOW_PRICE_INFO((byte) 25, "pages1/packagesalelist/packagesalelist"),
    /*海报—积分商品详情*/
    POSTER_INTEGRAL_ITEM_INFO((byte) 26, "pages/integralitem/integralitem"),
    /*扫码购*/
    SCAN_BUY((byte) 27, "pages/scancode/scancode"),
    /*门店分享*/
    SHOP_SHARE((byte) 28, "pages/storeinfo/storeinfo"),
    /*好友助力分享*/
    FRIEND_HELP_SHARE((byte) 29, "pages1/promoteinfo/promoteinfo"),
    /*测评*/
    ASSERT_START((byte) 30, "pages2/assessstart/assessstart"),
    /*优惠券礼包*/
    DISCOUNT_COUPON_PAGCKAGE((byte) 31, "pages1/couponpackage/couponpackage"),
    /*满包邮*/
    FULL_SHIP((byte) 32, "pages1/fullship/fullship"),
    /*多人抽奖*/
    PIN_LOTTERY((byte) 33, "pages1/pinlotterylist/pinlotterylist"),
    /*我要送礼*/
    PRESENT_GIFT((byte) 34, "pages1/presentinfo/presentinfo"),
    /*商品搜索页*/
    GOODS_SEARCH((byte) 35, "pages/search/search"),
    /*积分兑换优惠券页*/
    SCORE_COUPON((byte) 36, "pages/receiveCoupon/receiveCoupon"),
    /*砍价详情页分享*/
    BARGAIN_INFO_SHARE((byte) 98, "");


    private short type;
    private String url;

    QrCodeTypeEnum(byte type, String url) {
        this.type = type;
        this.url = url;
    }


    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPathUrl(String pathParam) {
        return pathParam == null ? this.url : this.url + "?" + pathParam;
    }

}

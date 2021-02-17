package com.meidianyi.shop.service.pojo.shop.market.message;

/**
 * 模版消息通传参类常量
 * @author 卢光耀
 * @date 2019-09-02 13:41
 *
*/
public class RabbitParamConstant {

    public static class Type{
        /** 预约取消通知 */
        public static final Integer BOOKING_CANCEL = 2001;
        /** 预约成功提醒 */
        public static final Integer BOOKING_SUCCESS = 2002;
        /** 预约到期提醒 */
        public static final Integer BOOKING_EXPIRED = 2003;
        /** 账户余额变动提醒 */
        public static final Integer MONEY_CHANGE = 2004;
        /** 订单发货提醒 */
        public static final Integer ORDER_SEND = 2005;
        /** 订单未支付通知 */
        public static final Integer ORDER_NO_PAY =2006;
        /** 订单支付成功通知 */
        public static final Integer ORDER_SUCCESS_PAY = 2007;
        /** 确认收货通知 */
        public static final Integer GET_GOODS = 2008;
        /** 退款失败通知 */
        public static final Integer FAIL_RETURN_MONEY= 2009;
        /** 退款状态通知 */
        public static final Integer STATUS_RETURN_MONEY = 2010;
        /** 门店自提到期提醒 */
        public static final Integer OWNER_GET_GOODS = 2011;
        /** 取货成功通知 */
        public static final Integer SUCCESS_GET_GOODS = 2012;
        /** 积分消费提醒 */
        public static final Integer POINTS_CONSUMPTION = 2013;
        /** 提现申请通知	 */
        public static final Integer GET_MONEY = 2014;
        /** 会员升级通知 */
        public static final Integer MEMBER_LEVEL_UP = 2015;

        /** 会员卡余额变动提醒 */
        public static final Integer CHANGE_MEMBER_CARD_MONEY = 3001;
        /** 会员卡领取成功通知 */
        public static final Integer SUCCESS_MEMBER_CARD_GET = 3002;
        /** 限次卡扣减通知 */
        public static final Integer REDUCE_LIMIT_TIMES_MEMBER_CARD = 3003;
        /** 卡券到期提醒 */
        public static final Integer EXPIRED_MEMBER = 3004;
        /** 卡券领取成功通知 */
        public static final Integer GET_COUPON = 3005;
        /** 拼团失败通知 */
        public static final Integer FAIL_TEAM = 3006;
        /** 拼团成功通知 */
        public static final Integer SUCCESS_TEAM = 3007;
        /** 自定义消息模板推送 */
        public static final Integer DIY_MESSAGE_TEMPLATE = 3008;
        /** 砍价成功提醒 */
        public static final Integer SUCCESS_BRING_DOWN = 3009;
        /** 砍价进度通知 */
        public static final Integer PROGRESS_BRING_DOWN = 3010;
        /** 审核通过提醒 */
        public static final Integer SUCCESS_REVIEW = 3011;
        /** 审核不通过提醒 */
        public static final Integer FAIL_REVIEW = 3012;
        /** 分销员等级升级提醒 */
        public static final Integer LEVEL_UP = 3013;
        /** 拼团抽奖结果通知 */
        public static final Integer LOTTERY_TEAM = 3014;
        /**
         * 新咨询订单提醒
         */
        public static final Integer NEW_CONSULTATION_ORDER = 3020;
        /**
         * 待处理订单通知
         */
        public static final Integer WAIT_HANDLE_ORDER = 3021;
        /**
         * 待售后订单通知
         */
        public static final Integer SALE_AFTER_ORDER = 3022;


        /**
         * 只发送小程序
         * 命名方式:ONLY_MA_相关类型
         * 赋值范围:4001开始
         * eg. public static final Integer ONLY_MA_ = 4001;
         */
        /** 拼团抽奖结果通知 */
        public static final Integer INVITE_SUCCESS_GROUPBUY = 4002;
        
        /** 好友助力邀请成功通知 */
        public static final Integer INVITE_SUCCESS_FRIEND_PROMOTE = 4003;

        /** 砍价成功通知 */
        public static final Integer INVITE_SUCCESS_BARGAIN = 4004;


		
		/**
		 * 以下两个不校验，为调用properties里定义的appId可用，不要随便调用
		 */
        /**
         *微信公众号模板消息类型,用户没有使用过小程序，比如扫公众号二维码
         */
        public static final Integer MP_TEMPLE_TYPE_NO_USER=1003;
      
        /**
         *微信公众号模板消息类型，.
         */
        public static final Integer MP_TEMPLE_TYP_NO=1004;
        
    }



}

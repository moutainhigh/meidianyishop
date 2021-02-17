package com.meidianyi.shop.service.foundation.jedis;

/**
 * redis key
 * @author 卢光耀
 * @date 2019-09-06 09:59
 *
*/
public class JedisKeyConstant {
    public static final String SEND_USER_KEY = "send:user:";

    /**taskJob lock*/
    public static final String TASK_JOB_LOCK = "lock:task:";

    /**商品品牌*/
    public static final String GOODS_BRAND = "goods:brand:shop-id_";
    /**商家分类*/
    public static final String GOODS_SORT= "goods:sort:shop-id_";


    /**
     * Es init
     */
    public static final String ES_INIT = "es:init";

    /**
     * goods lock(库存、销量)
     */
    public static final String GOODS_LOCK = "lock:goods:";

    /**
     * prescription lock(库存、销量)
     */
    public static final String PRESCRIPTION_LOCK = "lock:prescription:";
    /**
     * make prescription lock(库存、销量)
     */
    public static final String MAKE_PRESCRIPTION_LOCK = "lock:makePrescription:";
    /**
     * add prescription lock(医师)
     */
    public static final String ADD_PRESCRIPTION_LOCK = "lock:addPrescription:";
    /**
     * inquiry order refund lock(orderSn)
     */
    public static final String INQUIRY_ORDER_REFUND_LOCK = "lock:refundInquiryOrder:";
    //店铺配置
    /**
     * 分词配置
     */
    public static final String CONFIG_ANALYZER_STATUS = "config:analyzer:";
    /**
     * 分享配置
     */
    public static final String CONFIG_SHARE_CONFIG = "config:shareConfig:";
    /**
     * 是否显示logo
     */
    public static final String CONFIG_SHOW_LOGO = "config:showLogo:";
    /**
     * logo点击的跳转链接
     */
    public static final String CONFIG_LOGO_LINK = "config:logoLink:";
    /**
     * 店铺风格
     */
    public static final String CONFIG_SHOP_STYLE = "config:shopStyle:";
    /**
     * 店铺底部导航配置
     */
    public static final String CONFIG_BOTTOM = "config:bottom:";


    /**
     * 拼团处理订单定时任务
     */
    public static final String TASK_JOB_LOCK_ORDER_GROUP_BUY = "lock:task:order:group_buy:";

    public static class NotResubmit {
        /**
         * 下单锁（同一个用户同时只会存在一个正常可以完成的下单请求）+shopId+userid
         */
        public static final String ORDER_SUBMIT = "lock:orderSubmit";
        /**
         * 订单退款
         */
        public static final String ORDER_RETURN = "lock:orderReturn";

        /**
         * 提现申请锁+shopId+userid
         */
        public static final String WITHDRAW_APPLY = "lock:withdrawApply";
        /**
         * 医师提现申请锁+shopId+userid
         */
        public static final String DOCTOR_WITHDRAW_APPLY = "lock:doctorWithdrawApply";
        /**
         * 创建门店锁
         */
        public static final String ADD_STORE_LOCK = "lock:store:add:";
    }

    /**会话未读信息表*/
    public static final String IM_SESSION_ITEM_LIST_KEY = "session:shopId%d:sessionId%d:fromId%d:toId%d";
    /**会话已读信息表*/
    public static final String IM_SESSION_ITEM_LIST_KEY_BAK = "session:shopId%d:sessionId%d:bak";
    /**会话状态标识位key存在则表明会话可用，否则不可用*/
    public static final String IM_SESSION_STATUS = "session:shopId%d:sessionId%d:status";

    /**
     * 分销员二维码
     */
    public static final String DISTRIBUTION_QR_CODE = "distribution:qrCode:";
    // ============ 分销相关 end ============
}

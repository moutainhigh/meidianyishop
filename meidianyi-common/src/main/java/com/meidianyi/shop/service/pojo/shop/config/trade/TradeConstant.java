package com.meidianyi.shop.service.pojo.shop.config.trade;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Trade constant.
 *
 * @author liufei
 * @date 2019 /9/20
 */
public class TradeConstant {
    /**
     * The constant WXERROR_10000001.
     */
    public static final String WXERROR_10000001 = "-1";
    /**
     * The constant WXERROR_9300529.
     */
    public static final String WXERROR_9300529 = "9300529";
    /**
     * The constant WXERROR_9300530.
     */
    public static final String WXERROR_9300530 = "9300530";
    /**
     * The constant WXERROR_9300531.
     */
    public static final String WXERROR_9300531 = "9300531";
    /**
     * The constant WXERROR_9300532.
     */
    public static final String WXERROR_9300532 = "9300532";

    /**
     * The constant DELIVERY_ID.
     * {@value}
     */
    public static final String DELIVERY_ID = "delivery_id";
    /**
     * The constant DELIVERY_NAME.
     * {@value}
     */
    public static final String DELIVERY_NAME = "delivery_name";
    /**
     * 物流公司绑定状态，-1：未绑定状态
     * The constant STATUS_CODE.
     * {@value}
     */
    public static final Byte STATUS_CODE = -1;
    /**
     * 服务条款内容
     * The constant DOCUMENT.
     */
    public static final String DOCUMENT = "document";
    /**
     * 服务条款更新时间
     * The constant UPDATE_TIME.
     */
    public static final String UPDATE_TIME = "update_time";
    /**
     * {@value} 使用内省获取到的类属性列表中会多一个class属性
     */
    public static final String FIELD_CLAZZ = "class";
    /**
     * 退换货相关默认期限：7天
     * {@value}
     */
    public static final Byte BYTE_SEVEN = 7;

    /**
     * The constant TRADE_PROCESS_CONFIG.
     */
    public static final String TRADE_PROCESS_CONFIG = "trade_process_config";
    /**
     * The constant DELIVERY_LIST.
     */
    public static final String DELIVERY_LIST = "delivery_list";

    /**
     * The constant EMPTY_LIST.
     */
    public static final List<Integer> EMPTY_LIST = new ArrayList<>();
}

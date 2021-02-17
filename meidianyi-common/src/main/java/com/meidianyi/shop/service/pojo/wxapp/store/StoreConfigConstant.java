package com.meidianyi.shop.service.pojo.wxapp.store;

/**
 * @author 赵晓东
 * @description 门店状态常量类
 * @create 2020-09-03 16:36
 **/

public class StoreConfigConstant {
    /**
     * 已关店
     */
    public static final Byte STORE_BUSINESS_CLOSE = 0;
    /**
     * 在营业
     */
    public static final Byte STORE_BUSINESS_OPENING = 1;
    /**
     * 每天营业
     */
    public static final Byte STORE_BUSINESS_OPEN_EVERYDAY = 1;
    /**
     * 工作日营业
     */
    public static final Byte STORE_BUSINESS_WORKDAY = 0;
    /**
     * 开启自提
     */
    public static final Short STORE_AUTO_PICK_ENABLE = 1;
}

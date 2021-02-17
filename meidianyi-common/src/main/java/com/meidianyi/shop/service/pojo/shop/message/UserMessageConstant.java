package com.meidianyi.shop.service.pojo.shop.message;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-23 18:22
 **/

public class UserMessageConstant {

    /**
     * 消息类型 系统消息0，订单消息1，会话消息2
     */
    public static final Byte USER_MESSAGE_SYSTEM = 0;
    public static final Byte USER_MESSAGE_ORDER = 1;
    public static final Byte USER_MESSAGE_CHAT = 2;

    /**
     * 消息状态 0未读，1已读，3置顶
     */
    public static final Byte USER_MESSAGE_STATUS_NOT_READ = 0;
    public static final Byte USER_MESSAGE_STATUS_ALREADY_READ = 1;
    public static final Byte USER_MESSAGE_STATUS_TOP = 3;

    /**
     * 订单状态
     * 订单生成成功
     */
    public static final Byte ORDER_INFO_STATUS_START = 0;

    /**
     * 系统公告收件人
     */
    public static final Integer USER_MESSAGE_RECEIVE_ANN = 0;

}

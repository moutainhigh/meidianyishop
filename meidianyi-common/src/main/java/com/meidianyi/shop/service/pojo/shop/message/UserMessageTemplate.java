package com.meidianyi.shop.service.pojo.shop.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 赵晓东
 * @description 用户消息模板枚举类
 * 小程序端消息枚举
 * 系统消息 1开头四位数字
 * 订单消息 2开头四位数字
 * 会话消息 3开头四位数字
 * @create 2020-08-05 11:33
 **/

@Getter
@AllArgsConstructor
public enum UserMessageTemplate {

    /**
     * 订单待付款
     */
    USER_MESSAGE_ORDER_INFO_WAIT_PAY(2000, (byte) 0, "您订单号为%s的订单还未付款，请您及时付款。"),
    /**
     * 客户已取消
     */
    USER_MESSAGE_ORDER_INFO_ORDER_CANCELED(2001, (byte) 1, "您订单号为%s的订单已经取消。"),
    /**
     * 卖家关闭
     */
    USER_MESSAGE_ORDER_INFO_ORDER_CLOSED(2002, (byte) 2, "您订单号为%s的订单已被关闭。"),
    /**
     * 订单待发货
     */
    USER_MESSAGE_ORDER_INFO_ORDER_WAIT_DELIVERY(2003, (byte) 3, "您订单号为%s的订单正在准备发货。"),
    /**
     * 订单已发货
     */
    USER_MESSAGE_ORDER_INFO_ORDER_SHIPPED(2004, (byte) 4, "您订单号为%s的订单已经发货。"),
    /**
     * 订单已收货
     */
    USER_MESSAGE_ORDER_INFO_ORDER_RECEIVED(2005, (byte) 5, "您订单号为%s的订单已经收货。"),
    /**
     * 订单已完成
     */
    USER_MESSAGE_ORDER_INFO_ORDER_FINISHED(2006, (byte) 6, "您订单号为%s的订单已完成。"),
    /**
     * 订单退货中
     */
    USER_MESSAGE_ORDER_INFO_ORDER_RETURNING(2007, (byte) 7, "您订单号为%s的订单正在退货。"),
    /**
     * 订单完成退货
     */
    USER_MESSAGE_ORDER_INFO_ORDER_RETURN_FINISHED(2008, (byte) 8, "您订单号为%s的订单已退货成功。"),
    /**
     * 订单正在退款
     */
    USER_MESSAGE_ORDER_INFO_ORDER_REFUNDING(2009, (byte) 9, "您订单号为%s的订单已退货成功。"),
    /**
     * 订单退款成功
     */
    USER_MESSAGE_ORDER_INFO_ORDER_REFUND_FINISHED(2010, (byte) 10, "您订单号为%s的订单已退货成功。"),
    /**
     * 订单正在审核中
     */
    USER_MESSAGE_ORDER_INFO_ORDER_ORDER_TO_AUDIT(2014, (byte) 14, "您订单号为%s的订单正在审核。"),
    /**
     * 订单待开方
     */
    USER_MESSAGE_ORDER_INFO_ORDER_TO_AUDIT_OPEN(2010, (byte) 15, "您订单号为%s的订单在等待医师开具处方。"),
    /**
     * 用户文本会话消息新增
     */
    USER_MESSAGE_IM_SESSION_ADD(3001, (byte) 3001, "%s医师:%s"),
    /**
     * 用户图片会话消息新增
     */
    USER_MESSAGE_IM_SESSION_PICTURE_ADD(3002, (byte) 3002, "%s医师:[图片]"),
    /**
     * 用户处方会话消息新增
     */
    USER_MESSAGE_IM_SESSION_PRESCRIPTION_ADD(3003, (byte) 3003, "%s医师根据您的病情描述，为您开具以下处方");

    private Integer code;

    private byte orderStatus;

    private String message;

    public static String getMessageByCode(Integer code) {
        for (UserMessageTemplate item : UserMessageTemplate.values()) {
            if (item.code.equals(code)) {
                return item.message;
            }
        }
        return null;
    }

    /**
     * 根据订单状态获取模板消息内容
     *
     * @param orderStatus 订单状态 引于OrderConstant 39-76
     * @return String
     */
    public static String getMessageByOrderStatus(byte orderStatus) {
        for (UserMessageTemplate item : UserMessageTemplate.values()) {
            if (item.orderStatus == orderStatus) {
                return item.message;
            }
        }
        return null;
    }
}

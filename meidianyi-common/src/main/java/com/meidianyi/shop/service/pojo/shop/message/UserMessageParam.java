package com.meidianyi.shop.service.pojo.shop.message;

import lombok.Data;

/**
 * @author 赵晓东
 * @description 用户消息入参
 * @create 2020-07-23 15:33
 **/
@Data
public class UserMessageParam {
    /**
     * 消息内容
     */
    private String messageContent;
    /**
     * 消息类型 系统消息0USER_MESSAGE_SYSTEM，订单消息1USER_MESSAGE_ORDER，会话消息2USER_MESSAGE_CHAT
     */
    private Byte messageType;
    /**
     * 消息接受者id
     */
    private Integer receiverId;
    /**
     * 消息接受者姓名
     */
    private String receiverName;
    /**
     * 消息发送者id
     */
    private Integer senderId;
    /**
     * 消息发送者姓名
     */
    private String senderName;
    /**
     * 消息名称
     */
    private String messageName;
    /**
     * 消息关联id 系统为0，订单关联订单主键、会话关联会话主键
     */
    private Integer messageRelevanceId;
    /**
     * 消息已读状态
     */
    private Byte messageStatus;
    /**
     * 订单消息orderSn
     */
    private String messageRelevanceOrderSn;
    /**
     * 会话消息状态
     */
    private Byte messageChatStatus;
    /**
     * 所有消息未读总数
     */
    private Integer allMessageCount;
    /**
     * 系统公告未读总数
     */
    private Integer announcementMessageCount;
    /**
     * 订单消息未读总数
     */
    private Integer orderMessageCount;
    /**
     * 会话消息未读总数
     */
    private Integer imSessionMessageCount;
}

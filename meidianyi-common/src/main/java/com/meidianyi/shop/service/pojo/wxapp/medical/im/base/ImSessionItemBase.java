package com.meidianyi.shop.service.pojo.wxapp.medical.im.base;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 会话消息基础类
 * @author 李晓冰
 * @date 2020年07月22日
 */
@Data
public class ImSessionItemBase {
    /**会话消息*/
    private String message;
    /**会话类型*/
    private Byte type;

    private Timestamp sendTime;

    /**是否是留言信息*/
    private Byte isleavingMessage;
}

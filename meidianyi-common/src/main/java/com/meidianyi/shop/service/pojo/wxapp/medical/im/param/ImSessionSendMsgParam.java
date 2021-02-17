package com.meidianyi.shop.service.pojo.wxapp.medical.im.param;

import com.meidianyi.shop.service.pojo.wxapp.medical.im.base.ImSessionItemBase;
import lombok.Data;

/**
 * 发送消息入参类
 * @author 李晓冰
 * @date 2020年07月21日
 */
@Data
public class ImSessionSendMsgParam {
    /**
     * 会话id
     */
   private Integer sessionId;
    /**
     * 消息发送者
     */
    private Integer fromId;
    /**
     * 消息接受者
     */
    private Integer toId;
    /**
     * 消息内容
     */
    private ImSessionItemBase imSessionItem;
}

package com.meidianyi.shop.service.wechat.handler;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * App消息处理
 * @author lixinguo
 */
public interface WxAppMessageEventHandler {
    /**
     * 消息处理
     * @param message 消息
     * @param appId 消息对应的appId
     * @return 返回处理后消息，不处理返回null
     */
    WxMpXmlOutMessage handle(WxMpXmlMessage message,String appId);
}

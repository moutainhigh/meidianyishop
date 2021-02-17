package com.meidianyi.shop.service.wechat.handler;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;

/**
 * open组件消息处理
 *
 * @author lixinguo
 */
public interface WxComponentMessageHandler {

    /**
     * open组件消息处理
     * @param wxMessage Open组件消息
     * @return 返回success 或 ""
     */
    String handle(WxOpenXmlMessage wxMessage);
}


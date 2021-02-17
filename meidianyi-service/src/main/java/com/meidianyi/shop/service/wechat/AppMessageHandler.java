package com.meidianyi.shop.service.wechat;

import com.meidianyi.shop.service.saas.shop.MpAuthShopService;
import com.meidianyi.shop.service.wechat.handler.WxAppMessageEventHandler;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 公众号或小程序消息处理
 *
 * @author lixinguo
 */
@Component
@Slf4j
public class AppMessageHandler implements WxAppMessageEventHandler {

    @Autowired
    protected MpAuthShopService mpAuthShopService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage message, String appId) {
        log.debug("WxAppMessageEventHandler handle {}",message.toString());
        return mpAuthShopService.appEventHandler(message, appId);
    }
}

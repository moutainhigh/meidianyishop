package com.meidianyi.shop.service.wechat;

import com.meidianyi.shop.config.WxOpenConfig;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.wechat.api.impl.WxOpenComponentExtServiceImpl;
import com.meidianyi.shop.service.wechat.api.impl.WxOpenMaServiceExtraImpl;
import com.meidianyi.shop.service.wechat.api.impl.WxOpenMpServiceExtraImpl;
import com.meidianyi.shop.service.wechat.handler.WxAppMessageEventHandler;
import com.meidianyi.shop.service.wechat.handler.WxComponentMessageHandler;
import lombok.Getter;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.open.api.impl.WxOpenInRedisConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


/**
 * @author 新国
 */
@Service
public class OpenPlatform extends WxOpenServiceImpl {

    @Autowired
    protected WxOpenConfig wxOpenConfig;

    static final String AES = "aes";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private WxOpenMessageRouter wxOpenMessageRouter;

    /**
     * 必须实现这个Bean
     */
    @Autowired
    protected JedisManager jedis;

    /**
     * 公众号和小程序的消息处理器
     */
    @Autowired(required = false)
    protected WxAppMessageEventHandler wxAppMessageEventHandler;

    /**
     * Open开放平台组件消息处理器
     */
    @Autowired(required = false)
    protected WxComponentMessageHandler wxComponentMessageHandler;

    /**
     * 小程序扩展服务
     */
    @Getter
    protected WxOpenMaServiceExtraImpl maExtService = new WxOpenMaServiceExtraImpl(this);

    /**
     * 公众号扩展服务
     */
    @Getter
    protected WxOpenMpServiceExtraImpl mpExtService = new WxOpenMpServiceExtraImpl(this);

    /**
     * 开放平台扩展服务
     */
    @Getter
    protected WxOpenComponentExtServiceImpl componentExtService = new WxOpenComponentExtServiceImpl(this);


    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        WxOpenInRedisConfigStorage inRedisConfigStorage = new WxOpenInRedisConfigStorage(jedis.getJedisPool());
        inRedisConfigStorage.setComponentAppId(wxOpenConfig.getAppId());
        inRedisConfigStorage.setComponentAppSecret(wxOpenConfig.getAppSecret());
        inRedisConfigStorage.setComponentToken(wxOpenConfig.getToken());
        inRedisConfigStorage.setComponentAesKey(wxOpenConfig.getAesKey());
        setWxOpenConfigStorage(inRedisConfigStorage);
        this.wxOpenMessageRouter = new WxOpenMessageRouter(this);
        messageRouter();
    }

    /**
     * 公众号或小程序消息路由
     */
    protected void messageRouter() {
        wxOpenMessageRouter.rule().handler((wxMpXmlMessage, map, wxMpService, wxSessionManager) -> {
            logger.info("\n接收到 {} 公众号请求消息，内容：{}", wxMpService.getWxMpConfigStorage().getAppId(), wxMpXmlMessage);
            return null;
        }).next();
    }

    public WxOpenMessageRouter getWxOpenMessageRouter() {
        return wxOpenMessageRouter;
    }

    /**
     * 开放平台回调，包含ticket、授权、取消授权、更新授权回调
     */
    public String componetCallback(String requestBody, String timestamp,
                                   String nonce, String signature, String encType, String msgSignature) {
        this.logger.info(
            "\n接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
                + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
            signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!StringUtils.equalsIgnoreCase(AES, encType)
            || !this.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        // aes加密的消息
        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(requestBody,
            this.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
        try {
            String out = this.getWxOpenComponentService().route(inMessage);
            this.logger.debug("\n组装回复信息：{}", out);
            /**
             * 外置的Open组件处理
             */
            if (null != wxComponentMessageHandler) {
                out = this.wxComponentMessageHandler.handle(inMessage);
            }
        } catch (WxErrorException e) {
            this.logger.error("receive_ticket {}", e);
        }
        return "success";
    }

    /**
     * 公众号或小程序事件处理
     *
     * @param requestBody
     * @param appId
     * @param signature
     * @param timestamp
     * @param nonce
     * @param openid
     * @param encType
     * @param msgSignature
     * @return
     */
    public String appEvent(String requestBody, String appId, String signature, String timestamp,
                           String nonce, String openid, String encType, String msgSignature) {

        this.logger.info(
            "\n接收微信请求：[appId=[{}], openid=[{}], signature=[{}], encType=[{}], msgSignature=[{}],"
                + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
            appId, openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        if (!StringUtils.equalsIgnoreCase(AES, encType)
            || !this.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        // 解密
        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(requestBody,
            this.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
        String out = "";
        if (isGlobalTestAppId(appId)) {
            globalTest(appId, inMessage);
        } else {
            WxMpXmlOutMessage wMessage = null;

            /**
             * 公众号和小程序处理
             */
            if (wxAppMessageEventHandler != null) {
                wMessage = wxAppMessageEventHandler.handle(inMessage, appId);
            }

            if (wMessage != null) {
                logger.debug("\n 返回微信的消息wMessage为：\n{}", wMessage.toXml());
                out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(wMessage, this.getWxOpenConfigStorage());
            } else {
                WxMpXmlOutMessage outMessage = this.getWxOpenMessageRouter().route(inMessage, appId);
                logger.debug("\n 返回微信的消息outMessage为：\n{}", outMessage.toXml());
                if (outMessage != null) {
                    out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(outMessage, this.getWxOpenConfigStorage());
                }
            }
        }
        return out;
    }

    /**
     * 全网测试
     *
     * @param appId
     * @param message
     * @return
     */
    protected String globalTest(String appId, WxMpXmlMessage message) {
        String out = "";
        String mstTypeEvent = "event";
        String mstTypeText = "text";
        String testMsgTypeContent = "TESTCOMPONENT_MSG_TYPE_TEXT";
        String testMsgTypeContentCallback = "TESTCOMPONENT_MSG_TYPE_TEXT_callback";
        String queryAuthCodePrefix = "QUERY_AUTH_CODE:";
        String fromApiSuffix = "_from_api";
        String fromCallbackSuffix = "from_callback";
        if (isGlobalTestAppId(appId)) {
            try {
                if (StringUtils.equals(message.getMsgType(), mstTypeText)) {
                    if (StringUtils.equals(message.getContent(), testMsgTypeContent)) {
                        out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(
                            WxMpXmlOutMessage.TEXT().content(testMsgTypeContentCallback)
                                .fromUser(message.getToUser())
                                .toUser(message.getFromUser())
                                .build(),
                            this.getWxOpenConfigStorage());
                    } else if (StringUtils.startsWith(message.getContent(), queryAuthCodePrefix)) {
                        String msg = message.getContent().replace(queryAuthCodePrefix, "") + fromApiSuffix;
                        WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(msg).toUser(message.getFromUser())
                            .build();
                        this.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService()
                            .sendKefuMessage(kefuMessage);
                    }
                } else if (StringUtils.equals(message.getMsgType(), mstTypeEvent)) {
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT()
                        .content(message.getEvent() + fromCallbackSuffix)
                        .toUser(message.getFromUser()).build();
                    this.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService()
                        .sendKefuMessage(kefuMessage);
                }
            } catch (WxErrorException e) {
                logger.error("callback {}", e);
            }
        }
        return out;
    }

    /**
     * 是否用于全网发布测试appId
     *
     * @param appId
     * @return
     */
    protected boolean isGlobalTestAppId(String appId) {
        return (StringUtils.equalsAnyIgnoreCase(appId, "wxd101a85aa106f53e", "wx570bc396a51b8ff8"));
    }
}

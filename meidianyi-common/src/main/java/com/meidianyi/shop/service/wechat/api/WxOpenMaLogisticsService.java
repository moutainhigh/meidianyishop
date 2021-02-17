package com.meidianyi.shop.service.wechat.api;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import me.chanjar.weixin.open.util.json.WxOpenGsonBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liufei
 * @date 2019/9/20
 * 小程序调用-物流助手API
 */
public interface WxOpenMaLogisticsService extends WxOpenMaMpHttpBase {
    /**
     * 获取支持的快递公司列表 HTTPS调用请求地址
     * {@value}
     */
    static final String LOGISTICS_GET_ALL_DELIVERY = "https://api.weixin.qq.com/cgi-bin/express/business/delivery/getall";
    /**
     * 绑定物流公司 HTTPS调用请求地址
     * {@value}
     */
    static final String LOGISTICS_BIND_ACCOUNT = "https://api.weixin.qq.com/cgi-bin/express/business/account/bind";
    /**
     * 拉取已绑定账号 HTTPS调用请求地址
     * {@value}
     */
    static final String LOGISTICS_GET_ALL_ACCOUNT = "https://api.weixin.qq.com/cgi-bin/express/business/account/getall";


    /**
     * 获取支持的快递公司列表
     *
     * @param appId  https调用凭证 {@value LOGISTICS_GET_ALL_DELIVERY}
     * @return
     * @throws WxErrorException
     */
    default String getAllDelivery(String appId) throws WxErrorException {
        return post(appId, LOGISTICS_GET_ALL_DELIVERY, StringUtils.EMPTY);
    }

    /**
     * 绑定物流公司
     * @param appId https调用凭证 {@value LOGISTICS_BIND_ACCOUNT}
     * @param jsonParam 绑定参数
     * @return
     * @throws WxErrorException
     */
    default WxOpenResult bindAccount(String appId, String jsonParam) throws WxErrorException {
        String jsonResult = post(appId, LOGISTICS_BIND_ACCOUNT, jsonParam);
        return WxOpenGsonBuilder.create().fromJson(jsonResult, WxOpenResult.class);
    }

    /**
     * 拉取已绑定账号
     *
     * @param appId https调用凭证
     *              {@value LOGISTICS_GET_ALL_ACCOUNT}
     * @return
     * @throws WxErrorException
     */
    default String getAllAccount(String appId) throws WxErrorException {
        return post(appId, LOGISTICS_GET_ALL_ACCOUNT, StringUtils.EMPTY);
    }
}

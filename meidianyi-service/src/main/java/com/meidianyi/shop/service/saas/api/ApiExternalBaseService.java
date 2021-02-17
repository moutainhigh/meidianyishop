package com.meidianyi.shop.service.saas.api;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.ApiExternalConfig;
import com.meidianyi.shop.service.saas.external.ExternalRequestHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Service
@Slf4j
public class ApiExternalBaseService {
    @Autowired
    ApiExternalConfig apiExternalConfig;
    @Autowired
    ExternalRequestHistoryService externalRequestHistoryService;
    /**
     * 生成对应的签名
     * @param appId
     * @param appSecret
     * @param sessionKey
     * @param serviceName 请求服务方法名称
     * @param content 请求体内容
     * @param curSecond 当前时间 秒为单位 字符串
     * @return
     */
    public String generateSign(String appId, String appSecret, String sessionKey, String serviceName, String content, String curSecond) {
        List<String> list = new ArrayList<>(10);
        list.add(appId);
        list.add(appSecret);
        list.add(sessionKey);
        list.add(serviceName);
        list.add(content);
        list.sort(Comparator.naturalOrder());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append("&");
            }
        }
        String s = Util.md5(sb.toString());
        s = s + curSecond + apiExternalConfig.getSignKey();
        s = Util.md5(s);
        return s;
    }

    /**
     * 当前时间，秒
     * @return
     */
    protected Long getCurSecond() {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(timeZone);
        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * 添加请求记录
     * @param appId
     * @param shopId
     * @param serviceName
     * @param param
     */
    protected void addRequestHistory(String appId,Integer shopId,String serviceName,String param,Integer status){
        externalRequestHistoryService.insertRequest(appId,shopId,serviceName,param,status);
    }


}

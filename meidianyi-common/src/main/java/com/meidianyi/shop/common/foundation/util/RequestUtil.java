package com.meidianyi.shop.common.foundation.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * request工具类
 * @author 卢光耀
 * @date 2019-08-07 10:27
 *
*/
public class RequestUtil {

    private static final String UNKNOWN = "unknown";

    public static String getIp(HttpServletRequest request){
        String realip = request.getHeader("X-Real-IP");
        String forwardedip = request.getHeader("X-Forwarded-IP");
        if (StringUtils.isNotEmpty(forwardedip) && UNKNOWN.equalsIgnoreCase(forwardedip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = forwardedip.indexOf(",");
            if(index != -1){
                return forwardedip.substring(0,index);
            }else{
                return forwardedip;
            }
        }
        forwardedip = realip;

        if(StringUtils.isNotEmpty(forwardedip) && !UNKNOWN.equalsIgnoreCase(forwardedip)){
            return forwardedip;
        }
        if (StringUtils.isBlank(forwardedip) || UNKNOWN.equalsIgnoreCase(forwardedip)) {
            forwardedip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(forwardedip) || UNKNOWN.equalsIgnoreCase(forwardedip)) {
            forwardedip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(forwardedip) || UNKNOWN.equalsIgnoreCase(forwardedip)) {
            forwardedip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(forwardedip) || UNKNOWN.equalsIgnoreCase(forwardedip)) {
            forwardedip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(forwardedip) || UNKNOWN.equalsIgnoreCase(forwardedip)) {
            forwardedip = request.getRemoteAddr();
        }
        return forwardedip;
    }
}

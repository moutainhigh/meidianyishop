package com.meidianyi.shop.service.saas.api;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestParam;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.app.vo.AppAuthVo;
import com.meidianyi.shop.service.saas.external.AppAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 请求外部对接平台服务
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Slf4j
@Service
public class ApiExternalRequestService extends MainBaseService {
    @Autowired
    private ApiExternalBaseService apiExternalBaseService;
    @Autowired
    private AppAuthService appAuthService;

    /**
     * 请求外部服务统一调用入口
     * @param appId
     * @param shopId
     * @param serviceName
     * @param requestContentJson
     * @return
     */
    public ApiExternalRequestResult externalRequestGate(String appId, Integer shopId, String serviceName, String requestContentJson) {
        AppAuthVo appAuth = appAuthService.getAppAuth(appId, shopId);
        if (appAuth == null) {
            ApiExternalRequestResult vo = new ApiExternalRequestResult();
            vo.setError(ApiExternalRequestConstant.ERROR_CODE_NOT_AUTH);
            vo.setMsg("店铺授权信息不存在");
            log.warn("请求外部服务：" + vo.getMsg());
            apiExternalBaseService.addRequestHistory(appId,shopId,serviceName,requestContentJson,vo.getError());
            return vo;
        }
        String curSecond =apiExternalBaseService.getCurSecond().toString();
        String sign = apiExternalBaseService.generateSign(appId,appAuth.getAppSecret(),appAuth.getSessionKey(),serviceName,requestContentJson,curSecond);

        ApiExternalRequestParam requestParam = new ApiExternalRequestParam();
        requestParam.setAppId(appId);
        requestParam.setAppSecret(appAuth.getAppSecret());
        requestParam.setSessionKey(appAuth.getSessionKey());
        requestParam.setCurSecond(curSecond);
        requestParam.setServiceName(serviceName);
        requestParam.setContent(requestContentJson);
        requestParam.setSign(sign);

        ApiExternalRequestResult vo = post(appAuth.getRequestLocation(), requestParam);

        apiExternalBaseService.addRequestHistory(appId,shopId,serviceName,requestContentJson,vo.getError());
        return vo;
    }

    /**
     * 真正发送http请求
     * @param location
     * @param requestParam
     */
    private ApiExternalRequestResult post(String location, ApiExternalRequestParam requestParam) {
        HashMap<String, Object> param = new HashMap<>(7);
        param.put("appId", requestParam.getAppId());
        param.put("appSecret", requestParam.getAppSecret());
        param.put("sessionKey", requestParam.getSessionKey());
        param.put("serviceName", requestParam.getServiceName());
        param.put("content", requestParam.getContent());
        param.put("curSecond", requestParam.getCurSecond());
        param.put("sign", requestParam.getSign());
        ApiExternalRequestResult vo = null;
        String post=null;
        try {
            log.debug("请求外部："+location+"，请求参数："+param);
            HttpResponse response = HttpRequest.post(location).form(param).timeout(20000).execute();
            int successStatus = 200;
            if (response.getStatus()== successStatus){
                post =response.body();
            }else {
                log.warn("请求外部服务-返回码{}：" , response.getStatus());
                vo = new ApiExternalRequestResult();
                vo.setError(ApiExternalRequestConstant.ERROR_CODE_NET_ILLEGAL);
                vo.setMsg("返回码"+response.getStatus());
                return vo;
            }
        } catch (Exception e) {
            log.warn("请求外部服务-网络请求：" + e.getMessage());
            vo = new ApiExternalRequestResult();
            vo.setError(ApiExternalRequestConstant.ERROR_CODE_NET_ILLEGAL);
            vo.setMsg(e.getMessage());
            return vo;
        }

        try {
            vo = Util.parseJson(post, ApiExternalRequestResult.class);
        } catch (Exception e) {
            log.warn("请求外部服务-解析返回值：" + e.getMessage());
            vo = new ApiExternalRequestResult();
            vo.setError(ApiExternalRequestConstant.ERROR_CODE_PARSE_RETVAL);
            vo.setMsg(e.getMessage());
        }
        if (vo == null) {
            log.warn("请求外部服务-解析返回值：" +"json解析错误");
            vo = new ApiExternalRequestResult();
            vo.setError(ApiExternalRequestConstant.ERROR_CODE_PARSE_RETVAL);
            vo.setMsg("json解析错误");
        }
        if (vo.getError() == null) {
            log.warn("请求外部服务-解析返回值：" +"请求返回内容格式错误");
            vo = new ApiExternalRequestResult();
            vo.setError(ApiExternalRequestConstant.ERROR_CODE);
            vo.setMsg("请求返回内容格式错误");
        }
        return vo;
    }


}

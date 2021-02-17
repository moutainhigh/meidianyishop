package com.meidianyi.shop.controller.system;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateParam;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateResult;
import com.meidianyi.shop.common.pojo.saas.api.ApiJsonResult;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateConstant;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.saas.api.ApiExternalGateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序对接POS,ERP服务接口
 * @author 李晓冰
 * @date 2020年03月30日
 */
@RestController
@Slf4j
public class ApiExternalGateController extends ShopBaseService {

    @Autowired
    private ApiExternalGateService gateService;

    @PostMapping("/api/service/gateWay")
    public ApiExternalGateResult gateWay(ApiExternalGateParam param){
        try {
            // 日志请求记录
            apiCallLog(param);

            // 必要系统参数验证
            String nullKey = gateService.checkSystemParam(param);
            if (nullKey != null) {
                return response(ApiExternalGateConstant.ERROR_LACK_PARAM, ApiExternalGateConstant.ERROR_LACK_PARAM_MSG+"："+nullKey);
            }

            // 时间戳验证
            if (!gateService.checkTimeStamp(param)) {
                return response(ApiExternalGateConstant.ERROR_CODE_INVALID_TIMESTAMP, ApiExternalGateConstant.ERROR_CODE_INVALID_TIMESTAMP_MSG);
            }

            // 验证签名
            if (!gateService.checkSign(param)) {
                return response(ApiExternalGateConstant.ERROR_CODE_INVALID_SIGN, ApiExternalGateConstant.ERROR_CODE_INVALID_SIGN_MSG);
            }

            // 解析shopId
            Integer shopId = gateService.parseShopId(param);
            if (shopId == -1) {
                return response(ApiExternalGateConstant.ERROR_CODE_INVALID_SHOP, ApiExternalGateConstant.ERROR_CODE_INVALID_SHOP_MSG);
            }

            param.setShopId(shopId);
            if (!gateService.checkShop(param)) {
                return response(ApiExternalGateConstant.ERROR_CODE_INVALID_SHOP, ApiExternalGateConstant.ERROR_CODE_INVALID_SHOP_MSG);
            }

            // 验证应用店铺授权
            if (!gateService.checkAppAuth(param)) {
                return response(ApiExternalGateConstant.ERROR_CODE_INVALID_APP, ApiExternalGateConstant.ERROR_CODE_INVALID_APP_MSG);
            }

            // 验证请求的方法是否在规定的方法集合内
            if (!gateService.checkService(param)) {
                return response(ApiExternalGateConstant.ERROR_CODE_INVALID_SERVICE, ApiExternalGateConstant.ERROR_CODE_INVALID_SERVICE_MSG);
            }

            ApiExternalGateResult result = gateService.serviceFunCall(param);
            responseLog(result);
            return result;
        } catch (Exception e) {
           log.error("servcie gateWay error:", e);
           return response(ApiExternalGateConstant.ERROR_SYSTEM_FAIL, ApiExternalGateConstant.ERROR_SYSTEM_FAIL_MSG);
        }
    }

    /**
     * 统一返回信息出口
     * @param errorCode 错误码
     * @param errorMsg 错误信息
     * @return {@link ApiJsonResult}
     */
    private ApiExternalGateResult response(Integer errorCode, String errorMsg){
        ApiExternalGateResult apiJsonResult = new ApiExternalGateResult();
        apiJsonResult.setCode(errorCode);
        apiJsonResult.setMsg(errorMsg);
        responseLog(apiJsonResult);
        return apiJsonResult;
    }

    /**
     * 统一日志
     * @param apiExternalGateResult 接口返回信息
     */
    private void responseLog(ApiExternalGateResult apiExternalGateResult){
        log.info("service api response："+ Util.toJson(apiExternalGateResult));
    }

    private void apiCallLog(ApiExternalGateParam param) {
        log.info("service api call："+Util.toJson(param));
    }
}

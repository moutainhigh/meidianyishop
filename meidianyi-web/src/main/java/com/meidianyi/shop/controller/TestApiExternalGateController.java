package com.meidianyi.shop.controller;

import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestParam;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.controller.admin.AdminBaseController;
import com.meidianyi.shop.service.saas.api.ApiExternalBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 孔德成
 * @date 2020/7/17 8:51
 */
@RestController
@Slf4j
public class TestApiExternalGateController extends AdminBaseController {

    @Autowired
    protected  ApiExternalBaseService apiExternalBaseService;

    /**
     * 模拟医院接口
     * @return
     */
    @PostMapping("/api/test/hospital")
    public ApiExternalRequestResult test(ApiExternalRequestParam param){
        String serviceName = param.getServiceName();
        String sign = apiExternalBaseService.generateSign(param.getAppId(),param.getAppSecret(),param.getSessionKey(),serviceName,param.getContent(),param.getCurSecond());
        ApiExternalRequestResult apiExternalRequestResult=new ApiExternalRequestResult();
        if (param.getSign().equals(sign)){
            log.info("解析成功");
            switch (param.getServiceName()){

                case ApiExternalRequestConstant.SERVICE_NAME_FETCH_PATIENT_INFO:
                    log.info("拉取患者信息{}", param.getContent());
                    apiExternalRequestResult.setError(0);
                    apiExternalRequestResult.setMsg("success");
                    apiExternalRequestResult.setData("{\"code\":\"1\",\"name\":\"小明\",\"phone\":\"135\",\"birthday\":\"2020-03-04\",\"state\":1,\"remarks\":\"介绍111\",\"sex\":1,\"age\":13,\"identityType\":1,\"identityNo\":\"411527\",\"visitNo\":\"111\",\"carteVitalNo\":\"112221\"}");
                    return apiExternalRequestResult;
                case ApiExternalRequestConstant.SERVICE_NAME_UPLOAD_ORDER_PRESCRIPTION:
                    log.info(":同步订单{}",param.getContent());
                    return apiExternalRequestResult;
                default:
                    log.info("没有对应服务:[{}]",param.getServiceName());
            }

        }
        apiExternalRequestResult.setError(1);
        apiExternalRequestResult.setMsg("");
        return apiExternalRequestResult;
    }

}

package com.meidianyi.shop.service.saas.api;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateParam;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateResult;
import com.meidianyi.shop.db.main.tables.records.AppAuthRecord;
import com.meidianyi.shop.db.main.tables.records.AppRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiSyncOrderStatusParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * 小程序对接POS,ERP服务service层
 * @author 李晓冰
 * @date 2020年03月30日
 */
@Service
@Slf4j
public class ApiExternalGateService extends MainBaseService {

    @Autowired
    private ApiExternalBaseService apiExternalBaseService;

    /**
     * 验证系统级参数
     * 'app_id', 'app_secret', 'session_key', 'service_name'
     * @param param {@link ApiExternalGateParam}
     * @return null 表示必要字段都存在，否则代表第一个空字段的参数名
     */
    public String checkSystemParam(ApiExternalGateParam param) {
        String nullKey = null;
        if (StringUtils.isBlank(param.getAppId())) {
            nullKey = "appId";
        } else if (StringUtils.isBlank(param.getAppSecret())) {
            nullKey = "appSecret";
        } else if (StringUtils.isBlank(param.getSessionKey())) {
            nullKey = "sessionKey";
        } else if (StringUtils.isBlank(param.getServiceName())) {
            nullKey = "serviceName";
        } else {
            nullKey = null;
        }
        if (nullKey != null) {
            logPrinter(param.getAppId(), ApiExternalGateConstant.ERROR_LACK_PARAM_MSG + ":" + nullKey);
        }
        return nullKey;
    }

    /**
     * 校验接口调用时间
     * @param param {@link ApiExternalGateParam}
     * @return false时间参数不合法 可能是null或者当前时间和改时间差超过30秒，true
     */
    public boolean checkTimeStamp(ApiExternalGateParam param) {
        if (param.getCurSecond() == null) {
            logPrinter(param.getAppId(), "timestamp 为空");
            return false;
        }
        Long curSecond;
        try {
            curSecond = Long.parseLong(param.getCurSecond());
        } catch (NumberFormatException e) {
            return false;
        }
        Timestamp timestamp = new Timestamp(curSecond * 1000);
        Timestamp now = DateUtils.getLocalDateTime();
        // 大于30秒
        int timeout = 30000;
        if (now.getTime() - timestamp.getTime() > timeout) {
            logPrinter(param.getAppId(), "timestamp 超时");
            return false;
        }
        return true;
    }

    /**
     * 校验签名是否正确
     * @param param {@link ApiExternalGateParam}
     * @return true合法 false 错误
     */
    public boolean checkSign(ApiExternalGateParam param) {
        String s = apiExternalBaseService.generateSign(param.getAppId(), param.getAppSecret(), param.getSessionKey(), param.getServiceName(), param.getContent(), param.getCurSecond());
        if (!s.equals(param.getSign())) {
            logPrinter(param.getAppId(), "签名错误");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据sessionKey解析shopId 最后一个s字符后面的字符表示shopId
     * @param param {@link ApiExternalGateParam}
     * @return shopId -1 解析错误
     */
    public Integer parseShopId(ApiExternalGateParam param) {
        String sessionKey = param.getSessionKey();
        boolean ok = Pattern.matches(".*s\\d+", sessionKey);
        if (!ok) {
            return null;
        }
        int si = sessionKey.lastIndexOf('s');
        String shopIdStr = sessionKey.substring(si + 1);
        Integer shopId = null;
        try {
            shopId = Integer.parseInt(shopIdStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return shopId;
    }

    /**
     * 验证店铺是否有效
     * @param param {@link ApiExternalGateParam}
     * @return true有效， false无效
     */
    public boolean checkShop(ApiExternalGateParam param) {
        if (param.getShopId() == null) {
            return false;
        }
        ShopRecord shop = saas.shop.getShopById(param.getShopId());
        if (shop == null) {
            logPrinter(param.getAppId(), "店铺id无效");
            return false;
        }
        return true;
    }

    /**
     * 验证店铺是否存在有效授权
     * @param param
     * @return true有，false授权失效
     */
    public boolean checkAppAuth(ApiExternalGateParam param) {
        AppRecord appInfo = saas.shop.shopApp.getAppInfo(param.getAppId(), param.getAppSecret());
        if (appInfo == null) {
            logPrinter(param.getAppId(), "数据错误 appid：" + param.getAppId() + "appSecret：" + param.getAppSecret());
            return false;
        }

        AppAuthRecord appAuthRecord = saas.shop.shopApp.getAppAuthInfoBySessionKey(param.getSessionKey());
        if (appAuthRecord == null) {
            logPrinter(param.getAppId(), "店铺无有效授权，sessionKey：" + param.getSessionKey());
            return false;
        }
        return true;
    }

    /**
     * 判断请求的服务名称是否有效
     * @param param {@link ApiExternalGateParam}
     * @return true有效服务 false无效服务
     */
    public boolean checkService(ApiExternalGateParam param) {

        if (!ApiExternalGateConstant.SERVICE_NAMES.contains(param.getServiceName())) {
            return false;
        }
        return true;
    }


    /**
     * 转发调用具体的服务提供方法
     * @param param {@link ApiExternalGateParam}
     * @return 服务返回结果
     */
    public ApiExternalGateResult serviceFunCall(ApiExternalGateParam param) {
        String serviceName = param.getServiceName();
        ApiExternalGateResult apiExternalGateResult = null;
        switch (serviceName) {
            case ApiExternalGateConstant.SYNC_ORDER_STATUS:

                break;
            default:
                apiExternalGateResult = new ApiExternalGateResult();
                apiExternalGateResult.setCode(ApiExternalGateConstant.ERROR_CODE_INVALID_SERVICE);
                apiExternalGateResult.setMsg(ApiExternalGateConstant.ERROR_CODE_INVALID_SERVICE_MSG);
        }
        return apiExternalGateResult;
    }

    /**
     * 药房同步订单状态
     * @param apiExternalGateParam
     * @return
     */
    private ApiExternalGateResult syncOrderStatus(ApiExternalGateParam apiExternalGateParam) {
        ApiSyncOrderStatusParam apiSyncOrderStatusParam = Util.parseJson(apiExternalGateParam.getContent(), ApiSyncOrderStatusParam.class);
        if (apiSyncOrderStatusParam == null) {
            return contentErrorResult();
        }
        ApiExternalGateResult result = new ApiExternalGateResult();
        if (StringUtils.isBlank(apiSyncOrderStatusParam.getOrderSn())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数orderSn为空");
            return result;
        }
        if (apiSyncOrderStatusParam.getOrderStatus() == null) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数orderStatus非法");
            return result;
        }

        return null;
    }

    private ApiExternalGateResult contentErrorResult() {
        ApiExternalGateResult apiJsonResult = new ApiExternalGateResult();
        apiJsonResult.setCode(ApiExternalGateConstant.ERROR_CODE_SYNC_FAIL);
        apiJsonResult.setMsg("content 内容参数错误");
        return apiJsonResult;
    }

    private void logPrinter(String appId, String msg) {
        log.error("数据同步接口：" + ApiExternalGateConstant.APP_NAMES.get(appId) + "：" + msg);
    }
}

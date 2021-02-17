package com.meidianyi.shop.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.pojo.shop.config.trade.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.meidianyi.shop.service.pojo.shop.config.trade.TradeConstant.*;
import static com.meidianyi.shop.service.pojo.shop.market.form.FormConstant.MAPPER;
import static com.meidianyi.shop.service.shop.config.TradeService.DEFAULT_LOGISTICS;

/**
 * @author liufei
 * @date 2019/7/8
 */
@Slf4j
@RestController
public class AdminTradeController extends AdminBaseController {

    /**
     * 支付方式开关配置
     *
     * @param paymentConfigParam 支付方式及对应的配置项
     */
    @PostMapping("/api/admin/config/trade/enablePayment")
    public JsonResult enablePayment(@RequestBody @Validated PaymentConfigParam paymentConfigParam) {
        //更新支付配置开关
        shop().trade.updatePayment(paymentConfigParam.getBasicConfig());
        //更新默认支付配置
        shop().trade.updateDefaultPayConf(paymentConfigParam);
        return success();
    }

    /**
     * 查询支付方式开关配置
     */
    @PostMapping("/api/admin/config/trade/getPaymentEnabled")
    public JsonResult getPaymentEnabled() {
        List<PaymentConfigVo> payStatusList = shop().trade.getPaymentEnabled();
        Map<String, Byte> defaultPayConf = shop().trade.getDefaultPayConf();
        return success(new Tuple2<>(payStatusList, defaultPayConf));
    }

    /**
     * 微信支付密钥配置
     *
     * @param wxpayConfigParam 支付密钥相关信息
     */
    @PostMapping("/api/admin/config/trade/wxpayConfig")
    public JsonResult wxpayConfig(@RequestBody @Validated WxpayConfigParam wxpayConfigParam) {
        if (!saas.shop.mp.checkAuthShopExist(wxpayConfigParam.getAppId())) {
            return fail(JsonResultMessage.AUTH_SHOP_NOT_EXIST);
        }
        return saas.shop.mp.udpateWxpayConfig(wxpayConfigParam) > 0 ? success() : fail(JsonResultMessage.WECAHT_PAY_CONFIG_UPDATE_DAILED);
    }

    /**
     * 查询微信支付密钥配置
     */
    @GetMapping("/api/admin/config/trade/getWxpayConfig")
    public JsonResult getWxpayConfig() {
        MpAuthShopRecord record = saas.shop.mp.getAuthShopByShopId(shop().trade.getShopId());
        if (Objects.isNull(record)) {
            return success(new WxpayConfigParam());
        }
        if (saas.shop.mp.checkAuthShopExist(record.getAppId())) {
            return success(saas.shop.mp.getWxpayConfig(record.getAppId()));
        }
        return fail(JsonResultMessage.AUTH_SHOP_NOT_EXIST);
    }

    /**
     * 订单流程配置更新
     *
     * @param orderProcessParam 订单流程配置项信息
     */
    @PostMapping("/api/admin/config/trade/orderProcess")
    public JsonResult orderProcess(@RequestBody @Validated OrderProcessParam orderProcessParam) {
        shop().trade.updateOrderProcess(orderProcessParam);
        return success();
    }

    /**
     * 查询订单流程配置
     */
    @PostMapping("/api/admin/config/trade/getOrderProcessConfig")
    public JsonResult getOrderProcessConfig() {
        Map<String, Object> result = new HashMap<>(2);
        // 获取交易流程配置
        OrderProcessParam param = shop().trade.getOrderProcessConfig();
        result.put(TRADE_PROCESS_CONFIG, param);
        try {
            //微信物流助手物流公司列表
            List<LogisticsAccountInfo> deliveryList = shop().trade.combineAllLogisticsAccountInfo();
            result.put(DELIVERY_LIST, deliveryList);
        } catch (BusinessException e) {
            log.error("微信物流助手api调用失败，获取支持物流公司列表失败：{}", e.getMessage());
            result.put(DELIVERY_LIST, DEFAULT_LOGISTICS);
        }
        return success(result);
    }

    /**
     * 退换货配置更新
     *
     * @param returnConfigParam 退换货配置型信息
     */
    @PostMapping("/api/admin/config/trade/returnConfig")
    public JsonResult returnConfig(@RequestBody @Validated ReturnConfigParam returnConfigParam) {
        shop().config.returnConfigService.updateReturnConfig(returnConfigParam);
        return success();
    }

    /**
     * 查询退换货配置
     */
    @PostMapping("/api/admin/config/trade/getReturnConfig")
    public JsonResult getReturnConfig() {
        return success(shop().config.returnConfigService.getReturnConfigParam());
    }

    /**
     * 查询退换货配置-商家默认收货地址配置信息
     */
    @PostMapping("/api/admin/config/trade/getdefaultaddress")
    public JsonResult getDefaultAddress() {
        return success(shop().config.returnConfigService.getDefaultAddress());
    }

    /**
     * 服务条款配置
     *
     * @param param 服务条款配置内容
     */
    @PostMapping("/api/admin/config/trade/conftermsofservice")
    public JsonResult confTermsOfService(@RequestBody ServiceDocumentParam param) {
        try {
            shop().trade.confTermsOfService(param.getServiceDocument());
            return success();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return fail(JsonResultCode.CODE_CONFIG_UPDATE_FAILED, e.getMessage());
        }
    }

    /**
     * 查询服务条款配置
     *
     * @return 服务条款配置内容
     */
    @GetMapping("/api/admin/config/trade/gettermsofservice")
    public JsonResult getTermsOfService() {
        try {
            return success(shop().trade.getTermsOfService());
        } catch (IOException e) {
            log.error(e.getMessage());
            return fail();
        }
    }

    /**
     * 微信物流助手-绑定物流公司
     */
    @PostMapping("/api/admin/config/trade/bindaccount")
    public JsonResult bindAccount(@RequestBody @Validated BindAccountParam param) {
        try {
            WxOpenResult result = shop().trade.bindAccount(param);
            return success(result);
        } catch (WxErrorException e) {
            String message = e.getMessage();
            log.error("微信api logistics.bindAccount 调用失败：{}", message);
            JsonNode errorNode = null;
            try {
                errorNode = MAPPER.readTree(message);
            } catch (IOException ex) {
                log.error("微信错误消息[{}]反序列化过程失败：{}", message, ex.getMessage());
                return fail(JsonResultCode.CODE_FAIL);
            }
            String errorCode = errorNode.get("errcode").asText();
            switch (errorCode) {
                case WXERROR_9300529:
                    return fail(JsonResultCode.WX_9300529);
                case WXERROR_9300530:
                    return fail(JsonResultCode.WX_9300530);
                case WXERROR_9300531:
                    return fail(JsonResultCode.WX_9300531);
                case WXERROR_9300532:
                    return fail(JsonResultCode.WX_9300532);
                default:
                    return fail(JsonResultCode.CODE_FAIL);
            }
        }
    }

    /**
     * Schedule test json result.概览定时任务测试
     *
     * @return the json result
     */
    @GetMapping("/api/admin/schedule/test")
    public JsonResult scheduleTest() {
        shop().shopTaskService.statisticalTableInsert.insertTradesNow();
        shop().shopTaskService.statisticalTableInsert.insertTradesRecordSummary();
        shop().shopTaskService.statisticalTableInsert.insertDistributionTag();
        shop().shopTaskService.statisticalTableInsert.insertUserRfmSummary();
        shop().shopTaskService.statisticalTableInsert.insertUserSummaryTrend();
        shop().shopTaskService.statisticalTableInsert.insertTrades();
        shop().shopTaskService.goodsStatisticTaskService.insertGoodsSummary();
        shop().shopTaskService.goodsStatisticTaskService.insertOverview();
        return success();
    }

}

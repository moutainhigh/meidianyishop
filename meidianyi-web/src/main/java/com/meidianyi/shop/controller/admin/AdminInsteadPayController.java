package com.meidianyi.shop.controller.admin;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.market.insteadpay.InsteadPay;

/**
 * @author 李晓冰
 * @date 2019年08月19日
 */
@RestController
public class AdminInsteadPayController extends AdminBaseController {

    /**
     * 设置好友代付
     *
     * @param param 配置内容
     * @return 配置结果
     */
    @RequestMapping("/api/admin/market/instead/pay/set")
    public JsonResult setInsteadPayConfig(@RequestBody InsteadPay param) {
        int messageLength = 20;
        int ratioLen=3;
        int ratioNumLen=2;

        if (param.getStatus() == null) {
            return fail(JsonResultCode.INSTEAD_PAY_STATUS_IS_NULL);
        }

        //可以代付,进行参数检查
        if (param.getStatus()) {

            //未设置代付类型
            if (param.getSinglePay() == null && param.getMultiplePay() == null) {
                return fail(JsonResultCode.INSTEAD_PAY_NOT_SET_PAY_WAY);
            }
            if (param.getSinglePay() == null && !param.getMultiplePay()) {
                return fail(JsonResultCode.INSTEAD_PAY_NOT_SET_PAY_WAY);
            }
            if (param.getMultiplePay() == null && !param.getSinglePay()){
                return fail(JsonResultCode.INSTEAD_PAY_NOT_SET_PAY_WAY);
            }


            //开启了单人代付
            if (param.getSinglePay()) {
                //未填写单人代付留言
                if (StringUtils.isBlank(param.getOrderUserMessageSingle()) ||
                    StringUtils.isBlank(param.getInsteadPayMessageSingle())) {
                    return fail(JsonResultCode.INSTEAD_PAY_NOT_SET_SINGLE_PAY_MESSAGE);
                }
                //单人留言长度超过20
                if (param.getOrderUserMessageSingle().length() > messageLength ||
                    param.getInsteadPayMessageSingle().length() > messageLength) {
                    return fail(JsonResultCode.INSTEAD_PAY_SINGLE_PAY_MESSAGE_TOO_LONG);
                }
            }

            //开启了多人代付
            if (param.getMultiplePay()) {
                //未填写多人代付留言
                if (StringUtils.isBlank(param.getOrderUserMessageMultiple()) ||
                    StringUtils.isBlank(param.getInsteadPayMessageMultiple())) {
                    return fail(JsonResultCode.INSTEAD_PAY_NOT_SET_MULTIPLE_PAY_MESSAGE);
                }
                //多人留言长度超过20
                if (param.getOrderUserMessageMultiple().length() > messageLength ||
                    param.getInsteadPayMessageMultiple().length() > messageLength) {
                    return fail(JsonResultCode.INSTEAD_PAY_MULTIPLE_PAY_MESSAGE_TOO_LONG);
                }
                //多人代付金额比中文需要3个
                if (param.getPayRatioText() == null || param.getPayRatioText().size() != ratioLen) {
                    return fail(JsonResultCode.INSTEAD_PAY_NEED_AT_LEAST_THREE_PAY_RATIO);
                }
                //多人代付金额比数字需要2个
                if (param.getPayRatioNumber() == null || param.getPayRatioNumber().size() != ratioNumLen) {
                    return fail(JsonResultCode.INSTEAD_PAY_NEED_AT_LEAST_TWO_DOUBLE_PAY_RATIO);
                }
                //检查用户设置的金额比 范围
                int ratioNum1 = param.getPayRatioNumber().get(0);
                int ratioNum2 = param.getPayRatioNumber().get(1);
                int ratioMax = 100;
                if (ratioNum1 > ratioMax || ratioNum1 < 0 || ratioNum2 > ratioMax || ratioNum2 < 0) {
                    return fail(JsonResultCode.INSTEAD_PAY_VALUE_OVER_RANGE);
                }

            }

            if (param.getSinglePay() && param.getMultiplePay()) {
                param.setInsteadPayWay(InsteadPay.TWO_WAY);
            } else {
                param.setInsteadPayWay(InsteadPay.ONE_WAY);
            }
        } else {
            param.setInsteadPayWay(InsteadPay.NOT_SET);
        }

        shop().config.insteadPayConfigService.setInsteadPayConfig(param);
        return success();
    }

    /**
     * 获取好友代付配置项
     *
     * @return 代付配置
     */
    @RequestMapping("/api/admin/market/instead/pay/get")
    public JsonResult getInsteadPayConfig() {
        InsteadPay insteadPayConfig = shop().config.insteadPayConfigService.getInsteadPayConfig();
        return success(insteadPayConfig);
    }
}

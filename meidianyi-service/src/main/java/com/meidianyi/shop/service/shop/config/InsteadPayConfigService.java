package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.insteadpay.InsteadPay;
import org.springframework.stereotype.Service;

/**
 * 好友代付配置类
 *
 * @author 李晓冰
 * @date 2019年08月19日
 */
@Service
public class InsteadPayConfigService extends BaseShopConfigService {
    /**
     * 好友代付配置项key alue是json格式
     */
    public static String K_INSTEAD_PAY = "instead_pay";

    public void setInsteadPayConfig(InsteadPay insteadPayConfig) {
        String jsonStr = Util.toJson(insteadPayConfig);
        set(K_INSTEAD_PAY, jsonStr);
    }

    public InsteadPay getInsteadPayConfig() {
        InsteadPay insteadPay = null;
        String s = get(K_INSTEAD_PAY);
        if (s == null) {
            insteadPay = new InsteadPay();
            insteadPay.setStatus(false);
        } else {
            insteadPay = Util.parseJson(s, InsteadPay.class);
        }

        return insteadPay;
    }

}

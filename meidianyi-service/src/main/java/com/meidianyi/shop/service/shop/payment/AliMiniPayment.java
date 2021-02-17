package com.meidianyi.shop.service.shop.payment;

import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liufei
 * @date 10/23/19
 * 支付宝小程序支付
 */
@Service
public class AliMiniPayment extends ShopBaseService {
    /**
     * The Domain config.域名配置
     */
    @Autowired
    public DomainConfig domainConfig;

    /**
     * Create trade.todo 支付宝创建订单
     *
     * @param goodsName the goods name 商品名称
     * @param orderSn   the order sn 商家订单号
     * @param amount    the amount 单位元
     * @param buyerId   the buyer id 买家的支付宝唯一用户号（2088开头的16位纯数字）
     * @param notifyUrl the notify url 通知URL
     * @return the string 正确返回交易号
     */
    public String createTrade(String goodsName, String orderSn, String amount, String buyerId, String notifyUrl) {
        return null;
    }

    /**
     * Gets ali pay config.todo 获取阿里支付配置
     */
    public void getAliPayConfig() {
        String aliMiniPayNotifyUrl = domainConfig.mainUrl("/ali_mini/payment/notify/" + getShopId(), "http");
        // todo createTrade()
    }
}

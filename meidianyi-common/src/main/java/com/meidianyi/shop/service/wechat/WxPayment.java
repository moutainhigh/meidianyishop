package com.meidianyi.shop.service.wechat;

import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

/**
 * @author lixinguo
 */
public class WxPayment extends WxPayServiceImpl {
    public String shopName;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

}

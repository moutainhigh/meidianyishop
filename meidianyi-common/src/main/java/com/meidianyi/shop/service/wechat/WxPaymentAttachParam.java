package com.meidianyi.shop.service.wechat;

import lombok.Data;

/**
 * 微信支付附加信息
 * @author 孔德成
 * @date 2020/7/13 13:49
 */
@Data
public class WxPaymentAttachParam {

    /**
     * 是否是购物车订单 1是 0不是
     */
    private Byte isCart;

}

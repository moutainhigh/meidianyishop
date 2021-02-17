package com.meidianyi.shop.service.pojo.shop.order.pay.record;

import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-24 16:58
 **/

@Data
public class CheckVerifyCodeParam {

    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 核销码
     */
    private String verifyCode;

}

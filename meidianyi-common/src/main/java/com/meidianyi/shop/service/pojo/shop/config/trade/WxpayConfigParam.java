package com.meidianyi.shop.service.pojo.shop.config.trade;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author liufei
 * @date 2019/7/9
 */
@Data
public class WxpayConfigParam {
    @NotBlank
    private String appId;
    @NotBlank
    private String payMchId;
    @NotBlank
    private String payKey;

    /**
     * 0普通微信支付用户，1微信支付子商户
     **/
    private Byte isSubMerchant;

    /**
     * 支付证书
     */
    private String payCertContent;

    /**
     * 支付私钥
     */
    private String payKeyContent;
}

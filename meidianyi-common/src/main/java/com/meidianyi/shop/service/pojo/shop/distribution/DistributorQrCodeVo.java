package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * 分销员微信二维码出参
 * @author panjing
 * @data 2020/8/7 17:43
 */
@Data
public class DistributorQrCodeVo {

    public static final Byte QR_CODE_URL = 1;
    public static final Byte BASE64 = 2;

    /**
     * type=1时 extend为微信二维码海报url
     * type=2时 extend为微信二维码海报base64
     */
    private String extend;

    /**
     * 类型
     * type=1 微信二维码海报url
     * type=2 微信二维码海报base64
     */
    private Byte type;
}

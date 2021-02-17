package com.meidianyi.shop.service.pojo.shop.sms.account;

import com.meidianyi.shop.config.SmsApiConfig;
import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/8/5 9:03
 */
@Data
public class SmsAccountInfoParam {

    private String sid;
    private Integer version = SmsApiConfig.PRODUCT_WPB_WX;
}

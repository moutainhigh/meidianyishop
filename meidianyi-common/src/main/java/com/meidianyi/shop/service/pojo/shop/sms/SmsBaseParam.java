package com.meidianyi.shop.service.pojo.shop.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.config.SmsApiConfig;
import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/7/23 14:54
 */

@Data
public class SmsBaseParam {
    /**
     * 账号
     */
    private String sid;
    /**
     * 4 开放平台对应小程序
     */
    private Integer product = 4;
    /**
     * 通道，例：mxt，taobao
     */
    private String channel = SmsApiConfig.CHANNEL_DEFAULT;
    /**
     * -3验证码、-1行业、营销-2  默认验证码
     */
    private String ext =SmsApiConfig.EXT_CHECK_CODE;
    private Integer type =0;
    /**
     * 电话 ,分隔
     */
    private String mobiles;
    /**
     * 用户id 0系统
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer userId =0;
    /**
     * 短信内容
     */
    private String content;

}

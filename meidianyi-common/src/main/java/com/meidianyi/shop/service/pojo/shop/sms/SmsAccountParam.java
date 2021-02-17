package com.meidianyi.shop.service.pojo.shop.sms;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/7/24 16:47
 */
@Data
public class SmsAccountParam {
    /**
     * 卖家账号
     */
    @NotNull
    private String sid;
    /**
     * 开放平台对应小程序
     */
    private Integer version = 4;
    /**
     * 账号名称
     */
    private String companyName;
}

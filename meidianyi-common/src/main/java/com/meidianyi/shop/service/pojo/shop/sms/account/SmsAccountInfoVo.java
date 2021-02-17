package com.meidianyi.shop.service.pojo.shop.sms.account;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/8/5 9:08
 */
@Data
public class SmsAccountInfoVo {

    private Integer code;
    private String passId;
    private String balance;
    private String userId;
    private String companyName;
    private String recId;
    private String appId;
    private String smsNum;
    private String rechargeUrl;
    private String smsAccount;
}

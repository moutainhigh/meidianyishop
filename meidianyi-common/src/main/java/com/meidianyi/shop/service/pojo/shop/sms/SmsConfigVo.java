package com.meidianyi.shop.service.pojo.shop.sms;

import com.meidianyi.shop.service.pojo.shop.sms.account.SmsAccountInfoVo;
import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-17 14:24
 **/

@Data
public class SmsConfigVo {
    /**
     * 用户每天发送验证码
     */
    private Integer userCheckCodeNum;
    /**
     * 患者每天发送验证码
     */
    private Integer patientCheckCodeNum;
    /**
     * 营销短信
     */
    private Integer marketingNum;
    /**
     * 行业短信
     */
    private Integer industryNum;
    /**
     * 短信账户信息
     */
    private SmsAccountInfoVo smsAccountInfo;
}

package com.meidianyi.shop.service.pojo.shop.sms.template;

import lombok.Data;

/**
 * 短信模板
 * @author 孔德成
 * @date 2020/7/24 10:43
 */
public class SmsTemplate {


    /**
     * 患者的电话校验
     */
    public static final String PATIENT_CHECK_MOBILE = "【%s】验证码%s，10分钟内有效。验证码提供给他人可能导致账号被盗，请勿泄漏，谨防被骗";

    public static final String DOCTOR_CHECK_MOBILE = "【%s】医师认证验证码%s，10分钟内有效。验证码提供给他人可能导致账号被盗，请勿泄漏，谨防被骗";

    public static final String SALESCLERK_CHECK_MOBILE = "【%s】店员认证验证码%s，10分钟内有效。验证码提供给他人可能导致账号被盗，请勿泄漏，谨防被骗";

    /**
     * 测试
     */
    public static final String SMS_DEMO = "%s短信测试成功";

}

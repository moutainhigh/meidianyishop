package com.meidianyi.shop.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 孔德成
 * @date 2020/7/23 13:48
 */
@Configuration
@Getter
public class SmsApiConfig {
    /**
     * private static final String SMS_URL = "https://sms.huice.com:80/sms/platform/api";
     */
    @Value(value = "${sms.huice.url}")
    protected String smsUrl;
    @Value(value = "${sms.huice.charge.url}")
    private String chargeUrl;
    @Value(value = "${sms.huice.app.key}")
    private  String appKey ;
    @Value(value = "${sms.huice.app.secret}")
    private  String appSecret ;

    //*********接口方法*********//
    /**
     * 创建账号
     */
    public static final String METHOD_CREATE_ACCOUNT = "wdt.sms.account.add";
    /**
     * 发送短信
     */
    public static final String METHOD_SMS_SEND = "wdt.sms.send.send";

    /**
     * 查询账号信息
     */
    public static final String METHOD_SMS_CHECK = "wdt.sms.send.check";
    /**
     * 充值记录
     */
    public static final String METHOD_SMS_RECHARGE_RECORD = "wdt.sms.recharge.recharge_record";
    //************redis_key********//
    /**
     * 短信验证码
     */
    public static final String REDIS_KEY_SMS_CHECK_PATIENT_MOBILE="sms:%s:check_patient_mobile:%s:%s";
    /**
     * 医师短信验证码
     */
    public static final String REDIS_KEY_SMS_CHECK_DOCTOR_MOBILE = "sms:%s:check_doctor_mobile:%s:%s";
    /**
     * 店员短信验证码
     */
    public static final String REDIS_KEY_SMS_CHECK_SALESCLERK_MOBILE = "sms:%s:check_salesclerk_mobile:%s:%s";
    /**
     * 患者每天发送短信数量校验
     */
    public static final String REDIS_KEY_SMS_USER_CHECK_NUM = "sms:shopId_%s:patientId_%s:sms_num";

    /**
     * 通道默认""
     */
    public static final String CHANNEL_DEFAULT = "";
    /**
     * 微铺宝-小程序
     */
    public static final Integer PRODUCT_WPB_WX = 4;
    //********业务方式*******//
    /**
     * 验证码
     */
    public static final String EXT_CHECK_CODE  = "checkcode";
    /**
     * 营销
     */
    public static final String EXT_MARKET  = "-2";
    /**
     * 商业
     */
    public static final String EXT_BUSINESS  = "-1";




    public static final String POST = "POST";
    public static final String CHARSET = "UTF-8";

}

package com.meidianyi.shop.service.pojo.shop.sms;

import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-17 11:47
 **/
@Data
public class SmsConfigParam {

    /**
     * 店铺id
     */
    private Integer shopId;
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

}

package com.meidianyi.shop.service.pojo.shop.sms;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/7/23 15:51
 */
@Data
public class SendSmsParam {
    /**
     * 电话 ,分隔
     */
    private String mobiles;
    /**
     * 短信内容
     */
    private String content;
}

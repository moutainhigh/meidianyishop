package com.meidianyi.shop.service.pojo.shop.sms;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/7/24 13:41
 */
@Data
public class SmsResult {
    /**
     * 0成功
     */
    private String code;
    private String msg;
    private Object data;

    @Data
    public class SmsMobile{
        /**
         * 电话
         */
        private String phone;
        private Integer messageId;
    }

}

package com.meidianyi.shop.service.pojo.shop.sms.base;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/7/24 16:56
 */
@Data
public class SmsBaseRequest {
    /**
     * 分配的appKey
     */
    private String appKey;
    /**
     * 时间戳，单位为 秒
     */
    private Long timestamp;
    /**
     * 消息体 json格式
     */
    private String sms;
    /**
     * 方法名
     */
    private String apiMethod;
}

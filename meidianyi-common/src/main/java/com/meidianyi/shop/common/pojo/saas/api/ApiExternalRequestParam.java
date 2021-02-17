package com.meidianyi.shop.common.pojo.saas.api;

import lombok.Data;

/**
 * 对接外部系统请求参数包装类
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Data
public class ApiExternalRequestParam {

    /**
     * 数据库b2c_app表固定值
     */
    private String appId;
    private String appSecret;

    /**
     * 店铺配置对接his时动态生成
     * sessionKey最后一个s字符后面的字符串表示店铺id
     */
    private String sessionKey;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 业务请求参数对象json字符串
     */
    private String content;

    /**
     * 当前时间点，秒为单位的整数字符串
     */
    private String curSecond;

    /**
     * 签名，校验规则
     */
    private String sign;
}

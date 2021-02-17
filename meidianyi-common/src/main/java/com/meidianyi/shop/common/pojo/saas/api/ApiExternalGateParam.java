package com.meidianyi.shop.common.pojo.saas.api;

import lombok.Data;

/**
 * 小程序-对接Pos,Erp服务接口入参
 * 目前接口参数和php完全一致，避免pos、erp进行二次开发
 * @author 李晓冰
 * @date 2020年03月30日
 */
@Data
public class ApiExternalGateParam {
    /**
     * 固定值
     * 1> ERP服务:
     *         appId: 200000  appSecret:36a23c125e6fd10420eb3b6ed48ee057
     * 2> POS服务
     *         appId: 200001  appSecret:b141fbc4bd328822e955aeed011cfc85
     * 3> CRM服务
     *         appId: 200002  appSecret:2b7212759813c4c03ccc316f8cb1c654
     * 4> HIS服务
     *         appId: 200003  appSecret:7472c1efe6ca4215af1353317938b50a
     * 5> STORE服务
     *         appId: 200004  appSecret:8482c1efe6ca4215af1353317938a51c
     * */
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

    /**店铺id，从sessionKey中解析得到*/
    private Integer shopId;
    private String requestId;
}

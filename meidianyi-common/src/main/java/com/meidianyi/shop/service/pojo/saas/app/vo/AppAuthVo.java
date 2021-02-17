package com.meidianyi.shop.service.pojo.saas.app.vo;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Data
public class AppAuthVo {
    private Integer id;
    private Integer sysId;
    private Integer shopId;
    private String appId;
    private String appSecret;
    private String sessionKey;
    private String requestLocation;
    private Byte requestProtocol;
    private Byte status;
}

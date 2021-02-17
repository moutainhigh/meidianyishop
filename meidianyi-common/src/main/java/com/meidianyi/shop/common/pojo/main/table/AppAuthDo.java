package com.meidianyi.shop.common.pojo.main.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 应用授权表
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Data
public class AppAuthDo {
    private Integer   id;
    private Integer   sysId;
    private Integer   shopId;
    private String    appId;
    private String    sessionKey;
    private String    requestLocation;
    private Byte      requestProtocol;
    private Byte      status;
    private Timestamp createTime;
    private Timestamp updateTime;
}

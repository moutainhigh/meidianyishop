package com.meidianyi.shop.service.pojo.shop.anchor;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/9/7 9:29
 */
@Data
public class AnchorPointsListVo {

    private Integer   id;
    private String    event;
    private String    eventName;
    private Byte      eventType;
    private String    page;
    private String    module;
    private String    platform;
    private String    device;
    private Integer   storeId;
    private Integer   userId;
    private String    key;
    private String    value;
    private Timestamp updateTime;
    private Timestamp createTime;

}

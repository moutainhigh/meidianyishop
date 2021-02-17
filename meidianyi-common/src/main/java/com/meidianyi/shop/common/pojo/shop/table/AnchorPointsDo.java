package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 锚点
 * @author 孔德成
 * @date 2020/8/31 11:57
 */
@Data
public class AnchorPointsDo {

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

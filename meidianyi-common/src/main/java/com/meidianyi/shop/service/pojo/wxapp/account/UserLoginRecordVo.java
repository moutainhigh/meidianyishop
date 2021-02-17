package com.meidianyi.shop.service.pojo.wxapp.account;

import java.sql.Timestamp;

import lombok.Data;
/**
 * @author zhaojianqiang
 */
@Data
public class UserLoginRecordVo {
    private Long      id;
    private Integer   userId;
    private String    userIp;
    private Integer   count;
    private String    provinceCode;
    private String    province;
    private String    cityCode;
    private String    city;
    private String    districtCode;
    private String    district;
    private String    lat;
    private String    lng;
    private Timestamp createTime;
    private Timestamp updateTime;
}

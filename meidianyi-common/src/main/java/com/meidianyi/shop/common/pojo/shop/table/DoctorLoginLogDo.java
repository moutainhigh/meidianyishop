package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/9/16 14:02
 */
@Data
public class DoctorLoginLogDo {

    private Integer   id;
    private Integer   doctorId;
    private Integer   userId;
    private String    ip;
    private String    lat;
    private String    lng;
    private Timestamp updateTime;
    private Timestamp createTime;

}

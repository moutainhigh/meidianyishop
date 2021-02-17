package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/9/16
 **/
@Data
public class PharmacistDo {
    private Integer   id;
    private Byte      sex;
    private String    certificateCode;
    private String    professionalCode;
    private String    mobile;
    private Byte      status;
    private String    signature;
    private Byte      isDelete;
    private Timestamp createTime;
    private Timestamp updateTime;
}

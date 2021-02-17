package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/8/12 15:12
 */
@Data
public class DoctorCommentDo {

    private static final long serialVersionUID = -977132338;

    private Integer   id;
    private Integer   userId;
    private String    userName;
    private Integer   patientId;
    private String    patientName;
    private Integer   doctorId;
    private String    doctorCode;
    private String    doctorName;
    private Integer   orderId;
    private String    orderSn;
    private Integer   imSessionId;
    private Byte      stars;
    private Byte      isAnonymou;
    private String    tag;
    private String    commNote;
    private Integer   top;
    private Byte      auditStatus;
    private Byte      isDelete;
    private Timestamp createTime;
    private Timestamp updateTime;
}

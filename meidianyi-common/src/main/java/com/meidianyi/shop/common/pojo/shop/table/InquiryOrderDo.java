package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 */
@Data
@NoArgsConstructor
public class InquiryOrderDo extends ImSessionDo {

    private static final long serialVersionUID = 883131361;

    private Integer    orderId;
    private Integer    shopId;
    private String     orderSn;
    private Integer    userId;
    private Byte       orderStatus;
    private Integer    doctorId;
    private String     doctorName;
    private Integer    departmentId;
    private String     departmentName;
    private Integer    patientId;
    private String     patientMobile;
    private String     patientName;
    private Byte       patientSex;
    private Date patientBirthday;
    private String     patientIdentityCode;
    private Byte       patientIdentityType;
    private String     payCode;
    private String     payName;
    private String     paySn;
    private String     prepayId;
    private BigDecimal orderAmount;
    private Timestamp  payTime;
    private BigDecimal refundMoney;
    private Timestamp  limitTime;
    private Timestamp  cancelledTime;
    private Timestamp  finishedTime;
    private String     descriptionDisease;
    private String     imageUrl;
    private BigDecimal rebateProportion;
    private BigDecimal totalRebateMoney;
    private BigDecimal platformRebateProportion;
    private BigDecimal platformRebateMoney;
    private Byte       isDelete;
    private Byte       settlementFlag;
    private Timestamp  createTime;
    private Timestamp  updateTime;
}

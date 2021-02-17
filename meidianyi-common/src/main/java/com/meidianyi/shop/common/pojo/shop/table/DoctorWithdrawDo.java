package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 医生返利提现申请表
 * @author yangpengcheng
 * @date 2020/8/26
 **/
@Data
public class DoctorWithdrawDo {
    private Integer    id;
    private Integer    doctorId;
    private Byte       type;
    private Byte       status;
    private String     orderSn;
    private String     withdrawUserNum;
    private String     withdrawNum;
    private BigDecimal withdrawCash;
    private BigDecimal withdraw;
    private String     desc;
    private String     refuseDesc;
    private Timestamp checkTime;
    private Timestamp  refuseTime;
    private Timestamp  billingTime;
    private Timestamp  failTime;
    private Timestamp  descTime;
    private String     withdrawSource;
    private String     realName;
    private Timestamp  createTime;
    private Timestamp  updateTime;
}


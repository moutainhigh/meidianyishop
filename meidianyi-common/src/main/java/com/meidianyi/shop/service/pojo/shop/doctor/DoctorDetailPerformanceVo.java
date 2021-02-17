package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/9/23
 **/
@Data
public class DoctorDetailPerformanceVo {
    private Integer    doctorId;
    private Integer    consultationNumber;
    private BigDecimal inquiryMoney=BigDecimal.ZERO;
    private Integer    inquiryNumber;
    private BigDecimal prescriptionMoney=BigDecimal.ZERO;
    private Integer    prescriptionNum;
    private String name;
    private Timestamp lastTime;
    private Integer loginDays;
    private BigDecimal loginRate;
    private BigDecimal consumeMoney=BigDecimal.ZERO;

}

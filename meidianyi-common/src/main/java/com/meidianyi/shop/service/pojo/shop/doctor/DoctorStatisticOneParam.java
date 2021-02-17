package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chenjie
 * @date 2020年09月09日
 */
@Data
public class DoctorStatisticOneParam {
    private Integer    consultationNumber;
    private BigDecimal inquiryMoney;
    private Integer    inquiryNumber;
    private BigDecimal prescriptionMoney;
    private Integer    prescriptionNum;
    private BigDecimal consumeMoney;
}

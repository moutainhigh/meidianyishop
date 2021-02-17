package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chenjie
 * @date 2020年09月17日
 */
@Data
public class DoctorStatisticMinMaxVo {
    private Integer minConsultation;
    private Integer maxConsultation;
    private BigDecimal minInquiryMoney;
    private BigDecimal maxInquiryMoney;
}

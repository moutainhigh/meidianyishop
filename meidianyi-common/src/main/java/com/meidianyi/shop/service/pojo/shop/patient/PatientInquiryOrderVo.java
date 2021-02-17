package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-11 17:54
 **/

@Data
public class PatientInquiryOrderVo {

    private Integer inquiryCount;

    private BigDecimal totalAmount;

}

package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-21 17:40
 **/
@Data
public class DoctorQueryInquiryVo {

    private String patientName;

    private Integer patientId;

    private String orderSn;

    private String inqTime;

    private BigDecimal inquiryCost;
}

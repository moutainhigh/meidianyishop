package com.meidianyi.shop.service.pojo.shop.patient;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-09 11:29
 **/
@Data
public class PatientQueryDoctorVo {
    /**
     * 医师姓名
     */
    private String doctorName;
    /**
     * 科室名称
     */
    private String departmentName;
    /**
     * 处方消费金额
     */
    private BigDecimal prescriptionConsumptionAmount = new BigDecimal(0);
    /**
     * 问诊消费金额
     */
    private BigDecimal inquiryConsumptionAmount = new BigDecimal(0);
    /**
     * 消费金额
     */
    private BigDecimal consumptionAmount = new BigDecimal(0);
    /**
     * 处方数量
     */
    private Integer prescriptionNumber = 0;
    /**
     * 问诊数量
     */
    private Integer inquiryNumber = 0;
    /**
     * 医师code
     */
    private String doctorCode;
    /**
     * 医师id
     */
    private Integer doctorId;
}

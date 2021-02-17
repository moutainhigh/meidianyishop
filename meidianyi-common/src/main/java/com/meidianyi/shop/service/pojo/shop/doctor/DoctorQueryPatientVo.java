package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 赵晓东
 * @description 医师查询关联患者列表出参
 * @create 2020-09-21 15:34
 **/

@Data
public class DoctorQueryPatientVo {
    /**
     * 患者姓名
     */
    private String patientName;
    /**
     * 患者昵称
     */
    private String patientNickName;
    /**
     * 患者id
     */
    private String patientId;
    /**
     * 处方数量
     */
    private Integer prescriptionNum;
    /**
     * 问诊数量
     */
    private Integer inquiryNum;
    /**
     * 药品消费金额
     */
    private BigDecimal medicineCost;
    /**
     * 问诊消费金额
     */
    private BigDecimal inquiryCost;
}

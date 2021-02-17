package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-21 18:04
 **/
@Data
public class DoctorQueryPrescriptionVo {

    private String prescriptionCode;

    private Byte treatmentType;

    private String patientName;

    private String departmentName;

    private List<String> goodsNames;

    private BigDecimal totalPrice = new BigDecimal(0);

    private String orderSn;

    private Timestamp treatmentTime;

}

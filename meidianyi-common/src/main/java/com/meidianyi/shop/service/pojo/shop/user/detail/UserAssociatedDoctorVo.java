package com.meidianyi.shop.service.pojo.shop.user.detail;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-15 10:19
 **/
@Data
public class UserAssociatedDoctorVo {

    private String doctorName;

    private String departmentName;

    private Integer prescriptionNum;

    private Integer inquiryNum;

    private Boolean isFav = true;

    private BigDecimal totalCost;

    private String doctorCode;

    private Integer doctorId;

}

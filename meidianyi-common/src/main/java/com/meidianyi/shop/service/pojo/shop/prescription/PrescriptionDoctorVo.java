package com.meidianyi.shop.service.pojo.shop.prescription;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-15 16:00
 **/
@Data
public class PrescriptionDoctorVo {
    /**
     * 订单总数
     */
    private Integer totalCount = 0;
    /**
     * 订单总金额
     */
    private BigDecimal totalPrice = new BigDecimal(0);
}

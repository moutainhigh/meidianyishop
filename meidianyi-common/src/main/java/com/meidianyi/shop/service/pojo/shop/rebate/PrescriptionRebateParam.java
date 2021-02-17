package com.meidianyi.shop.service.pojo.shop.rebate;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 * @date 2020/8/26
 **/
@Data
public class PrescriptionRebateParam {
    private String prescriptionCode;
    private Integer doctorId;
    private BigDecimal totalMoney;
    private BigDecimal canCalculateMoney;
    private BigDecimal totalRebateMoney;
    private BigDecimal realRebateMoney;
    private BigDecimal platformRebateMoney;
    private BigDecimal platformRealRebateMoney;
    /**
     * 返利状态
     */
    private Byte status;
}

package com.meidianyi.shop.service.pojo.shop.rebate;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 * @date 2020/8/24
 **/
@Data
public class InquiryOrderRebateParam {
    /**
     * 问诊订单号
     */
    private String orderSn;
    /**
     * 医师id
     */
    private Integer doctorId;
    /**
     * 问诊金额
     */
    private BigDecimal totalMoney;
    /**
     * 返利总金额
     */
    private BigDecimal totalRebateMoney;
    /**
     * 平台返利金额
     */
    private BigDecimal platformRebateMoney;

    /**
     * 返利状态
     */
    private Byte status;
}

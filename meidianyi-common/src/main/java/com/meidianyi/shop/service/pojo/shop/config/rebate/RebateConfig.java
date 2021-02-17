package com.meidianyi.shop.service.pojo.shop.config.rebate;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 * @date 2020/8/24
 **/
@Data
@NoArgsConstructor
public class RebateConfig {
    /**
     * 开关
     */
    private Byte status;
    /**
     *自动审核开关
     */
    private Byte isAutomaticAudit;
    /**
     * 最小提现金额
     */
    private BigDecimal withdrawCashMix;
    /**
     * 最大提现金额
     */
    private BigDecimal withdrawCashMax;
    /**
     * 商品订单分成比例
     */
    private BigDecimal goodsSharingProportion;
    /**
     *处方药医生佣金比例
     */
    private BigDecimal rxMedicalDoctorProportion;
    /**
     *处方药平台佣金比例
     */
    private BigDecimal rxMedicalPlatformProportion;
    /**
     *非处方药医生佣金比例
     */
    private BigDecimal noRxMedicalDoctorProportion;
    /**
     *非处方药平台佣金比例
     */
    private BigDecimal noRxMedicalPlatformProportion;
    /**
     *咨询医生佣金比例
     */
    private BigDecimal inquiryOrderDoctorProportion;
    /**
     *咨询平台佣金比例
     */
    private BigDecimal inquiryOrderPlatformProportion;


}

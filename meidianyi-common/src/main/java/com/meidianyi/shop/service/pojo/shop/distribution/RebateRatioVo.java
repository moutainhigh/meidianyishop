package com.meidianyi.shop.service.pojo.shop.distribution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author 常乐
 * @Date 2020-03-12
 */
@Data
public class RebateRatioVo {
    /**
     * 返利策略id
     */
    private Integer rebateId;
    /**
     * 是否自购返利 0：关闭，1：开启
     */
    private Byte selfPurchase;
    /**
     * 首单返利比例
     */
    private Double firstRatio;
    /**
     * 直接返利比例
     */
    private Double fanliRatio;
    /**
     * 间接返利比例
     */
    private Double rebateRatio;
    /**
     * 首单返利开关
     */
    private Byte firstRebate;
    /**
     * 成本价保护
     */
    private BigDecimal costPriceProtect;
    /**最高直接返利金额*/
    private BigDecimal highDirectlyRebatePrice;
    /**最高间接返利金额*/
    private BigDecimal highIndirectRebatePrice;
    /**最高首单返利金额*/
    private BigDecimal highFirstRebatePrice;
    /**佣金计算方式 0:实际支付金额*返利比例；1：实际利润*返利比例*/
    @JsonIgnore
    private Byte strategyType;
}

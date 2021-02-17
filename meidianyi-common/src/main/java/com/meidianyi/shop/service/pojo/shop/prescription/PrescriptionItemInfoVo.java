package com.meidianyi.shop.service.pojo.shop.prescription;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2020/7/8 8:50
 */
@Data
public class PrescriptionItemInfoVo {

    /**
     * 通用名
     */
    private String    goodsCommonName;
    /**
     * 规格系数
     */
    private String    goodsQualityRatio;
    /**
     * 药品使用方式说明
     */
    private String    goodsUseMemo;

    /**
     * 生产企业
     */
    private String    goodsProductionEnterprise;

    /**
     * 单次用量
     */
        private Double perTimeNum;

    /**
     * 数量单位
     */
    private String perTimeUnit;

    /**
     * 单次剂量
     */
    private Double    perTimeDosage;
    /**
     * 计量单位
     */
    private String    perTimeDosageUnit;
    /**
     * 频次
     */
    private Double    frequency;
    /**
     * 总取药数量
     */
    private Double   dragSumNum;
    /**
     * 总取药单位
     */
    private String    dragSumUnit;

    /**
     * 药品总价格
     */
    private BigDecimal medicinePrice;

    /**
     * 药品Id
     */
    private Integer goodsId;
    /**
     * 规格id
     */
    private Integer prdId;
}

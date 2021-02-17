package com.meidianyi.shop.service.pojo.shop.medical.goods.base;

import lombok.Data;

/**
 * 药品信息基类
 * @author 李晓冰
 * @date 2020年07月16日
 */
@Data
public class GoodsMedicalBaseInfo {
    private String goodsCommonName;
    private String goodsCode;
    private String goodsBarCode;
    private String goodsAliasName;
    private String goodsQualityRatio;
    private Byte isRx;
    private Byte medicalType;
    private Byte insuranceFlag;
    private String insuranceCode;
    private String insuranceDatabaseName;
    private String goodsBasicUnit;
    private String goodsPackageUnit;
    private Double goodsUnitConvertFactor;
    private Double goodsEquivalentQuantity;
    private String goodsEquivalentUnit;
    private String goodsApprovalNumber;
    private String goodsProductionEnterprise;
    private String goodsMedicalInstruction;

    /**我方程序计算生产的字段*/
    private Integer medicalId;
    /**名称+规格系数+药企组成的唯一值*/
    private String goodsKeyComposedByNameQualityEnterprise;
    /**药房商品唯一值*/
    private String storeCode;
}

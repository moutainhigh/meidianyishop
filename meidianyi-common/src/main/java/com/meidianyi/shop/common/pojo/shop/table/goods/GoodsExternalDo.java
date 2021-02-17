package com.meidianyi.shop.common.pojo.shop.table.goods;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2020年10月13日
 */
@Data
public class GoodsExternalDo {
    private Integer id;
    private BigDecimal goodsPrice;
    private Integer goodsNumber;
    private String goodsCode;
    private String goodsBarCode;
    private String goodsCommonName;
    private String goodsAliasName;
    private String goodsQualityRatio;
    private Byte isMedical;
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
    private Byte isDelete;
    private Byte isMatch;
    private Integer state;
    private Timestamp createTime;
    private Timestamp updateTime;
}

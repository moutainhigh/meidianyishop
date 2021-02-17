package com.meidianyi.shop.service.pojo.shop.medical.goods.param;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 已匹配带插入的药品信息数据
 * @author 李晓冰
 * @date 2020年10月14日
 */
@Data
public class ExternalMatchedGoodsParam {
    private BigDecimal hisPrice;
    private BigDecimal storePrice;
    private Integer goodsNumber;
    private String hisGoodsCode;
    private String storeGoodsCode;
    private String goodsBarCode;
    private String goodsCommonName;
    private String goodsAliasName;
    private String goodsQualityRatio;
    private Byte source;
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
    private Integer fromHisId;
    /** 这个是药房中间表的id，不是门店id哦 */
    private Integer fromStoreId;

    /** 以下计算得到 */
    private String medicalKey;
}

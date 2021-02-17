package com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chenjie
 * @date 2020年09月17日
 */
@Data
public class GoodsAnalysisListVo {
    private Integer goodsId;
    private String goodsName;
    private String goodsQualityRatio;
    private String goodsProductionEnterprise;
    private String goodsLabels;
    private Integer pv;
    private Integer uv;
    private Integer addCartUserNum;
    private Integer collectionIncrementNum;
    private Integer saleNum;
    private BigDecimal saleMoney;
}

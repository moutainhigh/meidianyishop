package com.meidianyi.shop.service.pojo.shop.medical.goods.bo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.medical.goods.base.GoodsMedicalBaseInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * his对接药品信息类
 * @author 李晓冰
 * @date 2020年07月16日
 */
@Getter
@Setter
public class GoodsMedicalExternalRequestItemBo extends GoodsMedicalBaseInfo {
    private Integer goodsId;
    private BigDecimal goodsPrice;
    @JsonDeserialize(using = RequestGoodsNumberDeserialize.class)
    private Integer goodsNumber;
    private Integer state;
    private Byte isMedical = MedicalGoodsConstant.GOODS_IS_MEDICAL;
    private Byte source;
    private Byte hisStatus;
    private Byte storeStatus;
    private Integer lastUpdateTime;
    /**临时存储药房价格使用*/
    private BigDecimal storePrice;
}

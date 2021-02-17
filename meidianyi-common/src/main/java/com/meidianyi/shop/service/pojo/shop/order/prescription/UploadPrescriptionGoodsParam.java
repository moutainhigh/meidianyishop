package com.meidianyi.shop.service.pojo.shop.order.prescription;

import lombok.Data;

/**
 * 处方药品
 * @author 孔德成
 * @date 2020/7/16 15:33
 */
@Data
public class UploadPrescriptionGoodsParam {

    /**
     * 药品唯一编码
     */
    private String goodsCode;
    /**
     * 通用名
     */
    private String goodsCommonName;
    /**
     * 别名
     */
    private String goodsAliasName;
    /**
     * 药品规格系数
     */
    private String goodsQualityRatio;
    /**
     * 订单药品数量
     */
    private Integer orderNumber;
    /**
     * 是否是处方药 1是 0不是
     */
    private Byte isRx;
}

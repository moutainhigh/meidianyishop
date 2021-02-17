package com.meidianyi.shop.service.pojo.shop.store.goods;

import lombok.Data;

/**
 * 药品校验时需要使用的基础信息
 * @author 李晓冰
 * @date 2020年09月03日
 */
@Data
public class StoreGoodsBaseCheckInfo {
    private String goodsCommonName;
    private String goodsQualityRatio;
    private String goodsApprovalNumber;
    private String goodsProductionEnterprise;
    private String goodsNumber;
    private Integer productId;
}

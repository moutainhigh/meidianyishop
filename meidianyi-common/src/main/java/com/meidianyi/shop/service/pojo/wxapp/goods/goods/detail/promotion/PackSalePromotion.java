package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import com.meidianyi.shop.common.foundation.data.BaseConstant;

/**
 * 打包一口价促销活动
 * @author 李晓冰
 * @date 2020年04月26日
 */
@Getter
@Setter
public class PackSalePromotion extends PromotionBase {
    public PackSalePromotion() { super.promotionType = BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE; }
    /**打包类型 0金额 1折扣*/
    private Byte packageType;
    /**折扣或金额数*/
    private BigDecimal priceOrDiscount;
    /**商品打包件数*/
    private Integer goodsCount;
}

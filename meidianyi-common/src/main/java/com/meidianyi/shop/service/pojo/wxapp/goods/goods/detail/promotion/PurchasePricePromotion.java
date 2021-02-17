package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import com.meidianyi.shop.common.foundation.data.BaseConstant;

/**
 * 加价购促销信息
 * @author 李晓冰
 * @date 2020年01月14日
 */
@Getter
@Setter
public class PurchasePricePromotion extends PromotionBase {

    public PurchasePricePromotion() {
        super.promotionType = BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE;
    }

    /**加价购规则*/
    List<PurchasePriceRule> purchasePriceRules;

    @Data
     public static class PurchasePriceRule{
        /**满金额数*/
         private BigDecimal fullPrice;
         /**加价金额数*/
         private BigDecimal purchasePrice;
     }
}

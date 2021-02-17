package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.meidianyi.shop.common.foundation.data.BaseConstant;

/**
 * 满折满减活动策略营销
 * @author 李晓冰
 * @date 2020年01月13日
 */
@Data
public class FullReductionPromotion extends PromotionBase{

    public FullReductionPromotion() {
        super.promotionType = BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION;
    }

    /**活动类型 1每满减 2满减 3满折 4仅第X件打折*/
    private Byte type;
    /**是否会员专享*/
    private Boolean isExclusive;

    private List<FullReductionRule> rules;

    /**
     * 满折满减活动规则，
     * 通过判断fullMoney 是否为0来判断是满金额减还是满件数减
     */
    @Data
    public static class FullReductionRule{
       /**满多少金额，活动中指定使用满金额策略时使用*/
       private BigDecimal fullMoney;
       /**满几件或第几件（第X件打折），活动中指定使用满件数策略时使用*/
       private Integer amount;

       /**减多少钱，活动中指定使用满金额策略时使用*/
       private BigDecimal reduceMoney;
       /**折扣，活动中指定使用满件数策略时使用*/
       private BigDecimal discount;
   }
}

package com.meidianyi.shop.service.pojo.wxapp.cart.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/11/13 10:15
 */
@Getter
@Setter
public class CartActivityInfo extends GoodsActivityBaseMp {

    /**
     * 活动状态  0失效 1生效
     */
    private Byte status = CartConstant.ACTIVITY_STATUS_VALID;
    /**
     * 显示位置 0显示,1标签,2上方 3选择内容
     */
    private Byte type ;

    /**
     * 商品价格
     */
    private BigDecimal productPrice;
    /**
     * 限购数量最新
     */
    private Integer limitMinNum;
    /**
     * 限购数量最大
     */
    private Integer limitMaxNum;
    /**
     * 限制类型 1禁止超限购买，0超限全部恢复原价
     */
    private Byte limitNumberType;
    /**
     * 满折满减活动
     */
    private FullReduction fullReduction;

    /**
     * 加价购
     */
    private PurchasePrice purchasePrice;

    /**
     * 满折满减活动
     */
    @Setter
    @Getter
    public static  class FullReduction{
        /**活动类型 1每满减 2满减 3满折 4仅第X件打折*/
        @JsonProperty("FullReductiontype")
        private Byte fullReductionType;
        /**是否会员专享*/
        private Boolean isExclusive;
        /**
         * 当前活动信息
         */
        private String condition;
        /**
         *  1满金额 2满件数
         */
        private Byte rulesType;
        /**
         * 当前规则
         */
        private FullReductionRule rule;
        /**
         * 规则
         */
        private List<FullReductionRule> rules;

    }

    /**
     * 满折满减活动规则，
     * 通过判断fullMoney 是否为0来判断是满金额减还是满件数减
     */
    @Data
    public static class FullReductionRule{
        /**
         * 活动名称
         */
        private String name;
        /**
         * 使用 1生效 2不生效
         */
        private Byte userd;
        /**满多少金额，活动中指定使用满金额策略时使用*/
        private BigDecimal fullMoney;
        /**满几件或第几件（第X件打折），活动中指定使用满件数策略时使用*/
        private Integer amount;

        /**减多少钱，活动中指定使用满金额策略时使用*/
        private BigDecimal reduceMoney;
        /**折扣，活动中指定使用满件数策略时使用*/
        private BigDecimal discount;
        /**
         * 优惠总金额
         */
        private BigDecimal reduceTotalMoney =BigDecimal.ZERO;
    }


    /**
     * 加价购
     */
    @Data
    public static class PurchasePrice{
        /**
         * 当前活动信息
         */
        private String condition;
        /**
         * 当前规则
         */
       private PurchasePriceRule rule;

        /**
         * 加价购规则
         */
        private List<PurchasePriceRule> purchasePriceRule;
    }
    /**
     * 加价购
     */
    @Data
    public static class PurchasePriceRule{
        /**活动id*/
        private Integer activityId;
        /**规则id*/
        private Integer ruleId;
        private String name;
        /**满金额数*/
        private BigDecimal fullPrice;
        /**加价金额数*/
        private BigDecimal purchasePrice;
    }
}

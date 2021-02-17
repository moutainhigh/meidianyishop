package com.meidianyi.shop.service.pojo.wxapp.market.increasepurchase;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import com.meidianyi.shop.common.foundation.util.PageResult;

/**
 * @author: 王兵兵
 * @create: 2020-02-24 11:16
 **/
@Getter
@Setter
public class PurchaseGoodsListVo {
    /** 状态，0正常可用，1活动不存在，2活动未开始，3活动已过期   */
    private Byte state;

    /**
     * 当前分组下可选的商品列表
     */
    private PageResult<Goods> goods;

    /**
     * 换购规则
     */
    private List<Rule> rules;

    /**已加入购物车得活动商品总价 */
    private BigDecimal mainPrice;

    /**
     * 当前是否满足换购
     */
    private ChangeDoc changeDoc;

    @Setter
    @Getter
    public static class Goods{
        //商品原数据
        private Integer goodsId;
        private String goodsName;
        /**
         * 商品主图
         */
        private String goodsImg;
        /**
         * 商品价格，商品规格中的最低价格，（对于默认规格和自定义规格计算方式是一样的）
         */
        private BigDecimal shopPrice;
        /**
         * 市场价格
         */
        private BigDecimal marketPrice;
        private Byte goodsType;
        /**
         * 是否会员专享
         */
        private Byte isCardExclusive;
        private Integer sortId;
        private Integer catId;
        /**
         * 1默认规格，0自定义规格（多规格）
         */
        private Byte isDefaultProduct;
        /**
         * 单规格商品的规格ID
         */
        private Integer prdId;
        private Integer goodsNumber;

        //活动数据
        /**
         * 取价结果类型，0商品原价，1等级会员价，2限时降价，3首单特惠价
         */
        private Byte goodsPriceAction;
        private BigDecimal goodsPrice;
        private BigDecimal maxPrice;
        private Integer limitAmount;

        //购物车数据
        /**
         * 购物车里该商品的数量
         */
        private Integer cartGoodsNumber;
    }

    @Setter
    @Getter
    public static class Rule{
        /**
         * 满多少元
         */
        private BigDecimal fullPrice;

        /**
         * 换购价
         */
        private BigDecimal purchasePrice;
    }

    @Getter
    @Setter
    public static class ChangeDoc{
        /**
         * 0未选择商品，1还差diffPrice可以换购，2已满足一条换购规则
         */
        private Byte state;
        private BigDecimal diffPrice;
    }

}

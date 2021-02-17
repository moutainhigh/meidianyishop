package com.meidianyi.shop.service.pojo.wxapp.market.gift;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.gift.ProductVo;
import com.meidianyi.shop.service.pojo.shop.market.gift.RuleVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-03-24 16:19
 **/
@Getter
@Setter
public class GiftGoodsListVo {
    /** 状态，0已满足赠送条件，1活动不存在，2活动未开始，3活动已过期, 4购物车内未满足rule.fullPrice满赠价格， 5购物车内未满赠rule.fullNumber满赠件数   */
    private Byte state = 0;

    /**
     * 满足赠品活动的主商品
     */
    private PageResult<Goods> goods;

    /**
     * 赠品
     */
    private List<ProductVo> giftProductList;

    /**
     * 赠品活动规则
     */
    private RuleVo rule;

    /**
     * 购物车已选商品总价
     */
    private BigDecimal checkedGoodsPrice;
    private Integer cartGoodsNumber;

    /**
     * 基础配置
     */
    private Byte delMarket;
    private ShowCartConfigVo showCart;

    @Setter
    @Getter
    public static class Goods{
        //商品原数据
        private Integer goodsId;
        private String goodsName;
        /**
         * 在售状态1在售,0下架
         */
        private Byte isOnSale;
        /**
         * 商品库存，该字段是通过商品规格计算而来
         */
        private Integer goodsNumber;

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

        private Integer goodsSaleNum;
        private Integer commentNum;


        //活动数据
        /**
         * 取价结果类型，0商品原价，1等级会员价，2限时降价，3首单特惠价
         */
        private Byte goodsPriceAction;
        private BigDecimal goodsPrice;

        private BigDecimal maxPrice;
    }
}

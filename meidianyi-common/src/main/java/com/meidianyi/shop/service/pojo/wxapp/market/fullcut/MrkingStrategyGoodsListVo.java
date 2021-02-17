package com.meidianyi.shop.service.pojo.wxapp.market.fullcut;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.fullcut.MrkingStrategyCondition;
import com.meidianyi.shop.service.pojo.shop.member.card.SimpleMemberCardVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-02-18 10:12
 **/
@Getter
@Setter
public class MrkingStrategyGoodsListVo {
    /** 状态，0正常可用，1活动不存在，2活动未开始，3活动已过期 ，4活动设置了专属会员卡可参与，但该会员没有对应的卡，  */
    private Byte state = 0;

    private PageResult<Goods> goods;

    /** 活动类型：类型,1每满减 2满件 3满折 4仅第X件打折 */
    private Byte type;
    /** 优惠规则 */
    private List<MrkingStrategyCondition> condition;

    /**
     * 底边条输出的文案，根据当前购物车里的商品计算出
     */
    private FullPriceDoc fullPriceDoc;

    /**
     * 当前已选商品总价
     */
    private BigDecimal totalPrice;

    /**
     * state==4时，活动需要的会员卡列表
     */
    private List<SimpleMemberCardVo> cardList;

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


        //活动数据
        /**
         * 取价结果类型，0商品原价，1等级会员价，2限时降价，3首单特惠价
         */
        private Byte goodsPriceAction;
        private BigDecimal goodsPrice;

        private BigDecimal maxPrice;

        //购物车数据
        /**
         * 购物车里该商品的数量
         */
        private Integer cartGoodsNumber;
    }

    /**
     * 底边条输出的文案类，根据当前购物车里的商品计算出
     */
    @Getter
    @Setter
    public static class FullPriceDoc{
        /**
         * 类型，0“快选择商品参加满折满减活动吧”，购物车里没有东西；1"下单立减reduceMoney元"；2"再选diffPrice元，即可减reduceMoney元"；3"再选diffPrice元，即可打discount折"；4"再选diffNumber件，即可减reduceMoney元"；5"再选diffNumber件，即可打discount折"
         */
        private Byte docType;

        private BigDecimal diffPrice;

        private BigDecimal reduceMoney;

        private BigDecimal discount;

        private Integer diffNumber;
    }
}

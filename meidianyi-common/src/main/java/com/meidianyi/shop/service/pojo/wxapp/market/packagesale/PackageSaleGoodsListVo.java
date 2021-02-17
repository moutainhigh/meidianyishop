package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsProductVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-02-20 10:40
 **/
@Getter
@Setter
public class PackageSaleGoodsListVo {
    /** 状态，0正常可用，1活动不存在，2活动未开始，3活动已过期   */
    private Byte state;

    /**
     * 所有分组需要选择商品件数总和
     */
    private Integer totalGoodsNumber;
    /**
     * 用户当前已选择商品件数总和
     */
    private Integer totalSelectNumber;

    /**
     * 当前分组下可选的商品列表
     */
    private PageResult<Goods> goods;

    /**
     * tab切换栏数据
     */
    private List<GoodsGroup>  tabList;

    /**
     * 顶部活动解释文本
     */
    private Title title;

    /**
     * 总金额合优惠金额
     */
    private TotalMoney totalMoney;

    private Byte delMarket;
    private ShowCartConfig showCart;

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
         * 是否单规格商品（默认一个规格）
         */
        private Byte isDefaultProduct;
        private Integer goodsNumber;

        //用户已选择的该商品的数量
        private Integer chooseNumber;

        private List<GoodsProductVo> goodsProducts;
    }

    @Setter
    @Getter
    public static class GoodsGroup{
        /** 商品组名称 */
        private String groupName;
        /** 至少需要选择件数 */
        private Integer goodsNumber;
        /** 用户已经选择件数 */
        private Integer selectNumber;
    }

    @Getter
    @Setter
    public static class Title{
        private Byte packageType;

        /**
         * 满件数折扣类型的折扣率
         */
        private BigDecimal discountTotalRatio;

        /**
         * 几件一口价类型的一口价金额
         */
        private BigDecimal totalMoney;

        /** 所有组的总和件数要求 */
        private Integer totalGoodsNumber;
    }

    @Setter
    @Getter
    public static class TotalMoney{
        /**
         * 用户当前已选择商品金额总和（已根据活动规则折扣）
         */
        private BigDecimal totalSelectMoney;
        /**
         * 用户当前已选择商品金额根据活动规则优惠的金额
         */
        private BigDecimal discountMoney;
    }
}

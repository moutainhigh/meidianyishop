package com.meidianyi.shop.service.pojo.wxapp.goods.recommend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序-商品推荐展示信息
 * @author liangchen
 * @date 2019.12.30
 */
@Data
public class RecommendGoodsVo {
    /** 商品id集合 */
    private List<Integer> recommendGoodsIds;
    /** 商品详情集合 */
    private List<?> recommendGoods;
    /** 是否显示划线价开关 */
    private Byte delMarket;
    /** 商品搜索页以及推荐商品列表中会显示购买按钮 */
    private ShowCartConfig showCart;
    /**商品Id*/
    private Integer goodsId;
    @JsonIgnore
    private Integer sortId;
    @JsonIgnore
    private Integer catId;
    /**商品名称*/
    private String goodsName;
    /**商品图片*/
    private String goodsImg;
    /**商品价格*/
    private BigDecimal shopPrice;
    /**商品库存*/
    private Integer goodsNumber;
    /**商品最高返利*/
    private Double maxRebate;
    @JsonIgnore
    private BigDecimal marketPrice;
    /**划线价*/
    private BigDecimal prdLinePrice;
    /**是否为多规格商品 0：否；1：是*/
    private Byte isMorePrd = 0;
    /**最高规格价*/
    private BigDecimal highPrdPrice;
    /**最低规格价*/
    private BigDecimal lowPrdPrice;
    /**商品多图*/
    private List<String> goodsMoreImgs;
    /**商品推广语*/
    private String promotionLanguage;
    /**成本价保护*/
    private BigDecimal costPrice;
    /**商品销售数量*/
    protected Integer goodsSaleNum;
    /**最高返利金额，已考虑成本价*/
    private BigDecimal highRebate;
    /**是否已收藏到个人推广中心 0：否；1：是*/
    private Byte isCollection;
}

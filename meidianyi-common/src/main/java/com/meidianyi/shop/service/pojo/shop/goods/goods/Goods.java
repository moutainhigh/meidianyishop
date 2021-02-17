package com.meidianyi.shop.service.pojo.shop.goods.goods;

import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpec;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年07月05日
 */
@Data
public class Goods {
    private Integer goodsId;
    private Integer shopId;
    private String goodsName;
    /**
     * 商品广告词
     */
    private String goodsAd;
    /**
     * 商品货号
     */
    private String goodsSn;
    /**
     * 平台分类
     */
    private Integer catId;
    /**
     * 商品主图
     */
    private String goodsImg;
    /**
     * 商品子图列表
     */
    private List<String> goodsImgs;
    /**
     * 商品单位
     */
    private String unit;
    /**
     * 商品商家分类
     */
    private Integer sortId;
    /**
     * 商品标签列表
     */
    private List<Integer> goodsLabels;
    /**
     * 商品品牌
     */
    private Integer brandId;
    /**
     * 商品视频地址
     */
    private String goodsVideo;
    /**
     * 视频首图地址
     */
    private String goodsVideoImg;
    /**
     * 商品视频大小
     */
    private Integer goodsVideoSize;
    /**
     * 视频id
     */
    private Integer goodsVideoId;

    /**
     * 商品规格属性规格值信息
     */
    private List<GoodsSpec> goodsSpecs;
    /**
     * 商品规格信息
     */
    private List<GoodsSpecProduct> goodsSpecProducts;
    /**
     * 市场价格
     */
    private BigDecimal marketPrice;

    /**
     * 会员价-针对不同规格的会员价
     */
    private List<GoodsGradePrd> goodsGradePrds;
    /**
     * 最小限购数量
     */
    private Integer limitBuyNum;
    /**
     * 最大限购数量
     */
    private Integer limitMaxNum;
    /**
     * 初始销量
     */
    private Integer baseSale;
    /**
     * 运费模板id
     */
    private Integer deliverTemplateId;
    /**
     * 发货地址
     */
    private String deliverPlace;
    /**
     * 商品重量
     */
    private BigDecimal goodsWeight;

    /**
     * 是否会员专享
     */
    private Byte isCardExclusive;
    /**
     * 会员卡id集合
     */
    private List<Integer> memberCardIds;
    /**
     * 在售状态1在售,0下架
     */
    private Byte isOnSale;
    /**
     * 商品上架状态 0立即上架， 1审核通过 2 加入仓库
     */
    private Byte saleType;
    /**
     * 自定义上架时间
     */
    private Timestamp saleTime;
    /**
     * 是否在自定义内容上方
     */
    private Byte isPageUp;
    /**
     * 详情页装修模板id
     */
    private Integer goodsPageId;
    /**
     * 商品详情
     */
    private String goodsDesc;
    /**
     * 是否允许分销改价
     */
    private Byte canRebate;
    /**
     * 商品规格对应的分销改价
     */
    private List<GoodsRebatePrice> goodsRebatePrices;
    /**
     * 是否使用分销推广语0否，1是
     */
    private Byte promotionLanguageSwitch;
    /**
     * 分销推广语
     */
    private String promotionLanguage;
    /**
     * 商品分享海报
     */
    private GoodsSharePostConfig goodsSharePostConfig;

    private String shareConfig;

    /**
     * 商品库存，该字段是通过商品规格计算而来
     */
    private Integer goodsNumber;
    /**
     * 商品价格，商品规格中的最低价格，（对于默认规格和自定义规格计算方式是一样的）
     */
    private BigDecimal shopPrice;
    /**
     * 成本价格，商品规格中的最低价格，（对于默认规格和自定义规格计算方式是一样的）
     */
    private BigDecimal costPrice;

    /**是否是使用默认规格 0否 1是*/
    private Byte isDefaultProduct;

    /**直播间id*/
    private Integer roomId;
}

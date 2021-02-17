package com.meidianyi.shop.service.foundation.es.pojo.goods.product;

import com.meidianyi.shop.service.foundation.es.annotation.EsFiled;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiledTypeConstant;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luguangyao
 */
@Data
public class EsGoodsProductEntity {


    @EsFiled(name = EsSearchName.GOODS_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer goodsId;
    @EsFiled(name = EsSearchName.SHOP_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer shopId;
    @EsFiled(name = EsSearchName.GOODS_NAME,type = EsFiledTypeConstant.TEXT,searchAnalyzer = "ik_max_word",analyzer = "ik_max_word")
    private String goodsName;
    /**
     * admin鼠标悬浮时商品的展示名称(防止和带html的标签高亮字段)
     */
    private String goodsTitleName;
    /**
     * 商品广告词
     */
    @EsFiled(name = EsSearchName.GOODS_AD,type = EsFiledTypeConstant.KEYWORD,doc_values = false)
    private String goodsAd;
    /**
     * 商品描述
     */
    @EsFiled(name = EsSearchName.GOODS_DESC,type = EsFiledTypeConstant.KEYWORD,doc_values = false)
    private String goodsDesc;
    /**
     * 商品货号
     */
    @EsFiled(name = EsSearchName.GOODS_SN,type = EsFiledTypeConstant.TEXT,doc_values = false)
    private String goodsSn;
    /**
     * 平台分类
     */
    @EsFiled(name = EsSearchName.CAT_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer catId;
    /**
     * 商品主图
     */
    @EsFiled(name = EsSearchName.GOODS_IMG,type = EsFiledTypeConstant.KEYWORD,doc_values = false)
    private String goodsImg;

    /**
     * 商品单位
     */
    @EsFiled(name = EsSearchName.UNIT,type = EsFiledTypeConstant.KEYWORD,doc_values = false)
    private String unit;
    /**
     * 商品商家分类
     */
    @EsFiled(name = EsSearchName.SORT_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer sortId;

    /**
     * 商品品牌
     */
    @EsFiled(name = EsSearchName.BRAND_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer brandId;
    /**
     * 市场价格
     */
    @EsFiled(name = EsSearchName.MARKET_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private BigDecimal marketPrice;
    /**
     * 最小限购数量
     */
    @EsFiled(name = EsSearchName.LIMIT_BUY_NUM,type = EsFiledTypeConstant.INTEGER)
    private Integer limitBuyNum;
    /**
     * 最大限购数量
     */
    @EsFiled(name = EsSearchName.LIMIT_MAX_NUM,type = EsFiledTypeConstant.INTEGER)
    private Integer limitMaxNum;

    /**
     * 初始销量
     */
    @EsFiled(name = EsSearchName.ADD_SALE_NUM,type = EsFiledTypeConstant.INTEGER)
    private Integer addSaleNum;

    /**
     * 商品重量
     */
    @EsFiled(name = EsSearchName.GOODS_WEIGHT,type = EsFiledTypeConstant.SCALED_FLOAT,scaledNumber = "1000")
    private BigDecimal goodsWeight;

    /**
     * 是否会员专享
     */
    @EsFiled(name = EsSearchName.IS_CARD_EXCLUSIVE,type = EsFiledTypeConstant.BYTE)
    private Byte isCardExclusive;

    /**
     * 在售状态1在售,0下架
     */
    @EsFiled(name = EsSearchName.IS_ON_SALE,type = EsFiledTypeConstant.BYTE)
    private Byte isOnSale;
    /**
     * 商品上架状态 0立即上架， 1审核通过 2 加入仓库
     */
    @EsFiled(name = EsSearchName.SALE_TYPE,type = EsFiledTypeConstant.BYTE)
    private Byte saleType;

    /**
     * 是否在自定义内容上方
     */
    @EsFiled(name = EsSearchName.IS_PAGE_UP,type = EsFiledTypeConstant.BYTE)
    private Byte isPageUp;
    /**
     * 详情页装修模板id
     */
    @EsFiled(name = EsSearchName.GOODS_PAGE_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer goodsPageId;


    /**
     * 商品库存，该字段是通过商品规格计算而来
     */
    @EsFiled(name = EsSearchName.GOODS_NUMBER,type = EsFiledTypeConstant.INTEGER)
    private Integer goodsNumber;
    /**
     * 商品价格，商品规格中的最低价格，（对于默认规格和自定义规格计算方式是一样的）
     */
    @EsFiled(name = EsSearchName.SHOP_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private BigDecimal shopPrice;
    /**
     * 商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品
     */
    @EsFiled(name = EsSearchName.GOODS_TYPE,type = EsFiledTypeConstant.BYTE)
    private Byte goodsType;
    /**
     *  销售数量
     */
    @EsFiled(name = EsSearchName.GOODS_SALE_NUM,type = EsFiledTypeConstant.INTEGER)
    private Integer goodsSaleNum;
    /**
     *  收藏数量
     */
    @EsFiled(name = EsSearchName.GOODS_COLLECT_NUM,type = EsFiledTypeConstant.INTEGER)
    private Integer goodsCollectNum;

    /**
     *  子帐号id，主要用于官方店铺
     */
    @EsFiled(name = EsSearchName.SUB_ACCOUNT_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer subAccountId;
    /**
     *  审核状态,0待审核 1 审核通过 2 违规下架
     */
    @EsFiled(name = EsSearchName.STATE,type = EsFiledTypeConstant.BYTE)
    private Integer state;

    /**
     *  成本价
     */
    @EsFiled(name = EsSearchName.COST_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private Integer costPrice;
    /**
     *  商品来源,0：店铺自带；1、2..等：不同类型店铺第三方抓取自带商品来源
     */
    @EsFiled(name = EsSearchName.SOURCE,type = EsFiledTypeConstant.BYTE)
    private Byte source;
    /**
     * 是否控价：0不控价，1控价（不可修改价格）
     */
    @EsFiled(name = EsSearchName.IS_CONTROL_PRICE,type = EsFiledTypeConstant.BYTE)
    private Integer isControlPrice;
    /**
     * 商品浏览量
     */
    @EsFiled(name = EsSearchName.PV,type = EsFiledTypeConstant.INTEGER)
    private Integer pv;
    /**
     * 商品评论数
     */
    @EsFiled(name = EsSearchName.COMMENT_NUM,type = EsFiledTypeConstant.INTEGER)
    private Integer commentNum;
    /**
     * 商品初始销量
     */
    @EsFiled(name = EsSearchName.BASE_SALE,type = EsFiledTypeConstant.INTEGER)
    private Integer baseSale;
    /**
     * 商品展示价格
     */
    @EsFiled(name = EsSearchName.SHOW_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private BigDecimal showPrice;

    /**
     * 商家编码（拼接）
     */
    @EsFiled(name = EsSearchName.PRD_SNS,type = EsFiledTypeConstant.TEXT,doc_values = false)
    private String prdSns;
    /**
     * 平台分类名称（空格拼接）
     */
    @EsFiled(name = EsSearchName.CAT_NAME,type = EsFiledTypeConstant.TEXT)
    private String catName;

    @EsFiled(name = EsSearchName.FIRST_CAT_ID,type = EsFiledTypeConstant.INTEGER,copyTo = EsSearchName.FULL_CAT_ID)
    private Integer firstCatId;
    @EsFiled(name = EsSearchName.SECOND_CAT_ID,type = EsFiledTypeConstant.INTEGER,copyTo = EsSearchName.FULL_CAT_ID)
    private Integer secondCatId;
    @EsFiled(name = EsSearchName.THIRD_CAT_ID,type = EsFiledTypeConstant.INTEGER,copyTo = EsSearchName.FULL_CAT_ID)
    private Integer thirdCatId;
    @EsFiled(name = EsSearchName.FULL_CAT_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer fullCatId;
    /**
     * 商家分类名称（空格拼接）
     */
    @EsFiled(name = EsSearchName.SORT_NAME,type = EsFiledTypeConstant.TEXT)
    private String sortName;

    @EsFiled(name = EsSearchName.FIRST_SORT_ID,type = EsFiledTypeConstant.INTEGER,copyTo = EsSearchName.FULL_SORT_ID)
    private Integer firstSortId;
    @EsFiled(name = EsSearchName.SECOND_SORT_ID,type = EsFiledTypeConstant.INTEGER,copyTo = EsSearchName.FULL_SORT_ID)
    private Integer secondSortId;
    @EsFiled(name = EsSearchName.FULL_SORT_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer fullSortId;

    /**
     * 品牌名称
     */
    @EsFiled(name = EsSearchName.BRAND_NAME,type = EsFiledTypeConstant.TEXT)
    private String brandName;
    /**
     * 商品标签id
     */
    @EsFiled(name = EsSearchName.GOODS_LABEL,type = EsFiledTypeConstant.INTEGER)
    private List<Integer> goodsLabel;

    /**
     * 商品规格对应的的价格(最大)
     */
    @EsFiled(name = EsSearchName.MAX_SPEC_PRD_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private BigDecimal maxSpecPrdPrices;
    /**
     * 商品规格对应的的价格(最小)
     */
    @EsFiled(name = EsSearchName.MIN_SPEC_PRD_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private BigDecimal minSpecPrdPrices;
    /**
     * 数据修改时间
     */
    @EsFiled(name=EsSearchName.UPDATE_TIME,type = EsFiledTypeConstant.DATE)
    private String updateDate;
    /**
     * 存入es的时间
     */
    @EsFiled(name=EsSearchName.ADD_ES_TIME,type = EsFiledTypeConstant.DATE)
    private String addEsDate;

    /**
     * 商品的其他图片(不包含主图)
     */
    @EsFiled(name=EsSearchName.SECONDARY_GOODS_IMAGES,type = EsFiledTypeConstant.KEYWORD,doc_values = false,index = false)
    private List<String> secondaryGoodsImages;

    /**
     * 视频信息(JSON字符串包含：url,image,size,width,height,id)
     */
    @EsFiled(name=EsSearchName.VIDEO_INFO_JSON,type = EsFiledTypeConstant.KEYWORD,doc_values = false,index = false)
    private String videoInfo;

    /**
     * 运费模版id
     */
    @EsFiled(name = EsSearchName.FREIGHT_TEMPLATE_ID,type = EsFiledTypeConstant.INTEGER,doc_values = false)
    private Integer freightTemplateId;

    /**
     * 商品规格信息JSON(暂时先这样，后期看下是否有必要单独建个索引)
     */
    @EsFiled(name=EsSearchName.PRD_JSON,type = EsFiledTypeConstant.TEXT,doc_values = false,index = false)
    private String prdJson;
    /**
     * 是不是默认规格
     */
    @EsFiled(name=EsSearchName.DEFAULT_PRD,type = EsFiledTypeConstant.BOOL,doc_values = false)
    private Boolean defPrd;

    /**
     * 是否分销改价
     */
    @EsFiled(name=EsSearchName.CAN_REBATE,type = EsFiledTypeConstant.BYTE,doc_values = false)
    private Byte canRebate;
    /**
     * 商品创建的时间
     */
    @EsFiled(name=EsSearchName.CREATE_TIME,type = EsFiledTypeConstant.DATE)
    private String createTime;
    /**
     * 商品上架的时间
     */
    @EsFiled(name=EsSearchName.SALE_TIME,type = EsFiledTypeConstant.DATE)
    private String saleTime;

    /**
     * 商品总销售数量（base_sale+goods_sale）
     */
    @EsFiled(name=EsSearchName.TOTAL_SALE_NUMBER,type = EsFiledTypeConstant.INTEGER)
    private Integer totalSaleNumber;

    /**规格id*/
    @EsFiled(name = EsSearchName.Prd.PRD_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer prdId;
    @EsFiled(name = EsSearchName.Prd.PRD_NUMBER,type = EsFiledTypeConstant.INTEGER)
    private Integer prdNumber;
    /**商品重量*/
    @EsFiled(name = EsSearchName.Prd.PRD_WEIGHT,type = EsFiledTypeConstant.SCALED_FLOAT,scaledNumber = "1000")
    private BigDecimal prdWeight;
    /** 规格最终价格，数据库取prd_price*/
    @EsFiled(name = EsSearchName.Prd.PRD_REAL_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private BigDecimal prdRealPrice;
    /**规格划线价,首次取时取市场价*/
    @EsFiled(name = EsSearchName.Prd.PRD_LINE_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private BigDecimal prdLinePrice;
    @EsFiled(name = EsSearchName.Prd.PRD_SPECS,type = EsFiledTypeConstant.KEYWORD)
    private String prdSpecs;
    @EsFiled(name = EsSearchName.Prd.PRD_DESC,type = EsFiledTypeConstant.KEYWORD)
    private String prdDesc;
    @EsFiled(name = EsSearchName.Prd.PRD_IMG,type = EsFiledTypeConstant.KEYWORD)
    private String prdImg;
    @EsFiled(name = EsSearchName.Prd.PRD_CODES,type = EsFiledTypeConstant.KEYWORD)
    private String prdCodes;
    @EsFiled(name = EsSearchName.Prd.PRD_SN,type = EsFiledTypeConstant.KEYWORD)
    private String prdSn;


}

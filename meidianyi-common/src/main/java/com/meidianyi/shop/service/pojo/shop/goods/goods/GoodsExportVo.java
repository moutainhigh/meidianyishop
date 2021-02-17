package com.meidianyi.shop.service.pojo.shop.goods.goods;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-10-21 15:24
 **/
@ExcelSheet
@Data
public class GoodsExportVo {

    /**
     * 规格添加时间
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_CREATE_TIME,columnIndex = 0)
    private Timestamp createTime;
    /**
     * 商家一级分类名
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_SORT_NAME_PARENT,columnIndex = 1)
    private String sortNameParent;
    /**
     * 商家二级分类名
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_SORT_NAME_CHILD,columnIndex = 2)
    private String sortNameChild;
    /**
     * 品牌
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_BRAND_NAME,columnIndex = 3)
    private String brandName;
    /**
     * 商品货号
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GOODS_SN,columnIndex = 4)
    private String goodsSn;
    /**
     * 商品名称
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GOODS_NAME,columnIndex = 5)
    private String goodsName;
    /**
     * 规格名称
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_PRD_DESC,columnIndex = 6)
    private String prdDesc;
    /**
     * 商品广告
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GOODS_AD,columnIndex = 7)
    private String goodsAd;
    /**
     * 规格编码
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_PRD_SN,columnIndex = 8)
    private String prdSn;
    /**
     * 库存（规格的库存）
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_PRD_NUMBER,columnIndex = 9)
    private Integer prdNumber;
    /**
     * 成本价
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_PRD_COST_PRICE,columnIndex = 10)
    private BigDecimal prdCostPrice;
    /**
     * 市场价
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_MARKET_PRICE,columnIndex = 11)
    private BigDecimal prdMarketPrice;
    /**
     * 零售价
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_SHOP_PRICE,columnIndex = 12)
    private BigDecimal shopPrice;
    /**
     * 上架状态
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_IS_ON_SALE,columnIndex = 13)
    private Byte isOnSale;
    /**
     * 最小限购数量
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_LIMIT_BUY_NUMBER,columnIndex = 14)
    private Integer limitBuyNum;
    /**
     * 重量
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GOODS_WEIGHT,columnIndex = 15)
    private BigDecimal goodsWeight;
    /**
     * 单位量词
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_UNIT,columnIndex = 16)
    private String unit;
    /**
     * 商品主图
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GOODS_IMG,columnIndex = 17)
    private String goodsImg;

    /**
     * 规格条码
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_PRD_CODES,columnIndex = 18)
    private String prdCodes;

    /**
     * 商品详情图
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_IMG_URL,columnIndex = 19)
    private String imgUrl;

    /**
     * 商品默认发货地
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_DELIVER_PLACE,columnIndex = 20)
    private String deliverPlace;

    /**
     * 1到9级会员价
     */
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_1_PRICE,columnIndex = 21)
    private BigDecimal v1;
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_2_PRICE,columnIndex = 23)
    private BigDecimal v2;
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_3_PRICE,columnIndex = 24)
    private BigDecimal v3;
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_4_PRICE,columnIndex = 25)
    private BigDecimal v4;
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_5_PRICE,columnIndex = 26)
    private BigDecimal v5;
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_6_PRICE,columnIndex = 27)
    private BigDecimal v6;
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_7_PRICE,columnIndex = 28)
    private BigDecimal v7;
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_8_PRICE,columnIndex = 29)
    private BigDecimal v8;
    @ExcelColumn(columnName = JsonResultMessage.GOODS_EXPORT_COLUMN_GRADE_9_PRICE,columnIndex = 30)
    private BigDecimal v9;

    @ExcelIgnore
    private Integer goodsId;
    @ExcelIgnore
    private Integer brandId;
    @ExcelIgnore
    private Integer catId;
    @ExcelIgnore
    private Integer sortId;
    @ExcelIgnore
    private Integer prdId;

    public static final String CREATE_TIME = "createTime";
    public static final String SORT_NAME_PARENT = "sortNameParent";
    public static final String SORT_NAME_CHILD = "sortNameChild";
    public static final String BRAND_NAME = "brandName";
    public static final String GOODS_SN = "goodsSn";
    public static final String GOODS_NAME = "goodsName";
    public static final String PRD_DESC = "prdDesc";
    public static final String PRD_SN = "prdSn";
    public static final String GOODS_AD = "goodsAd";
    public static final String PRD_NUMBER = "prdNumber";
    public static final String PRD_COST_PRICE = "prdCostPrice";
    public static final String PRD_MARKET_PRICE = "prdMarketPrice";
    public static final String SHOP_PRICE = "shopPrice";
    public static final String IS_ON_SALE = "isOnSale";
    public static final String LIMIT_BUY_NUM = "limitBuyNum";
    public static final String GOODS_WEIGHT = "goodsWeight";
    public static final String UNIT = "unit";
    public static final String GOODS_IMG = "goodsImg";
    public static final String IMG_URL = "imgUrl";
    public static final String PRD_CODES = "prdCodes";
    public static final String DELIVER_PLACE = "deliverPlace";
    public static final String GRADE_CARD_PRICE = "gradeCardPrice";
}

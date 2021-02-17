package com.meidianyi.shop.service.pojo.shop.overview.commodity;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumnNotNull;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:liufei
 * @Date:2019/7/29
 * @Description:
 */
@Data
@ExcelSheet
public class ProductEffectExportVo{
    /** 商品信息，导出excel中格式为：商品名称 + 空格 + 商品价格 */
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.GOODS_EFFECT_GOODS_INFO)
    @ExcelColumnNotNull
    private String goodsInfo;
    /** 商品id */
    @ExcelIgnore
    private int goodsId;
    /** 商品名称 */
    @ExcelIgnore
    private String goodsName;
    /** 商品图片 */
    @ExcelIgnore
    private String goodsImg;
    /** 商品价格 */
    @ExcelIgnore
    private double shopPrice;
    /**
     * 商品标签名
     */
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.GOODS_EFFECT_GOODS_LABEL)
    @ExcelColumnNotNull
    private String name;
    /**
     * 商品品牌名
     */
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.GOODS_EFFECT_GOODS_BRAND)
    @ExcelColumnNotNull
    private String brandName;
    /**
     * 商品商家分类名
     */
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.GOODS_EFFECT_GOODS_SORT)
    @ExcelColumnNotNull
    private String sortName;
    /**
     * 访客数
     */
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.GOODS_EFFECT_UV)
    @ExcelColumnNotNull
    private int uv;
    /**
     * 浏览量
     */
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.GOODS_EFFECT_PV)
    @ExcelColumnNotNull
    private int pv;
    /**
     * 加购人数
     */
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.GOODS_EFFECT_CART_UV)
    @ExcelColumnNotNull
    private int cartUv;
    /**
     * 付款人数
     */
    @ExcelColumn(columnIndex = 7, columnName = JsonResultMessage.GOODS_EFFECT_PAID_UV)
    @ExcelColumnNotNull
    private int paidUv;
    /**
     * 新成交客户数
     */
    @ExcelColumn(columnIndex = 8, columnName = JsonResultMessage.GOODS_EFFECT_NEW_USER_NUM)
    @ExcelColumnNotNull
    private int newUserNumber;
    /**
     * 新成交客户数占比
     */
    @ExcelColumn(columnIndex = 9, columnName = JsonResultMessage.GOODS_EFFECT_NEW_USER_PERCENTAGE)
    @ExcelColumnNotNull
    private double newUserPercentage;
    /**  老成交客户数  */
    @ExcelColumn(columnIndex = 10, columnName = JsonResultMessage.GOODS_EFFECT_OLD_USER_NUM)
    @ExcelColumnNotNull
    private int oldUserNumber;
    /**
     * 老成交客户数占比
     */
    @ExcelColumn(columnIndex = 11, columnName = JsonResultMessage.GOODS_EFFECT_OLD_USER_PERCENTAGE)
    @ExcelColumnNotNull
    private double oldUserPercentage;
    /**  付款商品件数  */
    @ExcelColumn(columnIndex = 12, columnName = JsonResultMessage.GOODS_EFFECT_PAID_GOODS_NUM)
    @ExcelColumnNotNull
    private int paidGoodsNumber;
    /**  商品转化率  */
    @ExcelColumn(columnIndex = 13, columnName = JsonResultMessage.GOODS_EFFECT_UV_2_PAID)
    @ExcelColumnNotNull
    private double uv2paidGoods;
    /**
     * 销售额
     */
    @ExcelColumn(columnIndex = 14, columnName = JsonResultMessage.GOODS_EFFECT_GOODS_SALES)
    @ExcelColumnNotNull
    private BigDecimal goodsSales;
    /**
     * 推荐人数
     */
    @ExcelColumn(columnIndex = 15, columnName = JsonResultMessage.GOODS_EFFECT_RECONNEND_USER_NUM)
    @ExcelColumnNotNull
    private int recommendUserNum;
    /**
     * 收藏人数
     */
    @ExcelColumn(columnIndex = 16, columnName = JsonResultMessage.GOODS_EFFECT_COLLECT_USER_NUM)
    @ExcelColumnNotNull
    private int collectUseNum;
}

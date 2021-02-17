package com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu;

import lombok.Data;

import java.math.BigDecimal;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

/**
 * 导入的excel对应的对象装换类
 * @author 李晓冰
 * @date 2020年03月18日
 */
@ExcelSheet(importBindByColumnName=true)
@Data
public class GoodsVpuExcelImportModel {
    /**商品id*/
    @ExcelIgnore
    private Integer goodsId;

    /**一级分类名称*/
    @ExcelColumn(columnName = "vpu.import.goods.first.sort")
    private String firstSortName;
    /**二级分类名称*/
    @ExcelColumn(columnName = "vpu.import.goods.second.sort")
    private String secondSortName;
    @ExcelColumn(columnName = "vpu.import.goods.brand")
    private String brandName;
    /**货品编号*/
    @ExcelColumn(columnName = "vpu.import.goods.sn")
    private String goodsSn;
    /**商品名称*/
    @ExcelColumn(columnName = "vpu.import.goods.name")
    private String goodsName;
    /**对应sku，null表示默认sku*/
    @ExcelColumn(columnName = "vpu.import.goods.prd.desc")
    private String prdDesc;
    /**商品广告词*/
    @ExcelColumn(columnName = "vpu.import.goods.ad")
    private String goodsAd;
    /**商家编码*/
    @ExcelColumn(columnName = "vpu.import.goods.prd.sn")
    private String prdSn;
    /**商品库存*/
    @ExcelColumn(columnName = "vpu.import.goods.stock")
    private Integer stock;
    /**市场价*/
    @ExcelColumn(columnName = "vpu.import.goods.market.price")
    private BigDecimal marketPrice;
    /**商品店铺价格-零售价*/
    @ExcelColumn(columnName = "vpu.import.goods.shop.price")
    private BigDecimal shopPrice;
    @ExcelColumn(columnName = "vpu.import.goods.cost.price")
    private BigDecimal costPrice;
    /**是否上架1是0否*/
    @ExcelColumn(columnName = "vpu.import.goods.is.on.sale")
    private Byte isOnSale;
    /**最小限购数 0 表示不限制，用户不填的话更新操作需要手动设置为0*/
    @ExcelColumn(columnName = "vpu.import.goods.limit.buy.num")
    private Integer limitBuyNum;
    /**商品重量，不填默认是0*/
    @ExcelColumn(columnName = "vpu.import.first.goods.weight")
    private BigDecimal goodsWeight;
    /**商品计价单位*/
    @ExcelColumn(columnName = "vpu.import.goods.unit")
    private String unit;
    @ExcelColumn(columnName = "vpu.import.goods.main.img")
    private String goodsImg;
    /**商品图片使用;分隔*/
    @ExcelColumn(columnName = "vpu.import.goods.imgs")
    private String goodsImgsStr;
    /**商品描述信息，需要通过正则进行信息的处理过滤*/
    @ExcelColumn(columnName = "vpu.import.goods.desc")
    private String goodsDesc;
    /**默认发货地*/
    @ExcelColumn(columnName = "vpu.import.goods.deliver.place")
    private String deliverPlace;
    /**商品条码*/
    @ExcelColumn(columnName = "vpu.import.goods.prd.codes")
    private String prdCodes;
    /**
     * 创建示例数据
     * @return
     */
    public static GoodsVpuExcelImportModel createModelExample(){
        GoodsVpuExcelImportModel model = new GoodsVpuExcelImportModel();
        model.setFirstSortName("sort1");
        model.setSecondSortName("sort2");
        model.setGoodsSn("goodsSn01");
        model.setGoodsName("XXXX");
        model.setPrdDesc("color:red;size:xl");
        model.setGoodsAd("XXXXX");
        model.setPrdSn("XXXX");
        model.setStock(1);
        model.setMarketPrice(BigDecimal.valueOf(12));
        model.setShopPrice(BigDecimal.valueOf(10));
        model.setIsOnSale((byte) 1);
        model.setLimitBuyNum(10);
        model.setGoodsWeight(BigDecimal.valueOf(1.1));
        model.setUnit("X");
        model.setGoodsImgsStr("https://ss1.bdstatic.com/a.jpg;https://ss1.bdstatic.com/b.jpg");
        model.setGoodsDesc("<p style='background-color:red;'>不许脚本文件，使用行内样式，不许媒体标签，只用body内部标签</p><img url='https://ss1.bdstatic.com/a.jpg'/>");
        model.setDeliverPlace("Vpu");
        model.setPrdCodes("XXXX");
        return model;
    }
}

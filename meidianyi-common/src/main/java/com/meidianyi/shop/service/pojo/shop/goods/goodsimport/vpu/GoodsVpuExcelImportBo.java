package com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu;

import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.GoodsExcelImportBase;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2020年03月21日
 */
@Getter
@Setter
public class GoodsVpuExcelImportBo extends GoodsExcelImportBase {

    private int operateBusId;

    private String firstSortName;
    private String secondSortName;
    private String brandName;
    /**sku对应的规格组合id编号*/
    private String prdSpecs;
    private String goodsAd;
    private Integer stock;
    private BigDecimal marketPrice;
    private BigDecimal shopPrice;
    private BigDecimal costPrice;
    private Byte isOnSale;
    private Integer limitBuyNum;
    private BigDecimal prdWeight;
    private String unit;
    /**商品图片使用;分隔*/
    private String goodsImgsStr;
    private String goodsDesc;
    private String deliverPlace;

    public GoodsVpuExcelImportBo() {
    }

    public GoodsVpuExcelImportBo(GoodsVpuExcelImportModel m){
        firstSortName = m.getFirstSortName();
        secondSortName = m.getSecondSortName();
        brandName = m.getBrandName();
        goodsSn = m.getGoodsSn();
        goodsName = m.getGoodsName();
        prdDesc = m.getPrdDesc();
        goodsAd = m.getGoodsAd();
        prdSn = m.getPrdSn();
        stock = m.getStock();
        marketPrice = m.getMarketPrice();
        shopPrice = m.getShopPrice();
        costPrice = m.getCostPrice();
        isOnSale = m.getIsOnSale();
        limitBuyNum = m.getLimitBuyNum();
        prdWeight = m.getGoodsWeight();
        unit = m.getUnit();
        goodsDesc = m.getGoodsDesc();
        deliverPlace = m.getDeliverPlace();
        prdCodes = m.getPrdCodes();
        // 只要有图片就会更新商品的图片信息（不管填的是主图还是附图）
        if (m.getGoodsImg() != null) {
            goodsImgsStr = m.getGoodsImg();
        }
        if (m.getGoodsImgsStr() != null) {
            goodsImgsStr = goodsImgsStr==null?m.getGoodsImgsStr():goodsImgsStr+";"+m.getGoodsImgsStr();
        }
    }
}

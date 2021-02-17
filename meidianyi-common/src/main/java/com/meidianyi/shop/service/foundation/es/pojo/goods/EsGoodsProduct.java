package com.meidianyi.shop.service.foundation.es.pojo.goods;

import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiled;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiledTypeConstant;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;

import java.math.BigDecimal;

/**
 * ElasticSearch goods product used
 * @author luguangyao
 */
public class EsGoodsProduct {

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

    public EsGoodsProduct(){}

    public EsGoodsProduct(GoodsSpecProductRecord record) {
        this.prdId = record.getPrdId();
        this.prdNumber = record.getPrdNumber();
        this.prdRealPrice = record.getPrdPrice();
        this.prdLinePrice = record.getPrdMarketPrice();
        this.prdSpecs = record.getPrdSpecs();
        this.prdDesc = record.getPrdDesc();
        this.prdImg = record.getPrdImg();
        this.prdCodes = record.getPrdCodes();
        this.prdWeight = record.getPrdWeight();
        this.prdSn = record.getPrdSn();
    }

    public String getPrdSn() {
        return prdSn;
    }

    public void setPrdSn(String prdSn) {
        this.prdSn = prdSn;
    }

    public Integer getPrdId() {
        return prdId;
    }

    public void setPrdId(Integer prdId) {
        this.prdId = prdId;
    }

    public Integer getPrdNumber() {
        return prdNumber;
    }

    public void setPrdNumber(Integer prdNumber) {
        this.prdNumber = prdNumber;
    }

    public BigDecimal getPrdWeight() {
        return prdWeight;
    }

    public void setPrdWeight(BigDecimal prdWeight) {
        this.prdWeight = prdWeight;
    }

    public BigDecimal getPrdRealPrice() {
        return prdRealPrice;
    }

    public void setPrdRealPrice(BigDecimal prdRealPrice) {
        this.prdRealPrice = prdRealPrice;
    }

    public BigDecimal getPrdLinePrice() {
        return prdLinePrice;
    }

    public void setPrdLinePrice(BigDecimal prdLinePrice) {
        this.prdLinePrice = prdLinePrice;
    }

    public String getPrdSpecs() {
        return prdSpecs;
    }

    public void setPrdSpecs(String prdSpecs) {
        this.prdSpecs = prdSpecs;
    }

    public String getPrdDesc() {
        return prdDesc;
    }

    public void setPrdDesc(String prdDesc) {
        this.prdDesc = prdDesc;
    }

    public String getPrdImg() {
        return prdImg;
    }

    public void setPrdImg(String prdImg) {
        this.prdImg = prdImg;
    }

    public String getPrdCodes() {
        return prdCodes;
    }

    public void setPrdCodes(String prdCodes) {
        this.prdCodes = prdCodes;
    }
}

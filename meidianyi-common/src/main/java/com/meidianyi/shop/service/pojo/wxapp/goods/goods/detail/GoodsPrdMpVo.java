package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品规格
 * @author 李晓冰
 * @date 2019年11月08日
 */
@Data
public class GoodsPrdMpVo {
    //************ElasticSearch中的数据**************start
    /**规格id*/
    private Integer prdId;
    private Integer prdNumber;
    /**商品重量*/
    private BigDecimal prdWeight;
    /** 规格最终价格，数据库取prd_price*/
    private BigDecimal prdRealPrice;
    /**规格划线价,首次取时取市场价*/
    private BigDecimal prdLinePrice;
    private String prdSpecs;
    private String prdDesc;
    private String prdImg;
    private String prdCodes;
    //************ElasticSearch中的数据**************start


    public GoodsPrdMpVo() {
    }

    public GoodsPrdMpVo(GoodsSpecProductRecord record) {
        this.prdId = record.getPrdId();
        this.prdNumber = record.getPrdNumber();
        this.prdRealPrice = record.getPrdPrice();
        this.prdLinePrice = record.getPrdMarketPrice();
        this.prdSpecs = record.getPrdSpecs();
        this.prdDesc = record.getPrdDesc();
        this.prdImg = record.getPrdImg();
        this.prdCodes = record.getPrdCodes();
        this.prdWeight = record.getPrdWeight();
    }
}

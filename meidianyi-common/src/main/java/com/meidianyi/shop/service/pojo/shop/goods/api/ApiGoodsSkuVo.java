package com.meidianyi.shop.service.pojo.shop.goods.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductBakRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  对外接口-商品列表和详情中sku信息
 * @author 李晓冰
 * @date 2020年05月28日
 */
@Data
public class ApiGoodsSkuVo {
    @JsonProperty("sku_id")
    private Integer skuId;
    @JsonProperty("prd_sn")
    private String prdSn;
    @JsonProperty("prd_price")
    private BigDecimal prdPrice;
    @JsonProperty("prd_number")
    private Integer prdNumber;
    @JsonProperty("prd_desc")
    private String prdDesc;
    @JsonProperty("prd_img")
    private String prdImg;
    @JsonProperty("del_flag")
    private Byte delFlag = 0;

    public ApiGoodsSkuVo() {
    }

    public ApiGoodsSkuVo(GoodsSpecProductRecord record){
        this.skuId = record.getPrdId();
        this.prdSn = record.getPrdSn();
        this.prdPrice = record.getPrdPrice();
        this.prdNumber = record.getPrdNumber();
        this.prdDesc = record.getPrdDesc();
        this.prdImg = record.getPrdImg();
    }

    public ApiGoodsSkuVo(GoodsSpecProductBakRecord record){
        this.skuId = record.getPrdId();
        this.prdSn = record.getPrdSn();
        this.prdPrice = record.getPrdPrice();
        this.prdNumber = record.getPrdNumber();
        this.prdDesc = record.getPrdDesc();
        this.prdImg = record.getPrdImg();
        delFlag = 1;
    }
}

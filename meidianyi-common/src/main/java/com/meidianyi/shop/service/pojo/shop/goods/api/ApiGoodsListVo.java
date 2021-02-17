package com.meidianyi.shop.service.pojo.shop.goods.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import lombok.Getter;
import lombok.Setter;

/**
 * 对外接口-商品列表-商品信息
 * @author 李晓冰
 * @date 2020年05月28日
 */
@Getter
@Setter
public class ApiGoodsListVo extends ApiGoodsBaseVo{
    @JsonProperty("goods_img")
    private String goodsImg;

    public ApiGoodsListVo() {
        super();
    }

    public ApiGoodsListVo(GoodsRecord goodsRecord) {
        super(goodsRecord);
        this.goodsImg = goodsRecord.getGoodsImg();
    }
}

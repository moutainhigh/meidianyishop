package com.meidianyi.shop.service.pojo.shop.goods.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** 对外接口-商品详情-商品信息
 * @author 李晓冰
 * @date 2020年05月28日
 */
@Getter
@Setter
public class ApiGoodsDetailVo extends ApiGoodsBaseVo {

    /**商品删除标志*/
    @JsonProperty("del_flag")
    private Byte delFlag;

    /**php 弱类型，单商品返回的所有图片，但是字段名也是goods_img*/
    @JsonProperty("goods_img")
    private List<String> goodsImgs;

    @JsonIgnore
    private String goodsImg;

    public ApiGoodsDetailVo() {
        super();
    }

    public ApiGoodsDetailVo(GoodsRecord goodsRecord) {
        super(goodsRecord);
        this.delFlag = goodsRecord.getDelFlag();
        this.goodsImg = goodsRecord.getGoodsImg();
        this.goodsImgs = new ArrayList<>(2);
    }
}

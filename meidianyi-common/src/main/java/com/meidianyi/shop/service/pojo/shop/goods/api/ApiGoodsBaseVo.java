package com.meidianyi.shop.service.pojo.shop.goods.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 *  对外接口-商品列表和详情中商品信息基类
 * @author 李晓冰
 * @date 2020年05月28日
 */
@Data
public class ApiGoodsBaseVo {
    @JsonProperty("goods_id")
    private Integer goodsId;
    @JsonProperty("goods_sn")
    private String goodsSn;
    @JsonProperty("cat_name")
    private String catName;
    @JsonProperty("goods_name")
    private String goodsName;
    @JsonProperty("add_time")
    private Timestamp addTime;
    @JsonProperty("update_time")
    private Timestamp updateTime;
    @JsonProperty("sku_count")
    private Integer skuCount;
    @JsonProperty("sku_list")
    private List<ApiGoodsSkuVo> skuList;

    @JsonIgnore
    private Integer sortId;

    public ApiGoodsBaseVo() {
    }

    public ApiGoodsBaseVo(GoodsRecord goodsRecord) {
        this.goodsId = goodsRecord.getGoodsId();
        this.goodsSn = goodsRecord.getGoodsSn();
        this.goodsName = goodsRecord.getGoodsName();
        this.addTime = goodsRecord.getCreateTime();
        this.updateTime = goodsRecord.getUpdateTime();
        this.sortId = goodsRecord.getSortId();
    }
}

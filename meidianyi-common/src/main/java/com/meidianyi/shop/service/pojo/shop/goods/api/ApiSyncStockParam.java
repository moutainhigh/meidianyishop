package com.meidianyi.shop.service.pojo.shop.goods.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 对外接口-epr-ekb 商品库存同步
 * @author 李晓冰
 * @date 2020年05月29日
 */
@Data
public class ApiSyncStockParam {
    @JsonProperty("sku_id")
    private Integer skuId;

    @JsonProperty("goods_num")
    private Integer goodsNum;
}

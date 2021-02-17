package com.meidianyi.shop.service.pojo.shop.goods.pos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * pos同步商品规格信息-对应规格信息
 * @author 李晓冰
 * @date 2020年04月28日
 */
@Data
public class PosSyncGoodsPrdParam {
    /**
     * 规格编码
     */
    @JsonProperty("prd_sn")
    private String prdSn;

    /**
     * 规格条码
     */
    @JsonProperty("prd_codes")
    private String prdCodes;

    /**
     * 上下架状态
     */
    @JsonProperty("is_on_sale")
    private Byte isOnSale;

    /**
     * 规格价格
     */
    @JsonProperty("prd_price")
    private BigDecimal prdPrice;

    /**
     * 由goodsService 设置
     */
    @JsonIgnore
    private Integer prdId;
}
